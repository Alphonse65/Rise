package fr.rise;

import fr.rise.mods.*;

import java.util.ArrayList;
import java.util.List;

public final class Modules {
    private static final List<Module> ALL = new ArrayList<>();

    public static void init() {
        // Combat
        ALL.add(new KillAura()); ALL.add(new CrystalAura()); ALL.add(new Surround()); ALL.add(new AutoTotem());
        ALL.add(new AutoArmor()); ALL.add(new AutoEat()); ALL.add(new PanicGuard());
        // Mouvement
        ALL.add(new Flight()); ALL.add(new Speed()); ALL.add(new Step()); ALL.add(new Spider());
        ALL.add(new NoFall()); ALL.add(new Sprint()); ALL.add(new AutoWalk());
        // Visuel
        ALL.add(new Esp()); ALL.add(new OreEsp()); ALL.add(new EntityEsp()); ALL.add(new FullBright()); ALL.add(new HudInfos());
        // Monde
        ALL.add(new Nuker()); ALL.add(new Scaffold()); ALL.add(new AutoTool()); ALL.add(new ChestStealer()); ALL.add(new AutoFish());
        // Bots
        ALL.add(new FarmBot()); ALL.add(new DiamondMiner()); ALL.add(new BuilderBot()); ALL.add(new MarketBot()); ALL.add(new AutoSell());
        // Utile
        ALL.add(new AntiAFK()); ALL.add(new PlayerAlert());
    }

    public static List<Module> all() { return ALL; }

    public static List<Module> inCategory(Category c) {
        List<Module> l = new ArrayList<>();
        for (Module m : ALL) if (m.category == c) l.add(m);
        return l;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Module> T get(Class<T> c) {
        for (Module m : ALL) if (c.isInstance(m)) return (T) m;
        return null;
    }
}
