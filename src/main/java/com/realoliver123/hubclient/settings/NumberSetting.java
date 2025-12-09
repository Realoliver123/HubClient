package com.realoliver123.hubclient.settings;

public class NumberSetting extends Setting {
    public double value, min, max, increment;
    public NumberSetting(String name, double value, double min, double max, double increment) {
        super(name);
        this.value = value;
        this.min = min;
        this.max = max;
        this.increment = increment;
    }
    public double getValue() { return value; }
    public void setValue(double value) {
        this.value = Math.max(min, Math.min(max, value));
    }
}