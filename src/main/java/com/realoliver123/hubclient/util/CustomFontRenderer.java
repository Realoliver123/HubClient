package com.realoliver123.hubclient.util.font;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CustomFontRenderer {
    private final Font font;
    private final DynamicTexture tex;
    private final Map<Character, Glyph> glyphs = new HashMap<>();
    private int texWidth = 1024, texHeight = 1024;
    private float scaleFactor = 0.5f;
    private float spacing = 2.0f; // Gap between letters
    private final Random random = new Random();

    // Standard Minecraft Color Codes
    private final int[] colorCodes = new int[] {
            0x000000, 0x0000AA, 0x00AA00, 0x00AAAA, 0xAA0000, 0xAA00AA, 0xFFAA00, 0xAAAAAA,
            0x555555, 0x5555FF, 0x55FF55, 0x55FFFF, 0xFF5555, 0xFF55FF, 0xFFFF55, 0xFFFFFF
    };

    public CustomFontRenderer(Font font) {
        this.font = font;
        this.tex = setupTexture(font);
    }

    private DynamicTexture setupTexture(Font font) {
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();

        img = new BufferedImage(texWidth, texHeight, BufferedImage.TYPE_INT_ARGB);
        g2d = img.createGraphics();
        g2d.setFont(font);
        g2d.setColor(Color.WHITE);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

        int x = 0;
        int y = fm.getAscent();

        // Margin prevents "texture bleeding" (the dots)
        int margin = 3;

        for (int i = 32; i < 256; i++) {
            char c = (char) i;
            int w = fm.charWidth(c);
            int h = fm.getHeight();

            if (x + w + margin >= texWidth) {
                x = 0;
                y += h + margin;
            }

            g2d.drawString(String.valueOf(c), x, y);
            glyphs.put(c, new Glyph(x, y - fm.getAscent(), w, h));
            x += w + margin;
        }

        return new DynamicTexture(img);
    }

    public void drawString(String text, float x, float y, int color) {
        if (text == null) return;

        GlStateManager.bindTexture(tex.getGlTextureId());
        GlStateManager.enableBlend();
        GlStateManager.disableLighting();

        // Extract Initial Color (Theme/Chroma)
        float alpha = (color >> 24 & 0xFF) / 255.0F;
        float origRed = (color >> 16 & 0xFF) / 255.0F;
        float origGreen = (color >> 8 & 0xFF) / 255.0F;
        float origBlue = (color & 0xFF) / 255.0F;
        if (alpha == 0) alpha = 1f;

        GlStateManager.color(origRed, origGreen, origBlue, alpha);

        GL11.glPushMatrix();
        GL11.glTranslated(x, y, 0);
        GL11.glScalef(scaleFactor, scaleFactor, 1.0f);
        GL11.glBegin(GL11.GL_QUADS);

        // Set GL Color to Initial so text starts colored
        GL11.glColor4f(origRed, origGreen, origBlue, alpha);

        float currentX = 0;
        boolean obfuscated = false;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            // Handle Codes (§)
            if (c == '\u00A7' && i + 1 < text.length()) {
                int colorIndex = "0123456789abcdef".indexOf(text.toLowerCase().charAt(i + 1));
                char formatCode = text.toLowerCase().charAt(i + 1);

                if (colorIndex != -1) {
                    // Set specific color (e.g. §c red)
                    int cCode = colorCodes[colorIndex];
                    float r = (cCode >> 16 & 0xFF) / 255.0F;
                    float g = (cCode >> 8 & 0xFF) / 255.0F;
                    float b = (cCode & 0xFF) / 255.0F;
                    GL11.glColor4f(r, g, b, alpha);
                    obfuscated = false;
                } else if (formatCode == 'k') {
                    obfuscated = true;
                } else if (formatCode == 'r') {
                    obfuscated = false;
                    // RESET to the Original Color (Chroma)
                    GL11.glColor4f(origRed, origGreen, origBlue, alpha);
                }

                i++;
                continue;
            }

            if (obfuscated) c = (char) (random.nextInt(95) + 32);

            Glyph glyph = glyphs.get(c);
            if (glyph != null) {
                drawGlyph(glyph, currentX);
                currentX += glyph.width + spacing;
            }
        }

        GL11.glEnd();
        GL11.glPopMatrix();

        // Reset global state
        GlStateManager.color(1f, 1f, 1f, 1f);
    }

    public int getStringWidth(String text) {
        if (text == null) return 0;
        int width = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\u00A7' && i + 1 < text.length()) { i++; continue; }
            Glyph g = glyphs.get(c);
            if (g != null) width += g.width + spacing;
        }
        return (int) (width * scaleFactor);
    }

    private void drawGlyph(Glyph g, float x) {
        float u = (float) g.x / texWidth;
        float v = (float) g.y / texHeight;
        float u2 = (float) (g.x + g.width) / texWidth;
        float v2 = (float) (g.y + g.height) / texHeight;

        GL11.glTexCoord2f(u, v);
        GL11.glVertex2f(x, 0);
        GL11.glTexCoord2f(u, v2);
        GL11.glVertex2f(x, g.height);
        GL11.glTexCoord2f(u2, v2);
        GL11.glVertex2f(x + g.width, g.height);
        GL11.glTexCoord2f(u2, v);
        GL11.glVertex2f(x + g.width, 0);
    }

    private static class Glyph {
        int x, y, width, height;
        public Glyph(int x, int y, int width, int height) {
            this.x = x; this.y = y; this.width = width; this.height = height;
        }
    }
}