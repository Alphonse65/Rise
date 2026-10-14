package fr.rise.mods;

import fr.rise.*;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.slot.SlotActionType;

public class AutoArmor extends Module {
    private final Setting.Num delay = add(new Setting.Num("Delai (ticks)", 4, 1, 20, true));
    private static final EquipmentSlot[] SLOTS = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    private static final int[] SLOT_ID = {5, 6, 7, 8};
    private int timer;

    public AutoArmor() { super("Auto Armor", "Equipe automatiquement la meilleure armure.", Category.COMBAT); }

    private static int score(ItemStack s) {
        if (s.isEmpty()) return 0;
        String p = Registries.ITEM.getId(s.getItem()).getPath();
        if (p.contains("elytra")) return 0;
        if (p.startsWith("netherite")) return 6;
        if (p.startsWith("diamond")) return 5;
        if (p.startsWith("iron")) return 4;
        if (p.startsWith("chainmail")) return 3;
        if (p.startsWith("golden")) return 2;
        if (p.startsWith("leather") || p.startsWith("turtle")) return 1;
        return 1;
    }

    private void click(int slotId) {
        mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId, slotId, 0, SlotActionType.QUICK_MOVE, mc.player);
    }

    @Override public void onTick() {
        if (timer > 0) { timer--; return; }
        if (mc.currentScreen != null) return;
        for (int k = 0; k < 4; k++) {
            int cur = score(mc.player.getEquippedStack(SLOTS[k]));
            int bestIdx = -1, bestScore = cur;
            for (int i = 0; i < 36; i++) {
                ItemStack st = mc.player.getInventory().getStack(i);
                EquippableComponent eq = st.get(DataComponentTypes.EQUIPPABLE);
                if (eq == null || eq.slot() != SLOTS[k]) continue;
                int sc = score(st);
                if (sc > bestScore) { bestScore = sc; bestIdx = i; }
            }
            if (bestIdx < 0) continue;
            if (cur > 0) {
                if (Bots.freeSlots() == 0) continue;
                click(SLOT_ID[k]);                      // retire l'ancienne piece
            } else {
                click(bestIdx < 9 ? 36 + bestIdx : bestIdx);   // equipe la meilleure
            }
            timer = delay.i();
            return;
        }
    }
}
