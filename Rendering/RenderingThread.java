package com.example.examplemod.Rendering;

import com.example.examplemod.Rendering.bridge.FilesystemBridge;
import com.example.examplemod.Rendering.bridge.GlfwClipboardBridge;
import com.example.examplemod.Rendering.bridge.LoggerBridge;
import com.example.examplemod.Rendering.surface.MinecraftSurface;
import com.mojang.blaze3d.platform.GlStateManager;
import net.janrupf.ujr.api.*;
import net.janrupf.ujr.core.UltralightJavaReborn;
import net.janrupf.ujr.core.platform.PlatformEnvironment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import java.io.File;

public class RenderingThread {

    private static final Logger LOGGER = LogManager.getLogger();

    private static boolean running = true;

    public RenderingThread() {
        startRenderingLoop();
    }

    public static void startRenderingLoop() {
        System.out.println("---startRendering---");

        try (
                UltralightLoad Ultralight_ = new UltralightLoad();
        ) {
            Ultralight_.getView().resize(Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight());

            // Inf loop
            while (running) {
                System.out.println("Render STEP");
                Ultralight_.update();
                Ultralight_.render();

                // Getting surface from view
                MinecraftSurface surface = (MinecraftSurface) Ultralight_.getView().surface();

                // Rendering texture
                renderTexture(surface);

                try {
                    Thread.sleep(16); // like 60 fps
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private static void renderTexture(MinecraftSurface surface) {
        GlStateManager._pushMatrix();
        GlStateManager._enableTexture();
        GlStateManager._bindTexture(surface.getTextureId());

        // Отрисовка прямоугольника с текстурой
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuilder();
        bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        bufferBuilder.vertex(0, 0, 0).uv(0, 0).endVertex();
        bufferBuilder.vertex(0, 100, 0).uv(0, 1).endVertex();
        bufferBuilder.vertex(100, 100, 0).uv(1, 1).endVertex();
        bufferBuilder.vertex(100, 0, 0).uv(1, 0).endVertex();
        tessellator.end();

        GlStateManager._disableTexture();
        GlStateManager._popMatrix();
    }

    // Метод для остановки рендеринга
    public static void stopRendering() {
        running = false;
    }
}
