package fr.rise;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.function.Predicate;

public final class Util {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static double distToBox(Vec3d p, Box b) {
        double x = Math.max(b.minX, Math.min(p.x, b.maxX));
        double y = Math.max(b.minY, Math.min(p.y, b.maxY));
        double z = Math.max(b.minZ, Math.min(p.z, b.maxZ));
        return p.distanceTo(new Vec3d(x, y, z));
    }

    /** Index (0-8) du premier item de la barre rapide qui correspond, sinon -1. */
    public static int findHotbar(Predicate<ItemStack> pred) {
        for (int i = 0; i < 9; i++) if (pred.test(mc.player.getInventory().getStack(i))) return i;
        return -1;
    }

    public static void select(int slot) { if (slot >= 0 && slot < 9) mc.player.getInventory().selectedSlot = slot; }

    public static float[] rotationsTo(Vec3d from, Vec3d to) {
        double dx = to.x - from.x, dy = to.y - from.y, dz = to.z - from.z;
        double h = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90f;
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, h));
        return new float[]{yaw, pitch};
    }

    /** Pose un bloc en s'appuyant sur un voisin solide. */
    public static boolean place(BlockPos target) {
        if (!mc.world.getBlockState(target).isReplaceable()) return false;
        for (Direction d : Direction.values()) {
            BlockPos n = target.offset(d);
            BlockState s = mc.world.getBlockState(n);
            if (s.isAir() || s.isReplaceable() || !s.isSolidBlock(mc.world, n)) continue;
            Direction face = d.getOpposite();
            Vec3d hit = Vec3d.ofCenter(n).add(face.getOffsetX() * 0.5, face.getOffsetY() * 0.5, face.getOffsetZ() * 0.5);
            mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(hit, face, n, false));
            mc.player.swingHand(Hand.MAIN_HAND);
            return true;
        }
        return false;
    }
}
