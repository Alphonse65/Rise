package fr.rise.mods;

import fr.rise.*;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class Nuker extends Module {
    private final Setting.Num radius = add(new Setting.Num("Rayon", 4, 1, 5, true));
    private final Setting.Choice mode = add(new Setting.Choice("Mode", 0, "Tout", "Minerais"));
    private final Setting.Bool noBelow = add(new Setting.Bool("Ne pas creuser sous les pieds", true));

    public Nuker() { super("Nuker", "Casse automatiquement les blocs autour de vous.", Category.MONDE); }

    @Override public void onTick() {
        if (mc.currentScreen != null) return;
        BlockPos o = mc.player.getBlockPos();
        int r = radius.i();
        BlockPos best = null; double bd = Double.MAX_VALUE;
        Vec3d eye = mc.player.getEyePos();
        for (int dx = -r; dx <= r; dx++) for (int dy = -r; dy <= r; dy++) for (int dz = -r; dz <= r; dz++) {
            BlockPos p = o.add(dx, dy, dz);
            BlockState st = mc.world.getBlockState(p);
            if (st.isAir() || !st.getFluidState().isEmpty() || st.getHardness(mc.world, p) < 0) continue;
            if (noBelow.on() && p.getY() < mc.player.getBlockY()) continue;
            if (mode.idx() == 1) {
                String id = Registries.BLOCK.getId(st.getBlock()).getPath();
                if (!id.endsWith("_ore") && !id.equals("ancient_debris")) continue;
            }
            double d = eye.squaredDistanceTo(Vec3d.ofCenter(p));
            if (d > 20.25 || d >= bd) continue;
            bd = d; best = p;
        }
        if (best == null) return;
        mc.interactionManager.updateBlockBreakingProgress(best, Direction.UP);
        mc.player.swingHand(Hand.MAIN_HAND);
    }
}
