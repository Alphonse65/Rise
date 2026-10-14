package fr.rise.mods;

import fr.rise.*;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.screen.slot.SlotActionType;

public class ChestStealer extends Module {
    private final Setting.Num delay = add(new Setting.Num("Delai (ticks)", 1, 0, 10, true));
    private final Setting.Bool close = add(new Setting.Bool("Fermer quand vide", true));
    private int timer;

    public ChestStealer() { super("Chest Stealer", "Vide automatiquement coffres, barils et shulkers ouverts.", Category.MONDE); }

    @Override public void onTick() {
        ScreenHandler h = mc.player.currentScreenHandler;
        if (h == mc.player.playerScreenHandler) return;
        if (!(h instanceof GenericContainerScreenHandler) && !(h instanceof ShulkerBoxScreenHandler)) return;
        if (timer > 0) { timer--; return; }
        int size = h.slots.size() - 36;
        for (int i = 0; i < size; i++) {
            if (!h.slots.get(i).getStack().isEmpty()) {
                mc.interactionManager.clickSlot(h.syncId, i, 0, SlotActionType.QUICK_MOVE, mc.player);
                timer = delay.i();
                return;
            }
        }
        if (close.on()) mc.player.closeHandledScreen();
    }
}
