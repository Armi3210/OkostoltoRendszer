import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Main implements iBrands, iDays{
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ChargingStation cstation = new ChargingStation();
        ArrayList<ChargingSession> cslist = new ArrayList<>();
        boolean canceled = false;

        boolean leave = false;
        String a;
        do {
            canceled=false;
            try {
                System.out.println("""
            Please tell the id and the brand of the car,
            which port do you want to use
            (1 - Ultra Fast, 2 - Fast, 3 - Fast, 4 - Normal, 5 - Normal, 6 - Slow),
            the starting time (year-month-day hour:minute),
            and the charging minutes.
            
            (Example: AAA111, Ford, 1, 2001-09-11 09:11, 60)

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
                    else if(DAYS[Integer.parseInt(parts[3].split(" ")[0].split("-")[1])-1] < Integer.parseInt(parts[3].split(" ")[0].split("-")[2])) {
                        if (!((Objects.equals(parts[3].split(" ")[0].split("-")[1], "02"))
                                && (Objects.equals(parts[3].split(" ")[0].split("-")[2], "29"))
                                && ((Integer.parseInt(parts[3].split(" ")[0].split("-")[0]) % 4) == 0)
                                && ((Integer.parseInt(parts[3].split(" ")[0].split("-")[0])%100 != 0) || (Integer.parseInt(parts[3].split(" ")[0].split("-")[0])%400 == 0))))
                            throw new InvalidInputException("Invalid date!");
                    }
                    else if((Integer.parseInt(parts[3].split(" ")[1].split(":")[0])>23) || (Integer.parseInt(parts[3].split(" ")[1].split(":")[1])>59))
                        throw new InvalidInputException("Invalid time!");

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

                    if (remainingMinutes(cslist, parts[3],
                            cstation.getPorts().get(portNum - 1)) != 0) {
                        System.out.println("The selected port is occupied! It will be free after "
                                + remainingMinutes(cslist, parts[3],
                                cstation.getPorts().get(portNum - 1)) + " minutes!");
                        System.out.println(portRecommend(cstation,cslist,parts[3]));
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
                        if(hasDiscount(cs)) {
                            System.out.println("You have a discount! You pay 20% less!");
                        }
                        System.out.println("The price of this charging: " + cs.priceCalculation() + " Ft");
                        String b;
                        boolean done = false;
                        do {
                            System.out.println("If you want to cancel the charging, write 'cancel'!\nIf you want to pay with card, write 'pay'!");
                            b = sc.nextLine();
                            if (b.equalsIgnoreCase("cancel")) {
                                System.out.println("You cancelled the charging! See you later!");
                                done = true;
                                canceled = true;
                                cslist.remove(cs);
                            } else if (b.equalsIgnoreCase("pay")) {
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
                                    fw.close();
                                } catch (IOException ioe) {
                                    System.out.println("Error at file writing");
                                }
                            }
                        } while (!done);
                        if(!canceled){
                        System.out.println("Charging Started!");}
                    }

                }} catch (InvalidInputException iie) {
                System.out.println("Error: " + iie.getMessage());
            }
        } while (!leave);
        float sum = 0;
        for (ChargingSession cs : cslist) {
            sum+=cs.priceCalculation();
        }
        try {
        Float[] stats = statCalculation(cslist, cstation);
        System.out.println("Total revenue: " + sum+" Ft");
        System.out.println("Usement rate of ports:\nUltra Fast: "
                + stats[0]
                + "%\nFast1: "
                + stats[1]
                + "%\nFast2: "
                + stats[2]
                + "%\nNormal1: "
                + stats[3]
                + "%\nNormal2: "
                + stats[4]
                + "%\nSlow: "
                + stats[5] + "%"
        );} catch (NullPointerException _) {}
        System.out.println("All charging minutes: "+sumMinutes(cslist));
        sc.close();
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
            if(csMinutes+cs.getDurationMinutes()-24*60 >= tMinutes) {
                if (Objects.equals(csDate.split("-")[0], tDate.split("-")[0]) &&
                        Objects.equals(csDate.split("-")[1], tDate.split("-")[1])
                        && Integer.parseInt(csDate.split("-")[2])+1==Integer.parseInt(tDate.split("-")[2])){
                    return csMinutes-tMinutes+cs.getDurationMinutes()-24*60;
                }
                else if (Objects.equals(csDate.split("-")[0], tDate.split("-")[0]) &&
                        Objects.equals(Integer.parseInt(csDate.split("-")[1]), Integer.parseInt(tDate.split("-")[1])-1)
                        && Objects.equals(Integer.parseInt(csDate.split("-")[2]),DAYS[Integer.parseInt(csDate.split("-")[1])-1]) && Objects.equals(tDate.split("-")[2],"01")){
                    return csMinutes-tMinutes+cs.getDurationMinutes()-24*60;
                }
                else if (Objects.equals(Integer.parseInt(csDate.split("-")[0]), Integer.parseInt(tDate.split("-")[0])+1) &&
                        Objects.equals(csDate.split("-")[1], "12") && Objects.equals(tDate.split("-")[1], "01")
                        && Objects.equals(csDate.split("-")[2],"31") && Objects.equals(tDate.split("-")[2],"01")){
                    return csMinutes-tMinutes+cs.getDurationMinutes()-24*60;
                }
            }

            if (cs.getChargingPort().equals(port)
                    && tDate.equals(csDate)
                    && tMinutes >= csMinutes
                    && tMinutes < csMinutes+cs.getDurationMinutes()) {
                return csMinutes-tMinutes+cs.getDurationMinutes();
            }
            }
        return 0;
        }
    public static String portRecommend(ChargingStation cstation, ArrayList<ChargingSession> cslist, String a) {
        for (ChargingPort port : cstation.getPorts()) {
            if(!(remainingMinutes(cslist, a, port)>0)) {
                port.StopCharging();
            }
            if(!port.isOccupied()) {
                return "You can use a "+port.getChargeType()+" charger instead!";
            }
    }
        return "";
    }

    public static boolean hasDiscount(ChargingSession cs) {
        for (String s : BRANDS) {
            if(s.equalsIgnoreCase(cs.getDeviceBrand())) {
                return true;
            }
        }
        return false;
    }

    public static int sumMinutes(ArrayList<ChargingSession> cslist) {
        int sum = 0;
        for(ChargingSession cs : cslist) {
            sum+=cs.getDurationMinutes();
        }
        return sum;
    }

    public static Float[] statCalculation(ArrayList<ChargingSession> cslist, ChargingStation cstation){
        Float[] stats = new Float[6];
        for (int i = 0; i < 6; i++) {
            stats[i] = (float) 0;
        }
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
            if(!Float.isNaN(a)) {
                stats[i] = a;
            }
            i++;
        }
        return stats;
    }
}