package fr.rise.mods;

import fr.rise.*;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import org.joml.Matrix4f;

import java.util.*;

/** "Xray" par contours : minerais visibles a travers la roche (analyse etalee sur plusieurs ticks). */
public class OreEsp extends Module {
    private record Group(Setting.Bool on, float r, float g, float b, Block[] blocks) {}
    private record Hit(BlockPos pos, Block block, float r, float g, float b) {}

    private final List<Group> groups = new ArrayList<>();
    private final Setting.Num radius = add(new Setting.Num("Rayon (chunks)", 6, 1, 16, true));

    private final Map<Long, List<Hit>> cache = new HashMap<>();
    private final ArrayDeque<int[]> queue = new ArrayDeque<>();
    private final Map<Block, Group> byBlock = new HashMap<>();
    private int tick;

    private void grp(String name, boolean def, float r, float g, float b, Block... blocks) {
        groups.add(new Group(add(new Setting.Bool(name, def)), r, g, b, blocks));
    }

    public OreEsp() {
        super("Xray (minerais)", "Minerais en contours a travers la roche.", Category.VISUEL);
        grp("Diamant", true, 0f, 1f, 1f, Blocks.DIAMOND_ORE, Blocks.DEEPSLATE_DIAMOND_ORE);
        grp("Debris antiques", true, 0.6f, 0.35f, 0.2f, Blocks.ANCIENT_DEBRIS);
        grp("Emeraude", true, 0.1f, 1f, 0.2f, Blocks.EMERALD_ORE, Blocks.DEEPSLATE_EMERALD_ORE);
        grp("Or", true, 1f, 0.85f, 0f, Blocks.GOLD_ORE, Blocks.DEEPSLATE_GOLD_ORE, Blocks.NETHER_GOLD_ORE);
        grp("Fer", false, 0.9f, 0.75f, 0.6f, Blocks.IRON_ORE, Blocks.DEEPSLATE_IRON_ORE);
        grp("Charbon", false, 0.4f, 0.4f, 0.4f, Blocks.COAL_ORE, Blocks.DEEPSLATE_COAL_ORE);
        grp("Redstone", false, 1f, 0.1f, 0.1f, Blocks.REDSTONE_ORE, Blocks.DEEPSLATE_REDSTONE_ORE);
        grp("Lapis", false, 0.2f, 0.3f, 1f, Blocks.LAPIS_ORE, Blocks.DEEPSLATE_LAPIS_ORE);
        grp("Cuivre", false, 1f, 0.5f, 0.2f, Blocks.COPPER_ORE, Blocks.DEEPSLATE_COPPER_ORE);
        grp("Quartz", false, 1f, 1f, 1f, Blocks.NETHER_QUARTZ_ORE);
    }

    @Override public void onEnable() { cache.clear(); queue.clear(); tick = 0; }
    @Override public void onDisable() { cache.clear(); queue.clear(); }

    private void rebuild() {
        byBlock.clear();
        for (Group g : groups) if (g.on().on()) for (Block b : g.blocks()) byBlock.put(b, g);
        queue.clear();
        int pcx = mc.player.getChunkPos().x, pcz = mc.player.getChunkPos().z, r = radius.i();
        List<int[]> l = new ArrayList<>();
        for (int dx = -r; dx <= r; dx++) for (int dz = -r; dz <= r; dz++) l.add(new int[]{pcx + dx, pcz + dz});
        l.sort(Comparator.comparingInt(a -> (a[0] - pcx) * (a[0] - pcx) + (a[1] - pcz) * (a[1] - pcz)));
        queue.addAll(l);
        cache.keySet().removeIf(k -> {
            int cx = ChunkPos.getPackedX(k), cz = ChunkPos.getPackedZ(k);
            return Math.abs(cx - pcx) > r + 1 || Math.abs(cz - pcz) > r + 1;
        });
    }

    @Override public void onTick() {
        if (tick++ % 100 == 0) rebuild();
        for (int n = 0; n < 3 && !queue.isEmpty(); n++) {
            int[] c = queue.poll();
            scan(c[0], c[1]);
        }
    }

    private void scan(int cx, int cz) {
        long key = ChunkPos.toLong(cx, cz);
        WorldChunk chunk = mc.world.getChunkManager().getWorldChunk(cx, cz);
        if (chunk == null) { cache.remove(key); return; }
        List<Hit> list = new ArrayList<>();
        ChunkSection[] secs = chunk.getSectionArray();
        for (int i = 0; i < secs.length; i++) {
            ChunkSection s = secs[i];
            if (s == null || s.isEmpty() || !s.hasAny(st -> byBlock.containsKey(st.getBlock()))) continue;
            int bx = chunk.getPos().getStartX(), bz = chunk.getPos().getStartZ();
            int by = ChunkSectionPos.getBlockCoord(chunk.sectionIndexToCoord(i));
            for (int x = 0; x < 16; x++) for (int y = 0; y < 16; y++) for (int z = 0; z < 16; z++) {
                Block b = s.getBlockState(x, y, z).getBlock();
                Group g = byBlock.get(b);
                if (g != null) list.add(new Hit(new BlockPos(bx + x, by + y, bz + z), b, g.r(), g.g(), g.b()));
            }
        }
        cache.put(key, list);
    }

    @Override
    public void render3d(BufferBuilder b, Matrix4f m, Vec3d cam, float partial) {
        for (List<Hit> l : cache.values())
            for (Hit h : l)
                if (mc.world.getBlockState(h.pos()).getBlock() == h.block()) Gfx.box(b, m, h.pos(), cam, h.r(), h.g(), h.b());
    }
}
