package com.example.gestionprojets.service;

import com.example.gestionprojets.dao.ProjectDao;
import com.example.gestionprojets.dao.TaskDao;
import com.example.gestionprojets.entity.Project;
import com.example.gestionprojets.entity.Student;
import com.example.gestionprojets.entity.Task;
import com.example.gestionprojets.enums.TaskStatus;

import java.util.List;

public class TaskService {

    private final TaskDao taskDao = new TaskDao();
    private final ProjectDao projectDao = new ProjectDao();
    public long countByProject(Long projectId) {
        return taskDao.countByProject(projectId);
    }

    public long countDoneByProject(Long projectId) {
        return taskDao.countDoneByProject(projectId);
    }
    public boolean createTask(Task task, Long projectId, Long assignedStudentId, Student creator) {
        Project project = projectDao.findById(projectId);
        if (project == null) return false;

        boolean isMember = project.getMembers().stream()
                .anyMatch(m -> m.getId().equals(creator.getId()));

        if (!isMember) return false;

        task.setProject(project);
        task.setCreator(creator);

        if (assignedStudentId != null) {
            Student assigned = project.getMembers().stream()
                    .filter(m -> m.getId().equals(assignedStudentId))
                    .findFirst()
                    .orElse(null);
            task.setAssignedStudent(assigned);
        }

        taskDao.save(task);
        return true;
    }

    public List<Task> getTasksByProject(Project project) {
        return taskDao.findByProject(project);
    }

    public List<Task> getTasksAssignedTo(Student student) {
        return taskDao.findAssignedToStudent(student);
    }

    public List<Task> getTasksByProjectAndStatus(Long projectId, TaskStatus status) {
        return taskDao.findByProjectAndStatus(projectId, status);
    }

    public boolean updateStatus(Long taskId, TaskStatus newStatus, Student currentUser) {
        Task task = taskDao.findById(taskId);
        if (task == null) return false;
        if (task.getAssignedStudent() == null) return false;
        if (!task.getAssignedStudent().getId().equals(currentUser.getId())) return false;

        task.setStatus(newStatus);
        taskDao.update(task);
        return true;
    }

    public Long countAssignedTasks(Student student) {
        return taskDao.countAssignedTasks(student);
    }

    public Long countAssignedTasksByStatus(Student student, TaskStatus status) {
        return taskDao.countAssignedTasksByStatus(student, status);
    }
}