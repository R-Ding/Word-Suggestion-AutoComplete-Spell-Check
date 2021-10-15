package edu.caltech.cs2.types;

import edu.caltech.cs2.datastructures.IterableString;
import edu.caltech.cs2.datastructures.LinkedDeque;
import edu.caltech.cs2.interfaces.IDeque;

import java.util.Iterator;

public class NGram implements Iterable<String>, Comparable<NGram> {
    public static final String NO_SPACE_BEFORE = ",?!.-,:'";
    public static final String NO_SPACE_AFTER = "-'><=";
    public static final String REGEX_TO_FILTER = "”|\"|“|\\(|\\)|\\*";
    public static final String DELIMITER = "\\s+|\\s*\\b\\s*";
    private IDeque<String> data;

    public static String normalize(String s) {
        return s.replaceAll(REGEX_TO_FILTER, "").strip();
    }

    public NGram(IDeque<String> x) {
        this.data = new LinkedDeque<>();
        for (int i = 0; i < x.size(); i++) {
            this.data.addBack(x.peekFront());
            x.addBack(x.removeFront());
        }
    }

    public NGram(String data) {
        this(normalize(data).split(DELIMITER));
    }

    public NGram(String[] data) {
        this.data = new LinkedDeque<>();
        for (String s : data) {
            s = normalize(s);
            if (!s.isEmpty()) {
                this.data.addBack(s);
            }
        }
    }

    public NGram next(String word) {
        String[] data = new String[this.data.size()];
        for (int i = 0; i < data.length - 1; i++) {
            this.data.addBack(this.data.removeFront());
            data[i] = this.data.peekFront();
        }
        this.data.addBack(this.data.removeFront());
        data[data.length - 1] = word;
        return new NGram(data);
    }

    public String toString() {
        String result = "";
        String prev = "";
        for (String s : this.data) {
            result += ((NO_SPACE_AFTER.contains(prev) || NO_SPACE_BEFORE.contains(s) || result.isEmpty()) ?  "" : " ") + s;
            prev = s;
        }
        return result.strip();
    }

    @Override
    public Iterator<String> iterator() {
        return this.data.iterator();
    }


    @Override
    public int compareTo(NGram other) {
        if(this.data.size() != other.data.size()) {
            if (this.data.size() < other.data.size()) {
                return -1;
            }
            else {
                return 1;
            }
        }
        else{
            Iterator<String> opp = other.data.iterator();
            Iterator<String> useful = this.data.iterator();
            while(opp.hasNext()){
                String thisone = useful.next();
                String thatone = opp.next();
                if(thisone.compareTo(thatone)!= 0){
                    return thisone.compareTo(thatone);
                }
            }
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        NGram op = (NGram) o;
        if (!(o instanceof NGram) || this.data.size() != op.data.size()) {
            return false;
        }
        Iterator<String> opp = op.data.iterator();
        Iterator<String> useful = this.data.iterator();
        while(opp.hasNext()){
            if(!opp.next().equals(useful.next())){
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int[] values = new int[this.data.size()];
        int size = this.data.size();
        int output = 0;
        IDeque<String> holder =  this.data;
        int p = 0;
        for(String x : holder){
            values[p] = x.hashCode();
            p++;
        }
        for(int i = values.length - 1; i >= 0; i-- ){
            output += 37 * (output + values[i]);
        }
        return output;
    }
}