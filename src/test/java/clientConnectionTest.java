import org.junit.jupiter.api.Test;
import java.io.*;
import static org.junit.jupiter.api.Assertions.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class clientConnectionTest {

    @Test
    public void pathRoot() throws IOException {
        String result = "";
        Path filePath = getFilePath("/");
        if (Files.exists(filePath)) {
            result = "successful";
        } else {
            result = "failed";
        }

        assertEquals("successful", result);
    }

    @Test
    public void pathJson() throws IOException {
        String result = "";
        Path filePath = getFilePath("/json");
        if (Files.exists(filePath)) {
            result = "successful";
        } else {
            result = "failed";
        }

        assertEquals("successful", result);
    }

    @Test
    public void paths() throws IOException {
        String result = "";
        Path filePath = getFilePath("/brooks");
        if (Files.exists(filePath)) {
            result = "successful";
        } else {
            result = "failed";
        }

        assertEquals("failed", result);
    }

    @Test
    public void contentTypeHtml() throws IOException {
        Path filePath = getFilePath("/");
        String data = guessContentType(filePath);

        assertEquals(data, "text/html");
    }

    @Test
    public void contentTypeJson() throws IOException {
        Path filePath = getFilePath("/json");
        String data = guessContentType(filePath);

        assertEquals(data, "application/json");
    }

    @Test
    public void contentTypeUnknown() throws IOException {
        Path filePath = getFilePath("/brooks");
        String data = guessContentType(filePath);

        assertNotEquals(data, "application/json");
        assertNotEquals(data, "text/html");
        assertNull(data);
    }

    private static Path getFilePath(String path) {
        if ("/".equals(path)) {
            path = "/"+"home.html";
        }else if("/json".equals(path)){
            path = "/"+"http_server.json";
        }

        return Paths.get("src/main/resources/", path);
    }

    private static String guessContentType(Path filePath) throws IOException {
        return Files.probeContentType(filePath);
    }

}
