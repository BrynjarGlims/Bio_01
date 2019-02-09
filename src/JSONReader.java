import org.json.*;
import java.io.InputStream;

public class JSONReader {
    public static JSONObject readJSONFile(String path) {
        InputStream data = JSONReader.class.getResourceAsStream(path);
        if (data == null) {
            throw new NullPointerException("File not found");
        }

        JSONTokener tokenizer = new JSONTokener(data);

        return new JSONObject(tokenizer);
    }

    public static void main(String[] args) {
        JSONObject obj = JSONReader.readJSONFile("parameters.json");
        System.out.println(obj.getInt("populationSize"));
    }

}
