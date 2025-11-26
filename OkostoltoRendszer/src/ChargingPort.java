public class ChargingPort {
    private final ChargeType chargeType;
    private boolean occupied = false;

    public ChargingPort(ChargeType chargeType) {
        this.chargeType = chargeType;
    }

    public ChargeType getChargeType() {
        return chargeType;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void StartCharging() {
        this.occupied=true;
    }
}
