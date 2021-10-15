package wordcorrector;

import edu.caltech.cs2.datastructures.*;
import edu.caltech.cs2.interfaces.ICollection;
import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.IDictionary;
import edu.caltech.cs2.misc.CorrectionChoice;
import edu.caltech.cs2.types.NGram;

public class SpellingCorrector {
    private static final int MAX_EDITS = 2;
    private final AutoCompleteTrie dictionary;

    public SpellingCorrector(String dictFilename) {
        this.dictionary = new AutoCompleteTrie( (IDeque<Character> x) -> {
            char[] chars = new char[x.size()];
            for (int i = 0; i < chars.length; i++) {
                chars[i] = x.peekFront();
                x.addBack(x.removeFront());
            }
            return new IterableString(new String(chars));
        }, dictFilename);
    }

    /**
     * Computes the edit distance between strings a and b
     * @param a
     * @param b
     * @return the edit distance
     */
    public static int editDistance(String a, String b) {
        int[][] array = new int[a.length()+1][b.length()+1];
//        for (int i = 0; i < a.length()+1; i++) {
//            for (int j = 0; j < b.length()+1; j++) {
//                array[i][j] = -1;
//            }
//        }
        return editDistance(a, b, array);
    }

    private static int editDistance(String a, String b, int[][] memo) {
        // 2 base cases
        if (b.length() == 0) {
            return a.length();
        }
        else if (a.length() == 0) {
            return b.length();
        }

        else if (memo[a.length()][b.length()] != 0) {
            return memo[a.length()][b.length()];
        }
        // recursive cases
        else {
            String aCut = a.substring(0, a.length()-1);
            String bCut = b.substring(0, b.length()-1);

            // if last letters are both the same
            if (a.charAt(a.length() - 1) == (b.charAt(b.length() - 1))) {
                memo[a.length()][b.length()] = editDistance(aCut, bCut, memo);
                return memo[a.length()][b.length()];
            }
            // if last letters are different,
            else {
                memo[a.length()][b.length()] = Math.min(Math.min(editDistance(aCut, b, memo) + 1, editDistance(a, bCut, memo) + 1),
                        editDistance(aCut, bCut, memo) + 1);
                return memo[a.length()][b.length()];
            }
        }
    }

    /**
     * Uses the dictionary to autocomplete keyString if it can find one guaranteed completion
     * @param keyString - the prefix to be completed
     * @return the autocompleted string if exactly one is found
     */
    public String autocomplete(String keyString) {
        IDeque<IterableString> possibilities = this.dictionary.autocomplete(keyString);
        if (possibilities.size() == 1) {
            return possibilities.removeFront().toString();
        }
        return null;
    }

    /**
     * Returns true if the given word is misspelled (i.e. it's not in the dictionary)
     * @param str - the potentially misspelled word
     * @return A boolean indicating the correctness of the spelling
     */
    public boolean isMisspelled(String str) {
        if (this.dictionary.containsKey(new IterableString(str))) {
            return false;
        }
        return true;
    }

    /**
     * Get all possible valid corrections for segment
     * @param segment - the string you want to get corrections for
     * @return A collection of correction choices for the given segment
     */
    public ICollection<CorrectionChoice> getPossibleCorrections(String segment) {
        // recursively obtain all possible corrections
        IDictionary<String, IDictionary<String, CorrectionChoice>> correctionMap =
                new ChainingHashDictionary<String, IDictionary<String, CorrectionChoice>>(MoveToFrontDictionary::new);
        getPossibleCorrections(segment, 1, correctionMap);

        // now collapse the possible corrections into one list of corrections using a hashset
        // additionally, we must ensure that our words can be found in the dictionary
        IDictionary<String, CorrectionChoice> allCorrections = new ChainingHashDictionary<String, CorrectionChoice>(MoveToFrontDictionary::new);
        for (IDictionary<String, CorrectionChoice> l : correctionMap.values()) {
            for (CorrectionChoice c : l.values()) {
                AutoCompleteTrie.Word dictionaryWord = dictionary.get(new IterableString(c.word));
                if (dictionaryWord != null) {
                    int dictPosition = dictionaryWord.dictionaryIndex;
                    if (allCorrections.containsKey(c.word)) {
                        CorrectionChoice existing = allCorrections.get(c.word);
                        if (existing.editDistance > c.editDistance) {
                            allCorrections.put(c.word, new CorrectionChoice(c.word, c.editDistance, dictPosition));
                        }
                    }
                    else {
                        allCorrections.put(c.word, new CorrectionChoice(c.word, c.editDistance, dictPosition));
                    }
                }
            }
        }
        return allCorrections.values();
    }

