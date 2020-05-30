package complex.module.impl.visual;

import complex.Complex;
import complex.event.EventTarget;
import complex.event.impl.EventRender2D;
import complex.event.impl.EventRender3D;
import complex.module.Category;
import complex.module.Module;
import complex.module.data.Setting;
import complex.utils.render.Colors;
import complex.utils.render.RenderingUtils;
import net.minecraft.block.BlockChest;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import javax.vecmath.Vector4d;
import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static net.minecraft.client.renderer.GlStateManager.glNormal3f;

public class ChestESP extends Module {
    private String mode;
    public List<TileEntity> collectedEntities = new ArrayList<>();
    private final IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
    private final FloatBuffer modelview = GLAllocation.createDirectFloatBuffer(16);
    private final FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);
    private final FloatBuffer vector = GLAllocation.createDirectFloatBuffer(4);

    public ChestESP() {
        super("ChestESP", null, 0, 0, Category.Visual);
    }

    @Override
    public void setup() {
        ArrayList<String> options = new ArrayList<>();
        options.add("Box");
        options.add("2D");
        Complex.getSettingsManager().rSetting(new Setting("ChestESP Mode", this, "Box", options));
    }

    @EventTarget
    public void onRender3D(EventRender3D em) {
        mode = Complex.getSettingsManager().getSettingByName("ChestESP Mode").getValString();
        Iterator var3 = mc.theWorld.loadedTileEntityList.iterator();

        if (mode.equalsIgnoreCase("Box")) {
            this.setDisplayName("ChestESP §7Box");

            while (var3.hasNext()) {
                Object o = var3.next();
                if (o instanceof TileEntityChest) {
                    TileEntityLockable storage = (TileEntityLockable) o;
                    int Basic = Colors.getColor(new Color(97, 131, 255, 50));
                    int Trap = Colors.getColor(new Color(255, 187, 97, 50));
                    this.drawESPOnStorage(storage, storage.getPos().getX(), storage.getPos().getY(), storage.getPos().getZ(), Basic, Trap);
                }
            }
        }
    }
    @EventTarget
    public void onRender2D(EventRender2D event) {
        if (mode.equalsIgnoreCase("2D")) {
            this.setDisplayName("ChestESP §72D");
        }
    }

    public void drawESPOnStorage(TileEntityLockable storage, double x, double y, double z, int basic, int trap) {
        if (!storage.isLocked()) {
            TileEntityChest chest = (TileEntityChest) storage;
            Vec3d vec;
            Vec3d vec2;
            if (chest.adjacentChestZNeg != null) {
                vec = new Vec3d(x + 0.0625D, y, z - 0.9375D);
                vec2 = new Vec3d(x + 0.9375D, y + 0.875D, z + 0.9375D);
            } else if (chest.adjacentChestXNeg != null) {
                vec = new Vec3d(x + 0.9375D, y, z + 0.0625D);
                vec2 = new Vec3d(x - 0.9375D, y + 0.875D, z + 0.9375D);
            } else {
                if (chest.adjacentChestXPos != null || chest.adjacentChestZPos != null) {
                    return;
                }
                vec = new Vec3d(x + 0.0625D, y, z + 0.0625D);
                vec2 = new Vec3d(x + 0.9375D, y + 0.875D, z + 0.9375D);
            }

            RenderingUtils.pre3D();
            mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 2);
            if (((TileEntityChest) storage).getChestType() == BlockChest.Type.BASIC) {
                RenderingUtils.glColor(basic);
            } else if (((TileEntityChest) storage).getChestType() == BlockChest.Type.TRAP) {
                RenderingUtils.glColor(trap);
            }
            RenderingUtils.drawFilledBox(new AxisAlignedBB(vec.xCoord - RenderManager.renderPosX, vec.yCoord - RenderManager.renderPosY, vec.zCoord - RenderManager.renderPosZ, vec2.xCoord - RenderManager.renderPosX, vec2.yCoord - RenderManager.renderPosY, vec2.zCoord - RenderManager.renderPosZ));
            GL11.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);
            RenderingUtils.post3D();
        }
    }
}
