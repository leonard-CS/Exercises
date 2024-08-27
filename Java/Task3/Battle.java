import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Battle {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java Skibidi alliance.txt skibidi.txt");
            return;
        }

        File alliance = new File(args[0]);
        File skibidi = new File(args[1]);
        
        if (!alliance.exists()) {
            System.err.printf("Error: File '%s' not found. Please check the path and try again.%n", alliance.getName());
            return;
        }
        
        if (!skibidi.exists()) {
            System.err.printf("Error: File '%s' not found. Please check the path and try again.%n", skibidi.getName());
            return;
        }

        Set<String> validAbilities = new HashSet<>();
        validAbilities.add("PowerSurge");
        validAbilities.add("BopShield");
        validAbilities.add("EnergyBoost");
        validAbilities.add("ZapTrap");
        validAbilities.add("TitanBoost");

        // Read the contents of the alliance file
        try (BufferedReader br = new BufferedReader(new FileReader(alliance))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\s+");
                if (parts.length != 2) {
                    continue;
                }

                String ability = parts[0];
                if (!validAbilities.contains(ability)) {
                    System.err.printf("Warning: Unknown ability '%s' detected in '%s'. Ignored.", ability, alliance.getName());
                    continue;
                }
                
                try {
                    int parameter = Integer.parseInt(parts[1]);
                } catch (NumberFormatException e) {
                    System.err.printf("Error: Invalid parameter for '%s' in line '%s'. Line skipped.%n", ability, line);
                }
            }
        } catch (IOException e) {
            System.err.printf("Error reading file '%s': %s%n", alliance.getName(), e.getMessage());
        }
    }
}
