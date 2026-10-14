package fr.rise.mods;

import fr.rise.*;
import net.minecraft.block.BlockState;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

public class AutoTool extends Module {
    public AutoTool() { super("Auto Tool", "Choisit l'outil le plus rapide quand vous cassez un bloc.", Category.MONDE); }

    @Override public void onTick() {
        if (!mc.options.attackKey.isPressed() || mc.currentScreen != null) return;
        if (!(mc.crosshairTarget instanceof BlockHitResult hit) || hit.getType() != HitResult.Type.BLOCK) return;
        BlockState st = mc.world.getBlockState(hit.getBlockPos());
        int best = mc.player.getInventory().selectedSlot;
        float bestSpeed = mc.player.getInventory().getStack(best).getMiningSpeedMultiplier(st);
        for (int i = 0; i < 9; i++) {
            float sp = mc.player.getInventory().getStack(i).getMiningSpeedMultiplier(st);
            if (sp > bestSpeed) { bestSpeed = sp; best = i; }
        }
        Util.select(best);
    }
}
