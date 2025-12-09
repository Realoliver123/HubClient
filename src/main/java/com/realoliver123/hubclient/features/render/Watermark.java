package com.realoliver123.hubclient.features.render;

import com.realoliver123.hubclient.features.Module;
import com.realoliver123.hubclient.features.ModuleCategory;
import com.realoliver123.hubclient.settings.BooleanSetting;
import com.realoliver123.hubclient.settings.StringSetting;

public class Watermark extends Module {

    public static StringSetting customText = new StringSetting("Text", "HubClient");
    public static BooleanSetting sideGlitch = new BooleanSetting("Side Glitch", false);
    public static BooleanSetting showCoords = new BooleanSetting("Show Coords", true);

    public Watermark() {
        // CHANGED: Now in GUI category so it appears in that tab
        super("Watermark", ModuleCategory.GUI);
        this.addSettings(customText, sideGlitch, showCoords);
        if (!this.isToggled()) this.toggle(); // Enable by default
    }
}