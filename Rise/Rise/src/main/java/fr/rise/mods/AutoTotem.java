package fr.rise.mods;

import fr.rise.*;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

public class AutoTotem extends Module {
    private final Setting.Num delay = add(new Setting.Num("Delai (ticks)", 2, 0, 10, true));
    private int timer;

    public AutoTotem() { super("Auto Totem", "Remet un totem dans la main secondaire des qu'il manque.", Category.COMBAT); }

    @Override public void onTick() {
        if (timer > 0) { timer--; return; }
        if (mc.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING)) return;
        for (int i = 0; i < 36; i++) {
            if (mc.player.getInventory().getStack(i).isOf(Items.TOTEM_OF_UNDYING)) {
                int slotId = i < 9 ? 36 + i : i;
                mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId, slotId, 40, SlotActionType.SWAP, mc.player);
                timer = delay.i();
                return;
            }
        }
    }
}
