import com.udea.GPX.dto.SimpleRegisterDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;

public class TestDTOValidation {
    public static void main(String[] args) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        
        // Test user@domain specifically
        SimpleRegisterDTO dto = new SimpleRegisterDTO();
        dto.setFirstName("Juan");
        dto.setLastName("Pérez");
        dto.setEmail("user@domain");  // Este debería fallar
        dto.setPassword("password123");
        
        Set<ConstraintViolation<SimpleRegisterDTO>> violations = validator.validate(dto);
        
        System.out.println("Email: user@domain");
        System.out.println("Violations count: " + violations.size());
        
        if (violations.isEmpty()) {
            System.out.println("❌ NO HAY VIOLACIONES - ESTO ES EL PROBLEMA");
        } else {
            System.out.println("✅ HAY VIOLACIONES:");
            for (ConstraintViolation<SimpleRegisterDTO> violation : violations) {
                System.out.println("  - " + violation.getPropertyPath() + ": " + violation.getMessage());
            }
        }
        
        // Test también con un email válido para comparar
        System.out.println("\n" + "=".repeat(50));
        dto.setEmail("user@domain.com");
        violations = validator.validate(dto);
        
        System.out.println("Email: user@domain.com");
        System.out.println("Violations count: " + violations.size());
        
        if (violations.isEmpty()) {
            System.out.println("✅ NO HAY VIOLACIONES - ESTO ES CORRECTO");
        } else {
            System.out.println("❌ HAY VIOLACIONES (inesperado):");
            for (ConstraintViolation<SimpleRegisterDTO> violation : violations) {
                System.out.println("  - " + violation.getPropertyPath() + ": " + violation.getMessage());
            }
        }
    }
}
