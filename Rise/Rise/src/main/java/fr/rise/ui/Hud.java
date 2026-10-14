package fr.rise.ui;

import fr.rise.Module;
import fr.rise.Modules;
import fr.rise.mods.HudInfos;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.RenderTickCounter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public final class Hud {
    private static final int[] COLORS = {0xFFFF8CC6, 0xFFC9A8FF, 0xFF8FE3C0, 0xFFFFC38A, 0xFF9AD0FF};

    public static void render(DrawContext c, RenderTickCounter tc) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.options.hudHidden || mc.player == null) return;
        var tr = mc.textRenderer;

        c.drawTextWithShadow(tr, "\u2665 Rise", 5, 5, 0xFFFF8CC6);

        // liste des modules actifs
        List<Module> on = new ArrayList<>();
        for (Module m : Modules.all()) if (m.isEnabled()) on.add(m);
        on.sort(Comparator.comparingInt((Module m) -> -tr.getWidth(m.name)));
        int y = 4, i = 0, sw = c.getScaledWindowWidth();
        for (Module m : on) {
            int w = tr.getWidth(m.name), x = sw - w - 8, col = COLORS[i % COLORS.length];
            c.fill(x - 3, y - 1, sw, y + 10, 0x66000000);
            c.fill(sw - 2, y - 1, sw, y + 10, col);
            c.drawTextWithShadow(tr, m.name, x, y + 1, col);
            y += 11; i++;
        }

        // infos
        HudInfos h = Modules.get(HudInfos.class);
        if (h != null && h.isEnabled()) {
            List<String> lines = new ArrayList<>();
            if (h.coords.on()) {
                int x = mc.player.getBlockX(), yy = mc.player.getBlockY(), z = mc.player.getBlockZ();
                lines.add("XYZ " + x + " " + yy + " " + z);
                boolean nether = mc.world.getRegistryKey().getValue().getPath().equals("the_nether");
                lines.add((nether ? "Overworld " + x * 8 + " " + z * 8 : "Nether " + x / 8 + " " + z / 8));
            }
            if (h.fps.on()) lines.add("FPS " + mc.getCurrentFps());
            if (h.ping.on() && mc.getNetworkHandler() != null) {
                PlayerListEntry e = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
                if (e != null) lines.add("Ping " + e.getLatency() + " ms");
            }
            if (h.speed.on()) lines.add(String.format(Locale.US, "%.1f blocs/s", h.bps));
            int ly = c.getScaledWindowHeight() - 12 * lines.size() - 4;
            for (String s : lines) { c.drawTextWithShadow(tr, s, 5, ly, 0xFFFFD6EC); ly += 12; }
        }
    }
}
