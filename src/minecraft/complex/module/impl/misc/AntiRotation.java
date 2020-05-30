package complex.module.impl.misc;

import complex.event.EventTarget;
import complex.event.impl.EventUpdate;
import complex.module.Category;
import complex.module.Module;

public class AntiRotation extends Module {
    public AntiRotation() {
        super("AntiRotation", null, 0, 0, Category.Misc);
    }

    @Override
    public void onEnable() {
        this.setDisplayName("AntiRotation");
        super.onEnable();
    }

    @EventTarget
    public void onUpdate(EventUpdate eventUpdate) {
        float yaw = getRandomInRange(0, 360);
        float pitch = getRandomInRange(-90, 90);
        mc.thePlayer.rotationYawHead = yaw;
        mc.thePlayer.renderYawOffset = yaw;
        mc.thePlayer.rotationPitchHead = pitch;
    }

    private float getRandomInRange(float min, float max){
        return (float) (min + (Math.random()*(Math.abs(min)+Math.abs(max)+1)));
    }
}
