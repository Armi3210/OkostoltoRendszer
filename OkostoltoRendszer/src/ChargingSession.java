public class ChargingSession {
    private String deviceID;
    private ChargingPort chargingPort;
    private String startTime;
    private int durationMinutes;

    public ChargingSession(String deviceID, ChargingPort chargingPort, String startTime, int durationMinutes) {
        this.deviceID = deviceID;
        this.chargingPort = chargingPort;
        this.startTime = startTime;
        this.durationMinutes = durationMinutes;
    }

    public float priceCalculation() {
        float price;
        if(chargingPort.getChargeType()==ChargeType.Slow) {
            price = ((float)durationMinutes/60) * 400;
        } else if (chargingPort.getChargeType()==ChargeType.Normal) {
            price = ((float)durationMinutes/60) * 800;
        } else if (chargingPort.getChargeType()==ChargeType.Fast) {
            price = ((float)durationMinutes/60) * 1500;
        } else {
            price = ((float)durationMinutes/60) * 2000;
        }
        return price;
    }

}
