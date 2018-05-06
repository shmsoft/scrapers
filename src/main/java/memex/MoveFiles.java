package memex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Created by nehaojha on 24/04/18.
 */
public class MoveFiles {

    public static void main(String[] args) throws IOException, InterruptedException {
        String fileName = "file-list.txt";
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            stream.forEach(currFile -> {
                try {
                    String currFileName = currFile.substring(currFile.lastIndexOf("/") + 1);
                    System.out.println("Working with file : " + currFileName);

                    System.out.println("Downloading " + currFile);
                    Process downloadProcess = Runtime.getRuntime().exec("s3cmd -c .s3cfg-memex get " + currFile);
                    logProcess(downloadProcess);
                    System.out.println("Downloading Done.");

                    System.out.println("Uploading " + currFile);
                    Process uploadProcess = Runtime.getRuntime().exec("s3cmd -c .s3cfg put " + currFileName + " s3://shmsoft/memex-data/" + currFile.substring(currFile.indexOf("escorts") + 8, currFile.lastIndexOf("/") + 1));
                    logProcess(uploadProcess);
                    System.out.println("Uploading Done.");

                    System.out.println("Removing " + currFileName);
                    Process removeFileProc = Runtime.getRuntime().exec("rm " + currFileName);
                    logProcess(removeFileProc);
                    System.out.println("File removed.");
                    System.out.println("=============================================================================\n\n");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            });

        }
    }

    public static void logProcess(Process process) throws IOException, InterruptedException {
        process.waitFor();
        BufferedReader buf = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = "";
        while ((line = buf.readLine()) != null) {
            System.out.println(line);
        }
    }
}

