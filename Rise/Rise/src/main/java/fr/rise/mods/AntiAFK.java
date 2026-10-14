package fr.rise.mods;

import fr.rise.*;
import net.minecraft.util.Hand;

import java.util.Random;

public class AntiAFK extends Module {
    private final Setting.Num interval = add(new Setting.Num("Intervalle (s)", 15, 3, 120, true));
    private final Setting.Bool jump = add(new Setting.Bool("Sauter", true));
    private final Setting.Bool rotate = add(new Setting.Bool("Tourner la tete", true));
    private final Setting.Bool swing = add(new Setting.Bool("Taper dans le vide", true));
    private final Random rnd = new Random();
    private int timer;

    public AntiAFK() { super("Anti AFK", "Evite l'expulsion pour inactivite (files d'attente).", Category.UTILE); }

    @Override public void onTick() {
        if (++timer < interval.i() * 20) return;
        timer = 0;
        if (jump.on() && mc.player.isOnGround()) mc.player.jump();
        if (rotate.on()) mc.player.setYaw(mc.player.getYaw() + rnd.nextInt(41) - 20);
        if (swing.on()) mc.player.swingHand(Hand.MAIN_HAND);
    }
}
