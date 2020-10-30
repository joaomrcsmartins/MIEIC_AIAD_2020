package aiad;

import jade.core.Agent;

public class TrafficPoint extends Agent {
    protected Integer traffic;
    protected Coordinates position;

    public Integer getTraffic() {
        return traffic;
    }

    public void setTraffic(Integer traffic) {
        this.traffic = traffic;
    }

    public Coordinates getPosition() {
        return position;
    }

    public void setPosition(Coordinates position) {
        this.position = position;
    }

    public TrafficPoint(Integer traffic, Coordinates position){
        this.traffic = traffic;
        this.position = position;
    }
}
