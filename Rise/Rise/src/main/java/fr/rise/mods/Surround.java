package fr.rise.mods;

import fr.rise.*;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class Surround extends Module {
    private final Setting.Num perTick = add(new Setting.Num("Blocs par tick", 2, 1, 4, true));
    private final Setting.Bool center = add(new Setting.Bool("Centrer le joueur", true));
    private final Setting.Bool disable = add(new Setting.Bool("Se desactiver apres", false));

    public Surround() { super("Surround", "Entoure vos pieds d'obsidienne.", Category.COMBAT); }

    @Override public void onEnable() {
        if (center.on() && mc.player != null)
            mc.player.setPosition(Math.floor(mc.player.getX()) + 0.5, mc.player.getY(), Math.floor(mc.player.getZ()) + 0.5);
    }

    @Override public void onTick() {
        int slot = Util.findHotbar(s -> s.isOf(Items.OBSIDIAN) || s.isOf(Items.CRYING_OBSIDIAN));
        if (slot < 0) return;
        BlockPos feet = mc.player.getBlockPos();
        int prev = mc.player.getInventory().selectedSlot, placed = 0;
        boolean all = true;
        for (Direction d : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST}) {
            BlockPos p = feet.offset(d);
            if (!mc.world.getBlockState(p).isReplaceable()) continue;
            all = false;
            if (placed >= perTick.i()) break;
            Util.select(slot);
            if (Util.place(p)) placed++;
        }
        if (placed > 0) Util.select(prev);
        if (all && disable.on()) setEnabled(false);
    }
}
