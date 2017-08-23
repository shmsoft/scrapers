package reader;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Scanner;

/**
 * Created by hojha on 23/08/17.
 */
public class Reviewers {

    private static Queue<String> reviewers = new ArrayDeque<>();

    static {
        try (Scanner scanner = new Scanner(Reviewers.class.getClassLoader().getResourceAsStream("reviewer_only.csv"))) {
            while (scanner.hasNext()) {
                reviewers.add(scanner.nextLine());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Queue<String> getReviewers() {
        return reviewers;
    }
}
