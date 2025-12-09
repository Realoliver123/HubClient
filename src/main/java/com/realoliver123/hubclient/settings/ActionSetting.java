package com.realoliver123.hubclient.settings;

public class ActionSetting extends Setting {
    private Runnable action;
    public ActionSetting(String name, Runnable action) {
        super(name);
        this.action = action;
    }
    public void perform() { if (action != null) action.run(); }
}