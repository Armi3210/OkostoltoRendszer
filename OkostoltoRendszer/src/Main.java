import javax.sound.sampled.Port;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ChargingStation cstation = new ChargingStation();
        ArrayList<ChargingSession> cslist = new ArrayList<>();

        boolean leave = false;
        String a;
        do {
            System.out.println("Please tell the brand and the id of the device,\nwhich port do you want to use" +
                    "(1 - Ultra Fast, 2 - Fast, 3 - Fast, 4 - Normal, 5 - Normal, 6 - Slow)," +
                    "\nthe starting time (year-month-day hour:minute),\nand the charging minutes.\n" +
                    "\nIf you want to leave, type 'exit'!");
            a = sc.nextLine();
            leave = a.equals("exit");
            if (!leave) {
                StringTokenizer st = new StringTokenizer(a, ",");
                if (portOccupied(cstation, Integer.parseInt(a.split(",")[2]) - 1)) {
                    System.out.println("The selected port is occupied! It will be free after "
                            +remainingMinutes(cslist,a.split(",")[3],
                            cstation.getPorts().get(Integer.parseInt(a.split(",")[2]) - 1))+" minutes!");
                    System.out.println(portRecommend());
                }

                cslist.add(new ChargingSession(st.nextToken(), st.nextToken(),
                        cstation.getPorts().get(Integer.parseInt(st.nextToken()) + 1),
                        st.nextToken(), Integer.parseInt(st.nextToken())));
                System.out.println("Charging Started!");
            }

        } while (!leave);
        sc.close();
    }

    public static boolean portOccupied(ChargingStation cstation, int portNumber) {
        if (cstation.getPorts().get(portNumber).isOccupied()) {
            return true;
        }
        return false;
    }

    public static int remainingMinutes(ArrayList<ChargingSession> cslist,String time, ChargingPort port) {
        for (ChargingSession cs : cslist) {
            if(cs.getChargingPort().equals(port) &&
                    time.split(" ")[0].equals(cs.getStartTime().split(" ")[0])) {

            }
        }
    }

    public static String portRecommend(ChargingStation cstation) {
        for (ChargingPort port : )
    }
}