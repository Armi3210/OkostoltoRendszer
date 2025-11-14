public class ChargingSession implements iBrands{
    private String deviceID;
    private ChargingPort chargingPort;
    private String startTime;
    private int durationMinutes;
    private String deviceBrand;

    public ChargingSession(String deviceID, String deviceBrand,ChargingPort chargingPort, String startTime, int durationMinutes) {
        this.setDeviceID(deviceID);
        this.setDeviceBrand(deviceBrand);
        this.setChargingPort(chargingPort);
        this.setStartTime(startTime);
        this.setDurationMinutes(durationMinutes);
        this.getChargingPort().StartCharging();
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
        for (int i = 0; i < MARKAK.length; i++) {
            if(MARKAK[i].equals(this.getDeviceBrand())) {
                price*= 0.8F;
            }
        }
        return price;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public ChargingPort getChargingPort() {
        return chargingPort;
    }

    public void setChargingPort(ChargingPort chargingPort) {
        this.chargingPort = chargingPort;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getDeviceBrand() {
        return deviceBrand;
    }

    public void setDeviceBrand(String deviceBrand) {
        this.deviceBrand = deviceBrand;
    }
}
