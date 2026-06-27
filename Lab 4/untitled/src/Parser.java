import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

class RecursiveDescent {
    String input;
    StringBuilder output = new StringBuilder();
    int couter = 0;
    Boolean accept = true;

    public RecursiveDescent(String input) {
        this.input = input;
    }

    private void S() {
        if(couter >= input.length()) {
            accept = false;
            return;
        }

        char leftSymbol = input.charAt(couter++);
        output.append("S");
        if(leftSymbol == '\n')
            return;

        if(leftSymbol == 'a') {
            A();
            if(!accept)
                return;
            B();
        }
        else if(leftSymbol == 'b') {
            B();
            if(!accept)
                return;
            A();
        }
    }

    private void A() {
        if(couter >= input.length()) {
            accept = false;
            return;
        }

        char leftSymbol = input.charAt(couter++);
        output.append("A");

        if(leftSymbol == '\n')
            return;

        if(leftSymbol == 'b') {
            C();
        }
        else if(leftSymbol != 'a')
            accept = false;
    }

    private void B() {
        if(couter >= input.length() - 1) {
            output.append("B");
            accept = accept;
            return;
        }

        int tmpCounter = couter;

        char leftSymbol = input.charAt(tmpCounter++);
        char nextLeftSymbol = input.charAt(tmpCounter++);

        output.append("B");

        if(leftSymbol == '\n' || nextLeftSymbol == '\n')
            return;

        if(nextLeftSymbol == leftSymbol && leftSymbol == 'c') {
            couter = tmpCounter;
            S();
            if(!accept)
                return;
            leftSymbol = input.charAt(couter++);
            nextLeftSymbol = input.charAt(couter++);
            if(!(leftSymbol == 'b' && nextLeftSymbol == 'c')) //mozda je krivo??
                accept = false;
        }
    }

    private void C() {
        if(couter >= input.length()) {
            accept = false;
            return;
        }

        output.append("C");

        A();
        if(!accept)
            return;
        A();
    }

    @Override
    public String toString() {
        return "RecursiveDescent{" +
                "input='" + input + '\'' +
                '}';
    }

    public String output() {
        S();

        if(couter < input.length() - 1)
            accept = false;

        if(accept)
            output.append("\nDA");
        else
            output.append("\nNE");

        return output.toString();
    }
}

public class Parser {
    public static void main(String[] args) {
//        testOneFile("lab4_primjeri/test(/test.in");
//        tester();
        userInput();
    }
    static void tester() {
        String path = "lab4_primjeri/test";
        String folder = "";

        int[] counter = {0, 0};

        RecursiveDescent machine;

        for(Integer i = 1; i <= 20; i++) {
            try {
                path = "lab4_primjeri/test";
                if (i < 10)
                    folder = "0" + i;
                else
                    folder = i.toString();

                path += folder;
                String pathInput = path + "/test.in";
                String pathOutput = path + "/test.out";

                StringBuilder input = new StringBuilder();
                try {
                    Scanner scanner = new Scanner(new File(pathInput));

                    while (scanner.hasNextLine()) {
                        input.append(scanner.nextLine()).append("\n");
                    }
                    scanner.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                machine = new RecursiveDescent(input.toString());
                String myOutput = machine.output();

                input = new StringBuilder();
                try {
                    Scanner scanner = new Scanner(new File(pathOutput));

                    while (scanner.hasNextLine()) {
                        input.append(scanner.nextLine()).append("\n");
                    }
                    scanner.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                String correct = input.toString().strip().trim();

                System.out.print(i + "\t");
                if (correct.compareTo(myOutput) == 0) {
                    System.out.println("\u001B[32m" + "Correct " + "\u001B[0m");
                    counter[0]++;
                }
                else {
                    System.out.println("\u001B[31m" + "Wrong" + "\u001B[0m");
                    System.out.println("Correct: " + correct);
                    System.out.println("MyOutput:" + myOutput);
                    counter[1]++;
                }
            } catch (Exception e) {
                System.out.print(i + "\t");
                System.out.println("\u001B[31m" + "Wrong" + "\u001B[0m");
                System.out.println(e);
                counter[1]++;
            }
        }
        System.out.println("\u001B[32m" + "Correct: " + counter[0] + "\u001B[0m");
        System.out.println("\u001B[31m" + "Wrong: " + counter[1] + "\u001B[0m");
    }
    static void testOneFile(String path) {
        StringBuilder input = new StringBuilder();
        try {
            Scanner scanner = new Scanner(new File(path));

            while (scanner.hasNextLine()) {
                input.append(scanner.nextLine()).append("\n");
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        RecursiveDescent machine = new RecursiveDescent(input.toString());
        System.out.println(machine);
        System.out.println(machine.output());
    }
    static void userInput() {
        Scanner scanner = new Scanner(System.in);
        StringBuilder inputBuilder = new StringBuilder();

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.isEmpty()) {
                break;
            }
            inputBuilder.append(line).append("\n");
        }

        String input = inputBuilder.toString();
        scanner.close();

        RecursiveDescent machine = new RecursiveDescent(input);

        System.out.println(machine.output());
    }

}