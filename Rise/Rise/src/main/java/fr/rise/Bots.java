package fr.rise;

import fr.rise.mods.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

import static net.minecraft.client.MinecraftClient.getInstance;

public final class Bots {
    public static boolean baritoneLoaded() {
        FabricLoader l = FabricLoader.getInstance();
        return l.isModLoaded("baritone") || l.isModLoaded("baritone-meteor");
    }

    /** Commande Baritone (#...). Envoyee seulement si Baritone est installe (sinon elle fuiterait dans le chat public). */
    public static boolean baritone(String cmd) {
        var mc = getInstance();
        if (mc.player == null) return false;
        if (!baritoneLoaded()) {
            mc.player.sendMessage(Text.literal("\u00a7d[Rise] \u00a7cBaritone n'est pas installe."), false);
            return false;
        }
        mc.player.networkHandler.sendChatMessage("#" + cmd);
        return true;
    }

    public static void command(String cmd) {
        var mc = getInstance();
        if (mc.getNetworkHandler() == null) return;
        mc.getNetworkHandler().sendChatCommand(cmd.startsWith("/") ? cmd.substring(1) : cmd);
    }

    public static int freeSlots() {
        var p = getInstance().player;
        int n = 0;
        for (int i = 0; i < 36; i++) if (p.getInventory().getStack(i).isEmpty()) n++;
        return n;
    }

    public static boolean hasPickaxe() {
        var p = getInstance().player;
        for (int i = 0; i < 36; i++) if (p.getInventory().getStack(i).isIn(ItemTags.PICKAXES)) return true;
        return false;
    }

    public static boolean hasTotemInInventory() {
        var p = getInstance().player;
        for (int i = 0; i < 36; i++) if (p.getInventory().getStack(i).isOf(Items.TOTEM_OF_UNDYING)) return true;
        return false;
    }

    public static boolean hasTotem() {
        return getInstance().player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING) || hasTotemInInventory();
    }

    /** Amene l'item de l'inventaire (0-35) dans la main principale. */
    public static void toMainHand(int invIndex) {
        var mc = getInstance();
        if (invIndex < 9) { mc.player.getInventory().selectedSlot = invIndex; return; }
        mc.interactionManager.clickSlot(0, invIndex, mc.player.getInventory().selectedSlot, SlotActionType.SWAP, mc.player);
    }

    public static void stopAll() {
        if (baritoneLoaded()) baritone("stop");
        for (Module m : java.util.List.of(Modules.get(FarmBot.class), Modules.get(DiamondMiner.class),
                Modules.get(BuilderBot.class), Modules.get(MarketBot.class), Modules.get(AutoSell.class),
                Modules.get(Nuker.class), Modules.get(AutoWalk.class), Modules.get(AutoFish.class), Modules.get(Flight.class)))
            if (m != null) m.setEnabled(false);
    }
}
