package complex.utils.render;

import complex.utils.Location2;

public class Particles {
    public int ticks;
    public Location2 location;
    public String text;

    public Particles(final Location2 location, final String text) {
        this.location = location;
        this.text = text;
        this.ticks = 0;
    }
}