package com.example.examplemod.Rendering;

import com.example.examplemod.Rendering.bridge.FilesystemBridge;
import com.example.examplemod.Rendering.bridge.GlfwClipboardBridge;
import com.example.examplemod.Rendering.bridge.LoggerBridge;
import com.example.examplemod.Rendering.surface.MinecraftSurface;
import com.example.examplemod.Rendering.surface.MinecraftSurfaceFactory;
import net.janrupf.ujr.api.*;
import net.janrupf.ujr.core.UltralightJavaReborn;
import net.janrupf.ujr.core.platform.PlatformEnvironment;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class UltralightLoad implements AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger();

    private MinecraftSurface minecraftSurface;
    private UltralightView view;
    private UltralightRenderer renderer;

    private UltralightJavaReborn ujr;

    public UltralightLoad() {
        // Инициализируем Ultralight в потоке рендеринга
        PlatformEnvironment environment = PlatformEnvironment.load();
        ujr = new UltralightJavaReborn(environment);
        ujr.activate();
        LOGGER.info("Ultralight Java Reborn has been activated!");

        // Настройка Ultralight
        UltralightPlatform platform = UltralightPlatform.instance();
        platform.usePlatformFontLoader();
        platform.setFilesystem(new FilesystemBridge());
        platform.setClipboard(new GlfwClipboardBridge());
        platform.setLogger(new LoggerBridge());
        platform.setSurfaceFactory(new MinecraftSurfaceFactory());

        platform.setConfig(
                new UltralightConfigBuilder()
                        .cachePath(System.getProperty("java.io.tmpdir") + File.separator + "ujr-example-glfw2")
                        // Set a custom prefix to distinguish from other file systems
                        .resourcePathPrefix(FilesystemBridge.RESOURCE_PREFIX)
                        .build());

        renderer = UltralightRenderer.getOrCreate();

        int width = Minecraft.getInstance().getWindow().getWidth();
        int height = Minecraft.getInstance().getWindow().getHeight();
        System.out.println("width = "+width+" | height = "+height);
        // Create a new view for the window
        view = renderer.createView((int) width, (int) height,
            new UltralightViewConfigBuilder()
                .transparent(true)
                .enableImages(true)
                .build());

        view.loadURL("https://google.com");
    }
    public void terminate() {
        LOGGER.debug("Cleaning up Ultralight Java Reborn...");
        ujr.cleanup();
    }

    public UltralightView getView() {
        return view;
    }

    public void update() {
        UltralightRenderer.getOrCreate().update();
    }

    public void render() {
        System.out.println("UltralightRenderer = "+UltralightRenderer.getOrCreate());
        UltralightRenderer.getOrCreate().render();
    }

    @Override
    public void close() {
        terminate();
    }
}
