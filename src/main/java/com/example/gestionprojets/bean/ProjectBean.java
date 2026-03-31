package com.example.gestionprojets.bean;

import com.example.gestionprojets.entity.Project;
import com.example.gestionprojets.entity.Student;
import com.example.gestionprojets.entity.Task;
import com.example.gestionprojets.service.ProjectService;
import com.example.gestionprojets.service.TaskService;
import com.example.gestionprojets.util.AuthGuard;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Named("projectBean")
@ViewScoped
public class ProjectBean implements Serializable {

    @Inject
    private AuthBean authBean;

    private final ProjectService projectService = new ProjectService();
    private final TaskService taskService = new TaskService();

    private List<Project> myProjects;
    private Long selectedProjectId;
    private Project selectedProject;

    private String titre;
    private String description;
    private Date dateDebut;
    private Date dateFin;

    private String memberEmail;
    private List<String> memberEmails = new ArrayList<>();

    private List<Task> selectedProjectTasks;

    // Variables pour le popup d'ajout de membre
    private String newMemberEmail;
    private String addMemberMessage;
    private FacesMessage.Severity addMemberSeverity;

    @PostConstruct
    public void init() {
        AuthGuard.guardPage(authBean);
        loadProjects();
    }

    public void loadProjects() {
        Student currentUser = authBean.getCurrentUser();
        if (currentUser != null) {
            myProjects = projectService.getProjectsOfStudent(currentUser);
        }
    }

    public void loadSelectedProject() {
        if (selectedProjectId != null) {
            selectedProject = projectService.findById(selectedProjectId);

            if (selectedProject != null) {
                selectedProjectTasks = taskService.getTasksByProject(selectedProject);
            }
        }
    }

