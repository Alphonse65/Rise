package fr.rise.mods;

import fr.rise.*;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class NoFall extends Module {
    private final Setting.Num dist = add(new Setting.Num("Distance de chute", 2.5, 1, 10, false));

    public NoFall() { super("No Fall", "Annule les degats de chute.", Category.MOUVEMENT); }

    @Override public void onTick() {
        if (mc.player.fallDistance > dist.d() && !mc.player.isGliding())
            mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true, mc.player.horizontalCollision));
    }
}
