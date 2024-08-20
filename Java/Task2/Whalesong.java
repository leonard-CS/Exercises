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
                    if (parts.length == 2) hear(map, parts[1]);
                    break;
                case "augment":
                    if (parts.length == 2) augment(map, parts[1]);
                    break;
                case "clarify":
                    if (parts.length == 2) clarify(map, parts[1]);
                    break;
                case "normalise":
                    if (parts.length == 2) normalise(map, parts[1]);
                    break;
                case "reverse":
                    if (parts.length == 2) reverse(map, parts[1]);
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

    private static void reverse(Map<String, String> map, String name) {
        if (map.containsKey(name)) {
            System.out.println("Reversed song: " + reverseString(map.get(name)));
        } else {
            System.out.println("Error: No song to reverse.");
        }
    }

    private static String reverseString(String string) {
        StringBuilder sb = new StringBuilder(string);
        return sb.reverse().toString();
    }

    private static void normalise(Map<String, String> map, String name) {
        if (map.containsKey(name)) {
            System.out.println("Normalised song: " + map.get(name).toLowerCase());
        } else {
            System.out.println("Error: No song to normalise.");
        }
    }

    private static void clarify(Map<String, String> map, String name) {
        if (map.containsKey(name)) {
            System.out.println("Clarified song: " + replaceVowelsWithUpperCase(map.get(name)));
        } else {
            System.out.println("Error: No song to clarify.");
        }
    }

    private static String replaceVowelsWithUpperCase(String string) {
        String vowels = "aeiou";
        char[] charArray = string.toCharArray();

        for (int i = 0; i < charArray.length; i++) {
            if (vowels.indexOf(charArray[i]) != -1) {
                charArray[i] = Character.toUpperCase(charArray[i]);
            }
        }

        return new String(charArray);
    }

    private static void augment(Map<String, String> map, String name) {
        if (map.containsKey(name)) {
            System.out.println("Augmented song: " + map.get(name) + map.get(name));
        } else {
            System.out.println("Error: No song to augment.");
        }
    }

    private static void hear(Map<String, String> map, String name) {
        if (map.containsKey(name)) {
            System.out.println("Song for "+ name + ": " + map.get(name));
        } else {
            System.out.println("Error: No song found for this whale.");
        }
    }

    private static void play(Map<String, String> map, String name) {
        if (map.containsKey(name)) {
            System.out.println(map.get(name));
        } else {
            System.out.println("Error: No song found for this whale.");
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
