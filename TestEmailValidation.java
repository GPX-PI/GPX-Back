import com.udea.GPX.util.InputSanitizer;

public class TestEmailValidation {
    public static void main(String[] args) {
        String[] testEmails = {
            "user@domain.com",      // válido
            "user@domain",          // debería ser inválido
            "invalid-email",        // debería ser inválido
            "user@",               // debería ser inválido
            "@domain.com"          // debería ser inválido
        };
        
        for (String email : testEmails) {
            try {
                String result = InputSanitizer.sanitizeEmail(email);
                System.out.println(email + " -> VÁLIDO: " + result);
            } catch (Exception e) {
                System.out.println(email + " -> INVÁLIDO: " + e.getMessage());
            }
        }
    }
}
