package com.realoliver123.hubclient.util;

import com.realoliver123.hubclient.SkyblockQOL;
import com.realoliver123.hubclient.util.font.FontManager;
import net.minecraft.client.Minecraft;

public class DrawUtil {

    public static void drawString(String text, int x, int y, int color) {
        if (SkyblockQOL.customFont && FontManager.activeFont != null) {
            // FontManager now handles scaling internally!
            FontManager.activeFont.drawString(text, x, y, color);
        } else {
            Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(text, x, y, color);
        }
    }

    public static void drawCenteredString(String text, int x, int y, int color) {
        int width;
        if (SkyblockQOL.customFont && FontManager.activeFont != null) {
            // Get SCALED width (which CustomFontRenderer now returns correctly)
            width = FontManager.activeFont.getStringWidth(text);
            FontManager.activeFont.drawString(text, x - width / 2f, y, color);
        } else {
            width = Minecraft.getMinecraft().fontRendererObj.getStringWidth(text);
            Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(text, x - width / 2, y, color);
        }
    }

    public static String getFontName() {
        return SkyblockQOL.fontName;
    }
}