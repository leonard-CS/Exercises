import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class DeadpoolAndWolverine {
    
    public static void main(String[] args) {
        int targetSum = Integer.parseInt(args[0]);
        
        Scanner sc = new Scanner(System.in);
        TreeMap<String, Integer> treeMap = new TreeMap<>();
        
        System.out.println();
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line.trim().equals("EOF")) {
                break;
            }
            
            TreeMap<String, Integer> newMap = createMap(line);
            if (newMap != null) {
                treeMap.putAll(newMap);
            }
        }
        System.out.println();
        sc.close();

        List<Integer> scores = new ArrayList<>(treeMap.values());
        Collections.sort(scores);

        List<Integer> subScores = sumUntilTarget(scores, targetSum);

        List<String> subCharacters = findKeysForScores(treeMap, subScores);
        Collections.sort(subCharacters);
        System.out.println("Selected characters: " + subCharacters);
        System.out.println("Total expected revenue: " + sumList(subScores) + " million dollars");
    }

    private static TreeMap<String, Integer> createMap(String line) {
        boolean valid = true;
        String[] tokens = line.split(" ");
        
        if (tokens.length % 2 != 0) {
            valid = false;
        }
        
        TreeMap<String, Integer> resultMap = new TreeMap<>();
        
        for (int i = 0; i < tokens.length; i += 2) {
            String name = tokens[i];
            try {
                int score = Integer.parseInt(tokens[i + 1]);
                if (score >= 0) {
                    resultMap.put(name, score);
                } else {
                    valid = false;
                }
            } catch (NumberFormatException e) {
                valid = false;
            }
        }
        
        if (!valid) {
            System.out.println("Input '" + line + "' is not valid. Make sure your input is valid.");
            return null;
        }

        return resultMap;
    }
    
    private static List<Integer> sumUntilTarget(List<Integer> scores, int targetSum) {
        List<Integer> result = new ArrayList<>();
        int currentSum = 0;
        
        for (int score : scores) {
            if (currentSum + score > targetSum) {
                break;
            }
            result.add(score);
            currentSum += score;
        }
        
        return result;
    }

    private static List<String> findKeysForScores(TreeMap<String, Integer> treeMap, List<Integer> scores) {
        List<String> characters = new ArrayList<>();
        
        for (int score : scores) {
            for (Map.Entry<String, Integer> entry : treeMap.entrySet()) {
                String key = entry.getKey();
                Integer value = entry.getValue();
                if (value == score) {
                    characters.add(key);
                    treeMap.remove(key);
                    break;
                }
            }
        }
        return characters;
    }
    
    private static int sumList(List<Integer> list) {
        int sum = 0;
        for (int number : list) {
            sum += number;
        }
        return sum;
    }
}
