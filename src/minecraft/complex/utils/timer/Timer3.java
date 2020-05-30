package complex.utils.timer;

public class Timer3 {
    private long time = System.nanoTime() / 1000000;
    private long prevMS = 0;
    private long lastMS = 0L;

    public boolean hasTimeElapsed(long time, boolean reset) {
        if (this.time() >= time) {
            if (reset) {
                this.reset();
            }
            return true;
        }
        return false;
    }

    public boolean hasTimeElapsed(final double time) {
        return this.getTime() >= time;
    }

    public double getTime() {
        return System.nanoTime() / 1000000L - this.time;
    }

    public boolean delay(float milliSec) {
        if ((float)(this.time() - this.prevMS) >= milliSec) {
            return true;
        }
        return false;
    }

    public long time() {
        return System.nanoTime() / 1000000 - this.time;
    }

    public void reset() {
        this.time = System.nanoTime() / 1000000;
    }

    public long getDifference() {
        return this.time() - this.prevMS;
    }

    public boolean hasReached(long milliseconds) {
        return this.getCurrentMS() - this.lastMS >= milliseconds;
    }

    public boolean hasReachedfloat(float timeLeft) {
        return (float)(this.getCurrentMS() - this.lastMS) >= timeLeft;
    }

    public long getCurrentMS() {
        return System.nanoTime() / 1000000L;
    }
}
