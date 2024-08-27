import java.util.*;

public class Solution {
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
    }

    private static void processCommand(Map<String, String> map, String line) {
        String[] parts = line.split(" ");
        switch (parts[0]) {
            case "add":
                if (parts.length == 3)
                    add(map, parts[1], parts[2]);
                break;
            case "play":
                if (parts.length == 2)
                    play(map, parts[1]);
                break;
            case "hear":
                if (parts.length == 2)
                    hear(map, parts[1]);
                break;
            case "augment":
                if (parts.length == 2)
                    augment(map, parts[1]);
                break;
            case "clarify":
                if (parts.length == 2)
                    clarify(map, parts[1]);
                break;
            case "normalise":
                if (parts.length == 2)
                    normalise(map, parts[1]);
                break;
            case "reverse":
                if (parts.length == 2)
                    reverse(map, parts[1]);
                break;
            case "mirror":
                if (parts.length == 2)
                    mirror(map, parts[1]);
                break;
            case "uppercase":
                if (parts.length == 2)
                    uppercase(map, parts[1]);
                break;
            case "pattern":
                if (parts.length == 2)
                    pattern(map, parts[1]);
                break;
            case "echo":
                if (parts.length == 2)
                    echo(map, parts[1]);
                break;
            case "compress":
                if (parts.length == 2)
                    compress(map, parts[1]);
                break;
            case "expand":
                if (parts.length == 2)
                    expand(map, parts[1]);
                break;
            case "invert":
                if (parts.length == 2)
                    invert(map, parts[1]);
                break;
        }
    }

    private static void invert(Map<String, String> map, String name) {
        if (map.containsKey(name)) {
            String song = invertCase(map.get(name));
            System.out.println("Inverted song: " + song);
            map.put(name, song);
        } else {
            System.out.println("Error: No song to invert.");
        }
    }

    private static String invertCase(String string) {
        StringBuilder sb = new StringBuilder();

        char[] charArray = string.toCharArray();

        for (char c : charArray) {
            if (Character.isUpperCase(c)) {
                sb.append(Character.toLowerCase(c));
            } else if (Character.isLowerCase(c)) {
                sb.append(Character.toUpperCase(c));
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    private static void expand(Map<String, String> map, String name) {
        if (map.containsKey(name)) {
            String song = doubleConsonant(map.get(name));
            System.out.println("Expanded song: " + song);
            map.put(name, song);
        } else {
            System.out.println("Error: No song to expand.");
        }
    }

    private static String doubleConsonant(String string) {
        String vowels = "aeiou";
        StringBuilder sb = new StringBuilder();

        char[] charArray = string.toCharArray();

        for (char c : charArray) {
            sb.append(c);
            // Check if the character is not a vowel
            if (vowels.indexOf(Character.toLowerCase(c)) == -1) {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    private static void compress(Map<String, String> map, String name) {
        if (map.containsKey(name)) {
            String song = removeVowels(map.get(name));
            System.out.println("Compressed song: " + song);
            map.put(name, song);
        } else {
            System.out.println("Error: No song to compress.");
        }
    }

    private static String removeVowels(String string) {
        String vowels = "aeiou";
        StringBuilder sb = new StringBuilder();

        char[] charArray = string.toCharArray();

        for (char c : charArray) {
            // Check if the character is not a vowel
            if (vowels.indexOf(Character.toLowerCase(c)) == -1) {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    private static void echo(Map<String, String> map, String name) {
        if (map.containsKey(name)) {
            String song = repeatLastThreeCharacter(map.get(name), 5);
            System.out.println("Echoed song: " + song);
            map.put(name, song);
        } else {
            System.out.println("Error: No song to pattern.");
        }
    }

    private static String repeatLastThreeCharacter(String string, int times) {
        String lastThree = string.substring(string.length() - 3);

        StringBuilder sb = new StringBuilder(string);
        for (int i = 0; i < times; i++) {
            sb.append(lastThree);
        }

        return sb.toString();
    }

    private static void pattern(Map<String, String> map, String name) {
        if (map.containsKey(name)) {
            String song = replaceSecondCharacter(map.get(name));
            System.out.println("Patterned song: " + song);
            map.put(name, song);
        } else {
            System.out.println("Error: No song to pattern.");
        }
    }

    private static String replaceSecondCharacter(String string) {
        char[] charArray = string.toCharArray();

        for (int i = 1; i < charArray.length; i += 2) {
            charArray[i] = '*';
        }

        return new String(charArray);
    }

    private static void uppercase(Map<String, String> map, String name) {
        if (map.containsKey(name)) {
            String song = map.get(name).toUpperCase();
            System.out.println("Uppercase song: " + song);
            map.put(name, song);
        } else {
            System.out.println("Error: No song to uppercase.");
        }
    }

    private static void mirror(Map<String, String> map, String name) {
        if (map.containsKey(name)) {
            String song = map.get(name) + reverseString(map.get(name));
            System.out.println("Mirrored song: " + song);
            map.put(name, song);
        } else {
            System.out.println("Error: No song to mirror.");
        }
    }

    private static void reverse(Map<String, String> map, String name) {
        if (map.containsKey(name)) {
            String song = reverseString(map.get(name));
            System.out.println("Reversed song: " + song);
            map.put(name, song);
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
            String song = map.get(name).toLowerCase();
            System.out.println("Normalised song: " + song);
            map.put(name, song);
        } else {
            System.out.println("Error: No song to normalise.");
        }
    }

    private static void clarify(Map<String, String> map, String name) {
        if (map.containsKey(name)) {
            String song = replaceVowelsWithUpperCase(map.get(name));
            System.out.println("Clarified song: " + song);
            map.put(name, song);
        } else {
            System.out.println("Error: No song to clarify.");
        }
    }

    private static String replaceVowelsWithUpperCase(String string) {
        String vowels = "aeiou";
        char[] charArray = string.toCharArray();

        for (int i = 0; i < charArray.length; i++) {
            // Check if the character is a vowel
            if (vowels.indexOf(charArray[i]) != -1) {
                charArray[i] = Character.toUpperCase(charArray[i]);
            }
        }

        return new String(charArray);
    }

    private static void augment(Map<String, String> map, String name) {
        if (map.containsKey(name)) {
            String song = map.get(name) + map.get(name);
            System.out.println("Augmented song: " + song);
            map.put(name, song);
        } else {
            System.out.println("Error: No song to augment.");
        }
    }

    private static void hear(Map<String, String> map, String name) {
        if (map.containsKey(name)) {
            System.out.println("Song for " + name + ": " + map.get(name));
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
