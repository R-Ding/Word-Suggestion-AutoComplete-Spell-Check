package edu.caltech.cs2.misc;

import java.util.Comparator;

public class IntegerComparator implements Comparator<Integer> {
    @Override
    public int compare(Integer a, Integer b) {
        if (a < b) {
            return -1;
        }
        else if (a == b) {
            return 0;
        }
        return 1;
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj);
    }
}
