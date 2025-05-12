package ru.promelectronika.ocpp_charge_point.configuration;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ConfigurationObject {
    private Accessibility accessibility;
    private String type;
    private String unit;
    private String value;

}
