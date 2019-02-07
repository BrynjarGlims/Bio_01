import java.io.BufferedWriter;
import java.io.File;

public class FileWriter {

    public static void writer(Genome genome){
        BufferedWriter writer = null;
        try {
            //create a temporary file
            File logFile = new File("ourSolution/");

            // This will output the full path where the file will be written to...
            System.out.println(logFile.getCanonicalPath());

            writer = new BufferedWriter(new FileWriter(logFile));
            writer.write("Hello world!");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Close the writer regardless of what happens...
                writer.close();
            } catch (Exception e) {
            }
        }
    }
}
