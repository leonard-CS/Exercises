import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Battle {

    private static final Set<String> VALID_ABILITIES = new HashSet<>();

    static {
        VALID_ABILITIES.add("PowerSurge");
        VALID_ABILITIES.add("BopShield");
        VALID_ABILITIES.add("EnergyBoost");
        VALID_ABILITIES.add("ZapTrap");
        VALID_ABILITIES.add("TitanBoost");
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java Battle <alliance_file> <skibidi_file>");
            return;
        }

        File allianceFile = new File(args[0]);
        File skibidiFile = new File(args[1]);

        if (!allianceFile.exists()) {
            System.err.printf("Error: File '%s' not found. Please check the path and try again.%n",
                    allianceFile.getName());
            return;
        }

        if (!skibidiFile.exists()) {
            System.err.printf("Error: File '%s' not found. Please check the path and try again.%n",
                    skibidiFile.getName());
            return;
        }

        List<Integer> alliancePowers = new ArrayList<>();
        List<Integer> skibidiPowers = new ArrayList<>();

        Map<String, Integer> allianceAbilities = new LinkedHashMap<>();
        Map<String, Integer> skibidiAbilities = new LinkedHashMap<>();

        processFile(allianceFile, alliancePowers, allianceAbilities);
        processFile(skibidiFile, skibidiPowers, skibidiAbilities);

        // Further processing with alliancePowers and skibidiPowers
        // System.out.println("Alliance Abilities:");
        // for (Map.Entry<String, Integer> entry : allianceAbilities.entrySet()) {
        // System.out.println(entry.getKey() + ": " + entry.getValue());
        // }
        // System.out.println("Skibidi Abilities:");
        // for (Map.Entry<String, Integer> entry : skibidiAbilities.entrySet()) {
        // System.out.println(entry.getKey() + ": " + entry.getValue());
        // }

        applyAbilities(alliancePowers, allianceAbilities);
        applyAbilities(skibidiPowers, skibidiAbilities);

        int allianceTotal = alliancePowers.stream()
                .mapToInt(Integer::intValue) // Convert Integer to int
                .sum();
        int skibidiTotal = skibidiPowers.stream()
                .mapToInt(Integer::intValue) // Convert Integer to int
                .sum();

        skibidiTotal = applyZapTrap(allianceAbilities, skibidiTotal);
        allianceTotal = applyZapTrap(skibidiAbilities, allianceTotal);

        showResult(allianceTotal, skibidiTotal);
    }

    private static void showResult(int allianceTotal, int skibidiTotal) {
        if (allianceTotal > skibidiTotal) {
            System.out.println("Alliance wins");
        } else if (skibidiTotal > allianceTotal) {
            System.out.println("Skibidi wins");
        } else {
            System.out.println("Draw");
        }
    }

    private static int applyZapTrap(Map<String, Integer> abilities, int opponentTotal) {
        int value = abilities.getOrDefault("ZapTrap", 0);
        return opponentTotal * (100 - value) / 100;
    }

    private static void applyAbilities(List<Integer> powers, Map<String, Integer> abilities) {
        for (Map.Entry<String, Integer> entry : abilities.entrySet()) {
            String ability = entry.getKey();
            int value = entry.getValue();

            switch (ability) {
                case "PowerSurge":
                    powers.set(0, powers.get(0) * (1 + value) / 100);
                    break;
                case "BopShield":
                    for (int i = 0; i < powers.size(); i++) {
                        powers.set(i, powers.get(i) * (1 + value) / 100);
                    }
                    break;
                case "EnergyBoost":
                    for (int i = 0; i < powers.size(); i++) {
                        powers.set(i, powers.get(0) + value);
                    }
                    break;
                case "TitanBoost":
                    powers.set(powers.size() - 1, powers.get(powers.size() - 1) * value);
                    break;
            }
        }
    }

    private static void processFile(File file, List<Integer> powers, Map<String, Integer> abilities) {
        boolean inAbilitiesSection = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split("\\s+");

                // Check if we have at least two parts in the line
                if (parts.length < 2) {
                    continue;
                }

                String firstPart = parts[0];
                String secondPart = parts[1];

                // Check for the start of the abilities section
                if (VALID_ABILITIES.contains(firstPart)) {
                    inAbilitiesSection = true;
                }

                if (!inAbilitiesSection) {
                    // Processing characters and powers
                    try {
                        int value = Integer.parseInt(secondPart);
                        powers.add(value);
                    } catch (NumberFormatException e) {
                        System.err.printf("Error: Invalid parameter for '%s' in line '%s'. Line skipped.%n", firstPart,
                                line);
                    }
                } else {
                    // Processing abilities and values
                    if (!VALID_ABILITIES.contains(firstPart)) {
                        System.err.printf("Warning: Unknown ability '%s' detected in '%s'. Ignored.%n", firstPart,
                                file.getName());
                        continue;
                    }

                    try {
                        int value = Integer.parseInt(secondPart);
                        abilities.put(firstPart, value);
                    } catch (NumberFormatException e) {
                        System.err.printf("Error: Invalid parameter for '%s' in line '%s'. Line skipped.%n", firstPart,
                                line);
                    }
                }
            }
        } catch (IOException e) {
            System.err.printf("Error reading file '%s': %s%n", file.getName(), e.getMessage());
        }
    }

}
