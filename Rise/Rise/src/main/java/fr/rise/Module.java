package fr.rise;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public abstract class Module {
    protected static final MinecraftClient mc = MinecraftClient.getInstance();

    public final String name, description;
    public final Category category;
    public final List<Setting<?>> settings = new ArrayList<>();
    private boolean enabled;

    protected Module(String name, String description, Category category) {
        this.name = name; this.description = description; this.category = category;
    }

    protected <S extends Setting<?>> S add(S s) { settings.add(s); return s; }

    public boolean isEnabled() { return enabled; }
    public void toggle() { setEnabled(!enabled); }
    public void setEnabled(boolean e) {
        if (e == enabled) return;
        enabled = e;
        if (e) onEnable(); else onDisable();
    }

    protected void info(String msg) {
        if (mc.player != null) mc.player.sendMessage(Text.literal("\u00a7d[Rise] \u00a7f" + msg), false);
    }

    public void onEnable() {}
    public void onDisable() {}
    public void onTick() {}
    /** Ajoute des lignes 3D (coordonnees relatives a la camera) au buffer partage. */
    public void render3d(BufferBuilder b, Matrix4f m, Vec3d cam, float partial) {}
}
