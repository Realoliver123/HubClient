package com.realoliver123.hubclient.util;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class BasicFontRenderer {

    private final DynamicTexture texture;
    // Explicit types for Java 6 compatibility
    private final Map<Character, CharData> charMap = new HashMap<Character, CharData>();
    private final int textureID;
    private final Font font;

    private int fontHeight = 0;
    private float scaleFactor = 1.0f;

    private static class CharData {
        int width, height, u, v;
    }

    public BasicFontRenderer(String fontName, int size) {
        this.font = new Font(fontName, Font.PLAIN, size);
        this.texture = setupTexture();
        this.textureID = texture.getGlTextureId();

        if (this.fontHeight > 0) {
            this.scaleFactor = 9.0f / (float)this.fontHeight;
        }
    }

    private DynamicTexture setupTexture() {
        BufferedImage img = new BufferedImage(1024, 1024, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        g.setFont(this.font);
        g.setColor(Color.WHITE);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        FontMetrics metrics = g.getFontMetrics();
        this.fontHeight = metrics.getHeight();

        int x = 0;
        int y = 0;

        for (int i = 32; i < 256; i++) {
            char c = (char) i;
            int charWidth = metrics.charWidth(c);
            int charHeight = metrics.getHeight();

            if (x + charWidth >= 1024) {
                x = 0;
                y += charHeight;
            }

            g.drawString(String.valueOf(c), x, y + metrics.getAscent());

            CharData data = new CharData();
            data.width = charWidth;
            data.height = charHeight;
            data.u = x;
            data.v = y;
            charMap.put(c, data);

            x += charWidth + 5;
        }
        g.dispose();

        return new DynamicTexture(img);
    }

    public void drawString(String text, float x, float y, int color) {
        float alpha = (float) (color >> 24 & 255) / 255.0F;
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;
        if (alpha == 0) alpha = 1.0f;

        GlStateManager.pushMatrix();
        GlStateManager.scale(scaleFactor, scaleFactor, 1.0f);

        float scaledX = x / scaleFactor;
        float scaledY = y / scaleFactor;

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(red, green, blue, alpha);
        GlStateManager.enableTexture2D();
        GlStateManager.bindTexture(this.textureID);

        for (char c : text.toCharArray()) {
            CharData data = charMap.get(c);
            if (data != null) {
                drawChar(data, scaledX, scaledY + 2.0f);
                scaledX += data.width;
            }
        }

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public int getStringWidth(String text) {
        int width = 0;
        for (char c : text.toCharArray()) {
            CharData data = charMap.get(c);
            if (data != null) width += data.width;
        }
        return (int) (width * scaleFactor);
    }

    private void drawChar(CharData data, float x, float y) {
        float textureSize = 1024f;
        float u = data.u / textureSize;
        float v = data.v / textureSize;
        float uWidth = data.width / textureSize;
        float vHeight = data.height / textureSize;

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(u, v);
        GL11.glVertex2f(x, y);

        GL11.glTexCoord2f(u, v + vHeight);
        GL11.glVertex2f(x, y + data.height);

        GL11.glTexCoord2f(u + uWidth, v + vHeight);
        GL11.glVertex2f(x + data.width, y + data.height);

        GL11.glTexCoord2f(u + uWidth, v);
        GL11.glVertex2f(x + data.width, y);
        GL11.glEnd();
    }
}