    public void createProject() {
        try {
            if (dateDebut == null || dateFin == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Les dates sont obligatoires.", null));
                return;
            }

            if (dateFin.before(dateDebut)) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "La date de fin doit être après la date de début.", null));
                return;
            }

            Project project = new Project();
            project.setTitre(titre);
            project.setDescription(description);
            project.setDateDebut(dateDebut);
            project.setDateFin(dateFin);

            projectService.createProject(project, authBean.getCurrentUser(), memberEmails);

            resetForm();
            loadProjects();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Projet créé avec succès.", null));

        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur lors de la création du projet.", null));
        }
    }

    public String selectProject(Long id) {
        return "project-details?faces-redirect=true&projectId=" + id;
    }

    public void openAddMemberPopup() {
        // Cette méthode ouvre le popup côté client via JavaScript
        // Reset du message d'ajout de membre
        newMemberEmail = null;
        addMemberMessage = null;
        addMemberSeverity = null;
    }

    public void addMemberToSelectedProject() {
        if (selectedProject == null || selectedProject.getId() == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Aucun projet sélectionné.", null));
            return;
        }

        if (newMemberEmail == null || newMemberEmail.trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Veuillez saisir une adresse email.", null));
            return;
        }

        String result = projectService.addMember(selectedProject.getId(), newMemberEmail, authBean.getCurrentUser());

        switch (result) {
            case "SUCCESS":
                // Recharger le projet pour afficher le nouveau membre
                selectedProject = projectService.findById(selectedProject.getId());
                loadProjects();
                newMemberEmail = null;
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "✓ Membre ajouté avec succès.", null));
                break;

            case "EMAIL_NOT_FOUND":
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "❌ Aucun étudiant trouvé avec cet email.", null));
                break;

            case "ALREADY_MEMBER":
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "⚠️ Cet étudiant est déjà membre du projet.", null));
                break;

            case "NOT_ALLOWED":
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "❌ Seul le chef du projet peut ajouter des membres.", null));
                break;

            case "CANNOT_ADD_SELF":
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "❌ Vous ne pouvez pas vous ajouter vous-même.", null));
                break;

            case "INVALID_INPUT":
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "❌ Données invalides.", null));
                break;

            default:
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "❌ Erreur lors de l'ajout du membre.", null));
        }
    }

    public void removeMember(Long memberId) {
        if (selectedProject == null || selectedProject.getId() == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Aucun projet sélectionné.", null));
            return;
        }

        if (memberId == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Membre invalide.", null));
            return;
        }

        String result = projectService.removeMember(selectedProject.getId(), memberId, authBean.getCurrentUser());

        switch (result) {
            case "SUCCESS":
                // Recharger le projet
                selectedProject = projectService.findById(selectedProject.getId());
                loadProjects();
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "✓ Membre supprimé avec succès.", null));
                break;

            case "NOT_ALLOWED":
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "❌ Seul le chef du projet peut supprimer des membres.", null));
                break;

            case "CANNOT_REMOVE_CREATOR":
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "❌ Vous ne pouvez pas supprimer le chef du projet.", null));
                break;

            case "MEMBER_NOT_FOUND":
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "❌ Ce membre n'existe pas dans le projet.", null));
                break;

            case "INVALID_INPUT":
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "❌ Données invalides.", null));
                break;

            default:
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "❌ Erreur lors de la suppression du membre.", null));
        }
    }

    public void deleteProject(Long projectId) {
        boolean ok = projectService.deleteProject(projectId, authBean.getCurrentUser());

        if (ok) {
            loadProjects();

            if (selectedProject != null && selectedProject.getId().equals(projectId)) {
                selectedProject = null;
                selectedProjectTasks = null;
            }

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Projet supprimé.", null));
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Seul le chef du projet peut supprimer ce projet.", null));
        }
    }

    public boolean isChef(Project project) {
        return project != null
                && project.getCreator() != null
                && authBean.getCurrentUser() != null
                && project.getCreator().getId().equals(authBean.getCurrentUser().getId());
    }

    public void addEmailToList() {
        if (memberEmail != null && !memberEmail.trim().isEmpty()) {
            if (!memberEmails.contains(memberEmail.trim())) {
                memberEmails.add(memberEmail.trim());
            }
            memberEmail = null;
        }
    }

    private void resetForm() {
        titre = null;
        description = null;
        dateDebut = null;
        dateFin = null;
        memberEmail = null;
        memberEmails.clear();
    }

    public long getTotalTasks(Project project) {
        if (project == null || project.getId() == null) {
            return 0;
        }
        return taskService.countByProject(project.getId());
    }

    public long getDoneTasksCount(Project project) {
        if (project == null || project.getId() == null) {
            return 0;
        }
        return taskService.countDoneByProject(project.getId());
    }

    public int getProjectProgress(Project project) {
        long total = getTotalTasks(project);

        if (total == 0) {
            return 0;
        }

        long done = getDoneTasksCount(project);
        return (int) ((done * 100) / total);
    }

    public List<Project> getMyProjects() {
        return myProjects;
    }

    public Long getSelectedProjectId() {
        return selectedProjectId;
    }

    public void setSelectedProjectId(Long selectedProjectId) {
        this.selectedProjectId = selectedProjectId;
    }

    public Project getSelectedProject() {
        return selectedProject;
    }

    public String openProject(Long id) {
        return "/projectDetails.xhtml?faces-redirect=true&projectId=" + id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    public Date getDateFin() {
        return dateFin;
    }

    public void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }

    public String getMemberEmail() {
        return memberEmail;
    }

    public void setMemberEmail(String memberEmail) {
        this.memberEmail = memberEmail;
    }

    public List<String> getMemberEmails() {
        return memberEmails;
    }

    public List<Task> getSelectedProjectTasks() {
        return selectedProjectTasks;
    }

    public String getNewMemberEmail() {
        return newMemberEmail;
    }

    public void setNewMemberEmail(String newMemberEmail) {
        this.newMemberEmail = newMemberEmail;
    }

    public String getAddMemberMessage() {
        return addMemberMessage;
    }

    public void setAddMemberMessage(String addMemberMessage) {
        this.addMemberMessage = addMemberMessage;
    }

    public FacesMessage.Severity getAddMemberSeverity() {
        return addMemberSeverity;
    }

    public void setAddMemberSeverity(FacesMessage.Severity addMemberSeverity) {
        this.addMemberSeverity = addMemberSeverity;
    }

}