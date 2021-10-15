package edu.caltech.cs2.misc;

public class CorrectionChoice implements Comparable<CorrectionChoice> {
    public String word;
    public int editDistance;
    public int dictionaryPosition;

    public CorrectionChoice(String word, int editDistance, int dictionaryPosition) {
        this.word = word;
        this.editDistance = editDistance;
        this.dictionaryPosition = dictionaryPosition;
    }

    @Override
    public String toString() {
        return this.word + ":" + this.editDistance + ":" + this.dictionaryPosition;
    }

    @Override
    public int compareTo(CorrectionChoice other) {
        int result = this.editDistance - other.editDistance;
        if (result != 0) {
            return result;
        }
        return this.dictionaryPosition - other.dictionaryPosition;
    }

    @Override
    public boolean equals(Object othero) {
        if (!(othero instanceof CorrectionChoice)) {
            return false;
        }
        CorrectionChoice other = (CorrectionChoice) othero;
        return (this.word.equals(other.word) && this.editDistance == other.editDistance &&
                this.dictionaryPosition == other.dictionaryPosition);
    }

    @Override
    public int hashCode() {
        return this.word.hashCode();
    }
}
