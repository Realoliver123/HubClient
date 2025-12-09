package com.realoliver123.hubclient.util.font;

import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

public class FontManager {
    public static CustomFontRenderer activeFont;
    private static Map<String, CustomFontRenderer> fonts = new HashMap<>();

    public static void init() {
        // NULL = Default Minecraft Font
        fonts.put("Minecraft", null);

        // --- CUSTOM FONTS ---
        // Size 19-20 is usually best for MC GUIs
        fonts.put("Arial", new CustomFontRenderer(new Font("Arial", Font.PLAIN, 20)));
        fonts.put("Verdana", new CustomFontRenderer(new Font("Verdana", Font.PLAIN, 19)));
        fonts.put("Comic Sans", new CustomFontRenderer(new Font("Comic Sans MS", Font.PLAIN, 20)));
        fonts.put("Impact", new CustomFontRenderer(new Font("Impact", Font.PLAIN, 22)));
        fonts.put("Segoe UI", new CustomFontRenderer(new Font("Segoe UI", Font.PLAIN, 20)));
        fonts.put("Tahoma", new CustomFontRenderer(new Font("Tahoma", Font.PLAIN, 20)));
        fonts.put("Times New", new CustomFontRenderer(new Font("Times New Roman", Font.PLAIN, 20)));
        fonts.put("Courier", new CustomFontRenderer(new Font("Courier New", Font.BOLD, 20)));
        fonts.put("Georgia", new CustomFontRenderer(new Font("Georgia", Font.PLAIN, 20)));

        // Default
        activeFont = fonts.get("Arial");
    }

    public static void setFont(String name) {
        if (fonts.containsKey(name)) {
            activeFont = fonts.get(name);
        }
    }
}