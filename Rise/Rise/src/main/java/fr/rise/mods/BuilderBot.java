package fr.rise.mods;

import fr.rise.*;

import java.util.ArrayDeque;
import java.util.Queue;

public class BuilderBot extends Module {
    private final Setting.Choice mode = add(new Setting.Choice("Mode", 1, "Schematic", "Murs", "Remplir", "Coque", "Vider"));
    private final Setting.Text file = add(new Setting.Text("Fichier schematic", "maison.schematic"));
    private final Setting.Text block = add(new Setting.Text("Bloc", "cobblestone"));
    private final Setting.Num x1 = add(new Setting.Num("X1", 0, -30000, 30000, true));
    private final Setting.Num y1 = add(new Setting.Num("Y1", 64, -64, 320, true));
    private final Setting.Num z1 = add(new Setting.Num("Z1", 0, -30000, 30000, true));
    private final Setting.Num x2 = add(new Setting.Num("X2", 10, -30000, 30000, true));
    private final Setting.Num y2 = add(new Setting.Num("Y2", 70, -64, 320, true));
    private final Setting.Num z2 = add(new Setting.Num("Z2", 10, -30000, 30000, true));

    private final Queue<String> queue = new ArrayDeque<>();
    private int timer;

    public BuilderBot() { super("Builder Bot", "Construit via Baritone (schematic ou zone). Arret : Panic/#stop.", Category.BOTS); }

    @Override public void onEnable() {
        if (!Bots.baritoneLoaded()) { info("Baritone n'est pas installe."); setEnabled(false); return; }
        queue.clear();
        String b = block.s().trim().toLowerCase();
        if (mode.idx() == 0) {
            queue.add("build " + file.s().trim() + " " + x1.i() + " " + y1.i() + " " + z1.i());
        } else {
            queue.add("sel clear");
            queue.add("sel pos1 " + x1.i() + " " + y1.i() + " " + z1.i());
            queue.add("sel pos2 " + x2.i() + " " + y2.i() + " " + z2.i());
            switch (mode.idx()) {
                case 1 -> queue.add("sel walls " + b);
                case 2 -> queue.add("sel set " + b);
                case 3 -> queue.add("sel shell " + b);
                default -> queue.add("sel cleararea");
            }
        }
        timer = 0;
    }

    @Override public void onTick() {
        if (queue.isEmpty()) { setEnabled(false); return; }
        if (timer-- > 0) return;
        Bots.baritone(queue.poll());
        timer = 15;
    }
}
