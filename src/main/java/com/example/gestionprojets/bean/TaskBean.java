package com.example.gestionprojets.bean;

import com.example.gestionprojets.entity.Project;
import com.example.gestionprojets.entity.Task;
import com.example.gestionprojets.enums.TaskStatus;
import com.example.gestionprojets.service.ProjectService;
import com.example.gestionprojets.service.TaskService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Named("taskBean")
@ViewScoped
public class TaskBean implements Serializable {

    @Inject
    private AuthBean authBean;

    private final TaskService taskService = new TaskService();
    private final ProjectService projectService = new ProjectService();

    private List<Task> myTasks;
    private List<Project> myProjects;
    private List<TaskStatus> statuses;

    private Long projectId;
    private String titre;
    private String description;
    private Date dateDebut;
    private Date dateFin;
    private Long assignedStudentId;

    @PostConstruct
    public void init() {
        myTasks = taskService.getTasksAssignedTo(authBean.getCurrentUser());
        myProjects = projectService.getProjectsOfStudent(authBean.getCurrentUser());
        statuses = Arrays.asList(TaskStatus.values());
    }

    public void createTask() {
        Task task = new Task();
        task.setTitre(titre);
        task.setDescription(description);
        task.setDateDebut(dateDebut);
        task.setDateFin(dateFin);
        task.setStatus(TaskStatus.TODO);

        boolean ok = taskService.createTask(task, projectId, assignedStudentId, authBean.getCurrentUser());

        if (ok) {
            myTasks = taskService.getTasksAssignedTo(authBean.getCurrentUser());
            resetForm();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Tâche créée avec succès.", null));
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossible de créer la tâche.", null));
        }
    }

    public void updateTaskStatus(Long taskId, TaskStatus status) {
        boolean ok = taskService.updateStatus(taskId, status, authBean.getCurrentUser());

        if (ok) {
            myTasks = taskService.getTasksAssignedTo(authBean.getCurrentUser());
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Statut mis à jour.", null));
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossible de modifier ce statut.", null));
        }
    }

    public List<com.example.gestionprojets.entity.Student> getProjectMembers() {
        if (projectId == null) return List.of();
        Project project = projectService.findById(projectId);
        return project != null ? project.getMembers() : List.of();
    }

    private void resetForm() {
        projectId = null;
        titre = null;
        description = null;
        dateDebut = null;
        dateFin = null;
        assignedStudentId = null;
    }

    public List<Task> getMyTasks() { return myTasks; }
    public List<Project> getMyProjects() { return myProjects; }
    public List<TaskStatus> getStatuses() { return statuses; }
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Date getDateDebut() { return dateDebut; }
    public void setDateDebut(Date dateDebut) { this.dateDebut = dateDebut; }
    public Date getDateFin() { return dateFin; }
    public void setDateFin(Date dateFin) { this.dateFin = dateFin; }
    public Long getAssignedStudentId() { return assignedStudentId; }
    public void setAssignedStudentId(Long assignedStudentId) { this.assignedStudentId = assignedStudentId; }
}