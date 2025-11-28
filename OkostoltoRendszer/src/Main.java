import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
            try {
                System.out.println("""
            Please tell the brand and the id of the device,
            which port do you want to use
            (1 - Ultra Fast, 2 - Fast, 3 - Fast, 4 - Normal, 5 - Normal, 6 - Slow),
            the starting time (year-month-day hour:minute),
            and the charging minutes.

            If you want to leave, type 'exit'!
            """);

                a = sc.nextLine().trim();
                if (a.equalsIgnoreCase("exit")) {
                    leave = true;
                } else {

                    String[] parts = a.split(",");
                    if (parts.length != 5)
                        throw new InvalidInputException("You must enter 5 comma-separated values!");

                    for (int i = 0; i < parts.length; i++)
                        parts[i] = parts[i].trim();

                    int portNum;
                    try {
                        portNum = Integer.parseInt(parts[2]);
                        if (portNum < 1 || portNum > cstation.getPorts().size())
                            throw new InvalidInputException("Invalid port number!");
                    } catch (NumberFormatException e) {
                        throw new InvalidInputException("Port number must be an integer!");
                    }

                    if (!parts[3].matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}"))
                        throw new InvalidInputException("Invalid time format! Use: YYYY-MM-DD HH:MM");

                    int duration;
                    try {
                        duration = Integer.parseInt(parts[4]);
                        if (duration <= 0)
                            throw new InvalidInputException("Duration must be positive!");
                        if (duration > 120)
                            throw new InvalidInputException("You want to charge your vehicle too long");
                    } catch (NumberFormatException e) {
                        throw new InvalidInputException("Duration must be an integer!");
                    }

                    if (portOccupied(cstation, portNum - 1)) {
                        System.out.println("The selected port is occupied! It will be free after "
                                + remainingMinutes(cslist, parts[3],
                                cstation.getPorts().get(portNum - 1)) + " minutes!");
                        System.out.println(portRecommend(cstation));
                    } else {
                        StringTokenizer st = new StringTokenizer(a, ",");
                        cslist.add(new ChargingSession(
                                st.nextToken().strip(),
                                st.nextToken().strip(),
                                cstation.getPorts().get(Integer.parseInt(st.nextToken().strip()) - 1),
                                st.nextToken().strip(),
                                Integer.parseInt(st.nextToken().strip())
                        ));
                        ChargingSession cs = cslist.getLast();
                        System.out.println("The price of this charging: " + cs.priceCalculation() + " Ft");
                        String b;
                        boolean done = false;
                        do {
                            System.out.println("If you want to cancel the charging, write 'cancel'!\nIf you want to pay with card, write 'pay'!");
                            b = sc.nextLine();
                            if (b.equals("cancel")) {
                                System.out.println("You cancelled the charging! See you later!");
                                done = true;
                            } else if (b.equals("pay")) {
                                System.out.println("Thank you for charging here!");
                                done = true;
                                File dir = new File("OkostoltoRendszer/Data");
                                if (!dir.exists()) {
                                    dir.mkdir();
                                }
                                File f = new File(dir, cs.getStartTime().split(" ")[0] + ".txt");

                                try (FileWriter fw = new FileWriter(f, true)) {
                                    fw.write(cs.getDeviceID() + ", ");
                                    fw.write(cs.getDeviceBrand() + ", ");
                                    fw.write(cs.getChargingPort().getChargeType() + ", ");
                                    fw.write(cs.getStartTime() + ", ");
                                    fw.write(cs.getDurationMinutes() + ", ");
                                    fw.write(cs.priceCalculation() + "\n");
                                } catch (IOException ioe) {
                                    System.out.println("Error at file writing");
                                }
                            }
                        } while (!done);
                        System.out.println("Charging Started!");
                    }

                }} catch (InvalidInputException iie) {
                System.out.println("Error: " + iie.getMessage());
            }
        } while (!leave);
        try {
        for (float f : statCalculation(cslist,cstation)) {
            System.out.println(f+"%");
        } } catch (NullPointerException npe) {}
        float sum = 0;
        for (ChargingSession cs : cslist) {
            sum+=cs.priceCalculation();
        }
        System.out.println("Total revenue: "+sum);

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
    }
    public static Float[] statCalculation(ArrayList<ChargingSession> cslist, ChargingStation cstation){
        Float[] stats = new Float[6];
        for (ChargingSession cs : cslist) {
            for (int i = 0; i < 6; i++) {
                if(cstation.getPorts().indexOf(cs.getChargingPort()) == i) {
                    stats[i]++;
                }
            }
        }
        int i = 0;
        for (float a : stats) {
            a = a/cslist.size()*100;
            stats[i] = a;
            i++;
        }
        return stats;
    }
}