package fr.rise.mods;

import fr.rise.*;
import net.minecraft.item.BlockItem;
import net.minecraft.util.math.BlockPos;

public class Scaffold extends Module {
    private final Setting.Bool restore = add(new Setting.Bool("Remettre le slot", true));

    public Scaffold() { super("Scaffold", "Pose des blocs sous vos pieds pendant que vous marchez.", Category.MONDE); }

    @Override public void onTick() {
        if (mc.currentScreen != null) return;
        BlockPos below = mc.player.getBlockPos().down();
        if (!mc.world.getBlockState(below).isReplaceable()) return;
        int slot = Util.findHotbar(s -> s.getItem() instanceof BlockItem);
        if (slot < 0) return;
        int prev = mc.player.getInventory().selectedSlot;
        Util.select(slot);
        Util.place(below);
        if (restore.on()) Util.select(prev);
    }
}
