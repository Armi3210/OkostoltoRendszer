public class Main {
    public static void main(String[] args) {
        ChargingPort a = new ChargingPort(ChargeType.Fast);
        System.out.println(a.getChargeType());

        ChargingStation b = new ChargingStation();
        for (int i = 0; i < b.getPorts().size(); i++) {
            System.out.println(b.getPorts().get(i).getChargeType());
        }
    }
}