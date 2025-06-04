import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class URLEncoderTest {
    public static void main(String[] args) {
        try {
            System.out.println("=== URLEncoder Test ===");

            // Test con espacios
            String testSpaces = "Test User";
            String encodedSpaces = URLEncoder.encode(testSpaces, "UTF-8");
            System.out.println("Input: '" + testSpaces + "'");
            System.out.println("Encoded: '" + encodedSpaces + "'");
            System.out.println();

            // Test con caracteres especiales
            String testSpecial = "José María";
            String encodedSpecial = URLEncoder.encode(testSpecial, "UTF-8");
            System.out.println("Input: '" + testSpecial + "'");
            System.out.println("Encoded: '" + encodedSpecial + "'");
            System.out.println();

            // Test casos edge
            String testEdge = "Test & Special + Characters";
            String encodedEdge = URLEncoder.encode(testEdge, "UTF-8");
            System.out.println("Input: '" + testEdge + "'");
            System.out.println("Encoded: '" + encodedEdge + "'");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}