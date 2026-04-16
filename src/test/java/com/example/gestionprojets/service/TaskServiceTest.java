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
import com.example.gestionprojets.dao.TaskDao;
import com.example.gestionprojets.entity.Project;
import com.example.gestionprojets.entity.Student;
import com.example.gestionprojets.entity.Task;
import com.example.gestionprojets.enums.TaskStatus;

/**
 * Tests unitaires et fonctionnels pour TaskService.
 * Teste la création de tâches, assignation, et mise à jour du statut.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TaskService Tests")
public class TaskServiceTest {

    @Mock
    private TaskDao taskDao;

    @Mock
    private ProjectDao projectDao;

    @InjectMocks
    private TaskService taskService;

    private Student creator;
    private Student assignedStudent;
    private Student otherStudent;
    private Project project;
    private Task task;

    @BeforeEach
    void setUp() {
        // Initialiser les étudiants
        creator = new Student();
        creator.setId(1L);
        creator.setNom("Dupont");
        creator.setPrenom("Jean");
        creator.setEmail("jean.dupont@example.com");

        assignedStudent = new Student();
        assignedStudent.setId(2L);
        assignedStudent.setNom("Martin");
        assignedStudent.setPrenom("Marie");
        assignedStudent.setEmail("marie.martin@example.com");

        otherStudent = new Student();
        otherStudent.setId(3L);
        otherStudent.setNom("Bernard");
        otherStudent.setPrenom("Pierre");
        otherStudent.setEmail("pierre.bernard@example.com");

        // Initialiser le projet
        project = new Project();
        project.setId(1L);
        project.setTitre("Projet Test");
        project.setCreator(creator);
        project.setMembers(new ArrayList<>(List.of(creator, assignedStudent, otherStudent)));
        project.setDateDebut(new Date());
        project.setDateFin(new Date(System.currentTimeMillis() + 30 * 24 * 60 * 60 * 1000));

        // Initialiser la tâche
        task = new Task();
        task.setId(1L);
        task.setTitre("Tâche Test");
        task.setDescription("Description de la tâche");
        task.setDateDebut(new Date());
        task.setDateFin(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000));
        task.setStatus(TaskStatus.TODO);
    }

    @Nested
    @DisplayName("Creation de Tâche")
    class CreationTache {

        @Test
        @DisplayName("Créer une tâche avec assignation à un étudiant")
        void testCreateTaskWithAssignment() {
            // Arrange
            when(projectDao.findById(1L)).thenReturn(project);

            // Act
            boolean result = taskService.createTask(task, 1L, assignedStudent.getId(), creator);

            // Assert
            assertTrue(result);
            assertEquals(project, task.getProject());
            assertEquals(creator, task.getCreator());
            assertEquals(assignedStudent, task.getAssignedStudent());
            verify(taskDao, times(1)).save(task);
        }

        @Test
        @DisplayName("Créer une tâche sans assignation")
        void testCreateTaskWithoutAssignment() {
            // Arrange
            when(projectDao.findById(1L)).thenReturn(project);

            // Act
            boolean result = taskService.createTask(task, 1L, null, creator);

            // Assert
            assertTrue(result);
            assertEquals(project, task.getProject());
            assertEquals(creator, task.getCreator());
            assertNull(task.getAssignedStudent());
            verify(taskDao, times(1)).save(task);
        }

        @Test
        @DisplayName("Ne pas créer une tâche pour un projet inexistant")
        void testCreateTaskProjectNotFound() {
            // Arrange
            when(projectDao.findById(999L)).thenReturn(null);

            // Act
            boolean result = taskService.createTask(task, 999L, assignedStudent.getId(), creator);

            // Assert
            assertFalse(result);
            verify(taskDao, never()).save(any());
        }

        @Test
        @DisplayName("Ne pas créer une tâche si le créateur n'est pas membre du projet")
        void testCreateTaskCreatorNotMember() {
            // Arrange
            Project projectWithoutCreator = new Project();
            projectWithoutCreator.setId(1L);
            projectWithoutCreator.setMembers(new ArrayList<>(List.of(assignedStudent, otherStudent)));

            when(projectDao.findById(1L)).thenReturn(projectWithoutCreator);

            // Act
            boolean result = taskService.createTask(task, 1L, assignedStudent.getId(), creator);

            // Assert
            assertFalse(result);
            verify(taskDao, never()).save(any());
        }

        @Test
        @DisplayName("Ne pas assigner une tâche à quelqu'un qui n'est pas membre du projet")
        void testCreateTaskAssignedNotMember() {
            // Arrange
            Student nonMemberStudent = new Student();
            nonMemberStudent.setId(99L);
            when(projectDao.findById(1L)).thenReturn(project);

            // Act
            boolean result = taskService.createTask(task, 1L, nonMemberStudent.getId(), creator);

            // Assert
            assertTrue(result); // La tâche est créée
            assertNull(task.getAssignedStudent()); // Mais sans assigné
            verify(taskDao, times(1)).save(task);
        }
    }

    @Nested
    @DisplayName("Assignation de Tâche")
    class AssignationTache {

        @Test
        @DisplayName("Assigner une tâche à un étudiant membre")
        void testAssignTaskToMember() {
            // Arrange
            task.setProject(project);
            task.setCreator(creator);
            project.getMembers().add(assignedStudent);

            // Act
            task.setAssignedStudent(assignedStudent);

            // Assert
            assertEquals(assignedStudent, task.getAssignedStudent());
        }

        @Test
        @DisplayName("Récupérer les tâches assignées à un étudiant")
        void testGetTasksAssignedToStudent() {
            // Arrange
            List<Task> assignedTasks = new ArrayList<>(List.of(task));
            when(taskDao.findAssignedToStudent(assignedStudent)).thenReturn(assignedTasks);

            // Act
            List<Task> result = taskService.getTasksAssignedTo(assignedStudent);

            // Assert
            assertEquals(1, result.size());
            assertTrue(result.contains(task));
            verify(taskDao, times(1)).findAssignedToStudent(assignedStudent);
        }

        @Test
        @DisplayName("Compter les tâches assignées à un étudiant")
        void testCountAssignedTasks() {
            // Arrange
            when(taskDao.countAssignedTasks(assignedStudent)).thenReturn(5L);

            // Act
            Long count = taskService.countAssignedTasks(assignedStudent);

            // Assert
            assertEquals(5L, count);
            verify(taskDao, times(1)).countAssignedTasks(assignedStudent);
        }
    }

    @Nested
    @DisplayName("Mise a jour du Statut")
    class MiseAJourStatut {

        @Test
        @DisplayName("Mettre à jour le statut d'une tâche avec succès")
        void testUpdateTaskStatusSuccess() {
            // Arrange
            task.setAssignedStudent(assignedStudent);
            when(taskDao.findById(1L)).thenReturn(task);

            // Act
            boolean result = taskService.updateStatus(1L, TaskStatus.IN_PROGRESS, assignedStudent);

            // Assert
            assertTrue(result);
            assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
            verify(taskDao, times(1)).update(task);
        }

        @Test
        @DisplayName("Mettre à jour le statut à DONE")
        void testUpdateTaskStatusToDone() {
            // Arrange
            task.setAssignedStudent(assignedStudent);
            when(taskDao.findById(1L)).thenReturn(task);

            // Act
            boolean result = taskService.updateStatus(1L, TaskStatus.DONE, assignedStudent);

            // Assert
            assertTrue(result);
            assertEquals(TaskStatus.DONE, task.getStatus());
            verify(taskDao, times(1)).update(task);
        }

        @Test
        @DisplayName("Ne pas mettre à jour si la tâche n'existe pas")
        void testUpdateTaskStatusNotFound() {
            // Arrange
            when(taskDao.findById(999L)).thenReturn(null);

            // Act
            boolean result = taskService.updateStatus(999L, TaskStatus.IN_PROGRESS, assignedStudent);

            // Assert
            assertFalse(result);
            verify(taskDao, never()).update(any());
        }

        @Test
        @DisplayName("Ne pas mettre à jour si l'étudiant ne l'est pas assigné")
        void testUpdateTaskStatusNotAssignedUser() {
            // Arrange
            task.setAssignedStudent(assignedStudent);
            when(taskDao.findById(1L)).thenReturn(task);

            // Act
            boolean result = taskService.updateStatus(1L, TaskStatus.IN_PROGRESS, otherStudent);

            // Assert
            assertFalse(result);
            verify(taskDao, never()).update(any());
        }

        @Test
        @DisplayName("Ne pas mettre à jour si aucun étudiant n'est assigné à la tâche")
        void testUpdateTaskStatusNoAssignedStudent() {
            // Arrange
            task.setAssignedStudent(null);
            when(taskDao.findById(1L)).thenReturn(task);

            // Act
            boolean result = taskService.updateStatus(1L, TaskStatus.IN_PROGRESS, assignedStudent);

            // Assert
            assertFalse(result);
            verify(taskDao, never()).update(any());
        }
    }

    @Nested
    @DisplayName("Statistiques des Tâches")
    class StatistiquesTask {

        @Test
        @DisplayName("Compter le nombre total de tâches dans un projet")
        void testCountTasksByProject() {
            // Arrange
            when(taskDao.countByProject(1L)).thenReturn(10L);

            // Act
            long count = taskService.countByProject(1L);

            // Assert
            assertEquals(10L, count);
            verify(taskDao, times(1)).countByProject(1L);
        }

        @Test
        @DisplayName("Compter les tâches complétées dans un projet")
        void testCountDoneTasksByProject() {
            // Arrange
            when(taskDao.countDoneByProject(1L)).thenReturn(7L);

            // Act
            long count = taskService.countDoneByProject(1L);

            // Assert
            assertEquals(7L, count);
            verify(taskDao, times(1)).countDoneByProject(1L);
        }

        @Test
        @DisplayName("Récupérer les tâches d'un projet par statut")
        void testGetTasksByProjectAndStatus() {
            // Arrange
            List<Task> inProgressTasks = new ArrayList<>(List.of(task));
            when(taskDao.findByProjectAndStatus(1L, TaskStatus.IN_PROGRESS))
                    .thenReturn(inProgressTasks);

            // Act
            List<Task> result = taskService.getTasksByProjectAndStatus(1L, TaskStatus.IN_PROGRESS);

            // Assert
            assertEquals(1, result.size());
            verify(taskDao, times(1)).findByProjectAndStatus(1L, TaskStatus.IN_PROGRESS);
        }

        @Test
        @DisplayName("Compter les tâches assignées par statut")
        void testCountAssignedTasksByStatus() {
            // Arrange
            when(taskDao.countAssignedTasksByStatus(assignedStudent, TaskStatus.TODO))
                    .thenReturn(3L);

            // Act
            Long count = taskService.countAssignedTasksByStatus(assignedStudent, TaskStatus.TODO);

            // Assert
            assertEquals(3L, count);
            verify(taskDao, times(1)).countAssignedTasksByStatus(assignedStudent, TaskStatus.TODO);
        }
    }

    @Nested
    @DisplayName("Fonctionnalites Principales")
    class FonctionnalitesPrincipales {

        @Test
        @DisplayName("Scénario complet: Créer un projet, ajouter des membres, créer et assigner une tâche")
        void testCompleteWorkflow() {
            // Étape 1: Créer un projet avec des membres (testé dans ProjectService)
            // Étape 2: Créer une tâche dans le projet
            when(projectDao.findById(1L)).thenReturn(project);

            boolean taskCreated = taskService.createTask(task, 1L, assignedStudent.getId(), creator);
            assertTrue(taskCreated);
            assertEquals(project, task.getProject());
            assertEquals(creator, task.getCreator());
            assertEquals(assignedStudent, task.getAssignedStudent());

            // Étape 3: L'étudiant assigné met à jour le statut
            when(taskDao.findById(1L)).thenReturn(task);

            boolean statusUpdated = taskService.updateStatus(1L, TaskStatus.IN_PROGRESS, assignedStudent);
            assertTrue(statusUpdated);
            assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());

            // Étape 4: Terminer la tâche
            boolean taskDone = taskService.updateStatus(1L, TaskStatus.DONE, assignedStudent);
            assertTrue(taskDone);
            assertEquals(TaskStatus.DONE, task.getStatus());
        }

        @Test
        @DisplayName("Scénario: Gérer plusieurs tâches dans un projet")
        void testMultipleTasksWorkflow() {
            // Arrange
            Task task2 = new Task();
            task2.setId(2L);
            task2.setTitre("Tâche 2");
            task2.setCreator(creator);
            task2.setProject(project);
            task2.setAssignedStudent(otherStudent);

            when(projectDao.findById(1L)).thenReturn(project);
            when(taskDao.countByProject(1L)).thenReturn(2L);
            when(taskDao.countDoneByProject(1L)).thenReturn(0L);
            when(taskDao.findByProjectAndStatus(1L, TaskStatus.TODO))
                    .thenReturn(new ArrayList<>(List.of(task, task2)));

            // Act
            boolean task1Created = taskService.createTask(task, 1L, assignedStudent.getId(), creator);
            boolean task2Created = taskService.createTask(task2, 1L, otherStudent.getId(), creator);
            long totalTasks = taskService.countByProject(1L);
            long completedTasks = taskService.countDoneByProject(1L);
            List<Task> todoTasks = taskService.getTasksByProjectAndStatus(1L, TaskStatus.TODO);

            // Assert
            assertTrue(task1Created);
            assertTrue(task2Created);
            assertEquals(2L, totalTasks);
            assertEquals(0L, completedTasks);
            assertEquals(2, todoTasks.size());
        }
    }
}

