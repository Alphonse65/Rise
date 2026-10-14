package fr.rise.mods;

import fr.rise.*;

public class Speed extends Module {
    private final Setting.Num speed = add(new Setting.Num("Vitesse", 0.4, 0.1, 1.5, false));
    private final Setting.Bool bhop = add(new Setting.Bool("Bunny hop", true));

    public Speed() { super("Speed", "Vitesse de deplacement augmentee.", Category.MOUVEMENT); }

    @Override public void onTick() {
        double f = mc.player.input.movementForward, s = mc.player.input.movementSideways;
        if (f == 0 && s == 0) return;
        if (bhop.on() && mc.player.isOnGround()) mc.player.jump();
        double yaw = Math.toRadians(mc.player.getYaw());
        double vx = -Math.sin(yaw) * f + Math.cos(yaw) * s;
        double vz = Math.cos(yaw) * f + Math.sin(yaw) * s;
        double len = Math.sqrt(vx * vx + vz * vz);
        if (len == 0) return;
        mc.player.setVelocity(vx / len * speed.d(), mc.player.getVelocity().y, vz / len * speed.d());
    }
}
