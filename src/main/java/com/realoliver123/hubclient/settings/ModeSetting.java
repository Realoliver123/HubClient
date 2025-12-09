package com.realoliver123.hubclient.settings;
import java.util.Arrays;
import java.util.List;

public class ModeSetting extends Setting {
    private int index;
    private List<String> modes;
    public ModeSetting(String name, String defaultMode, String... modes) {
        super(name);
        this.modes = Arrays.asList(modes);
        this.index = this.modes.indexOf(defaultMode);
    }
    public String getMode() { return modes.get(index); }
    public void cycle() {
        if (index < modes.size() - 1) index++; else index = 0;
    }
    public boolean is(String mode) { return modes.get(index).equalsIgnoreCase(mode); }
}