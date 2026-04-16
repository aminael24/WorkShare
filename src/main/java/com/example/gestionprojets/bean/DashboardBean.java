package com.example.gestionprojets.bean;

import com.example.gestionprojets.entity.Project;
import com.example.gestionprojets.entity.Student;
import com.example.gestionprojets.enums.TaskStatus;
import com.example.gestionprojets.service.ProjectService;
import com.example.gestionprojets.service.TaskService;
import com.example.gestionprojets.util.AuthGuard;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.List;

@Named("dashboardBean")
@ViewScoped
public class DashboardBean implements Serializable {

    @Inject
    private AuthBean authBean;

    @Inject
    private ProjectService projectService;
    @Inject
    private TaskService taskService;

    private Long totalProjects;
    private Long createdProjects;
    private Long assignedTasks;
    private Long todoTasks;
    private Long inProgressTasks;
    private Long doneTasks;

    private List<Project> recentProjects;

    @PostConstruct
    public void init() {
        AuthGuard.guardPage(authBean);

        Student currentUser = authBean.getCurrentUser();
        if (currentUser == null) {
            return;
        }

        totalProjects = projectService.countProjects(currentUser);
        createdProjects = projectService.countCreatedProjects(currentUser);
        assignedTasks = taskService.countAssignedTasks(currentUser);
        todoTasks = taskService.countAssignedTasksByStatus(currentUser, TaskStatus.TODO);
        inProgressTasks = taskService.countAssignedTasksByStatus(currentUser, TaskStatus.IN_PROGRESS);
        doneTasks = taskService.countAssignedTasksByStatus(currentUser, TaskStatus.DONE);

        recentProjects = projectService.getRecentProjectsOfStudent(currentUser, 6);
    }

    public Long getTotalProjects() { return totalProjects; }
    public Long getCreatedProjects() { return createdProjects; }
    public Long getAssignedTasks() { return assignedTasks; }
    public Long getTodoTasks() { return todoTasks; }
    public Long getInProgressTasks() { return inProgressTasks; }
    public Long getDoneTasks() { return doneTasks; }
    public List<Project> getRecentProjects() { return recentProjects; }
}