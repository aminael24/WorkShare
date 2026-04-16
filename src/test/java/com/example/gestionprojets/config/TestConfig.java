package com.example.gestionprojets.config;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.example.gestionprojets.entity.Project;
import com.example.gestionprojets.entity.Student;
import com.example.gestionprojets.entity.Task;

/**
 * Configuration globale pour les tests.
 * Utilise Testcontainers pour lancer une instance MySQL en Docker.
 */
@Testcontainers
public class TestConfig {

    @Container
    public static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("test_gestionprojets")
            .withUsername("test_user")
            .withPassword("test_password")
            .withInitScript("schema.sql");

    private static SessionFactory sessionFactory;

    /**
     * Initialise la SessionFactory pour les tests avec la base de données MySQL du conteneur.
     */
    public static SessionFactory initializeSessionFactory() {
        if (sessionFactory != null) {
            return sessionFactory;
        }

        Configuration config = new Configuration();

        // Configuration Hibernate avec les paramètres du conteneur MySQL
        config.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        config.setProperty("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
        config.setProperty("hibernate.connection.url", mysql.getJdbcUrl());
        config.setProperty("hibernate.connection.username", mysql.getUsername());
        config.setProperty("hibernate.connection.password", mysql.getPassword());
        config.setProperty("hibernate.show_sql", "false");
        config.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        config.setProperty("hibernate.current_session_context_class", "thread");

        // Enregistrer les entités
        config.addAnnotatedClass(Student.class);
        config.addAnnotatedClass(Project.class);
        config.addAnnotatedClass(Task.class);

        sessionFactory = config.buildSessionFactory();
        return sessionFactory;
    }

    /**
     * Retourne la SessionFactory actuelle.
     */
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            initializeSessionFactory();
        }
        return sessionFactory;
    }

    /**
     * Ferme la SessionFactory (à appeler dans tearDown).
     */
    public static void closeSessionFactory() {
        if (sessionFactory != null && sessionFactory.isOpen()) {
            sessionFactory.close();
            sessionFactory = null;
        }
    }
}

