package complex.event.impl;

import complex.event.Event;
import net.minecraft.block.Block;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class EventBoundingBox extends Event {
    public Block block;
    public BlockPos pos;
    public AxisAlignedBB boundingBox;

    public EventBoundingBox(final Block block, final BlockPos pos, final AxisAlignedBB boundingBox) {
        this.block = block;
        this.pos = pos;
        this.boundingBox = boundingBox;
    }

    public Block getBlock() {
        return block;
    }

    public BlockPos getPos() {
        return pos;
    }

    public AxisAlignedBB getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(AxisAlignedBB boundingBox) {
        this.boundingBox = boundingBox;
    }
}
