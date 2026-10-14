package fr.rise.mods;

import fr.rise.*;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class FullBright extends Module {
    private int tick;
    public FullBright() { super("Fullbright", "Vision nocturne visuelle (cote client).", Category.VISUEL); }

    @Override public void onTick() {
        if (tick++ % 100 == 0)
            mc.player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 400, 0, false, false));
    }
    @Override public void onDisable() {
        if (mc.player != null) mc.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
    }
}
