package udemy.clone.model.util;

import org.springframework.security.core.context.SecurityContextHolder;
import udemy.clone.config.JwtAuthentication;

public class Util {
    public static String API = "/api";

    public static JwtAuthentication getAuthentication() {
        return (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
    }
}