    /**
     * Recursively obtain all possible words that are within MAX_EDITS of segment.
     *
     * @param numEdits - the number of edits to consider
     * @param segment - the word fragment we wish to autocorrect
     * @param possibleCorrections - A dictionary that maps a string to a deque of correction choices that are an edit
     *                            of segment
     */
    private void getPossibleCorrections(String segment, int numEdits,
                                        IDictionary<String, IDictionary<String, CorrectionChoice>> possibleCorrections)
    {
        // if we've already maxed out our number of edits, or if we've already got corrections for this segment, return
        if (numEdits > MAX_EDITS || possibleCorrections.containsKey(segment)) {
            return;
        }

        // create the list of possible corrections for this segment
        possibleCorrections.put(segment, new ChainingHashDictionary<String, CorrectionChoice>(MoveToFrontDictionary::new));

        // get possible edits for the segment
        IDeque<String> edits = getPossibleEdits(segment);
        for (String edit : edits) {
            if (!possibleCorrections.get(segment).containsKey(edit)) {
                possibleCorrections.get(segment).put(edit, new CorrectionChoice(edit, numEdits, -1));
            }
            // call recursively to get next round of edits
            getPossibleCorrections(edit, numEdits + 1, possibleCorrections);
        }
    }

    /**
     * Get all possible strings that are within 1 edit away from segment. An edit is a transposition, deletion,
     * replacement, or insertion.
     *
     * @param segment - the string to find all possible edits for
     * @return An array deque of strings containing all possible edits
     */
    private IDeque<String> getPossibleEdits(String segment) {
        IDeque<String> edited = new ArrayDeque<>();
        for (int i = 0; i < segment.length(); i++) {
            String left = segment.substring(0, i);
            String right = segment.substring(i);

            // transposition
            if (right.length() >= 2) {
                edited.add(left + right.charAt(1) + right.charAt(0) + right.substring(2));
            }

            // deletion
            if (right.length() >= 1) {
                edited.add(left + right.substring(1));
            }

            IterableString alphabet = new IterableString("abcdefghijklmnopqrstuvwxyz");
            // replaces
            if (right.length() >= 1) {
                for (char letter : alphabet) {
                    edited.add(left + letter + right.substring(1));
                }
            }

            // insertions
            for (char letter : alphabet) {
                edited.add(left + letter + right);
            }
        }
        return edited;
    }



    /**
     * Determines the best possible spelling correction
     * @param ngrams the map of ngrams to use when getting suggested words
     * @param text - the context (the n words preceding the misspelled word)
     * @param segment - the misspelled word
     * @param numConsidered - the number of options to consider when retrieving suggestions
     * @return the best spelling correction
     */
    public String getBestCorrection(NGramMap ngrams, String text, String segment, int numConsidered) {
        // Throw error if the text given isn't a proper ngram for the given ngrams map

        CorrectionChoice best = null;
        if (ngrams != null) {
            // get suggestions that are at most MAX_EDITS away
            String[] suggestions = ngrams.getWordsAfter(new NGram(text), numConsidered);
            for (String s : suggestions) {
                int distance = editDistance(s, segment);
                if (distance <= MAX_EDITS) {
                    if (best == null || distance < best.editDistance) {
                        int dictionaryPos = dictionary.get(new IterableString(s)).dictionaryIndex;
                        best = new CorrectionChoice(s, distance, dictionaryPos);
                    }
                }
            }
        }

        // If the number of suggestions that are at most MAX_EDITS away is 0, then we look at all possible corrections
        // and select the one with the closest edit distance
        if (best == null) {
            ICollection<CorrectionChoice> corrections = getPossibleCorrections(segment);
            for (CorrectionChoice correction : corrections) {
                if (best == null) {
                    best = correction;
                }
                else {
                    if (correction.editDistance < best.editDistance) {
                        best = correction;
                    }
                    else if (correction.editDistance == best.editDistance) {
                        if (correction.dictionaryPosition < best.dictionaryPosition) {
                            best = correction;
                        }
                    }
                }
            }
        }
        if (best == null) {
            return null;
        }
        return best.word;
    }
}
