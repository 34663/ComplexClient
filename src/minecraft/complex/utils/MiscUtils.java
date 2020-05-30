package complex.utils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialTransparent;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Random;

public final class MiscUtils implements MCUtil {

    public static int a(final int n, final int n2) {
        return new Random().nextInt(n2 - n + 1) + n;
    }

    public static double a(final double n, final double n2) {
        double n3 = new Random().nextDouble() * (n2 - n);
        if (n3 > n2) {
            n3 = n2;
        }
        double n4 = n3 + n;
        if (n4 > n2) {
            n4 = n2;
        }
        return n4;
    }

    public static double a(final double n, final double n2, final double n3) {
        return n2 + (n - n2) * n3;
    }

    public static boolean func2(Entity en) {
        IBlockState iBlockState = null;
        ArrayList<BlockPos> poses = func1(en);
        for (BlockPos pos : poses) {
            Block block = mc.theWorld.getBlockState(pos).getBlock();
            if ((!(block.getMaterial() instanceof MaterialTransparent)) && (block.getMaterial() != Material.AIR) && (!(block instanceof BlockLiquid)) && (block.isFullCube())) {
                return true;
            }
        }
        return false;
    }

    public static ArrayList<BlockPos> func1(Entity en) {
        BlockPos pos1 = new BlockPos(en.boundingBox.minX, en.boundingBox.minY - 0.01D, en.boundingBox.minZ);
        BlockPos pos2 = new BlockPos(en.boundingBox.maxX, en.boundingBox.minY - 0.01D, en.boundingBox.maxZ);
        Iterable<BlockPos.MutableBlockPos> collisionBlocks = BlockPos.getAllInBoxMutable(pos1, pos2);
        ArrayList<BlockPos> returnList = new ArrayList();
        for (BlockPos pos3 : collisionBlocks) {
            returnList.add(pos3);
        }
        return returnList;
    }

    public static void showErrorPopup(final String title, final String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public static void showURL(final String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        }catch(final IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static File openFileChooser() {
        if (mc.isFullScreen())
            mc.toggleFullscreen();

        final JFileChooser fileChooser = new JFileChooser();
        final JFrame frame = new JFrame();

        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        frame.setVisible(true);
        frame.toFront();
        frame.setVisible(false);

        final int action = fileChooser.showOpenDialog(frame);
        frame.dispose();

        return action == JFileChooser.APPROVE_OPTION ? fileChooser.getSelectedFile() : null;
    }

    public static File saveFileChooser() {
        if (mc.isFullScreen())
            mc.toggleFullscreen();

        final JFileChooser fileChooser = new JFileChooser();
        final JFrame frame = new JFrame();

        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        frame.setVisible(true);
        frame.toFront();
        frame.setVisible(false);

        final int action = fileChooser.showSaveDialog(frame);
        frame.dispose();

        return action == JFileChooser.APPROVE_OPTION ? fileChooser.getSelectedFile() : null;
    }
}