package reader;

import java.util.*;

/**
 * Created by hojha on 23/08/17.
 */
public class Provider {

    private static Set<Integer> providers = new HashSet<>();

    static {
        boolean skipHeader = true;
        try (Scanner scanner = new Scanner(Provider.class.getClassLoader().getResourceAsStream("provider_id.csv"))) {
            while (scanner.hasNext()) {
                if (skipHeader) {
                    skipHeader = false;
                    scanner.nextLine();
                } else {
                    providers.add(Integer.valueOf(scanner.nextLine().split(",")[0]));
                }
            }
        }
    }

    public static List<Integer> getProviders() {
        List<Integer> integerList = new ArrayList<>(providers.size());
        providers.forEach(p -> {
            if (p > 7134) {
                integerList.add(p);
            }
        });
        integerList.sort(Integer::compare);
        return integerList;
    }
}
