package fr.rise.mods;

import fr.rise.*;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;

public class AutoEat extends Module {
    private final Setting.Num hunger = add(new Setting.Num("Manger sous (faim)", 14, 1, 19, true));
    private final Setting.Bool gap = add(new Setting.Bool("Pomme doree si vie basse", true));
    private final Setting.Num gapHealth = add(new Setting.Num("Vie pour pomme doree", 10, 2, 19, true));

    private boolean eating, eatingGap, usedOnce;
    private int prevSlot = -1, ticks;

    public AutoEat() { super("Auto Eat", "Mange automatiquement (et pomme doree si vie basse).", Category.COMBAT); }

    private static boolean isGap(ItemStack s) { return s.isOf(Items.GOLDEN_APPLE) || s.isOf(Items.ENCHANTED_GOLDEN_APPLE); }

    private static boolean goodFood(ItemStack s) {
        if (s.get(DataComponentTypes.FOOD) == null || isGap(s)) return false;
        String p = Registries.ITEM.getId(s.getItem()).getPath();
        return !(p.equals("rotten_flesh") || p.equals("spider_eye") || p.equals("poisonous_potato")
                || p.equals("pufferfish") || p.equals("chorus_fruit") || p.equals("suspicious_stew"));
    }

    private void stop() {
        mc.options.useKey.setPressed(false);
        if (prevSlot >= 0) Util.select(prevSlot);
        eating = false; eatingGap = false; usedOnce = false; prevSlot = -1; ticks = 0;
    }

    @Override public void onDisable() { if (eating && mc.player != null) stop(); }

    @Override public void onTick() {
        if (mc.currentScreen != null) { if (eating) stop(); return; }
        int food = mc.player.getHungerManager().getFoodLevel();

        if (eating) {
            ticks++;
            if (mc.player.isUsingItem()) usedOnce = true;
            if (ticks > 120 || (usedOnce && !mc.player.isUsingItem() && (eatingGap || food >= 20))) { stop(); return; }
            mc.options.useKey.setPressed(true);
            return;
        }

        boolean needGap = gap.on() && mc.player.getHealth() <= gapHealth.i();
        int gapSlot = needGap ? Util.findHotbar(AutoEat::isGap) : -1;
        int foodSlot = food <= hunger.i() ? Util.findHotbar(AutoEat::goodFood) : -1;
        int slot = gapSlot >= 0 ? gapSlot : foodSlot;
        if (slot < 0) return;

        prevSlot = mc.player.getInventory().selectedSlot;
        eatingGap = gapSlot >= 0;
        Util.select(slot);
        mc.options.useKey.setPressed(true);
        eating = true;
    }
}
