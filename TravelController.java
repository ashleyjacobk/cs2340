import java.util.Scanner;
public class TravelController {
    public void commandLoop() {
        Scanner commandLineInput = new Scanner(System.in);
        String wholeInputLine;
        String[] tokens;
        final String DELIMITER = ",";
        Integer val_A, val_B;

        val_A = 0;
        val_B = 0;
        while (true) {
            try {
                // Display a command request prompt
                System.out.print(" $> ");

                // Determine the next command and echo it to the monitor for testing purposes
                wholeInputLine = commandLineInput.nextLine();
                tokens = wholeInputLine.split(DELIMITER);
                // System.out.println(" echo >> " + wholeInputLine);

                // Handle comments and simulation system instructions
                if (tokens[0].indexOf("//") == 0) {
                    // System.out.println(wholeInputLine);
                } else if (wholeInputLine.equals("")) {
                    continue;
                } else if (tokens[0].equals("update_A")) {
                    val_A = Integer.parseInt(tokens[1]);
                } else if (tokens[0].equals("update_B")) {
                    Integer temp = Integer.parseInt(tokens[1]);
                    if (temp < val_A) {
                        displayMessage("error", "B must be greater than or equal to A");
                    } else {
                        val_B = temp;
                    }
                } else if (tokens[0].equals("values")) {
                    displayMessage("info", "A = " + val_A + " & B = " + val_B);
                } else if (tokens[0].equals("count")) {
                    if (tokens[1].equals("up")) {
                        for (Integer i = val_A; i <= val_B; i = i + 1) { System.out.println(i); }
                    } else if (tokens[1].equals("down")) {
                        for (Integer i = val_B; i >= val_A; i = i - 1) { System.out.println(i); }
                    } else {
                        displayMessage("error", "invalid direction");
                    }
                } else if (tokens[0].equals("exit")) {
                    System.out.println("exit acknowledged");
                    break;
                } else {
                    System.out.println("command " + tokens[0] + " NOT acknowledged");
                }

            } catch (Exception e) {
                displayMessage("error", "during command loop >> execution");
                e.printStackTrace();
                System.out.println();
            }
        }

        System.out.println("simulation terminated");
        commandLineInput.close();
    }

    void displayMessage(String status, String text_output) {
        System.out.println(status.toUpperCase() + ": " + text_output);
    }
}
