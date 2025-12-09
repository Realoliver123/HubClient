package com.realoliver123.hubclient.handler;

import com.realoliver123.hubclient.SkyblockQOL;
import com.realoliver123.hubclient.features.ModuleManager;
import com.realoliver123.hubclient.features.render.Watermark;
import com.realoliver123.hubclient.util.DrawUtil;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import java.awt.Color;

public class RenderHandler {
    private final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Text event) {
        Watermark watermarkMod = (Watermark) ModuleManager.modules.stream()
                .filter(m -> m instanceof Watermark).findFirst().orElse(null);

        if (watermarkMod != null && watermarkMod.isToggled()) {

            // Get Active Color (Chroma vs Theme)
            int color = SkyblockQOL.chromaText ? getChromaColor(0) : SkyblockQOL.themeColor;

            // 1. Build Watermark
            String text = Watermark.customText.getText();
            String display = "";

            if (!text.isEmpty()) {
                if (Watermark.sideGlitch.isEnabled()) {
                    // \u00A7k = Magic/Glitch
                    // \u00A7r = Reset
                    display = "\u00A7k|||\u00A7r " + text + " \u00A7k|||\u00A7r ";
                } else {
                    display = text + " ";
                }
            }

            // 2. Add Coordinates
            if (Watermark.showCoords.isEnabled() && mc.thePlayer != null) {
                int x = (int) mc.thePlayer.posX;
                int y = (int) mc.thePlayer.posY;
                int z = (int) mc.thePlayer.posZ;
                display += "[" + x + ", " + y + ", " + z + "]";
            }

            // 3. Draw
            DrawUtil.drawString(display, 5, 5, color);
        }
    }

    private int getChromaColor(long offset) {
        return Color.HSBtoRGB((System.currentTimeMillis() + offset) % 2000L / 2000.0f, 0.8f, 0.8f);
    }
}