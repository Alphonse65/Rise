package fr.rise.mods;

import fr.rise.*;

public class HudInfos extends Module {
    public final Setting.Bool coords = add(new Setting.Bool("Coordonnees", true));
    public final Setting.Bool fps = add(new Setting.Bool("FPS", true));
    public final Setting.Bool ping = add(new Setting.Bool("Ping", true));
    public final Setting.Bool speed = add(new Setting.Bool("Vitesse (blocs/s)", true));
    public double bps;
    private double px, pz;

    public HudInfos() { super("HUD Infos", "Coordonnees, FPS, ping et vitesse a l'ecran.", Category.VISUEL); }

    @Override public void onTick() {
        bps = Math.hypot(mc.player.getX() - px, mc.player.getZ() - pz) * 20;
        px = mc.player.getX(); pz = mc.player.getZ();
    }
}
