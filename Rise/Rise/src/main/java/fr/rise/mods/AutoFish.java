package fr.rise.mods;

import fr.rise.*;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

public class AutoFish extends Module {
    private final Setting.Num recastDelay = add(new Setting.Num("Delai recast (ticks)", 20, 5, 100, true));
    private int timer;

    public AutoFish() { super("Auto Fish", "Peche automatiquement (canne a peche en main).", Category.MONDE); }

    @Override public void onTick() {
        if (!mc.player.getMainHandStack().isOf(Items.FISHING_ROD)) return;
        if (timer > 0) { timer--; return; }
        FishingBobberEntity hook = mc.player.fishHook;
        if (hook == null) {
            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);   // lance
            timer = recastDelay.i();
        } else if (hook.age > 20 && hook.isTouchingWater() && hook.getVelocity().y < -0.075) {
            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);   // remonte
            timer = recastDelay.i();
        }
    }
}
