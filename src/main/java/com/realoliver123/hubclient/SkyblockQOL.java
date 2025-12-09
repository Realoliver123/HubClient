package com.realoliver123.hubclient;

import com.realoliver123.hubclient.config.ConfigHandler;
import com.realoliver123.hubclient.features.ModuleManager;
import com.realoliver123.hubclient.gui.GuiSkyblockQOL;
import com.realoliver123.hubclient.handler.RenderHandler;
import com.realoliver123.hubclient.util.font.FontManager; // NEW IMPORT
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

@Mod(modid = SkyblockQOL.MODID, version = SkyblockQOL.VERSION)
public class SkyblockQOL {
    public static final String MODID = "hubclient";
    public static final String VERSION = "1.0";

    // --- GLOBAL VARIABLES ---
    public static int guiX = 0;
    public static int guiY = 0;
    public static int selectedCategory = 0;

    // Theme & Customization
    public static int themeColor = 0xFF00E5FF;
    public static float themeHue = 0.5f;
    public static boolean chromaText = false;

    // FONT VARIABLES
    public static boolean customFont = false; // Master toggle
    public static String fontName = "Arial";  // Selected font name

    // Feature Specific
    public static String espImagePath = "";

    // --- COORDINATES & WATERMARK ---
    public static boolean showCoords = true;
    public static String watermarkText = "HubClient";
    public static boolean watermarkGlitch = false;

    public static KeyBinding openGuiKey;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        ModuleManager.init();

        // Initialize Fonts
        FontManager.init();

        ConfigHandler.loadConfig();

        openGuiKey = new KeyBinding("Open GUI", Keyboard.KEY_RSHIFT, "HubClient");
        ClientRegistry.registerKeyBinding(openGuiKey);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new RenderHandler());
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (openGuiKey.isPressed()) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiSkyblockQOL());
        }
        ModuleManager.onKeyInput();
    }
}