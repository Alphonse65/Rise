package fr.rise.mods;

import fr.rise.*;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.*;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class Esp extends Module {
    private final Setting.Num radius = add(new Setting.Num("Rayon (chunks)", 12, 1, 32, true));
    private final Setting.Bool chests = add(new Setting.Bool("Coffres", true));
    private final Setting.Bool enders = add(new Setting.Bool("Ender chests", true));
    private final Setting.Bool shulkers = add(new Setting.Bool("Shulker boxes", true));
    private final Setting.Bool spawners = add(new Setting.Bool("Spawners", true));
    private final Setting.Bool anchors = add(new Setting.Bool("Respawn anchors", true));

    private record Hit(BlockPos pos, float r, float g, float b) {}
    private final List<Hit> hits = new ArrayList<>();
    private int tick;

    public Esp() { super("ESP Stockage", "Contours a travers les murs : coffres, shulkers, spawners, anchors.", Category.VISUEL); }

    @Override public void onDisable() { hits.clear(); }

    @Override
    public void onTick() {
        if (tick++ % 20 != 0) return;
        List<Hit> found = new ArrayList<>();
        int pcx = mc.player.getChunkPos().x, pcz = mc.player.getChunkPos().z, r = radius.i();

        for (int dx = -r; dx <= r; dx++) for (int dz = -r; dz <= r; dz++) {
            WorldChunk chunk = mc.world.getChunkManager().getWorldChunk(pcx + dx, pcz + dz);
            if (chunk == null) continue;

            for (BlockEntity be : chunk.getBlockEntities().values()) {
                BlockPos p = be.getPos();
                if (be instanceof EnderChestBlockEntity) { if (enders.on()) found.add(new Hit(p, 0.7f, 0.2f, 1f)); }
                else if (be instanceof ChestBlockEntity) { if (chests.on()) found.add(new Hit(p, 1f, 0.65f, 0f)); }
                else if (be instanceof ShulkerBoxBlockEntity) { if (shulkers.on()) found.add(new Hit(p, 0f, 0.9f, 0.9f)); }
                else if (be instanceof MobSpawnerBlockEntity) { if (spawners.on()) found.add(new Hit(p, 1f, 0.1f, 0.1f)); }
            }

            if (!anchors.on()) continue;
            ChunkSection[] secs = chunk.getSectionArray();
            for (int i = 0; i < secs.length; i++) {
                ChunkSection s = secs[i];
                if (s == null || s.isEmpty() || !s.hasAny(st -> st.isOf(Blocks.RESPAWN_ANCHOR))) continue;
                int bx = chunk.getPos().getStartX(), bz = chunk.getPos().getStartZ();
                int by = ChunkSectionPos.getBlockCoord(chunk.sectionIndexToCoord(i));
                for (int x = 0; x < 16; x++) for (int y = 0; y < 16; y++) for (int z = 0; z < 16; z++)
                    if (s.getBlockState(x, y, z).isOf(Blocks.RESPAWN_ANCHOR))
                        found.add(new Hit(new BlockPos(bx + x, by + y, bz + z), 0.9f, 0f, 0.7f));
            }
        }
        hits.clear(); hits.addAll(found);
    }

    @Override
    public void render3d(BufferBuilder b, Matrix4f m, Vec3d cam, float partial) {
        for (Hit h : hits) Gfx.box(b, m, h.pos(), cam, h.r(), h.g(), h.b());
    }
}
