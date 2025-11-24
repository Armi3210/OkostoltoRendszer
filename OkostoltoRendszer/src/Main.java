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
            System.out.println("""
                    Please tell the brand and the id of the device,
                    which port do you want to use\
                    (1 - Ultra Fast, 2 - Fast, 3 - Fast, 4 - Normal, 5 - Normal, 6 - Slow),\
                    
                    the starting time (year-month-day hour:minute),
                    and the charging minutes.
                    
                    If you want to leave, type 'exit'!""");
            a = sc.nextLine();
            leave = a.equals("exit");
            if (!leave) {
                StringTokenizer st = new StringTokenizer(a, ",");
                if (portOccupied(cstation, Integer.parseInt(a.split(",")[2].strip()) - 1)) {
                    System.out.println("The selected port is occupied! It will be free after "
                            + remainingMinutes(cslist, a.split(",")[3].strip(),
                            cstation.getPorts().get(Integer.parseInt(a.split(",")[2].strip()) - 1)) + " minutes!");
                    System.out.println(portRecommend(cstation));
                } else {
                        cslist.add(new ChargingSession(st.nextToken().strip(), st.nextToken().strip(),
                                cstation.getPorts().get(Integer.parseInt(st.nextToken().strip()) - 1),
                                st.nextToken().strip(), Integer.parseInt(st.nextToken().strip())));

                    System.out.println("Charging Started!");
                }

            }

        } while (!leave);
        sc.close();
    }

    public static boolean portOccupied(ChargingStation cstation, int portNumber) {
        return cstation.getPorts().get(portNumber).isOccupied();
    }

    public static int remainingMinutes(ArrayList<ChargingSession> cslist,String time, ChargingPort port) {
        String[] tParts = time.split(" ");
        String tDate = tParts[0];
        String[] tHM = tParts[1].split(":");
        int tMinutes = Integer.parseInt(tHM[0]) * 60 + Integer.parseInt(tHM[1]);
        for (ChargingSession cs : cslist) {
            String[] csParts = cs.getStartTime().split(" ");
            String csDate = csParts[0];
            String[] csHM = csParts[1].split(":");
            int csMinutes = Integer.parseInt(csHM[0]) * 60 + Integer.parseInt(csHM[1]);

            if (cs.getChargingPort().equals(port)
                    && tDate.equals(csDate)
                    && tMinutes > csMinutes
                    && tMinutes< csMinutes+cs.getDurationMinutes()) {
                return csMinutes-tMinutes;
            }
            }
        return 0;
        }
    public static String portRecommend(ChargingStation cstation) {
        for (ChargingPort port : cstation.getPorts()) {
            if(!port.isOccupied()) {
                return "You can use a "+port.getChargeType()+" charger instead!";
            }
    }
        return "";
    }}
//
