import java.util.*;
import java.util.stream.Collectors;
import java.io.File;
import java.io.FileNotFoundException;

class PushdownAutomaton {
    List<List<String>> input = new ArrayList<>();
    List<String> states, inputSymbols, stackSymbols, startStack = new ArrayList<>(), startState = new ArrayList<>();
    String finalState;
    Map<List<String>, List<String>> transitionFunctions = new HashMap<List<String>, List<String>>();

    @Override
    public String toString() {
        return "PushdownAutomaton{" +
                "\ninput=" + input +
                "\nstates=" + states +
                "\ninputSymbols=" + inputSymbols +
                "\nstackSymbols=" + stackSymbols +
                "\nstartStack=" + startStack +
                "\nstartState=" + startState +
                "\nfinalStates='" + finalState + '\'' +
                "\ntransitionFunctions=" + transitionFunctions +
                '}';
    }

    PushdownAutomaton(String text) {
        int i = 1;
        String[] tmp;
        for (String t: text.split("\n")) {
            switch (i){
                case 1:
                    input = Arrays.stream(t.split("\\|")).map(a -> Arrays.asList(a.split(","))).collect(Collectors.toList());
                    break;
                case 2: states = Arrays.asList(t.split(","));
                    break;
                case 3: inputSymbols = Arrays.asList(t.split(","));
                    break;
                case 4: stackSymbols = Arrays.asList(t.split(","));
                    break;
                case 5: finalState = t;
                    break;
                case 6: startState.add(t);
                    break;
                case 7: startStack.add(t);
                    break;
                default:
                    tmp = t.split("->");
                    transitionFunctions.put(Arrays.asList(tmp[0].split(",")), Arrays.asList(tmp[1].split(",")));
                    break;
            }
            i++;
        }
    }
    String output() {
        String currentState = startState.get(0);
        String stack = startStack.get(0);

        StringBuilder output = new StringBuilder();

        List<String> next;

//        System.out.println(outputStates.get(0));

//        System.out.println(outputStates.get(0));

        String symbol = "";

        for(List<String> inputSymbols: input) {
            currentState = startState.get(0);
            stack = startStack.get(0);
            output.append(currentState + "#" + stack + "|");

            for(int i = 0; i <= inputSymbols.size(); i++) {
                if(i < inputSymbols.size())
                    symbol = inputSymbols.get(i);
                else if(currentState.compareTo(finalState) == 0 || transitionFunctions.get(Arrays.asList(currentState, "$", stack.charAt(0) + "")) == null)
                    break;

                while((next = transitionFunctions.get(Arrays.asList(currentState, symbol, stack.charAt(0) + ""))) == null && !(currentState.compareTo(finalState) == 0 && i == inputSymbols.size())) {
                    next = transitionFunctions.get(Arrays.asList(currentState, "$", stack.charAt(0) + ""));
                    if(next == null && i < inputSymbols.size()) {
                        output.append("fail|");
                        output.append(0);
//                        return output.toString().strip().trim();
                        break;
                    }
                    else if(next == null){
                        break;
                    }
                    currentState = next.get(0);
                    if(next.get(1).length() > 1) {
                        if(next.get(1).charAt(next.get(1).length() - 1) == stack.charAt(0))
                            stack = next.get(1).substring(0, next.get(1).length() - 1) + stack;
                        else
                            stack = next.get(1).substring(0, next.get(1).length()) + stack.substring(1, stack.length());
                    }
                    else if(next.get(1).compareTo("$") == 0) {
                        if(stack.length() == 1)
                            stack = "$";
                        else
                            stack = stack.substring(1, stack.length());
                    }

                    output.append(currentState + "#" + stack + "|");
                }
                if(output.charAt(output.length() - 1) == '0')
                    break;

                if(next != null) {
                    currentState = next.get(0);
                    if(next.get(1).length() > 1) {
                        if(next.get(1).charAt(next.get(1).length() - 1) == stack.charAt(0))
                            stack = next.get(1).substring(0, next.get(1).length() - 1) + stack;
                        else
                            stack = next.get(1).substring(0, next.get(1).length()) + stack.substring(1, stack.length());
                    }
                    else if(next.get(1).compareTo("$") == 0) {
                        if(stack.length() == 1)
                            stack = "$";
                        else
                            stack = stack.substring(1, stack.length());
                    }
                    output.append(currentState + "#" + stack + "|");
                }
            }

            if(output.charAt(output.length() - 1) != '0') {
                if (currentState.compareTo(finalState) == 0)
                    output.append(1);
                else {
                    output.append(0);
                }
            }
            output.append("\n");
        }


        return output.toString().strip().trim();
    }
}

public class SimPa {
    public static void main(String[] args) {
//        testOneFile("lab3_primjeri/test20/primjer.in");
//        tester();
        userInput();
    }

    static void tester() {
        String path = "lab2_primjeri[1]/test";
        String folder = "";

        int[] counter = {0, 0};

        PushdownAutomaton machine;

        for(Integer i = 1; i <= 25; i++) {
            try {
                path = "lab3_primjeri/test";
                if (i < 10)
                    folder = "0" + i;
                else
                    folder = i.toString();

                path += folder;
                String pathInput = path + "/primjer.in";
                String pathOutput = path + "/primjer.out";

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

                machine = new PushdownAutomaton(input.toString());
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

        PushdownAutomaton machine = new PushdownAutomaton(input.toString());
        System.out.println(machine);
        System.out.println(machine.output().trim().strip());

        /*
        System.out.println(machine);
        machine.removeUnreachableStates(new ArrayList<>(Arrays.asList(machine.start)));
        System.out.println(machine);
        */
        //System.out.println("\n" + machine.output());
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

        PushdownAutomaton machine = new PushdownAutomaton(input);

        System.out.println(machine.output());
    }
}