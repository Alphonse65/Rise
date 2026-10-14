package fr.rise.mods;

import fr.rise.*;

public class AutoSell extends Module {
    private final Setting.Text command = add(new Setting.Text("Commande de vente", "/sell all"));
    private final Setting.Choice trigger = add(new Setting.Choice("Declencheur", 0, "Inventaire plein", "Intervalle"));
    private final Setting.Num freeLimit = add(new Setting.Num("Slots libres max", 2, 0, 10, true));
    private final Setting.Num interval = add(new Setting.Num("Intervalle (s)", 60, 5, 600, true));
    private final Setting.Num cooldown = add(new Setting.Num("Cooldown (s)", 10, 2, 60, true));
    private int timer;

    public AutoSell() { super("Auto Sell", "Vend automatiquement via la commande du serveur.", Category.BOTS); }

    @Override public void onTick() {
        timer++;
        if (timer < cooldown.i() * 20) return;
        boolean go = trigger.idx() == 1 ? timer >= interval.i() * 20 : Bots.freeSlots() <= freeLimit.i();
        if (!go) return;
        Bots.command(command.s());
        timer = 0;
    }
}
