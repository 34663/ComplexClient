package complex.event.impl;

import complex.event.Event;

public class EventText extends Event {
    private String text;

    public EventText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        super.setCancelled(cancelled);
    }
}
