package complex.module.impl.movement;

import complex.Complex;
import complex.command.Command;
import complex.event.EventTarget;
import complex.event.impl.EventRender2D;
import complex.event.impl.EventSafeWalk;
import complex.event.impl.EventUpdate;
import complex.module.Category;
import complex.module.Module;
import complex.module.data.Setting;
import complex.utils.*;
import complex.utils.timer.Timer;
import complex.utils.timer.Timer2;
import complex.utils.timer.Timer3;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TestScaffold extends Module {
    private static List<Block> invalidBlocks = Arrays.asList(
            Blocks.ENCHANTING_TABLE, Blocks.FURNACE, Blocks.CARPET, Blocks.CRAFTING_TABLE, Blocks.TRAPPED_CHEST, Blocks.CHEST, Blocks.DISPENSER,
            Blocks.AIR, Blocks.WATER, Blocks.LAVA, Blocks.FLOWING_WATER, Blocks.FLOWING_LAVA, Blocks.SAND,
            Blocks.SNOW_LAYER, Blocks.TORCH, Blocks.ANVIL, Blocks.JUKEBOX, Blocks.STONE_BUTTON, Blocks.WOODEN_BUTTON, Blocks.LEAVES,
            Blocks.NOTEBLOCK, Blocks.STONE_PRESSURE_PLATE, Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE,
            Blocks.WOODEN_PRESSURE_PLATE, Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, Blocks.STONE_SLAB, Blocks.WOODEN_SLAB, Blocks.STONE_SLAB2,
            Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM, Blocks.YELLOW_FLOWER, Blocks.RED_FLOWER, Blocks.GLASS_PANE, Blocks.STAINED_GLASS_PANE,
            Blocks.IRON_BARS, Blocks.CACTUS, Blocks.LADDER, Blocks.END_GATEWAY, Blocks.WATERLILY, Blocks.ICE
    );
    public boolean placing;
    public float keepYaw, keepPitch;
    private int facing = (int) getRandomInRange(2, 4);
    private double height;
    private double y;
    private int currentHeldItem;
    private Timer3 towerTimer;
    private Timer placeTimer;

    public TestScaffold() {
        super("TestScaffold", null, 0, 0, Category.Movement);
        towerTimer = new Timer3();
        placeTimer = new Timer();
    }

    @Override
    public void setup() {
        ArrayList<String> toweroptions = new ArrayList<>();
        toweroptions.add("Normal");
        Complex.getSettingsManager().rSetting(new Setting("Tower Mode", this, "Normal", toweroptions));
        Complex.getSettingsManager().rSetting(new Setting("PlaceDelay", this, 50L, 1L, 500L, false));
        Complex.getSettingsManager().rSetting(new Setting("TestSwing", this, false));
        Complex.getSettingsManager().rSetting(new Setting("Safe Walk", this, false));
        Complex.getSettingsManager().rSetting(new Setting("TestTower", this, true));
        Complex.getSettingsManager().rSetting(new Setting("KeepY", this, false));
    }

    @Override
    public void onEnable() {
        y = mc.thePlayer.posY;
        placing = false;
        currentHeldItem = mc.thePlayer.inventory.currentItem;
        super.onEnable();
    }
    @Override
    public void onDisable() {
        mc.thePlayer.inventory.currentItem = currentHeldItem;
        placing = false;
        super.onDisable();
    }

    @EventTarget
    public void onRender(EventRender2D event) {
        int color = new Color(255, 0, 0, 255).getRGB();

        if (this.getBlockCount() >= 64 && 128 > this.getBlockCount()) {
            color = new Color(255, 255, 0, 255).getRGB();
        } else if (this.getBlockCount() >= 128) {
            color = new Color(0, 255, 0, 255).getRGB();
        }

        GlStateManager.enableBlend();
        mc.fontRendererObj.drawStringWithShadow(this.getBlockCount() + "", event.getResolution().getScaledWidth() / 2F - mc.fontRendererObj.getStringWidth(String.valueOf(this.getBlockCount())) / 2F, event.getResolution().getScaledHeight() / 2 - 25, color);
    }
    @EventTarget
    public void onUpdate(EventUpdate event) {
        boolean tower = Complex.getSettingsManager().getSettingByName("TestTower").getValBoolean();
        boolean swing = Complex.getSettingsManager().getSettingByName("TestSwing").getValBoolean();
        boolean samey = Complex.getSettingsManager().getSettingByName("KeepY").getValBoolean();
        float placeDelay = (float) Complex.getSettingsManager().getSettingByName("PlaceDelay").getValDouble();
        String towerMode = Complex.getSettingsManager().getSettingByName("Tower Mode").getValString();
        this.setDisplayName("TestScaffold");

        if (samey) {
            if ((!this.mc.thePlayer.isMoving() && this.mc.gameSettings.keyBindJump.isKeyDown()) || this.mc.thePlayer.isCollidedVertically || this.mc.thePlayer.onGround) {
                y = mc.thePlayer.posY;
            }
        } else {
            y = mc.thePlayer.posY;
        }
        BlockPos underPos = new BlockPos(mc.thePlayer.posX, y - 1, mc.thePlayer.posZ);
        Block underBlock = mc.theWorld.getBlockState(underPos).getBlock();
        BlockData blockData = find(new Vec3d(0, 0, 0));

        if (event.isPre()) {
            if (tower) {
                if (getBlockSlot() != -1 && mc.thePlayer.movementInput.jump) {
                    if (towerMode.equalsIgnoreCase("Normal")) {
                        mc.thePlayer.motionZ = 0;
                        mc.thePlayer.motionX = 0;
                        if (mc.thePlayer.onGround) {
                            mc.thePlayer.jump();

                            if (towerTimer.hasReached(1500)) {
                                mc.thePlayer.motionY = -0.28;
                                towerTimer.reset();
                            }
                        } else if (mc.theWorld.getBlockState(underPos).getBlock().getMaterial().isReplaceable() && blockData != null) {
                            mc.thePlayer.motionY = .41955;
                        }
                    }
                }
            }
        } else {
            if (getBlockSlot() == -1)
                if (towerTimer.delay(150)) {
                    getBlocksFromInventory();
                    towerTimer.reset();
                }
        }

        mc.thePlayer.rotationPitchHead = 70;
        if (mc.theWorld.getBlockState(underPos).getBlock().getMaterial().isReplaceable() && blockData != null) {
            placing = true;
            if (getBlockSlot() != -1) {
                if (event.isPre()) {
                    BlockPos sideBlock = blockData.position;
                    float yaw = BlockUtils.aimAtBlock(sideBlock)[0];
                    float pitch = BlockUtils.aimAtBlock(sideBlock)[1];
                    event.setYaw(yaw);
                    event.setPitch(pitch);
                    mc.thePlayer.renderYawOffset = yaw;
                    mc.thePlayer.rotationYawHead = yaw;
                    mc.thePlayer.rotationPitchHead = pitch;
                } else if (event.isPost()) {
                    mc.thePlayer.inventory.currentItem = getBlockSlot();
                    double hitvecx = (blockData.position.getX() + height) + getRandomInRange(-.08, .29) + (blockData.face.getFrontOffsetX() / facing);
                    double hitvecy = (blockData.position.getY() + height) + getRandomInRange(-.08, .29) + (blockData.face.getFrontOffsetY() / facing);
                    double hitvecz = (blockData.position.getZ() + height) + getRandomInRange(-.08, .29) + (blockData.face.getFrontOffsetZ() / facing);
                    Vec3d vec = new Vec3d(hitvecx, hitvecy, hitvecz);
                    boolean b = false;

                    for (int i = 0; i < 9; ++i) {
                        if (this.mc.thePlayer.inventory.getStackInSlot(i) != null && this.mc.thePlayer.inventory.getStackInSlot(i).stackSize != 0 && this.mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemBlock && !this.invalidBlocks.contains(((ItemBlock)this.mc.thePlayer.inventory.getStackInSlot(i).getItem()).getBlock())) {
                            this.mc.thePlayer.connection.sendPacket(new CPacketHeldItemChange(this.mc.thePlayer.inventory.currentItem = i));
                            b = true;
                            break;
                        }
                    }
                    if (!b) {
                        for (int j = 0; j < 45; ++j) {
                            if (this.mc.thePlayer.inventory.getStackInSlot(j) != null && this.mc.thePlayer.inventory.getStackInSlot(j).stackSize != 0 && this.mc.thePlayer.inventory.getStackInSlot(j).getItem() instanceof ItemBlock && !this.invalidBlocks.contains(((ItemBlock) this.mc.thePlayer.inventory.getStackInSlot(j).getItem()).getBlock())) {
                                this.mc.playerController.windowClick(this.mc.thePlayer.inventoryContainer.windowId, j, 8, ClickType.SWAP, this.mc.thePlayer);
                                break;
                            }
                        }
                    }

                    if (placeTimer.delay(placeDelay)) {
                        mc.playerController.processRightClickBlock(mc.thePlayer, mc.theWorld, blockData.position, blockData.face, vec, EnumHand.MAIN_HAND);

                        if (swing) {
                            mc.thePlayer.swingArm(EnumHand.MAIN_HAND);
                        } else {
                            mc.thePlayer.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                        }
                        mc.thePlayer.connection.sendPacket(new CPacketHeldItemChange(this.mc.thePlayer.inventory.currentItem = currentHeldItem));
                        placeTimer.reset();
                    }
                    mc.thePlayer.inventory.currentItem = currentHeldItem;
                    mc.playerController.updateController();
                }
            }
        } else {
            placing = false;
        }
    }
    @EventTarget
    public void onSafeWalk(EventSafeWalk event) {
        if (Complex.getSettingsManager().getSettingByName("Safe Walk").getValBoolean()) {
            event.setCancelled(Complex.getSettingsManager().getSettingByName("KeepY").getValBoolean() ? (!this.mc.gameSettings.keyBindJump.isKeyDown() && this.mc.thePlayer.onGround) : this.mc.thePlayer.onGround);
        }
    }

    private void getBlocksFromInventory() {
        if (mc.currentScreen instanceof GuiChest) {
            return;
        }

        for (int index = 9; index < 36; index++) {
            final ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(index).getStack();

            if (stack == null) {
                continue;
            }

            if (isValid(stack.getItem())) {
                mc.playerController.windowClick(0, index, 6, ClickType.SWAP, mc.thePlayer);
                break;
            }
        }
    }
    public BlockData find(Vec3d offset3) {
        double xx = mc.thePlayer.posX;
        double yy = mc.thePlayer.posY;
        double zz = mc.thePlayer.posZ;
        EnumFacing[] invert = new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN, EnumFacing.SOUTH, EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.WEST};
        BlockPos position = new BlockPos(new Vec3d(xx, yy, zz).add(offset3)).offset(EnumFacing.DOWN);

        for (EnumFacing facing : EnumFacing.values()) {
            BlockPos offset = position.offset(facing);

            if (mc.theWorld.getBlockState(offset).getBlock() instanceof BlockAir || rayTrace(mc.thePlayer.getLook(0.0f), getPositionByFace(offset, invert[facing.ordinal()]))) {
                continue;
            }
            return new BlockData(offset, invert[facing.ordinal()]);
        }

        BlockPos[] offsets = new BlockPos[]{new BlockPos(-1, 0, 0), new BlockPos(1, 0, 0), new BlockPos(0, 0, -1), new BlockPos(0, 0, 1), new BlockPos(-1, -1, 0), new BlockPos(1, -1, 0), new BlockPos(0, -1, -1), new BlockPos(0, -1, 1), new BlockPos(0, -1, 0), new BlockPos(0, 1, 0)};
        for (BlockPos offset : offsets) {
            BlockPos offsetPos = position.add(0, 0, offset.getZ());

            if (!(mc.theWorld.getBlockState(offsetPos).getBlock() instanceof BlockAir) || (mc.theWorld.getBlockState(offsetPos).getBlock() instanceof BlockLiquid)) {
                continue;
            }
            for (EnumFacing facing : EnumFacing.values()) {
                BlockPos offset2 = offsetPos.offset(facing);

                if (mc.theWorld.getBlockState(offset2).getBlock() instanceof BlockAir || rayTrace(mc.thePlayer.getLook(0.01f), getPositionByFace(offset, invert[facing.ordinal()]))) {
                    continue;
                }
                return new BlockData(offset2, invert[facing.ordinal()]);
            }
        }
        return null;
    }
    private int getBlockCount() {
        int blockCount = 0;

        for (int i = 0; i < 45; ++i) {
            if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                continue;
            }

            ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            Item item = is.getItem();

            if (!isValid(item)) {
                continue;
            }
            blockCount += is.stackSize;
        }
        return blockCount;
    }
    private int getBlockSlot() {
        int n = 0;
        for (int i = 0; i < 45; ++i) {
            if (this.mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack stack = this.mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                final Item item = stack.getItem();
                if (stack.getItem() instanceof ItemBlock) {
                    if (!invalidBlocks.contains(((ItemBlock) item).getBlock())) {
                        n += stack.stackSize;
                    }
                }
            }
        }
        return n;
    }
    public static boolean isValid(Item item) {
        if (!(item instanceof ItemBlock)) {
            return false;
        } else {
            ItemBlock iBlock = (ItemBlock) item;
            Block block = iBlock.getBlock();
            return !invalidBlocks.contains(block);
        }
    }
    private float[] getRotation(final double n, final double n2, final double n3) {
        final double n4 = n - this.mc.thePlayer.posX;
        final double n5 = n2 - this.mc.thePlayer.posY;
        final double n6 = n3 - this.mc.thePlayer.posZ;
        final double n7 = MathHelper.sqrt_double(n4 * n4 + n6 * n6);
        return new float[] { (float)(Math.atan2(n6, n4) * 180.0 / 3.141592653589793) - 90.0f, (float)(-(Math.atan2(n5, n7) * 180.0 / 3.141592653589793)), (float)(-(Math.atan2(n5, n7) * 180.0 / 3.141592653589793)) };
    }
    private float[] getBlockRotations(int x, int y, int z, EnumFacing facing) {
        Entity temp = new EntitySnowball(mc.theWorld);
        temp.posX = (x + 0.5);
        temp.posY = (y + (height = 0.5));
        temp.posZ = (z + 0.5);
        return mc.thePlayer.canEntityBeSeen(temp) ? getAngles(temp) : getRotationToBlock(new BlockPos(x, y, z), facing);
    }
    private float[] getAngles(Entity e) {
        return new float[]{getYawChangeToEntity(e) + mc.thePlayer.rotationYaw, getPitchChangeToEntity(e) + mc.thePlayer.rotationPitch};
    }
    private float getYawChangeToEntity(Entity entity) {
        double deltaX = entity.posX - mc.thePlayer.posX;
        double deltaZ = entity.posZ - mc.thePlayer.posZ;
        double yawToEntity;
        final double v = Math.toDegrees(Math.atan(deltaZ / deltaX));

        if ((deltaZ < 0) && (deltaX < 0)) {
            yawToEntity = 90 + v;
        } else {
            if ((deltaZ < 0) && (deltaX > 0.0D)) {
                yawToEntity = -90 + v;
            } else {
                yawToEntity = Math.toDegrees(-Math.atan(deltaX / deltaZ));
            }
        }
        return MathHelper.wrapDegrees(-(mc.thePlayer.rotationYaw - (float) yawToEntity));
    }
    private float getPitchChangeToEntity(Entity entity) {
        double deltaX = entity.posX - mc.thePlayer.posX;
        double deltaZ = entity.posZ - mc.thePlayer.posZ;
        double deltaY = entity.posY - 1.6D + entity.getEyeHeight() - 0.4 - mc.thePlayer.posY;
        double distanceXZ = MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ);
        double pitchToEntity = -Math.toDegrees(Math.atan(deltaY / distanceXZ));
        return -MathHelper.wrapDegrees(mc.thePlayer.rotationPitch - (float) pitchToEntity);
    }
    public float[] getRotationToBlock(BlockPos pos, EnumFacing face) {
        double random = getRandomInRange(.45, .55);
        int ranface = (int) getRandomInRange(2, 4);
        double xDiff = pos.getX() + (height = random) - mc.thePlayer.posX + face.getDirectionVec().getX() / (facing = ranface);
        double zDiff = pos.getZ() + (height = random) - mc.thePlayer.posZ + face.getDirectionVec().getZ() / (facing = ranface);
        double yDiff = pos.getY() - mc.thePlayer.posY - 1;
        double distance = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
        float yaw = (float) -Math.toDegrees(Math.atan2(xDiff, zDiff));
        float pitch = (float) -Math.toDegrees(Math.atan(yDiff / distance));
        return new float[]{Math.abs(yaw - mc.thePlayer.rotationYaw) < .1 ? mc.thePlayer.rotationYaw : yaw, Math.abs(pitch - mc.thePlayer.rotationPitch) < .1 ? mc.thePlayer.rotationPitch : pitch};
    }
    private double getRandomInRange(double min, double max){
        return min + (Math.random()*(Math.abs(min)+Math.abs(max)+1));
    }
    private int getRandomInRange(int min, int max){
        return (int) (min + (Math.random()*(Math.abs(min)+Math.abs(max)+1)));
    }
    public Vec3d getPositionByFace(BlockPos position, EnumFacing facing) {
        Vec3d offset = new Vec3d((double) facing.getDirectionVec().getX() / 2.0, (double) facing.getDirectionVec().getY() / 2.0, (double) facing.getDirectionVec().getZ() / 2.0);
        Vec3d point = new Vec3d((double) position.getX() + 0.5, (double) position.getY() + 0.5, (double) position.getZ() + 0.5);
        return point.add(offset);
    }
    private boolean rayTrace(Vec3d origin, Vec3d position) {
        Vec3d difference = position.subtract(origin);
        int steps = 10;
        double x = difference.xCoord / (double) steps;
        double y = difference.yCoord / (double) steps;
        double z = difference.zCoord / (double) steps;
        Vec3d point = origin;

        for (int i = 0; i < steps; ++i) {
            BlockPos blockPosition = new BlockPos(point = point.addVector(x, y, z));
            IBlockState blockState = mc.theWorld.getBlockState(blockPosition);

            if (blockState.getBlock() instanceof BlockLiquid || blockState.getBlock() instanceof BlockAir) {
                continue;
            }

            AxisAlignedBB boundingBox = blockState.getBlock().getCollisionBoundingBox(blockState, mc.theWorld, blockPosition);
            if (boundingBox == null) {
                boundingBox = new AxisAlignedBB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
            }
            if (!boundingBox.offset(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ()).isVecInside(point)) {
                continue;
            }
            return true;
        }
        return false;
    }

    private static class BlockData {
        public BlockPos position;
        public EnumFacing face;

        private BlockData(BlockPos position, EnumFacing face) {
            this.position = position;
            this.face = face;
        }
    }
}
