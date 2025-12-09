package com.realoliver123.hubclient.config;

import com.realoliver123.hubclient.SkyblockQOL;
import com.realoliver123.hubclient.features.Module;
import com.realoliver123.hubclient.features.ModuleManager;
import com.realoliver123.hubclient.settings.BooleanSetting;
import com.realoliver123.hubclient.settings.NumberSetting;
import com.realoliver123.hubclient.settings.StringSetting;
import com.realoliver123.hubclient.settings.Setting;
import com.realoliver123.hubclient.util.font.FontManager; // Import
import net.minecraftforge.common.config.Configuration;
import java.io.File;
import java.awt.Color;

public class ConfigHandler {
    public static Configuration config;

    public static void loadConfig() {
        File configFile = new File("config/hubclient.cfg");
        config = new Configuration(configFile);
        config.load();

        // Visuals
        SkyblockQOL.guiX = config.get("GUI", "PosX", 0).getInt();
        SkyblockQOL.guiY = config.get("GUI", "PosY", 0).getInt();
        SkyblockQOL.chromaText = config.get("Visuals", "Chroma", false).getBoolean();

        // LOAD FONT
        SkyblockQOL.fontName = config.get("Visuals", "FontName", "Arial").getString();
        if (SkyblockQOL.fontName.equals("Minecraft")) {
            SkyblockQOL.customFont = false;
        } else {
            SkyblockQOL.customFont = true;
            FontManager.setFont(SkyblockQOL.fontName);
        }

        SkyblockQOL.showCoords = config.get("Visuals", "ShowCoords", true).getBoolean();
        SkyblockQOL.watermarkText = config.get("Visuals", "WatermarkText", "HubClient").getString();
        SkyblockQOL.watermarkGlitch = config.get("Visuals", "WatermarkGlitch", false).getBoolean();

        SkyblockQOL.themeHue = (float) config.get("Visuals", "ThemeHue", 0.5).getDouble();
        SkyblockQOL.themeColor = Color.HSBtoRGB(SkyblockQOL.themeHue, 1.0f, 1.0f);
        SkyblockQOL.espImagePath = config.get("Visuals", "ESPImage", "").getString();

        // Modules
        for (Module m : ModuleManager.modules) {
            boolean toggled = config.get("Modules", m.getName() + "_Enabled", false).getBoolean();
            if (toggled && !m.isToggled()) m.toggle();

            for (Setting s : m.getSettings()) {
                if (s instanceof BooleanSetting) {
                    boolean val = config.get("Settings", m.getName() + "_" + s.name, ((BooleanSetting) s).isEnabled()).getBoolean();
                    ((BooleanSetting) s).setEnabled(val);
                } else if (s instanceof NumberSetting) {
                    double val = config.get("Settings", m.getName() + "_" + s.name, ((NumberSetting) s).getValue()).getDouble();
                    ((NumberSetting) s).setValue(val);
                } else if (s instanceof StringSetting) {
                    String val = config.get("Settings", m.getName() + "_" + s.name, ((StringSetting) s).getText()).getString();
                    ((StringSetting) s).setText(val);
                }
            }
        }
    }

    public static void saveConfig() {
        config.get("GUI", "PosX", 0).set(SkyblockQOL.guiX);
        config.get("GUI", "PosY", 0).set(SkyblockQOL.guiY);
        config.get("Visuals", "Chroma", false).set(SkyblockQOL.chromaText);

        // SAVE FONT
        config.get("Visuals", "FontName", "Arial").set(SkyblockQOL.fontName);

        config.get("Visuals", "ShowCoords", true).set(SkyblockQOL.showCoords);
        config.get("Visuals", "WatermarkText", "HubClient").set(SkyblockQOL.watermarkText);
        config.get("Visuals", "WatermarkGlitch", false).set(SkyblockQOL.watermarkGlitch);

        config.get("Visuals", "ThemeHue", 0.5).set(SkyblockQOL.themeHue);
        config.get("Visuals", "ESPImage", "").set(SkyblockQOL.espImagePath);

        for (Module m : ModuleManager.modules) {
            config.get("Modules", m.getName() + "_Enabled", false).set(m.isToggled());
            for (Setting s : m.getSettings()) {
                if (s instanceof BooleanSetting) {
                    config.get("Settings", m.getName() + "_" + s.name, false).set(((BooleanSetting) s).isEnabled());
                } else if (s instanceof NumberSetting) {
                    config.get("Settings", m.getName() + "_" + s.name, 0.0).set(((NumberSetting) s).getValue());
                } else if (s instanceof StringSetting) {
                    config.get("Settings", m.getName() + "_" + s.name, "HubClient").set(((StringSetting) s).getText());
                }
            }
        }
        config.save();
    }
}