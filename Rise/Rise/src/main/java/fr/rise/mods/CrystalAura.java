package fr.rise.mods;

import fr.rise.*;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class CrystalAura extends Module {
    private final Setting.Num placeRange = add(new Setting.Num("Portee pose", 4.5, 1, 6, false));
    private final Setting.Num breakRange = add(new Setting.Num("Portee casse", 4.5, 1, 6, false));
    private final Setting.Num targetRange = add(new Setting.Num("Portee cible", 10, 3, 16, false));
    private final Setting.Num minDmg = add(new Setting.Num("Degats min cible", 5, 1, 20, false));
    private final Setting.Num maxSelf = add(new Setting.Num("Degats max sur moi", 5, 0, 20, false));
    private final Setting.Num delay = add(new Setting.Num("Delai (ticks)", 2, 0, 10, true));
    private final Setting.Bool place = add(new Setting.Bool("Poser", true));
    private final Setting.Bool breakC = add(new Setting.Bool("Casser", true));
    private final Setting.Bool autoSwitch = add(new Setting.Bool("Changer de slot", true));
    private final Setting.Bool rotate = add(new Setting.Bool("Rotation", true));
    private int timer;

    public CrystalAura() { super("Crystal Aura", "Pose et fait exploser des cristaux pres des joueurs ennemis.", Category.COMBAT); }

    /** Estimation des degats d'un cristal (sans armure ni obstacles : volontairement pessimiste). */
    private static double dmg(Vec3d crystal, Entity e) {
        double ratio = Math.sqrt(e.squaredDistanceTo(crystal)) / 12.0;
        if (ratio > 1) return 0;
        double impact = 1 - ratio;
        return (impact * impact + impact) / 2 * 84 + 1;
    }

    private boolean safe(double self) {
        return self <= maxSelf.d() && self < mc.player.getHealth() + mc.player.getAbsorptionAmount() - 1;
    }

    @Override public void onTick() {
        if (timer > 0) { timer--; return; }
        if (mc.currentScreen != null) return;

        PlayerEntity target = null; double best = Double.MAX_VALUE;
        for (PlayerEntity p : mc.world.getPlayers()) {
            if (p == mc.player || !p.isAlive() || p.isSpectator()) continue;
            double d = mc.player.distanceTo(p);
            if (d <= targetRange.d() && d < best) { best = d; target = p; }
        }
        if (target == null) return;
        Vec3d eye = mc.player.getEyePos();

        // 1) Casser
        if (breakC.on()) {
            EndCrystalEntity bestC = null; double bestD = -1;
            for (Entity e : mc.world.getEntities()) {
                if (!(e instanceof EndCrystalEntity c)) continue;
                if (Util.distToBox(eye, c.getBoundingBox()) > breakRange.d()) continue;
                Vec3d pos = c.getPos();
                double dt = dmg(pos, target), ds = dmg(pos, mc.player);
                if (dt < minDmg.d() * 0.5 || !safe(ds)) continue;
                if (dt > bestD) { bestD = dt; bestC = c; }
            }
            if (bestC != null) {
                if (rotate.on()) { float[] r = Util.rotationsTo(eye, bestC.getBoundingBox().getCenter()); mc.player.setYaw(r[0]); mc.player.setPitch(r[1]); }
                mc.interactionManager.attackEntity(mc.player, bestC);
                mc.player.swingHand(Hand.MAIN_HAND);
                timer = delay.i();
                return;
            }
        }

        // 2) Poser
        if (!place.on()) return;
        boolean offhand = mc.player.getOffHandStack().isOf(Items.END_CRYSTAL);
        int slot = offhand ? -1 : Util.findHotbar(s -> s.isOf(Items.END_CRYSTAL));
        if (!offhand && slot < 0) return;

        int r = (int) Math.ceil(placeRange.d());
        BlockPos o = mc.player.getBlockPos();
        BlockPos bestPos = null; double bestScore = -1e9;
        for (int dx = -r; dx <= r; dx++) for (int dy = -r; dy <= r; dy++) for (int dz = -r; dz <= r; dz++) {
            BlockPos base = o.add(dx, dy, dz);
            if (!mc.world.getBlockState(base).isOf(Blocks.OBSIDIAN) && !mc.world.getBlockState(base).isOf(Blocks.BEDROCK)) continue;
            if (!mc.world.getBlockState(base.up()).isAir() || !mc.world.getBlockState(base.up(2)).isAir()) continue;
            Vec3d cpos = new Vec3d(base.getX() + 0.5, base.getY() + 1, base.getZ() + 0.5);
            if (eye.distanceTo(cpos) > placeRange.d()) continue;
            Box bb = new Box(base.up());
            if (!mc.world.getOtherEntities(null, bb.stretch(0, 1, 0)).isEmpty()) continue;
            double dt = dmg(cpos, target), ds = dmg(cpos, mc.player);
            if (dt < minDmg.d() || !safe(ds)) continue;
            double score = dt - ds;
            if (score > bestScore) { bestScore = score; bestPos = base; }
        }
        if (bestPos == null) return;

        Hand hand = offhand ? Hand.OFF_HAND : Hand.MAIN_HAND;
        if (!offhand) {
            if (!autoSwitch.on() && mc.player.getInventory().selectedSlot != slot) return;
            Util.select(slot);
        }
        if (rotate.on()) { float[] rr = Util.rotationsTo(eye, Vec3d.ofCenter(bestPos)); mc.player.setYaw(rr[0]); mc.player.setPitch(rr[1]); }
        Vec3d hit = Vec3d.ofCenter(bestPos).add(0, 0.5, 0);
        mc.interactionManager.interactBlock(mc.player, hand, new BlockHitResult(hit, Direction.UP, bestPos, false));
        mc.player.swingHand(hand);
        timer = delay.i();
    }
}
