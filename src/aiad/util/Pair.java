package aiad.util;

import java.io.Serializable;
import java.util.Objects;

public class Pair implements Serializable {
    Object k;
    Object v;

    public Pair(Object key, Object value) {
        k = key;
        v = value;
    }

    public Object getKey() {
        return k;
    }

    public Object getValue() {
        return v;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pair)) return false;
        Pair pair = (Pair) o;
        return k.equals(pair.k) &&
                v.equals(pair.v);
    }

    @Override
    public int hashCode() {
        return Objects.hash(k, v);
    }

    @Override
    public String toString() {
        return "Pair {" +
                "k=" + k +
                ", v=" + v +
                '}';
    }

}