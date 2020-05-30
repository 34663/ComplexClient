package complex.event.impl;

import complex.event.Event;
import net.minecraft.block.Block;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class EventLiquidCollide extends Event {
    private Block block;
    private BlockPos pos;
    private AxisAlignedBB bounds;

    public EventLiquidCollide(Block block, BlockPos pos, AxisAlignedBB bounds) {
        this.block = block;
        this.pos = pos;
        this.bounds = bounds;
    }

    public AxisAlignedBB getBounds() {
        return bounds;
    }

    public void setBounds(AxisAlignedBB bounds) {
        this.bounds = bounds;
    }

    public BlockPos getPos() {
        return pos;
    }

    public Block getBlock() {
        return block;
    }
}
