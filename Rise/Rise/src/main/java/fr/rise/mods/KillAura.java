package fr.rise.mods;

import fr.rise.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

public class KillAura extends Module {
    private final Setting.Num range = add(new Setting.Num("Portee", 4.2, 1, 6, false));
    private final Setting.Bool players = add(new Setting.Bool("Joueurs", true));
    private final Setting.Bool hostiles = add(new Setting.Bool("Monstres", true));
    private final Setting.Bool animals = add(new Setting.Bool("Animaux", false));
    private final Setting.Bool rotate = add(new Setting.Bool("Rotation", true));
    private final Setting.Bool waitCd = add(new Setting.Bool("Attendre le cooldown", true));
    private final Setting.Num cps = add(new Setting.Num("CPS (sans cooldown)", 12, 1, 20, true));
    private int timer;

    public KillAura() { super("KillAura", "Attaque automatiquement l'entite la plus proche.", Category.COMBAT); }

    @Override public void onTick() {
        if (mc.currentScreen != null) return;
        timer++;
        LivingEntity target = null;
        double best = Double.MAX_VALUE;
        Vec3d eye = mc.player.getEyePos();
        for (Entity e : mc.world.getEntities()) {
            if (!(e instanceof LivingEntity le) || e == mc.player || !le.isAlive()) continue;
            boolean ok = (players.on() && e instanceof PlayerEntity p && !p.isSpectator())
                    || (hostiles.on() && e instanceof Monster) || (animals.on() && e instanceof AnimalEntity);
            if (!ok) continue;
            double d = Util.distToBox(eye, e.getBoundingBox());
            if (d <= range.d() && d < best) { best = d; target = le; }
        }
        if (target == null) return;

        if (rotate.on()) {
            float[] r = Util.rotationsTo(eye, target.getBoundingBox().getCenter());
            mc.player.setYaw(r[0]); mc.player.setPitch(r[1]);
        }
        if (waitCd.on()) { if (mc.player.getAttackCooldownProgress(0.5f) < 1f) return; }
        else { if (timer < 20.0 / cps.i()) return; }
        timer = 0;
        mc.interactionManager.attackEntity(mc.player, target);
        mc.player.swingHand(Hand.MAIN_HAND);
    }
}
