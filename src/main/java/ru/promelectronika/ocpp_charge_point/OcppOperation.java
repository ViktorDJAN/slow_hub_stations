package ru.promelectronika.ocpp_charge_point;




public class OcppOperation {

    private static ChargePointOcpp chargePointOcpp ;

    public static ChargePointOcpp getChargePointOcpp() {
        return chargePointOcpp;
    }

    public static void setChargePointOcpp(ChargePointOcpp chargePointOcpp) {
        OcppOperation.chargePointOcpp = chargePointOcpp;
    }
}
