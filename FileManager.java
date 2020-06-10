package crawler;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class FileManager {

    public static void exportLogs(String path, List<String> data) throws IOException {

        File file = new File(path);

        try (PrintWriter printWriter = new PrintWriter(file)) {
            data.forEach(printWriter::println);
        }
    }
}
