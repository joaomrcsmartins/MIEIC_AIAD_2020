package aiad.util;

import aiad.TrafficPoint;

public class ClientPair extends Pair implements Comparable {
    public ClientPair(TrafficPoint key, Double value) {
        super(key, value);
    }

    @Override
    public TrafficPoint getKey() {
        return (TrafficPoint) super.getKey();
    }

    @Override
    public Double getValue() {
        return (Double) super.getValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientPair)) return false;
        Pair pair = (Pair) o;
        return k.equals(pair.k);
    }

    @Override
    public int compareTo(Object o) {
        if (this == o) return 0;
        if (!(o instanceof ClientPair)) throw new ClassCastException("Object is not of type ClientPair");
        ClientPair cp2 = (ClientPair) o;
        Double o1Value = this.getValue();
        Double o2Value = cp2.getValue();
        return o1Value.compareTo(o2Value);
    }


    public boolean isSatisfied() {
        return getKey().getTraffic().equals(getValue());
    }
}
