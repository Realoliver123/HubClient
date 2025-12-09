package com.realoliver123.hubclient.settings;

public class StringSetting extends Setting {
    private String text;

    public StringSetting(String name, String defaultValue) {
        super(name);
        this.text = defaultValue;
    }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}