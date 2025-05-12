package ru.promelectronika.enums;

public enum NetworkType {
    ONE_PHASE_NETWORK(1),
    THREE_PHASE_NETWORK(3),
    UNDEFINED(0);

    private final int phaseQuantity;

    NetworkType(int phaseQuantity) {
        this.phaseQuantity = phaseQuantity;
    }

    public int getPhaseQuantity() {
        return phaseQuantity;
    }
}
