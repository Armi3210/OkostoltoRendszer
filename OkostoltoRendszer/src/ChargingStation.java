import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class ChargingStation {
    private ArrayList<ChargingPort> Ports = new ArrayList<>();

    public ChargingStation() {
        Ports.addAll(Arrays.asList(new ChargingPort(ChargeType.Ultra_Fast),new ChargingPort(ChargeType.Fast),new ChargingPort(ChargeType.Fast),new ChargingPort(ChargeType.Normal),new ChargingPort(ChargeType.Normal),new ChargingPort(ChargeType.Slow)));
    }

    public ArrayList<ChargingPort> getPorts() {
        return Ports;
    }
}
