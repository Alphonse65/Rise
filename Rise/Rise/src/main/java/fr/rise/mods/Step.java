package fr.rise.mods;

import fr.rise.*;
import net.minecraft.entity.attribute.EntityAttributes;

public class Step extends Module {
    private final Setting.Num height = add(new Setting.Num("Hauteur", 2.0, 1, 4, false));

    public Step() { super("Step", "Monte les blocs sans sauter.", Category.MOUVEMENT); }

    @Override public void onTick() {
        var a = mc.player.getAttributeInstance(EntityAttributes.STEP_HEIGHT);
        if (a != null) a.setBaseValue(height.d());
    }

    @Override public void onDisable() {
        if (mc.player == null) return;
        var a = mc.player.getAttributeInstance(EntityAttributes.STEP_HEIGHT);
        if (a != null) a.setBaseValue(0.6);
    }
}
