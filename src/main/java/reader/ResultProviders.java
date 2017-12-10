package reader;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by hojha on 23/08/17.
 */
public class ResultProviders {

    private static List<Integer> resultProviders = new ArrayList<>();

    public static List<Integer> getResultProviders() {
        try (Scanner scanner = new Scanner(ResultProviders.class.getClassLoader().getResourceAsStream("result"))) {
            while (scanner.hasNext()) {
                resultProviders.add(Integer.valueOf(scanner.nextLine().split("\\|")[0].trim()));
            }
        }
        List<Integer> integerList = new ArrayList<>(resultProviders.size());
        resultProviders.forEach(p -> {
            integerList.add(p);
        });
        return integerList;
    }
}
