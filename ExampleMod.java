package com.example.examplemod;

import com.example.examplemod.Rendering.RenderingThread;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ExampleMod.MOD_ID)
public class ExampleMod
{
    public static final String MOD_ID = "examplemod";

    public ExampleMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);

        //new Thread(RenderingThread::startRenderingLoop).start();
        RenderingThread.startRenderingLoop();

        MinecraftForge.EVENT_BUS.addListener(this::onClientSetup);
    }

    private void onClientSetup(FMLClientSetupEvent event) {

    }

    public static boolean isPlayerInGame() {
        Minecraft mc = Minecraft.getInstance();
        return mc.player != null && mc.level != null;
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        //MinecraftForge.EVENT_BUS.register(new RenderingEngine());
        //MinecraftForge.EVENT_BUS.register(new RenderingEngine2());
    }

}
