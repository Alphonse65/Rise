package fr.rise;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public final class Gfx {
    public static void box(BufferBuilder b, Matrix4f m, BlockPos p, Vec3d cam, float r, float g, float bl) {
        aabb(b, m, new Box(p), cam, r, g, bl);
    }

    public static void aabb(BufferBuilder b, Matrix4f m, Box box, Vec3d cam, float r, float g, float bl) {
        float x1 = (float) (box.minX - cam.x), y1 = (float) (box.minY - cam.y), z1 = (float) (box.minZ - cam.z);
        float x2 = (float) (box.maxX - cam.x), y2 = (float) (box.maxY - cam.y), z2 = (float) (box.maxZ - cam.z);
        line(b, m, x1, y1, z1, x2, y1, z1, r, g, bl); line(b, m, x2, y1, z1, x2, y1, z2, r, g, bl);
        line(b, m, x2, y1, z2, x1, y1, z2, r, g, bl); line(b, m, x1, y1, z2, x1, y1, z1, r, g, bl);
        line(b, m, x1, y2, z1, x2, y2, z1, r, g, bl); line(b, m, x2, y2, z1, x2, y2, z2, r, g, bl);
        line(b, m, x2, y2, z2, x1, y2, z2, r, g, bl); line(b, m, x1, y2, z2, x1, y2, z1, r, g, bl);
        line(b, m, x1, y1, z1, x1, y2, z1, r, g, bl); line(b, m, x2, y1, z1, x2, y2, z1, r, g, bl);
        line(b, m, x2, y1, z2, x2, y2, z2, r, g, bl); line(b, m, x1, y1, z2, x1, y2, z2, r, g, bl);
    }

    /** Coordonnees relatives a la camera. */
    public static void line(BufferBuilder b, Matrix4f m, float x1, float y1, float z1, float x2, float y2, float z2,
                            float r, float g, float bl) {
        b.vertex(m, x1, y1, z1).color(r, g, bl, 1f);
        b.vertex(m, x2, y2, z2).color(r, g, bl, 1f);
    }
}
