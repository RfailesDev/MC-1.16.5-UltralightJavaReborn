package com.example.examplemod.Rendering.surface;

import net.janrupf.ujr.api.surface.UltralightSurface;
import net.janrupf.ujr.api.surface.UltralightSurfaceFactory;

/**
 * This replaces the default bitmap surface factory so that Ultralight renders into GL textures.
 */
public class MinecraftSurfaceFactory implements UltralightSurfaceFactory {
    @Override
    public UltralightSurface createSurface(long width, long height) {
        return new MinecraftSurface(width, height);
    }

    @Override
    public void destroySurface(UltralightSurface surface) {
        ((MinecraftSurface) surface).destroy();
    }
}
