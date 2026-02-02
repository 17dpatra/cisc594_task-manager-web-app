package io.taskmanager.authentication;


import io.taskmanager.authentication.dto.user.UserRole;
import io.taskmanager.authentication.dto.user.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {
        // utility class
    }

    /**
     * @return CurrentUser or null if no authentication present
     */
    public static UserPrincipal getCurrentUser() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserPrincipal currentUser) {
            return currentUser;
        }

        return null;
    }

    /**
     * @return current user id or null
     */
    public static long getCurrentUserId() {
        return requireCurrentUser().id();
    }

    /**
     * @return current user or throw if not authenticated
     */
    public static UserPrincipal requireCurrentUser() {
        UserPrincipal user = getCurrentUser();
        if (user == null) {
            throw new IllegalStateException("No authenticated user in security context");
        }
        return user;
    }

    /**
     * @return true if current user has given authority
     */
    public static boolean hasAuthority(String authority) {
        UserPrincipal user = getCurrentUser();
        if (user == null) return false;

        return user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(authority));
    }

    /**
     * Convenience helper
     */
    public static boolean isGlobalAdmin() {
        return hasAuthority(UserRole.GLOBAL_ADMIN.toString());
    }
}
