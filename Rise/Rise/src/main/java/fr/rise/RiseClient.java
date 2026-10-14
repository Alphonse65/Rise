package fr.rise;

import com.mojang.blaze3d.systems.RenderSystem;
import fr.rise.ui.ClickGui;
import fr.rise.ui.Hud;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.*;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

public class RiseClient implements ClientModInitializer {
    private static KeyBinding guiKey;

    @Override
    public void onInitializeClient() {
        Modules.init();
        guiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.rise.gui", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, "key.categories.misc"));

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            while (guiKey.wasPressed()) {
                if (mc.currentScreen == null) mc.setScreen(new ClickGui());
            }
            if (mc.player == null || mc.world == null) return;
            for (Module m : Modules.all()) if (m.isEnabled()) m.onTick();
        });

        WorldRenderEvents.LAST.register(this::render3d);
        HudRenderCallback.EVENT.register(Hud::render);
    }

    private void render3d(WorldRenderContext ctx) {
        boolean any = false;
        for (Module m : Modules.all()) if (m.isEnabled()) { any = true; break; }
        if (!any || ctx.matrixStack() == null) return;

        Vec3d cam = ctx.camera().getPos();
        float partial = MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(true);
        Matrix4f mat = ctx.matrixStack().peek().getPositionMatrix();

        RenderSystem.disableDepthTest();   // visible a travers les murs
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.lineWidth(2.5f);
        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);

        BufferBuilder buf = Tessellator.getInstance().begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        for (Module m : Modules.all()) if (m.isEnabled()) m.render3d(buf, mat, cam, partial);
        BuiltBuffer built = buf.endNullable();
        if (built != null) BufferRenderer.drawWithGlobalProgram(built);

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }
}
