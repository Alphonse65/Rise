package fr.rise.mods;

import fr.rise.*;

public class DiamondMiner extends Module {
    private final Setting.Num targetY = add(new Setting.Num("Hauteur Y", -58, -60, 20, true));
    private final Setting.Num minHealth = add(new Setting.Num("Vie min", 8, 1, 20, true));
    private final Setting.Bool stopFull = add(new Setting.Bool("Stop si inventaire plein", true));

    private int timer, phase = -1;

    public DiamondMiner() { super("Diamond Miner", "Descend puis mine les diamants (Baritone requis).", Category.BOTS); }

    @Override public void onEnable() {
        timer = 0; phase = -1;
        if (!Bots.baritoneLoaded()) { info("Baritone n'est pas installe."); setEnabled(false); }
    }
    @Override public void onDisable() { if (Bots.baritoneLoaded() && mc.player != null) Bots.baritone("stop"); }

    @Override public void onTick() {
        if (mc.player.getHealth() + mc.player.getAbsorptionAmount() <= minHealth.i()) { info("Vie trop basse : arret."); setEnabled(false); return; }
        if (!Bots.hasPickaxe()) { info("Pas de pioche."); setEnabled(false); return; }
        if (stopFull.on() && Bots.freeSlots() == 0) { info("Inventaire plein : arret."); setEnabled(false); return; }

        int wanted = mc.player.getY() > targetY.i() + 3 ? 0 : 1;
        if (wanted != phase || timer++ > 600) {   // renvoie la commande si changement de phase ou toutes les 30 s
            phase = wanted; timer = 0;
            if (wanted == 0) Bots.baritone("goto " + targetY.i());
            else Bots.baritone("mine diamond_ore deepslate_diamond_ore");
        }
    }
}
