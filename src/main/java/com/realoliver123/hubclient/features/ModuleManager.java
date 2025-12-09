package com.realoliver123.hubclient.features;

import com.realoliver123.hubclient.features.combat.AimAssist; // IMPORT THIS
import com.realoliver123.hubclient.features.combat.AutoClicker;
import com.realoliver123.hubclient.features.macros.AutoMiner;
import com.realoliver123.hubclient.features.player.Sprint;
import com.realoliver123.hubclient.features.render.ESP;
import com.realoliver123.hubclient.features.render.FullBright;
import com.realoliver123.hubclient.features.render.ImageESP;
import com.realoliver123.hubclient.features.render.VisualSpin;
import com.realoliver123.hubclient.features.render.Watermark;
import org.lwjgl.input.Keyboard;
import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    public static List<Module> modules = new ArrayList<>();

    public static void init() {
        modules.add(new AutoClicker());

        // --- ADD AIM ASSIST HERE ---
        modules.add(new AimAssist());

        modules.add(new ImageESP());
        modules.add(new FullBright());
        modules.add(new Sprint());
        modules.add(new AutoMiner());
        modules.add(new Watermark());
        modules.add(new VisualSpin());
        modules.add(new ESP());
    }

    public static List<Module> getModulesByCategory(ModuleCategory c) {
        List<Module> filtered = new ArrayList<>();
        for (Module m : modules) {
            if (m.getCategory() == c) filtered.add(m);
        }
        return filtered;
    }

    public static void onKeyInput() {
        for (Module m : modules) {
            if (m.getKeyBind() != 0 && Keyboard.isKeyDown(m.getKeyBind())) {
                m.toggle();
            }
        }
    }
}