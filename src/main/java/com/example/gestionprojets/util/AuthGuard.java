package com.example.gestionprojets.util;

import com.example.gestionprojets.bean.AuthBean;
import jakarta.faces.context.FacesContext;

/**
 * Authentication guard utility class to protect pages from unauthorized access.
 * Use this in @PostConstruct methods to redirect unauthenticated users.
 */
public class AuthGuard {

    private static final String LANDING_PAGE = "index.xhtml";

    /**
     * Checks if the user is authenticated. If not, redirects to the landing page.
     *
     * @param authBean the AuthBean instance containing the current user
     * @return true if user is authenticated, false otherwise
     */
    public static boolean isAuthenticated(AuthBean authBean) {
        if (authBean == null || authBean.getCurrentUser() == null) {
            redirectToLanding();
            return false;
        }
        return true;
    }

    /**
     * Checks if the user is authenticated and redirects if not.
     * This method can be called directly in bean initialization.
     *
     * @param authBean the AuthBean instance containing the current user
     */
    public static void guardPage(AuthBean authBean) {
        if (!isAuthenticated(authBean)) {
            redirectToLanding();
        }
    }

    /**
     * Redirects to the landing page
     */
    private static void redirectToLanding() {
        try {
            FacesContext.getCurrentInstance()
                    .getExternalContext()
                    .redirect(LANDING_PAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the current authenticated user from AuthBean.
     * Returns null if user is not authenticated.
     *
     * @param authBean the AuthBean instance
     * @return the current user or null if not authenticated
     */
    public static Object getCurrentUser(AuthBean authBean) {
        if (isAuthenticated(authBean)) {
            return authBean.getCurrentUser();
        }
        return null;
    }
}

