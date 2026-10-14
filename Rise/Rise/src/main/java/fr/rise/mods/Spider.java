package fr.rise.mods;

import fr.rise.*;
import net.minecraft.util.math.Vec3d;

public class Spider extends Module {
    private final Setting.Num speed = add(new Setting.Num("Vitesse", 0.25, 0.1, 0.6, false));

    public Spider() { super("Spider", "Grimpe aux murs.", Category.MOUVEMENT); }

    @Override public void onTick() {
        if (!mc.player.horizontalCollision) return;
        Vec3d v = mc.player.getVelocity();
        mc.player.setVelocity(v.x, speed.d(), v.z);
    }
}
