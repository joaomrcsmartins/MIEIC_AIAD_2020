package aiad.access_point;

import aiad.Coordinates;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;

public class FlyingAccessPoint extends AccessPoint {
    private int battery; //in percentage (0-100%)
    static int LOW_ENERGY_LVL = 10;
    private boolean charging;

    public FlyingAccessPoint(double trafficCapacity, Coordinates pos) {
        super(trafficCapacity, pos);
        this.battery = 100;
        this.charging = false;
    }

    public boolean isNear(FlyingAccessPoint drone) {
        return drone.getPos().getDistance(this.getPos()) <= MAX_RANGE;
    }

    public boolean isAvailable() {
        return !charging && super.isAvailable();
    }

    public void startCharging() {
        charging = true;
    }

    public void charge() {
        if (!charging) return;
        if (battery < 100)
            battery++;
        else
            charging = false;
    }

    public void discharge() {
        if (charging) return;
        if (battery > 0) battery--;
        if (battery <= LOW_ENERGY_LVL)
            System.out.println("LOW LEVEL WARNING!\nShould return to base and charge.");
    }

    @Override
    protected void setup() {
        super.setup();
        //TODO: add setup agent behavior when communicating with other FAPs
    }
}
