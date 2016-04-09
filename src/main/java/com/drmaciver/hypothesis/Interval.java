package com.drmaciver.hypothesis;

/**
 * Created by david on 4/9/16.
 */
class Interval implements Comparable<Interval> {
    final int start;

    final int end;

    public Interval(int start, int end) {
        super();
        this.start = start;
        this.end = end;
    }

    public int compareTo(Interval other) {
        if (length() < other.length())
            return -1;
        if (length() > other.length())
            return 1;
        if (start < other.start)
            return -1;
        if (start > other.start)
            return 1;
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Interval other = (Interval) obj;
        return end == other.end && start == other.start;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + end;
        result = prime * result + start;
        return result;
    }

    int length() {
        return end - start;
    }
}
