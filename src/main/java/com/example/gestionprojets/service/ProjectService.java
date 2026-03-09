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
        Project project = projectDao.findById(projectId);

        if (project == null) {
            return "PROJECT_NOT_FOUND";
        }

        if (!project.getCreator().getId().equals(currentUser.getId())) {
            return "NOT_ALLOWED";
        }

        Student student = studentDao.findByEmail(email);

        if (student == null) {
            return "EMAIL_NOT_FOUND";
        }

        boolean alreadyMember = project.getMembers().stream()
                .anyMatch(m -> m.getId().equals(student.getId()));

        if (alreadyMember) {
            return "ALREADY_MEMBER";
        }

        project.getMembers().add(student);
        projectDao.update(project);

        return "SUCCESS";
    }


    public Long countProjects(Student student) {
        return projectDao.countProjectsByStudent(student);
    }

    public Long countCreatedProjects(Student student) {
        return projectDao.countCreatedProjectsByStudent(student);
    }

}