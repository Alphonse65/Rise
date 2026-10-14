package fr.rise.mods;

import fr.rise.*;

public class Sprint extends Module {
    public Sprint() { super("Sprint auto", "Court automatiquement quand vous avancez.", Category.MOUVEMENT); }

    @Override public void onTick() {
        if (mc.player.input.movementForward > 0 && !mc.player.horizontalCollision
                && !mc.player.isSneaking() && mc.player.getHungerManager().getFoodLevel() > 6)
            mc.player.setSprinting(true);
    }
}
