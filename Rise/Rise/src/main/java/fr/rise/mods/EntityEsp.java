package fr.rise.mods;

import fr.rise.*;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class EntityEsp extends Module {
    private final Setting.Bool players = add(new Setting.Bool("Joueurs", true));
    private final Setting.Bool hostiles = add(new Setting.Bool("Monstres", true));
    private final Setting.Bool animals = add(new Setting.Bool("Animaux", false));
    private final Setting.Bool items = add(new Setting.Bool("Objets au sol", true));
    private final Setting.Bool crystals = add(new Setting.Bool("Cristaux", true));
    private final Setting.Bool tracers = add(new Setting.Bool("Tracers (lignes)", true));
    private final Setting.Num range = add(new Setting.Num("Portee", 128, 16, 256, true));

    public EntityEsp() { super("Entity ESP", "Boites et lignes sur les entites, a travers les murs.", Category.VISUEL); }

    @Override
    public void render3d(BufferBuilder b, Matrix4f m, Vec3d cam, float partial) {
        Vec3d look = mc.player.getRotationVec(partial);
        for (Entity e : mc.world.getEntities()) {
            if (e == mc.player || mc.player.distanceTo(e) > range.i()) continue;
            float r, g, bl;
            if (e instanceof PlayerEntity && players.on()) { r = 1f; g = 0.5f; bl = 0.8f; }
            else if (e instanceof Monster && hostiles.on()) { r = 1f; g = 0.2f; bl = 0.2f; }
            else if (e instanceof AnimalEntity && animals.on()) { r = 0.4f; g = 1f; bl = 0.5f; }
            else if (e instanceof ItemEntity && items.on()) { r = 1f; g = 0.9f; bl = 0.2f; }
            else if (e instanceof EndCrystalEntity && crystals.on()) { r = 0.9f; g = 0.2f; bl = 1f; }
            else continue;

            Vec3d lp = e.getLerpedPos(partial);
            Box box = e.getBoundingBox().offset(lp.x - e.getX(), lp.y - e.getY(), lp.z - e.getZ());
            Gfx.aabb(b, m, box, cam, r, g, bl);
            if (tracers.on()) {
                Vec3d c = box.getCenter();
                Gfx.line(b, m, (float) look.x, (float) look.y, (float) look.z,
                        (float) (c.x - cam.x), (float) (c.y - cam.y), (float) (c.z - cam.z), r, g, bl);
            }
        }
    }
}
