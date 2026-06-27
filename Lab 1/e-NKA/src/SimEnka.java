import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;
import java.io.*;

class StateMachine{
    List<List<String>> input = new ArrayList<>();
    List<String> states, symbols, start = new ArrayList<>();
    String finalStates;
    Map<List<String>, List<String>> transitionFunctions = new HashMap<List<String>, List<String>>();

    @Override
    public String toString() {
        return "StateMachine{" +
                "input=" + input +
                "\n states=" + states +
                "\n symbols=" + symbols +
                "\n finalStates='" + finalStates + '\'' +
                "\n start='" + start + '\'' +
                "\n transitionFunctions=" + transitionFunctions +
                '}';
    }

    StateMachine(String text) {
        int i = 1;
        String[] tmp;
        for (String t: text.split("\n")) {
            switch (i){
                case 1:
                    input = Arrays.stream(t.split("\\|")).map(a -> Arrays.asList(a.split(","))).collect(Collectors.toList());
                    break;
                case 2: states = Arrays.asList(t.split(","));
                    break;
                case 3: symbols = Arrays.asList(t.split(","));
                    break;
                case 4: finalStates = t;
                    break;
                case 5: start.add(t);
                    break;
                default:
                    tmp = t.split("->");
                    transitionFunctions.put(Arrays.asList(tmp[0].split(",")), Arrays.asList(tmp[1].split(",")));
                    break;
            }
            i++;
        }

        if(transitionFunctions.get(Arrays.asList(start.get(0), "$")) != null) {
            start.addAll(removeEpsilon(start, new HashSet<>()));
        }
        start = start.stream().distinct().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
    }

    List<String> removeEpsilon(List<String> states, Set<String> visitedStates) {
        Set<String> epsilonStates = new HashSet<>();
        if(visitedStates.containsAll(states))
            return states;
        else
            visitedStates.addAll(states);

        for (String state: states) {
            if(transitionFunctions.get(Arrays.asList(state, "$")) != null) {
                epsilonStates.addAll(transitionFunctions.get(Arrays.asList(state, "$")));
                epsilonStates.remove(state);
                epsilonStates.remove("#");

                epsilonStates.addAll(removeEpsilon(new ArrayList<>(epsilonStates), visitedStates));
            }
        }
        states.addAll(new ArrayList<>(epsilonStates));
        return states;
    }


    String output() {
        List<String> currentStates;
        List<String> tmpCurrentStates;
        List<String> epsilonStates;
        String output = new String();

        for(List<String> inputSymbols: input) {
            currentStates = new ArrayList<>(start);
            tmpCurrentStates = new ArrayList<>(currentStates);
            output += currentStates.toString();

            for(String symbol: inputSymbols) {
                for(String currentState: tmpCurrentStates) {
                    currentStates.remove(currentState);

                    if(transitionFunctions.get(Arrays.asList(currentState, symbol)) != null)
                        currentStates.addAll(transitionFunctions.get(Arrays.asList(currentState, symbol)));
                    else
                        currentStates.add("#");
                }

                tmpCurrentStates = new ArrayList<>(currentStates);
                for (String currentState : tmpCurrentStates) {
                    epsilonStates = transitionFunctions.get(Arrays.asList(currentState, "$"));
                    if(epsilonStates != null) {
                        currentStates.addAll(epsilonStates);
                        currentStates.addAll(removeEpsilon(new ArrayList<>(epsilonStates), new HashSet<>(/*currentStates*/)));
                    }
                }

                currentStates = currentStates
                        .stream()
                        .distinct()
                        .collect(Collectors.toList());

                currentStates.sort(Comparator.naturalOrder());

                if(currentStates.size() > 1)
                    currentStates.removeAll(Collections.singleton("#"));

                output += currentStates
                        .stream()
                        .distinct()
                        .collect(Collectors.toList());

                tmpCurrentStates = new ArrayList<>(currentStates);
            }
            output += "\n";
        }
        output = output.replaceAll("]\\[", "\\|")
                .replaceAll(", ", ",")
                .replace('[', '|')
                .replace(']', ' ')
                .replaceFirst("\\|", "")
                .replaceAll("[\\n]\\|", "\n")
                .replace(" ", "")
                .strip();

        return output;
    }
}

public class SimEnka {
    public static void main(String[] args) {
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

        StateMachine machine = new StateMachine(input);

        System.out.println(machine.output());
    }
}