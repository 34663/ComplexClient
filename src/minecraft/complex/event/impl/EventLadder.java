package complex.event.impl;

import complex.event.Event;

public class EventLadder extends Event {
    private double motionY;
    private boolean pre;
    //Dans entitylivingbase
    public EventLadder(double motionY, boolean pre) {
        this.motionY = motionY;
        this.pre = pre;
    }

    public double getMotionY() {
        return motionY;
    }
    public void setMotionY(double motiony) {
        this.motionY = motiony;
    }

    public boolean isPre() {
        return pre;
    }

    public boolean isPost() {
        return !pre;
    }
}
