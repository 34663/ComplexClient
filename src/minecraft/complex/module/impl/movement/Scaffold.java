package complex.module.impl.movement;

import complex.Complex;
import complex.command.Command;
import complex.event.EventTarget;
import complex.event.impl.EventRender2D;
import complex.event.impl.EventUpdate;
import complex.module.Category;
import complex.module.Module;
import complex.module.data.Setting;
import complex.utils.BlockUtils;
import complex.utils.MoveUtils;
import complex.utils.PlayerUtil;
import complex.utils.RotationUtils;
import complex.utils.font.CFontRenderer;
import complex.utils.font.FontLoaders;
import complex.utils.timer.Timer;
import complex.utils.timer.Timer3;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Scaffold extends Module {
    private static final List<Block> blacklistedBlocks = Arrays.asList(Blocks.AIR, Blocks.WATER, Blocks.FLOWING_WATER, Blocks.LAVA, Blocks.FLOWING_LAVA, Blocks.ENCHANTING_TABLE, Blocks.CARPET, Blocks.GLASS_PANE, Blocks.STAINED_GLASS_PANE, Blocks.IRON_BARS, Blocks.SNOW_LAYER, Blocks.ICE, Blocks.PACKED_ICE, Blocks.COAL_ORE, Blocks.DIAMOND_ORE, Blocks.EMERALD_ORE, Blocks.CHEST, Blocks.TORCH, Blocks.ANVIL, Blocks.TRAPPED_CHEST, Blocks.NOTEBLOCK, Blocks.JUKEBOX, Blocks.TNT, Blocks.GOLD_ORE, Blocks.IRON_ORE, Blocks.LAPIS_ORE, Blocks.LIT_REDSTONE_ORE, Blocks.QUARTZ_ORE, Blocks.REDSTONE_ORE, Blocks.WOODEN_PRESSURE_PLATE, Blocks.STONE_PRESSURE_PLATE, Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, Blocks.STONE_BUTTON, Blocks.WOODEN_BUTTON, Blocks.LEAVES, Blocks.BEACON, Blocks.LADDER, Blocks.SAPLING, Blocks.OAK_FENCE, Blocks.RED_FLOWER, Blocks.YELLOW_FLOWER, Blocks.FLOWER_POT, Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM, Blocks.SAND, Blocks.TALLGRASS, Blocks.TRIPWIRE_HOOK, Blocks.TRIPWIRE, Blocks.GRAVEL, Blocks.DISPENSER, Blocks.DROPPER, Blocks.CRAFTING_TABLE, Blocks.FURNACE, Blocks.REDSTONE_TORCH, Blocks.STANDING_SIGN, Blocks.WALL_SIGN);
    public static float yaw = 999, pitch = 999;
    private List<Block> invalid;
    private BlockData blockData;
    public static double moveSpeed;
    private int slot;
    private double offsetToUse = 0.0D;
    private float oldYaw;
    private float oldPitch;
    public final Timer timer = new Timer();
    public final Timer towertimer = new Timer();
    public final Timer3 timer3 = new Timer3();
    public boolean hypixel = false;
    public static List<Block> getBlacklistedBlocks() { return blacklistedBlocks; }

    public Scaffold() {
        super("Scaffold", null, 0, 0, Category.Movement);
    }

    @Override
    public void setup() {
        ArrayList<String> options = new ArrayList<>();
        options.add("New");
        options.add("Cube");
        Complex.getSettingsManager().rSetting(new Setting("Scaffold Mode", this, "New", options));
        Complex.getSettingsManager().rSetting(new Setting("Tower", this, true));
        Complex.getSettingsManager().rSetting(new Setting("NoSwing", this, true));
    }

    @Override
    public void onEnable() {
        super.onEnable();
        yaw = 999;
        pitch = 999;
        this.invalid = Arrays.asList(Blocks.AIR, Blocks.WATER, Blocks.FIRE, Blocks.FLOWING_WATER, Blocks.LAVA, Blocks.FLOWING_LAVA, Blocks.ICE, Blocks.PACKED_ICE, Blocks.NOTEBLOCK, Blocks.CHEST, Blocks.ANVIL);
    }

    @EventTarget
    public void onRender2D(EventRender2D event) {
        CFontRenderer font = FontLoaders.Tahoma20;
        yaw = 999;
        pitch = 999;
        ScaledResolution sr = new ScaledResolution(mc);
        font.drawStringWithShadow(getTotalBlocks() + " Blocks", sr.getScaledWidth() / 2 - 6 + 20, sr.getScaledHeight() / 2 - 4, 0xffffff);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        String Mode = Complex.getSettingsManager().getSettingByName("Scaffold Mode").getValString();
        boolean tower = Complex.getSettingsManager().getSettingByName("Tower").getValBoolean();

        BlockPos underPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ);
        this.setDisplayName("Scaffold §7" + Mode);
        this.hypixel = true;

        if (Mode.equalsIgnoreCase("New") || Mode.equalsIgnoreCase("Cube")) {
            if (Mode.equalsIgnoreCase("Cube")) {
                mc.thePlayer.setSprinting(false);
                MoveUtils.setMotion(0.05F);
            }
            if (event.isPre()) {
                int tempSlot = getBlockSlot();
                this.blockData = null;
                this.slot = -1;

                if (tempSlot == -1) {
                    return;
                }
                if (!mc.gameSettings.keyBindJump.pressed) {
                    this.timer.reset();
                }
                if (tempSlot != -1) {
                    double forward = MovementInput.moveForward;
                    double strafe = MovementInput.moveStrafe;
                    double x2 = Math.cos(Math.toRadians(mc.thePlayer.rotationYaw + 90.0F));
                    double z2 = Math.sin(Math.toRadians(mc.thePlayer.rotationYaw + 90.0F));
                    Random random = new Random();
                    double xOffset = MovementInput.moveForward * (this.hypixel ? 0.0D : mc.gameSettings.keyBindJump.pressed ? 1.0D : this.offsetToUse * x2);
                    double zOffset = MovementInput.moveForward * (this.hypixel ? 0.0D : mc.gameSettings.keyBindJump.pressed ? 1.0D : this.offsetToUse * z2);
                    double x = mc.thePlayer.posX + (this.hypixel ? 0.0D : mc.gameSettings.keyBindJump.pressed ? 0.1D : xOffset);
                    double y = mc.thePlayer.posY - 0.1D;
                    double z = mc.thePlayer.posZ + (this.hypixel ? 0.0D : mc.gameSettings.keyBindJump.pressed ? 0.1D : zOffset);
                    float yaww = mc.thePlayer.rotationYaw;
                    x += forward * 0.45D * Math.cos(Math.toRadians(yaww + 90.0F)) + strafe * 0.45D * Math.sin(Math.toRadians(yaww + 90.0F));
                    z += forward * 0.45D * Math.sin(Math.toRadians(yaww + 90.0F)) - strafe * 0.45D * Math.cos(Math.toRadians(yaww + 90.0F));
                    BlockPos blockBelow1 = new BlockPos(x, y, z);

                    if (mc.theWorld.getBlockState(blockBelow1).getBlock() == Blocks.AIR) {
                        BlockData data = getBlockData(underPos);
                        this.offsetToUse = 0.0D;
                        this.blockData = getBlockData(blockBelow1);
                        this.slot = tempSlot;

                        if (this.blockData != null) {
                            float yaw = BlockUtils.aimAtBlock(this.blockData.position)[0];
                            float pitch = BlockUtils.aimAtBlock(this.blockData.position)[1];
                            event.setYaw(yaw);
                            event.setPitch(pitch);
                            mc.thePlayer.renderYawOffset = yaw;
                            mc.thePlayer.rotationYawHead = yaw;
                            mc.thePlayer.rotationPitchHead = pitch;
                        }
                    }
                }
            }
            if ((event.isPost()) && (this.blockData != null)) {
                boolean wasSprintng = mc.thePlayer.isSprinting();
                if (wasSprintng) {
                    mc.thePlayer.setSprinting(true);
                }
                if ((wasSprintng) && (canSprint()) && (mc.gameSettings.keyBindForward.pressed)) {
                    mc.thePlayer.setSprinting(true);
                }
            } else {
                int tempSlot = getBlockSlot();
                this.blockData = null;
                this.slot = -1;

                if (tempSlot == -1) {
                    return;
                }
                double x = Scaffold.mc.thePlayer.posX;
                double y = Scaffold.mc.thePlayer.posY - 0.8;
                double z = Scaffold.mc.thePlayer.posZ;
                BlockPos belowPlayer = new BlockPos(mc.thePlayer).down();
                BlockPos pos = new BlockPos(x, y, z);
                boolean wasSprintng = mc.thePlayer.isSprinting();

                if (wasSprintng) {
                    mc.thePlayer.setSprinting(true);
                }
                if (event.isPre()) {
                    if (!mc.gameSettings.keyBindJump.pressed) {
                        this.timer.reset();
                    }
                } else if (getMaterial(belowPlayer).isReplaceable()) {
                    this.blockData = getBlockData(belowPlayer);
                    this.slot = mc.thePlayer.inventory.currentItem;
                    boolean b = false;
                    for (int i = 0; i < 9; ++i) {
                        if (this.mc.thePlayer.inventory.getStackInSlot(i) != null && this.mc.thePlayer.inventory.getStackInSlot(i).stackSize != 0 && this.mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemBlock && !this.invalid.contains(((ItemBlock)this.mc.thePlayer.inventory.getStackInSlot(i).getItem()).getBlock())) {
                            this.mc.thePlayer.connection.sendPacket(new CPacketHeldItemChange(this.mc.thePlayer.inventory.currentItem = i));
                            b = true;
                            break;
                        }
                    }
                    if (!b) {
                        for (int j = 0; j < 45; ++j) {
                            if (this.mc.thePlayer.inventory.getStackInSlot(j) != null && this.mc.thePlayer.inventory.getStackInSlot(j).stackSize != 0 && this.mc.thePlayer.inventory.getStackInSlot(j).getItem() instanceof ItemBlock && !this.invalid.contains(((ItemBlock)this.mc.thePlayer.inventory.getStackInSlot(j).getItem()).getBlock())) {
                                this.mc.playerController.windowClick(this.mc.thePlayer.inventoryContainer.windowId, j, 8, ClickType.SWAP, this.mc.thePlayer);
                                break;
                            }
                        }
                    }

                    if (this.blockData != null) {
                        if (!(!(Scaffold.mc.thePlayer.inventory.getStackInSlot(tempSlot).getItem() instanceof ItemBlock) || blacklistedBlocks.contains(((ItemBlock) Scaffold.mc.thePlayer.inventory.getStackInSlot(tempSlot).getItem()).getBlock()) || Scaffold.mc.thePlayer.isPotionActive(Potion.getPotionById(0)) && !this.timer.delay(100.0f) || this.placeBlockHypixel(pos) || this.placeBlockHypixel(pos.add(1, 0, 0)) || this.placeBlockHypixel(pos.add(0, 0, 1)) || this.placeBlockHypixel(pos.add(-1, 0, 0)))) {
                            this.placeBlockHypixel(pos.add(0, 0, -1));
                        }
                    }
                    if (placeBlockHypixel(pos) || this.placeBlockHypixel(pos.add(1, 0, 0)) || this.placeBlockHypixel(pos.add(0, 0, 1)) || this.placeBlockHypixel(pos.add(-1, 0, 0)) || this.placeBlockHypixel(pos.add(0, 0, -1))) {
                        if (Complex.getSettingsManager().getSettingByName("NoSwing").getValBoolean()) {
                            for (int i = 0; i < 45; ++i) {
                                mc.thePlayer.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                                break;
                            }
                        } else {
                            mc.thePlayer.swingArm(EnumHand.MAIN_HAND);
                        }
                    }
                    mc.thePlayer.inventory.currentItem = slot;
                    mc.playerController.updateController();
                }
                if ((wasSprintng) && (canSprint()) && (mc.gameSettings.keyBindForward.pressed)) {
                    mc.thePlayer.setSprinting(true);
                }
            }
        }
    }

    private boolean invCheck() {
        for (int i = 36; i < 45; ++i) {
            ItemStack item;
            if (!Scaffold.mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() || !Scaffold.isValid(item = Scaffold.mc.thePlayer.inventoryContainer.getSlot(i).getStack()))
                continue;
            return false;
        }
        return true;
    }
    public static boolean isValid(ItemStack item) {
        if (Scaffold.isEmpty(item)) {
            return false;
        }
        if (item.getUnlocalizedName().equalsIgnoreCase("tile.cactus")) {
            return false;
        }
        if (!(item.getItem() instanceof ItemBlock)) {
            return false;
        }
        return !blacklistedBlocks.contains(((ItemBlock) item.getItem()).getBlock());
    }
    public static boolean isEmpty(ItemStack stack) {
        return stack == null;
    }
    public float[] getRotations(BlockPos block, EnumFacing face) {
        String Mode = Complex.getSettingsManager().getSettingByName("Scaffold Mode").getValString();
        double x = block.getX() + 0.5 - mc.thePlayer.posX + (double) face.getFrontOffsetX() / 2;
        double z = block.getZ() + 0.5 - mc.thePlayer.posZ + (double) face.getFrontOffsetZ() / 2;
        double y = (block.getY() + 0.5);
        if (Mode.equalsIgnoreCase("Legit")) {
            double dist = mc.thePlayer.getDistance(block.getX() + 0.5 + (double) face.getFrontOffsetX() / 2, block.getY(), block.getZ() + 0.5 + (double) face.getFrontOffsetZ() / 2);
            if (dist > 1.5) {
                y += 0.5;
                x += (double) face.getFrontOffsetX() / 8;
                z += (double) face.getFrontOffsetZ() / 8;
            }
        }
        double d1 = mc.thePlayer.posY + mc.thePlayer.getEyeHeight() - y;
        double d3 = MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float) (Math.atan2(z, x) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) (Math.atan2(d1, d3) * 180.0D / Math.PI);
        if (yaw < 0.0F) {
            yaw += 360f;
        }
        return new float[]{yaw, pitch};
    }
    private class BlockData {
        public BlockPos position;
        public EnumFacing face;

        public BlockData(BlockPos position, EnumFacing face) {
            this.position = position;
            this.face = face;
        }
    }
    private int getTotalBlocks() {
        int totalCount = 0;
        int i = 9;

        while (i < 45) {
            ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();

            if ((itemStack != null) && ((itemStack.getItem() instanceof ItemBlock)) && (itemStack.stackSize >= 1)) {
                totalCount += itemStack.stackSize;
            }
            i++;
        }
        return totalCount;
    }
    private int getBlockSlot() {
        int i = 36;

        while (i < 45) {
            ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();

            if ((itemStack != null) && ((itemStack.getItem() instanceof ItemBlock)) && (itemStack.stackSize >= 1) &&
                    (Block.getBlockFromItem(itemStack.getItem()).getDefaultState().isFullBlock())) {
                return i - 36;
            }
            i++;
        }
        return -1;
    }
    private int blockInHotbar() {
        for (int i = 36; i < 45; i++) {
            ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if ((stack != null) && ((stack.getItem() instanceof ItemBlock))) {
                return i;
            }
        }
        return 0;
    }
    public Material getMaterial(BlockPos pos) {
        return getBlock(pos).getMaterial();
    }
    private boolean canSprint() {
        return (!mc.thePlayer.isCollidedHorizontally) && (!mc.thePlayer.isSneaking()) && (
                mc.thePlayer.getFoodStats().getFoodLevel() > 6);
    }
    public Block getBlock(BlockPos pos) {
        return getState(pos).getBlock();
    }
    public IBlockState getState(BlockPos pos) {
        return mc.theWorld.getBlockState(pos);
    }
    private BlockData getBlockData(BlockPos pos) {
        if (!this.invalid.contains(mc.theWorld.getBlockState(pos.add(0, -1, 0)).getBlock())) {
            return new BlockData(pos.add(0, -1, 0), EnumFacing.UP);
        }
        if (!this.invalid.contains(mc.theWorld.getBlockState(pos.add(-1, 0, 0)).getBlock())) {
            return new BlockData(pos.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!this.invalid.contains(mc.theWorld.getBlockState(pos.add(1, 0, 0)).getBlock())) {
            return new BlockData(pos.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!this.invalid.contains(mc.theWorld.getBlockState(pos.add(0, 0, -1)).getBlock())) {
            return new BlockData(pos.add(0, 0, -1), EnumFacing.SOUTH);
        }
        if (!this.invalid.contains(mc.theWorld.getBlockState(pos.add(0, 0, 1)).getBlock())) {
            return new BlockData(pos.add(0, 0, 1), EnumFacing.NORTH);
        }
        BlockPos add = pos.add(-1, 0, 0);
        if (!this.invalid.contains(mc.theWorld.getBlockState(add.add(-1, 0, 0)).getBlock())) {
            return new BlockData(add.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!this.invalid.contains(mc.theWorld.getBlockState(add.add(1, 0, 0)).getBlock())) {
            return new BlockData(add.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!this.invalid.contains(mc.theWorld.getBlockState(add.add(0, 0, -1)).getBlock())) {
            return new BlockData(add.add(0, 0, -1), EnumFacing.SOUTH);
        }
        if (!this.invalid.contains(mc.theWorld.getBlockState(add.add(0, 0, 1)).getBlock())) {
            return new BlockData(add.add(0, 0, 1), EnumFacing.NORTH);
        }
        BlockPos add2 = pos.add(1, 0, 0);
        if (!this.invalid.contains(mc.theWorld.getBlockState(add2.add(-1, 0, 0)).getBlock())) {
            return new BlockData(add2.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!this.invalid.contains(mc.theWorld.getBlockState(add2.add(1, 0, 0)).getBlock())) {
            return new BlockData(add2.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!this.invalid.contains(mc.theWorld.getBlockState(add2.add(0, 0, -1)).getBlock())) {
            return new BlockData(add2.add(0, 0, -1), EnumFacing.SOUTH);
        }
        if (!this.invalid.contains(mc.theWorld.getBlockState(add2.add(0, 0, 1)).getBlock())) {
            return new BlockData(add2.add(0, 0, 1), EnumFacing.NORTH);
        }
        BlockPos add3 = pos.add(0, 0, -1);
        if (!this.invalid.contains(mc.theWorld.getBlockState(add3.add(-1, 0, 0)).getBlock())) {
            return new BlockData(add3.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!this.invalid.contains(mc.theWorld.getBlockState(add3.add(1, 0, 0)).getBlock())) {
            return new BlockData(add3.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!this.invalid.contains(mc.theWorld.getBlockState(add3.add(0, 0, -1)).getBlock())) {
            return new BlockData(add3.add(0, 0, -1), EnumFacing.SOUTH);
        }
        if (!this.invalid.contains(mc.theWorld.getBlockState(add3.add(0, 0, 1)).getBlock())) {
            return new BlockData(add3.add(0, 0, 1), EnumFacing.NORTH);
        }
        BlockPos add4 = pos.add(0, 0, 1);
        if (!this.invalid.contains(mc.theWorld.getBlockState(add4.add(-1, 0, 0)).getBlock())) {
            return new BlockData(add4.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!this.invalid.contains(mc.theWorld.getBlockState(add4.add(1, 0, 0)).getBlock())) {
            return new BlockData(add4.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!this.invalid.contains(mc.theWorld.getBlockState(add4.add(0, 0, -1)).getBlock())) {
            return new BlockData(add4.add(0, 0, -1), EnumFacing.SOUTH);
        }
        if (!this.invalid.contains(mc.theWorld.getBlockState(add4.add(0, 0, 1)).getBlock())) {
            return new BlockData(add4.add(0, 0, 1), EnumFacing.NORTH);
        }
        return null;
    }
    public boolean placeBlockHypixel(BlockPos pos) {
        boolean tower = Complex.getSettingsManager().getSettingByName("Tower").getValBoolean();
        Vec3d eyesPos = new Vec3d(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
        EnumFacing[] values;
        int length = (values = EnumFacing.values()).length;
        for (int i = 0; i < length; i++) {
            EnumFacing side = values[i];
            BlockPos neighbor = pos.offset(side);
            EnumFacing side2 = side.getOpposite();

            if (eyesPos.squareDistanceTo(new Vec3d(pos).addVector(0.5D, 0.5D, 0.5D)) < eyesPos.squareDistanceTo(new Vec3d(neighbor).addVector(0.5D, 0.5D, 0.5D))) {
                if (canBeClicked(neighbor)) {
                    Vec3d hitVec = new Vec3d(neighbor).addVector(0.5D, 0.5D, 0.5D).add(new Vec3d(side2.getDirectionVec()).scale(0.5D));
                    if (eyesPos.squareDistanceTo(hitVec) <= 18.0624D) {
                        RotationUtils.getBlockRotations(hitVec);
                        mc.playerController.processRightClickBlock(mc.thePlayer, mc.theWorld, neighbor, side2, hitVec, EnumHand.MAIN_HAND);

                        if (tower) {
                            if (mc.thePlayer.movementInput.jump) {
                                mc.timer.timerSpeed = 0.8765432F;
                                mc.thePlayer.motionY = 0.4199382043D;
                                mc.thePlayer.motionX = 0.0D;
                                mc.thePlayer.motionZ = 0.0D;
                                if (timer3.hasReached(1500L)) {
                                    mc.thePlayer.motionY = -0.279929103D;
                                    this.towertimer.reset();
                                    if (timer3.hasReached(3L)) {
                                        mc.thePlayer.motionY = 0.4199382043D;
                                    }
                                }
                            } else if (!mc.thePlayer.movementInput.jump) {
                                mc.timer.timerSpeed = 1.0F;
                            }
                        }
                    }
                    mc.rightClickDelayTimer = 4;
                    return true;
                }
            }
        }
        return false;
    }
    private void grabBlocks() {
        for (int i = 9; i < 36; i++) {
            ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();

            if ((stack != null) && ((stack.getItem() instanceof ItemBlock)) && (stack.stackSize >= 1) && (Block.getBlockFromItem(stack.getItem()).getDefaultState().isFullBlock())) {
                PlayerControllerMP playerController = mc.playerController;

                int windowId = mc.thePlayer.openContainer.windowId;
                int slotId = i;
                int p_78753_3_ = 1;
                int p_78753_4_ = 2;

                playerController.windowClick(windowId, slotId, 1, ClickType.SWAP, mc.thePlayer);

                break;
            }
        }
    }
    private boolean grabBlock() {
        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if ((stack != null) && ((stack.getItem() instanceof ItemBlock))) {
                for (int x = 36; x < 45; x++) {
                    try {
                        Item localItem = mc.thePlayer.inventoryContainer.getSlot(x).getStack().getItem();
                    } catch (NullPointerException ex) {
                        swap(i, x - 36);
                        return true;
                    }
                }
                swap(i, 1);
                return true;
            }
        }
        return false;
    }
    private void swap(int slot, int hotbarNum) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, hotbarNum, ClickType.SWAP, mc.thePlayer);
    }
    public static double randomNumber(double max, double min) {
        return (Math.random() * (max - min)) + min;
    }
    public boolean canBeClicked(BlockPos pos) {
        return getBlock(pos).canCollideCheck(getState(pos), false);
    }
    private void ItemSpoof() {
        ItemStack is = new ItemStack(Item.getItemById(261));
        try {
            for (int i = 36; i < 45; i++) {
                int theSlot = i - 36;

                if (!mc.thePlayer.inventoryContainer.canAddItemToSlot(mc.thePlayer.inventoryContainer.getSlot(i), is, true) && mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem() instanceof ItemBlock && mc.thePlayer.inventoryContainer.getSlot(i).getStack() != null) {
                    if (isValid(mc.thePlayer.inventoryContainer.getSlot(i).getStack()) && mc.thePlayer.inventoryContainer.getSlot(i).getStack().stackSize != 0) {
                        if (mc.thePlayer.inventory.currentItem != theSlot) {
                            mc.thePlayer.inventory.currentItem = theSlot;
                            mc.playerController.updateController();
                        }
                        break;
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }
    private float[] a(final double n, final double n2, final double n3) {
        final double n4 = n - mc.thePlayer.posX;
        final double n5 = n2 - mc.thePlayer.posY;
        final double n6 = n3 - mc.thePlayer.posZ;
        final double n7 = MathHelper.sqrt_double(n4 * n4 + n6 * n6);
        return new float[]{(float) (Math.atan2(n6, n4) * 180.0 / 3.141592653589793) - 90.0f, (float) (-(Math.atan2(n5, n7) * 180.0 / 3.141592653589793)), (float) (-(Math.atan2(n5, n7) * 180.0 / 3.141592653589793))};
    }
}
