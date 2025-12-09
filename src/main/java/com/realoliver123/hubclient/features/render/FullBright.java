package com.realoliver123.hubclient.features.render;

import com.realoliver123.hubclient.features.Module;
import com.realoliver123.hubclient.features.ModuleCategory;
import net.minecraft.client.Minecraft;

public class FullBright extends Module {

    // --- FIX: DEFINE 'mc' HERE ---
    private final Minecraft mc = Minecraft.getMinecraft();
    private float oldGamma;

    public FullBright() {
        super("FullBright", ModuleCategory.RENDER);
    }

    @Override
    public void onEnable() {
        // Save the old brightness level
        if (mc.gameSettings != null) {
            oldGamma = mc.gameSettings.gammaSetting;
            // Set brightness to max (Night Vision effect)
            mc.gameSettings.gammaSetting = 1000f;
        }
    }

    @Override
    public void onDisable() {
        // Restore the original brightness level
        if (mc.gameSettings != null) {
            mc.gameSettings.gammaSetting = oldGamma;
        }
    }
}