package fr.rise.mods;

import fr.rise.*;
import net.minecraft.block.*;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class FarmBot extends Module {
    private final Setting.Choice mode = add(new Setting.Choice("Mode", 0, "Portee", "Baritone"));
    private final Setting.Num reach = add(new Setting.Num("Portee", 4, 1, 5, true));
    private final Setting.Num delay = add(new Setting.Num("Delai (ticks)", 2, 0, 10, true));
    private final Setting.Bool replant = add(new Setting.Bool("Replanter", true));
    private final Setting.Num bRange = add(new Setting.Num("Rayon Baritone", 30, 5, 100, true));

    private static final List<Item> SEEDS = List.of(Items.WHEAT_SEEDS, Items.CARROT, Items.POTATO, Items.BEETROOT_SEEDS, Items.NETHER_WART);
    private int timer;

    public FarmBot() { super("Farm Bot", "Recolte et replante les cultures (Portee ou Baritone #farm).", Category.BOTS); }

    @Override public void onEnable() { if (mode.idx() == 1) Bots.baritone("farm " + bRange.i()); }
    @Override public void onDisable() { if (mode.idx() == 1) Bots.baritone("stop"); }

    @Override public void onTick() {
        if (mode.idx() != 0 || mc.currentScreen != null) return;
        if (timer > 0) { timer--; return; }
        BlockPos o = mc.player.getBlockPos();
        int r = reach.i();

        for (int dx = -r; dx <= r; dx++) for (int dy = -2; dy <= 2; dy++) for (int dz = -r; dz <= r; dz++) {
            BlockPos pos = o.add(dx, dy, dz);
            if (!inReach(pos)) continue;
            BlockState st = mc.world.getBlockState(pos);
            boolean mature = (st.getBlock() instanceof CropBlock c && c.isMature(st))
                    || (st.getBlock() instanceof NetherWartBlock && st.get(NetherWartBlock.AGE) >= 3);
            if (mature) {
                mc.interactionManager.attackBlock(pos, Direction.UP);
                mc.player.swingHand(Hand.MAIN_HAND);
                timer = delay.i();
                return;
            }
        }
        if (!replant.on()) return;
        for (int dx = -r; dx <= r; dx++) for (int dy = -2; dy <= 1; dy++) for (int dz = -r; dz <= r; dz++) {
            BlockPos soil = o.add(dx, dy, dz);
            BlockState ss = mc.world.getBlockState(soil);
            boolean farmland = ss.getBlock() instanceof FarmlandBlock, sand = ss.isOf(Blocks.SOUL_SAND);
            if ((!farmland && !sand) || !mc.world.getBlockState(soil.up()).isAir() || !inReach(soil)) continue;
            int slot = -1;
            for (int i = 0; i < 9; i++) {
                var s = mc.player.getInventory().getStack(i);
                if (SEEDS.contains(s.getItem()) && (sand == s.isOf(Items.NETHER_WART))) { slot = i; break; }
            }
            if (slot < 0) return;
            mc.player.getInventory().selectedSlot = slot;
            Vec3d hit = Vec3d.ofCenter(soil).add(0, 0.5, 0);
            mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(hit, Direction.UP, soil, false));
            mc.player.swingHand(Hand.MAIN_HAND);
            timer = delay.i();
            return;
        }
    }

    private boolean inReach(BlockPos p) { return mc.player.getEyePos().squaredDistanceTo(Vec3d.ofCenter(p)) <= 20.25; }
}
