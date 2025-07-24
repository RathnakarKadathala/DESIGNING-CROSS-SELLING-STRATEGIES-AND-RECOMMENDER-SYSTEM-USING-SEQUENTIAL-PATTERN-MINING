import java.io.IOException;
import java.util.*;

public class Main8FinalFinal {

    private static List<Integer> normalizePrefix(List<Integer> prefix) {
        List<Integer> sorted = new ArrayList<>(prefix);
        Collections.sort(sorted);
        return sorted;
    }

    public static void main(String[] args) throws IOException {
        String inputPath = args[0];
        double minUtilOccupancy = Double.parseDouble(args[1]);
        double minSup = Double.parseDouble(args[2]);
        String outputPath = "results.txt";

        System.out.println("\n=============  HUSPM ALGORITHM - STARTS ==========\n");

        AHUS8FinalFinal ahus = new AHUS8FinalFinal();
        ahus.runAlgorithm(inputPath, outputPath, minUtilOccupancy, minSup);

        System.out.println("\n--------------- STATISTICS ---------------------");
        System.out.println("MINIMUM UTILITY OCCUPANCY THRESHOLD: " + ahus.minUtility);
        System.out.println("MINIMUM SUPPORT THRESHOLD: " + ahus.minSupport);
        System.out.println("HIGH UTILITY Occupancy PATTERN COUNT : " + ahus.totalPatterns);
        System.out.println("TOTAL EXECUTION TIME : " + ahus.timeElapsed + " ms");
        System.out.println("--------------------------------------------------");
        System.out.println("\n==================================================");

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("\nEnter prefix sequence (space-separated integers) or type 'exit' to quit: ");
            String line = scanner.nextLine();
            if (line.equalsIgnoreCase("exit")) break;

            try {
                String[] tokens = line.trim().split("\\s+");
                List<Integer> userPrefix = new ArrayList<>();
                for (String token : tokens) {
                    userPrefix.add(Integer.parseInt(token));
                }
                userPrefix = normalizePrefix(userPrefix);

                Set<Integer> recs = getRecommendations(userPrefix, ahus.CHUSP_map);
                // Exclude items already in the prefix from recommendations
                recs.removeAll(userPrefix);
                System.out.println("Recommendations for prefix " + userPrefix + ": " + recs);
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter space-separated integers.");
            }
        }
        scanner.close();
    }

    public static Set<Integer> getRecommendations(List<Integer> prefix, Map<Integer, List<ArrayList<Integer>>> chuspMap) {
        Set<Integer> recommendations = new HashSet<>();

        for (List<ArrayList<Integer>> patterns : chuspMap.values()) {
            for (ArrayList<Integer> pattern : patterns) {
                List<Integer> cleanPattern = new ArrayList<>();
                for (int item : pattern) cleanPattern.add(item == 0 ? -1 : item);

                if (isSubsequence(prefix, cleanPattern)) {
                    int startIndex = indexAfterPrefix(prefix, cleanPattern);
                    for (int i = startIndex; i < cleanPattern.size(); i++) {
                        int item = cleanPattern.get(i);
                        if (item > 0) recommendations.add(item);
                    }
                }
            }
        }
        return recommendations;
    }

    private static boolean isSubsequence(List<Integer> prefix, List<Integer> pattern) {
        int i = 0, j = 0;
        while (i < prefix.size() && j < pattern.size()) {
            if (prefix.get(i).equals(pattern.get(j))) i++;
            j++;
        }
        return i == prefix.size();
    }

    private static int indexAfterPrefix(List<Integer> prefix, List<Integer> pattern) {
        int i = 0, j = 0;
        while (i < prefix.size() && j < pattern.size()) {
            if (prefix.get(i).equals(pattern.get(j))) i++;
            j++;
        }
        return j;
    }
}
