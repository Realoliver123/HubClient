package com.realoliver123.hubclient.features;

public enum ModuleCategory {
    COMBAT("Combat"),
    RENDER("Render"),
    PLAYER("Player"),
    MACROS("Macros"),
    GUI("GUI");

    public String name;
    ModuleCategory(String name) { this.name = name; }
}