package com.example.gestionprojets.service;

import com.example.gestionprojets.dao.ProjectDao;
import com.example.gestionprojets.dao.StudentDao;
import com.example.gestionprojets.entity.Project;
import com.example.gestionprojets.entity.Student;

import java.util.ArrayList;
import java.util.List;

public class ProjectService {

    private final ProjectDao projectDao = new ProjectDao();
    private final StudentDao studentDao = new StudentDao();

    public void createProject(Project project, Student creator, List<String> memberEmails) {
        project.setCreator(creator);

        List<Student> members = new ArrayList<>();
        members.add(creator);

        if (memberEmails != null) {
            for (String email : memberEmails) {
                if (email != null && !email.trim().isEmpty()) {
                    Student student = studentDao.findByEmail(email.trim());
                    if (student != null && members.stream().noneMatch(s -> s.getId().equals(student.getId()))) {
                        members.add(student);
                    }
                }
            }
        }

        project.setMembers(members);
        projectDao.save(project);
    }

    public List<Project> getProjectsOfStudent(Student student) {
        return projectDao.findByStudent(student);
    }

    public List<Project> getProjectsCreatedBy(Student student) {
        return projectDao.findCreatedByStudent(student);
    }
    public List<Project> getRecentProjectsOfStudent(Student student, int limit) {
        return projectDao.findRecentByStudent(student, limit);
    }
    public Project findById(Long id) {
        return projectDao.findById(id);
    }

    public boolean deleteProject(Long projectId, Student currentUser) {
        Project project = projectDao.findById(projectId);

        if (project == null) return false;
        if (!project.getCreator().getId().equals(currentUser.getId())) return false;

        projectDao.delete(project);
        return true;
    }

    public String addMember(Long projectId, String email, Student currentUser) {
        // Validation des paramètres
        if (projectId == null || email == null || email.trim().isEmpty()) {
            return "INVALID_INPUT";
        }

        Project project = projectDao.findById(projectId);

        // Vérifier si le projet existe
        if (project == null) {
            return "PROJECT_NOT_FOUND";
        }

        // Vérifier si l'utilisateur courant est le créateur du projet
        if (currentUser == null || !project.getCreator().getId().equals(currentUser.getId())) {
            return "NOT_ALLOWED";
        }

        // Vérifier si le membre ne s'ajoute pas lui-même
        if (email.trim().equalsIgnoreCase(currentUser.getEmail())) {
            return "CANNOT_ADD_SELF";
        }

        // Chercher l'étudiant par email
        Student student = studentDao.findByEmail(email.trim());

        if (student == null) {
            return "EMAIL_NOT_FOUND";
        }

        // Vérifier si l'étudiant est déjà membre
        boolean alreadyMember = project.getMembers().stream()
                .anyMatch(m -> m.getId().equals(student.getId()));

        if (alreadyMember) {
            return "ALREADY_MEMBER";
        }

        // Ajouter le membre et sauvegarder
        project.getMembers().add(student);
        projectDao.update(project);

        return "SUCCESS";
    }

    /**
     * Ajouter plusieurs membres au projet
     */
    public String addMultipleMembers(Long projectId, List<String> emails, Student currentUser) {
        if (projectId == null || emails == null || emails.isEmpty()) {
            return "INVALID_INPUT";
        }

        Project project = projectDao.findById(projectId);

        if (project == null) {
            return "PROJECT_NOT_FOUND";
        }

        if (currentUser == null || !project.getCreator().getId().equals(currentUser.getId())) {
            return "NOT_ALLOWED";
        }

        int addedCount = 0;
        for (String email : emails) {
            if (email != null && !email.trim().isEmpty()) {
                String result = addMember(projectId, email, currentUser);
                if ("SUCCESS".equals(result)) {
                    addedCount++;
                }
            }
        }

        if (addedCount == 0) {
            return "NO_MEMBERS_ADDED";
        }

        return "SUCCESS_" + addedCount;
    }

    /**
     * Supprimer un membre du projet
     */
    public String removeMember(Long projectId, Long memberId, Student currentUser) {
        // Validation des paramètres
        if (projectId == null || memberId == null) {
            return "INVALID_INPUT";
        }

        Project project = projectDao.findById(projectId);

        // Vérifier si le projet existe
        if (project == null) {
            return "PROJECT_NOT_FOUND";
        }

        // Vérifier si l'utilisateur courant est le créateur du projet
        if (currentUser == null || !project.getCreator().getId().equals(currentUser.getId())) {
            return "NOT_ALLOWED";
        }

        // Vérifier si on essaie de supprimer le créateur
        if (project.getCreator().getId().equals(memberId)) {
            return "CANNOT_REMOVE_CREATOR";
        }

        // Chercher le membre
        Student memberToRemove = project.getMembers().stream()
                .filter(m -> m.getId().equals(memberId))
                .findFirst()
                .orElse(null);

        if (memberToRemove == null) {
            return "MEMBER_NOT_FOUND";
        }

        // Supprimer le membre
        project.getMembers().remove(memberToRemove);
        projectDao.update(project);

        return "SUCCESS";
    }

    /**
     * Vérifier si un étudiant est membre d'un projet
     */
    public boolean isMember(Long projectId, Long studentId) {
        Project project = projectDao.findById(projectId);
        if (project == null) return false;

        return project.getMembers().stream()
                .anyMatch(m -> m.getId().equals(studentId));
    }

    /**
     * Vérifier si un étudiant est le créateur d'un projet
     */
    public boolean isCreator(Long projectId, Long studentId) {
        Project project = projectDao.findById(projectId);
        if (project == null) return false;

        return project.getCreator().getId().equals(studentId);
    }


    public Long countProjects(Student student) {
        return projectDao.countProjectsByStudent(student);
    }

    public Long countCreatedProjects(Student student) {
        return projectDao.countCreatedProjectsByStudent(student);
    }

}