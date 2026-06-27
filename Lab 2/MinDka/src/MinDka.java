import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;

class StateMachine {
    public List<String> states;
    public List<String> symbols;
    public List<String> finalStates = new ArrayList<>();
    public String start;
    public Map<List<String>, String> transitionFunctions = new HashMap<List<String>, String>();

    @Override
    public String toString() {
        return "StateMachine{" +
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
                case 1: states = new ArrayList<>(Arrays.asList(t.split(",")));
                    break;
                case 2: symbols = Arrays.asList(t.split(","));
                    break;
                case 3: finalStates = new ArrayList<>(Arrays.asList(t.split(",")));
                    break;
                case 4: start = t;
                    break;
                default:
                    tmp = t.split("->");
                    transitionFunctions.put(Arrays.asList(tmp[0].split(",")), tmp[1]);
                    break;
            }
            i++;
        }
    }

    void removeUnreachableStates(List<String> reachableStates) {
        String nexState;
        int size = reachableStates.size();
        String state = "";

        for(String symbol: symbols) {
            for(int i = 0; i < size; i++) {
                state = reachableStates.get(i);
                nexState = transitionFunctions.get(Arrays.asList(state, symbol));
                if(!reachableStates.contains(nexState)) {
                    reachableStates.add(nexState);
                    removeUnreachableStates(reachableStates);
                    return;
                }
            }
        }
        reachableStates.sort(Comparator.naturalOrder());
        List<String> unreachableStates = new ArrayList<>(states);
        unreachableStates.removeAll(reachableStates);
        states = new ArrayList<>(reachableStates);
        finalStates.removeAll(unreachableStates);

        size = transitionFunctions.size();
        for(String symbol: symbols) {
            for(String un: unreachableStates) {
                transitionFunctions.remove(Arrays.asList(un, symbol));
            }
            for(String s: states) {
                if(unreachableStates.contains(transitionFunctions.get(Arrays.asList(s, symbol))))
                    transitionFunctions.remove(Arrays.asList(s, symbol));
            }
        }
    }
    String output() {
        removeUnreachableStates(new ArrayList<>(Arrays.asList(start)));
        if(states.size() > 1) {
            Group group = new Group(new HashSet<>(finalStates));
            ArrayList<String> tmp = new ArrayList<>(states);
            tmp.removeAll(finalStates);
            group.addGroup(new HashSet<>(tmp));

            group.groupfy();

            List<TreeSet<String>> groupStates = group.get();
            for (Set<String> sameStates : groupStates) {
                sameStates.stream().sorted();
                if (sameStates.size() > 1) {
                    if(sameStates.contains(start))
                        start = sameStates.stream().findFirst().get();

                    for(List<String> key: transitionFunctions.keySet()) {
                        if(sameStates.contains(transitionFunctions.get(key)))
                            transitionFunctions.put(key, sameStates.stream().findFirst().get());
                    }

                    sameStates.remove(sameStates.stream().findFirst().get());
                    transitionFunctions.values().removeAll(sameStates);
                    for (String state : sameStates) {
                        states.remove(state);
                        finalStates.remove(state);
                        for(String symbol: symbols) {
                            transitionFunctions.remove(Arrays.asList(state, symbol));
                        }
                    }
                }
            }
        }

        List<List<String>> sortedKeys;
        sortedKeys = new ArrayList<>(transitionFunctions.keySet());

        // If numbers are equal, compare by char
        Collections.sort(sortedKeys, Comparator.comparing((List<String> list) -> list.get(0)).thenComparing(list -> list.get(1)));

        String output = (states
                + "\n" + symbols
                + "\n" + finalStates
                + "\n" + start
                + "\n" )
                .replace("[", "")
                .replace("]", "")
                .replace("{", "")
                .replace("}", "")
                .replace(" ", "")
                .replace("=", "->");

        for(List<String> key: sortedKeys) {
            output += key.toString().replace("[", "").replace("]", "").replace(" ", "") + "->" + transitionFunctions.get(key) + "\n";
        }

        return output;
    }

    public class Group {
        Map<Integer, Set<String>> groups;
        int groupStart = 0;
        int groupEnd;

        public int size() {
            return groupEnd;
        }

        public List<TreeSet<String>> get(){
            List<TreeSet<String>> list = new ArrayList<>();
            for(int i = 0; i <= groupEnd; i++)
                list.add(new TreeSet<>(groups.get(i)));
            return list;
        }

        public Group(Set<String> states) {
            this.groups = new HashMap<Integer, Set<String>>();
            this.groups.put(groupStart, states);
            groupEnd = groupStart;
        }
        public Group() {}

        public void addGroup(Set<String> states) {
            this.groups.put(++groupEnd, states);
        }

        public List<Integer> getGroups(String state) {
            List<Integer> currentGroups = new ArrayList<>();
            for(String symbol: symbols) {
                String currentState = transitionFunctions.get(new ArrayList<>(Arrays.asList(state, symbol)));
                for(int i = groupStart; i <= groupEnd; i++)
                    if(groups.get(i).contains(currentState)) {
                        currentGroups.add(i);
                        break;
                    }
            }
            return currentGroups;
        }

        public void groupfy() {
            int currentSize = 0;
            int newSize = 0;

            do {
                currentSize = this.size();
                this.groupfyInternal();
                newSize = this.size();
            }while(currentSize != newSize);
        }

        void groupfyInternal() {
            int newEnd = groupEnd;
            for(int i = 0; i <= groupEnd; i++) {
                List<Integer> firstInTheGroup = getGroups(groups.get(0).stream().findFirst().get());
                Set<String> newGroup = new HashSet<>();

                for(String state: groups.get(i)) {
                    if(firstInTheGroup.hashCode() != getGroups(state).hashCode())
                        newGroup.add(state);
                }
                groups.get(i).removeAll(newGroup);
                if(groups.get(i).isEmpty())
                    groups.put(i, newGroup);
                else if(!newGroup.isEmpty())
                    groups.put(++newEnd, newGroup);

            }
            groupEnd = newEnd;
        }
    }
}

public class MinDka {
    public static void main(String[] args) {
//      testOneFile("lab2_primjeri[1]/test14/t.ul");
        //tester();
        userInput();
    }

    static void tester() {
        String path = "lab2_primjeri[1]/test";
        String folder = "";
        StateMachine machine;

        for(Integer i = 1; i <= 14; i++) {
            path = "lab2_primjeri[1]/test";
            if (i < 10)
                folder = "0" + i;
            else
                folder = i.toString();

            path += folder;
            String pathInput = path + "/t.ul";
            String pathOutput = path + "/t.iz";

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

            machine = new StateMachine(input.toString());
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

            String correct = input.toString();
//                String myOutput = machine.output();

            System.out.print(i + "\t");
            if (correct.compareTo(myOutput) == 0)
                System.out.println("\u001B[32m" + "Correct " + "\u001B[0m");
            else {
                System.out.println("\u001B[31m" + "Wrong" + "\u001B[0m");
                System.out.println(correct);
                System.out.println(myOutput);
            }
        }
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

        StateMachine machine = new StateMachine(input.toString());
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

        StateMachine machine = new StateMachine(input);

        System.out.println(machine.output());
    }
}