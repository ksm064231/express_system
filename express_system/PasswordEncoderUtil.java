import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderUtil {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String adminPassword = "admin123";
        String courierPassword = "courier123";
        String ownerPassword = "owner123";
        
        System.out.println("admin123: " + encoder.encode(adminPassword));
        System.out.println("courier123: " + encoder.encode(courierPassword));
        System.out.println("owner123: " + encoder.encode(ownerPassword));
    }
}