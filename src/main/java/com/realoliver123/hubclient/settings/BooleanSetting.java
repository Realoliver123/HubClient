package com.realoliver123.hubclient.settings;

public class BooleanSetting extends Setting {
    private boolean enabled;
    public BooleanSetting(String name, boolean defaultValue) {
        super(name);
        this.enabled = defaultValue;
    }
    public void toggle() { this.enabled = !this.enabled; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean b) { this.enabled = b; }
}