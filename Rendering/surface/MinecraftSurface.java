package com.example.examplemod.Rendering.surface;

import net.janrupf.ujr.api.math.IntRect;
import net.janrupf.ujr.api.surface.UltralightSurface;
import net.janrupf.ujr.api.util.UltralightBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL42;

import java.nio.ByteBuffer;

public class MinecraftSurface implements UltralightSurface {
    public static int textureId; // Убираем инициализацию
    public static ByteBuffer buffer;
    private long width;
    private long height;
    private IntRect dirtyBounds;

    public MinecraftSurface(long width, long height) {
        resize(width, height);
    }

    @Override
    public long width() {
        return width;
    }

    @Override
    public long height() {
        return height;
    }

    @Override
    public long rowBytes() {
        return width * 4;
    }

    @Override
    public long size() {
        return width * height * 4;
    }
    @Override
    public UltralightBuffer lockPixels() {
        return new UltralightBuffer() {
            @Override
            public void close() {
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
                GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, (int) width, (int) height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
            }

            @Override
            public ByteBuffer asByteBuffer() {
                return buffer;
            }
        };
    }

    @Override
    public void resize(long width, long height) {
        this.width = width;
        this.height = height;

        textureId = GL30.glGenTextures();

        // Configure the texture
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, textureId);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_S, GL30.GL_CLAMP_TO_EDGE);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_T, GL30.GL_CLAMP_TO_EDGE);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR);
        GL42.glTexStorage2D(GL30.GL_TEXTURE_2D, 1, GL30.GL_RGBA8, (int) width, (int) height);

        /*
        // Creating OpenGL texture here
        textureId = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, (int) width, (int) height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, 0);
         */

        // Update buffer
        buffer.clear();
        buffer.limit((int) (width * height * 4));
    }


    @Override
    public void setDirtyBounds(IntRect bounds) {
        this.dirtyBounds = bounds;
    }

    @Override
    public IntRect dirtyBounds() {
        if (dirtyBounds != null) {
            return dirtyBounds;
        } else {
            return new IntRect(0, 0, 0, 0);
        }
    }

    @Override
    public void clearDirtyBounds() {
        this.dirtyBounds = null;
    }

    public int getTextureId() {
        return textureId;
    }

    public void destroy() {
        GL11.glDeleteTextures(textureId);
    }
}