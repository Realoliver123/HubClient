package com.realoliver123.hubclient.features;

import com.realoliver123.hubclient.settings.Setting;
import net.minecraftforge.common.MinecraftForge;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Module {
    private String name;
    private ModuleCategory category;
    private boolean toggled;
    private int keyBind;
    private List<Setting> settings = new ArrayList<>();

    public Module(String name, ModuleCategory category) {
        this.name = name;
        this.category = category;
        this.keyBind = 0;
        this.toggled = false;
    }

    public void addSettings(Setting... settings) {
        this.settings.addAll(Arrays.asList(settings));
    }
    public List<Setting> getSettings() { return this.settings; }
    public String getName() { return name; }
    public ModuleCategory getCategory() { return category; }
    public boolean isToggled() { return toggled; }
    public int getKeyBind() { return keyBind; }
    public void setKeyBind(int key) { this.keyBind = key; }

    public void toggle() {
        this.toggled = !this.toggled;
        if (this.toggled) {
            onEnable();
            MinecraftForge.EVENT_BUS.register(this);
        } else {
            onDisable();
            MinecraftForge.EVENT_BUS.unregister(this);
        }
    }
    public void onEnable() {}
    public void onDisable() {}
}