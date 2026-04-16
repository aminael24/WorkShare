package com.example.gestionprojets.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.gestionprojets.dao.ProjectDao;
import com.example.gestionprojets.dao.StudentDao;
import com.example.gestionprojets.entity.Project;
import com.example.gestionprojets.entity.Student;

/**
 * Tests unitaires et fonctionnels pour ProjectService.
 * Teste la création de projets, ajout/suppression de membres.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProjectService Tests")
public class ProjectServiceTest {

    @Mock
    private ProjectDao projectDao;

    @Mock
    private StudentDao studentDao;

    @InjectMocks
    private ProjectService projectService;

    private Student creator;
    private Student member1;
    private Student member2;
    private Project project;

    @BeforeEach
    void setUp() {
        // Initialiser les données de test
        creator = new Student();
        creator.setId(1L);
        creator.setNom("Dupont");
        creator.setPrenom("Jean");
        creator.setEmail("jean.dupont@example.com");
        creator.setPassword("password123");

        member1 = new Student();
        member1.setId(2L);
        member1.setNom("Martin");
        member1.setPrenom("Marie");
        member1.setEmail("marie.martin@example.com");
        member1.setPassword("password123");

        member2 = new Student();
        member2.setId(3L);
        member2.setNom("Bernard");
        member2.setPrenom("Pierre");
        member2.setEmail("pierre.bernard@example.com");
        member2.setPassword("password123");

        project = new Project();
        project.setId(1L);
        project.setTitre("Projet Test");
        project.setDescription("Description du projet test");
        project.setDateDebut(new Date());
        project.setDateFin(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000)); // +7 jours
    }

    @Nested
    @DisplayName("Creation de Projet")
    class CreationProjetTests {

        @Test
        @DisplayName("Créer un projet sans membres additionnels")
        void testCreateProjectWithoutMembers() {
            // Arrange
            List<String> memberEmails = null;

            // Act
            projectService.createProject(project, creator, memberEmails);

            // Assert
            assertEquals(creator, project.getCreator());
            assertTrue(project.getMembers().contains(creator));
            assertEquals(1, project.getMembers().size());
            verify(projectDao, times(1)).save(project);
        }

        @Test
        @DisplayName("Créer un projet avec des membres additionnels")
        void testCreateProjectWithMembers() {
            // Arrange
            List<String> memberEmails = new ArrayList<>();
            memberEmails.add("marie.martin@example.com");
            memberEmails.add("pierre.bernard@example.com");

            when(studentDao.findByEmail("marie.martin@example.com")).thenReturn(member1);
            when(studentDao.findByEmail("pierre.bernard@example.com")).thenReturn(member2);

            // Act
            projectService.createProject(project, creator, memberEmails);

            // Assert
            assertEquals(creator, project.getCreator());
            assertEquals(3, project.getMembers().size());
            assertTrue(project.getMembers().contains(creator));
            assertTrue(project.getMembers().contains(member1));
            assertTrue(project.getMembers().contains(member2));
            verify(projectDao, times(1)).save(project);
        }

        @Test
        @DisplayName("Créer un projet ignore les emails invalides")
        void testCreateProjectIgnoresInvalidEmails() {
            // Arrange
            List<String> memberEmails = new ArrayList<>();
            memberEmails.add("valid.email@example.com");
            memberEmails.add(""); // email vide
            memberEmails.add("invalid@notfound.com"); // email inexistant

            when(studentDao.findByEmail("valid.email@example.com")).thenReturn(member1);
            when(studentDao.findByEmail("invalid@notfound.com")).thenReturn(null);

            // Act
            projectService.createProject(project, creator, memberEmails);

            // Assert
            assertEquals(2, project.getMembers().size());
            assertTrue(project.getMembers().contains(creator));
            assertTrue(project.getMembers().contains(member1));
        }

        @Test
        @DisplayName("Créer un projet évite les doublons de membres")
        void testCreateProjectAvoidsDuplicateMembers() {
            // Arrange
            List<String> memberEmails = new ArrayList<>();
            memberEmails.add("marie.martin@example.com");
            memberEmails.add("marie.martin@example.com"); // même email deux fois

            when(studentDao.findByEmail("marie.martin@example.com")).thenReturn(member1);

            // Act
            projectService.createProject(project, creator, memberEmails);

            // Assert
            assertEquals(2, project.getMembers().size());
            verify(projectDao, times(1)).save(project);
        }
    }

    @Nested
    @DisplayName("Ajout de Membre")
    class AjoutMembre {

        @Test
        @DisplayName("Ajouter un membre avec succès")
        void testAddMemberSuccess() {
            // Arrange
            project.setCreator(creator);
            project.setMembers(new ArrayList<>(List.of(creator)));
            when(projectDao.findById(1L)).thenReturn(project);
            when(studentDao.findByEmail("marie.martin@example.com")).thenReturn(member1);

            // Act
            String result = projectService.addMember(1L, "marie.martin@example.com", creator);

            // Assert
            assertEquals("SUCCESS", result);
            assertTrue(project.getMembers().contains(member1));
            verify(projectDao, times(1)).update(project);
        }

        @Test
        @DisplayName("Ne pas ajouter un membre si l'utilisateur n'est pas créateur")
        void testAddMemberNotAllowedNonCreator() {
            // Arrange
            project.setCreator(creator);
            project.setMembers(new ArrayList<>(List.of(creator)));
            when(projectDao.findById(1L)).thenReturn(project);

            // Act
            String result = projectService.addMember(1L, "marie.martin@example.com", member1);

            // Assert
            assertEquals("NOT_ALLOWED", result);
            verify(projectDao, never()).update(project);
        }

        @Test
        @DisplayName("Ne pas ajouter un email qui n'existe pas")
        void testAddMemberEmailNotFound() {
            // Arrange
            project.setCreator(creator);
            project.setMembers(new ArrayList<>(List.of(creator)));
            when(projectDao.findById(1L)).thenReturn(project);
            when(studentDao.findByEmail("nonexistent@example.com")).thenReturn(null);

            // Act
            String result = projectService.addMember(1L, "nonexistent@example.com", creator);

            // Assert
            assertEquals("EMAIL_NOT_FOUND", result);
            verify(projectDao, never()).update(project);
        }

        @Test
        @DisplayName("Ne pas ajouter un membre déjà présent")
        void testAddMemberAlreadyMember() {
            // Arrange
            project.setCreator(creator);
            project.setMembers(new ArrayList<>(List.of(creator, member1)));
            when(projectDao.findById(1L)).thenReturn(project);
            when(studentDao.findByEmail("marie.martin@example.com")).thenReturn(member1);

            // Act
            String result = projectService.addMember(1L, "marie.martin@example.com", creator);

            // Assert
            assertEquals("ALREADY_MEMBER", result);
            verify(projectDao, never()).update(project);
        }

        @Test
        @DisplayName("Ne pas permettre au créateur d'ajouter lui-même")
        void testAddMemberCannotAddSelf() {
            // Arrange
            project.setCreator(creator);
            project.setMembers(new ArrayList<>(List.of(creator)));
            when(projectDao.findById(1L)).thenReturn(project);

            // Act
            String result = projectService.addMember(1L, "jean.dupont@example.com", creator);

            // Assert
            assertEquals("CANNOT_ADD_SELF", result);
            verify(projectDao, never()).update(project);
        }

        @Test
        @DisplayName("Valider les entrées null")
        void testAddMemberInvalidInput() {
            // Act & Assert
            assertEquals("INVALID_INPUT", projectService.addMember(null, "email@example.com", creator));
            assertEquals("INVALID_INPUT", projectService.addMember(1L, null, creator));
            assertEquals("INVALID_INPUT", projectService.addMember(1L, "  ", creator));
        }
    }

    @Nested
    @DisplayName("Suppression de Projet")
    class SuppressionProjet {

        @Test
        @DisplayName("Supprimer un projet avec succès par son créateur")
        void testDeleteProjectSuccess() {
            // Arrange
            project.setCreator(creator);
            when(projectDao.findById(1L)).thenReturn(project);

            // Act
            boolean result = projectService.deleteProject(1L, creator);

            // Assert
            assertTrue(result);
            verify(projectDao, times(1)).delete(project);
        }

        @Test
        @DisplayName("Ne pas supprimer un projet si l'utilisateur n'est pas créateur")
        void testDeleteProjectNotAllowedNonCreator() {
            // Arrange
            project.setCreator(creator);
            when(projectDao.findById(1L)).thenReturn(project);

            // Act
            boolean result = projectService.deleteProject(1L, member1);

            // Assert
            assertFalse(result);
            verify(projectDao, never()).delete(project);
        }

        @Test
        @DisplayName("Ne pas supprimer un projet inexistant")
        void testDeleteProjectNotFound() {
            // Arrange
            when(projectDao.findById(999L)).thenReturn(null);

            // Act
            boolean result = projectService.deleteProject(999L, creator);

            // Assert
            assertFalse(result);
            verify(projectDao, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("Recherche de Projets")
    class RechercheProjet {

        @Test
        @DisplayName("Récupérer les projets d'un étudiant")
        void testGetProjectsOfStudent() {
            // Arrange
            List<Project> projects = new ArrayList<>(List.of(project));
            when(projectDao.findByStudent(creator)).thenReturn(projects);

            // Act
            List<Project> result = projectService.getProjectsOfStudent(creator);

            // Assert
            assertEquals(1, result.size());
            assertTrue(result.contains(project));
            verify(projectDao, times(1)).findByStudent(creator);
        }

        @Test
        @DisplayName("Récupérer les projets créés par un étudiant")
        void testGetProjectsCreatedBy() {
            // Arrange
            List<Project> projects = new ArrayList<>(List.of(project));
            when(projectDao.findCreatedByStudent(creator)).thenReturn(projects);

            // Act
            List<Project> result = projectService.getProjectsCreatedBy(creator);

            // Assert
            assertEquals(1, result.size());
            verify(projectDao, times(1)).findCreatedByStudent(creator);
        }

        @Test
        @DisplayName("Trouver un projet par son ID")
        void testFindProjectById() {
            // Arrange
            when(projectDao.findById(1L)).thenReturn(project);

            // Act
            Project result = projectService.findById(1L);

            // Assert
            assertNotNull(result);
            assertEquals(project, result);
            verify(projectDao, times(1)).findById(1L);
        }
    }
}

