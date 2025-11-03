public class ChargingPort {
    private ChargeType chargeType;
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
    public void EndCharging() {
        this.occupied=false;
    }
}
