package fr.rise.mods;

import fr.rise.*;
import net.minecraft.text.Text;

public class PanicGuard extends Module {
    private final Setting.Num health = add(new Setting.Num("Seuil de vie", 8, 1, 20, true));
    private final Setting.Bool noTotemOnly = add(new Setting.Bool("Seulement sans totem", true));
    private final Setting.Bool disconnect = add(new Setting.Bool("Se deconnecter", false));

    public PanicGuard() { super("Panic Guard", "Coupe les bots (et deconnecte) si la vie est trop basse.", Category.COMBAT); }

    @Override public void onTick() {
        if (mc.player.getHealth() + mc.player.getAbsorptionAmount() > health.i()) return;
        if (noTotemOnly.on() && Bots.hasTotem()) return;
        Bots.stopAll();
        info("Vie basse : bots arretes.");
        if (disconnect.on() && mc.getNetworkHandler() != null)
            mc.getNetworkHandler().getConnection().disconnect(Text.literal("[PanicGuard] Vie basse."));
        setEnabled(false);
    }
}
