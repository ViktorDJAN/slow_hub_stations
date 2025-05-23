package ru.promelectronika.ocpp_charge_point.configuration;


public class ConfigurationObject {
    private Accessibility accessibility;
    private String type;
    private String unit;
    private String value;

    public ConfigurationObject(Accessibility accessibility, String type, String unit, String value) {
        this.accessibility = accessibility;
        this.type = type;
        this.unit = unit;
        this.value = value;
    }

    public Accessibility getAccessibility() {
        return accessibility;
    }

    public void setAccessibility(Accessibility accessibility) {
        this.accessibility = accessibility;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
