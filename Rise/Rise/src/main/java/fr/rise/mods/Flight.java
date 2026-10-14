package fr.rise.mods;

import fr.rise.*;

public class Flight extends Module {
    private final Setting.Num speed = add(new Setting.Num("Vitesse", 1.0, 0.1, 5, false));
    private final Setting.Num vspeed = add(new Setting.Num("Vitesse verticale", 0.6, 0.1, 3, false));
    private final Setting.Bool antiKick = add(new Setting.Bool("Anti-kick (petite chute)", true));
    private int tick;

    public Flight() { super("Flight", "Vol libre (Espace = monter, Shift = descendre).", Category.MOUVEMENT); }

    @Override public void onTick() {
        double f = mc.player.input.movementForward, s = mc.player.input.movementSideways;
        double yaw = Math.toRadians(mc.player.getYaw());
        double vx = (-Math.sin(yaw) * f + Math.cos(yaw) * s) * speed.d();
        double vz = (Math.cos(yaw) * f + Math.sin(yaw) * s) * speed.d();
        double vy = 0;
        if (mc.options.jumpKey.isPressed()) vy = vspeed.d();
        else if (mc.options.sneakKey.isPressed()) vy = -vspeed.d();
        else if (antiKick.on() && tick++ % 40 == 0) vy = -0.06;
        mc.player.setVelocity(vx, vy, vz);
        mc.player.fallDistance = 0;
    }
}
