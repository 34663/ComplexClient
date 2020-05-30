package complex.utils.timer;

public class Timer4 {
    private long time = this.getTime();

    private long getTime() {
        return System.nanoTime() / 1000000L;
    }

    public boolean hasReached(long milliseconds) {
        if (this.getTime() - this.time >= milliseconds) {
            this.w9iFhyeJTO1tpJ5();
            return true;
        } else {
            return false;
        }
    }

    public void w9iFhyeJTO1tpJ5() {
        this.time = this.getTime();
    }
}
