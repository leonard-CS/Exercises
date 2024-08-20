package Java.Task2;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class Whalesong {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Map<String, String> map = new HashMap<>();

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.trim().equals("exit")) {
                break;
            }

            processCommand(map, line);
        }

        scanner.close();
        System.out.println(map);
    }

    private static void processCommand(Map<String, String> map, String line) {
        String[] parts = line.split(" ");
            switch (parts[0]) {
                case "add":
                    if (parts.length == 3) add(map, parts[1], parts[2]);
                    break;
                case "play":
                    if (parts.length == 2) play(map, parts[1]);
                    break;
                case "hear":
                    break;
                case "augment":
                    break;
                case "clarify":
                    break;
                case "normalise":
                    break;
                case "reverse":
                    break;
                case "mirror":
                    break;
                case "uppercase":
                    break;
                case "pattern":
                    break;
                case "echo":
                    break;
                case "compress":
                    break;
                case "expand":
                    break;
                case "invert":
                    break;
            }
    }

    private static void play(Map<String, String> map, String name) {
        if (!map.containsKey(name)) {
            System.out.println("Error: No song found for this whale.");
        } else {
            System.out.println(map.get(name));
        }
    }

    private static void add(Map<String, String> map, String name, String song) {
        if (map.containsKey(name)) {
            System.out.println("Error: Whale already has a song.");
        } else {
            map.put(name, song);
            System.out.println("Song added for " + name);
        }
    }
}
