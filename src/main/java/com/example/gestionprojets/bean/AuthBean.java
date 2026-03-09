package com.example.gestionprojets.bean;

import com.example.gestionprojets.entity.Student;
import com.example.gestionprojets.service.StudentService;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

import java.io.Serializable;

@Named("authBean")
@SessionScoped
public class AuthBean implements Serializable {

    private String nom;
    private String prenom;
    private String registerEmail;
    private String registerPassword;

    private String loginEmail;
    private String loginPassword;

    private Student currentUser;

    private final StudentService studentService = new StudentService();

    public String login() {
        Student student = studentService.login(loginEmail, loginPassword);

        if (student != null) {
            currentUser = student;
            return "dashboard.xhtml?faces-redirect=true";
        }

        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(
                FacesMessage.SEVERITY_ERROR,
                "Email ou mot de passe incorrect.",
                "Email ou mot de passe incorrect."
        ));

        return null;
    }

    public String register() {
        Student student = new Student();
        student.setNom(nom);
        student.setPrenom(prenom);
        student.setEmail(registerEmail);
        student.setPassword(registerPassword);

        boolean created = studentService.register(student);

        if (created) {
            currentUser = student;
            return "dashboard.xhtml?faces-redirect=true";
        }

        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(
                FacesMessage.SEVERITY_ERROR,
                "Cet email existe déjà.",
                "Cet email existe déjà."
        ));

        return null;
    }



    public String logout() {
        FacesContext.getCurrentInstance()
                .getExternalContext()
                .invalidateSession();

        return "index.xhtml?faces-redirect=true";
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getRegisterEmail() {
        return registerEmail;
    }

    public void setRegisterEmail(String registerEmail) {
        this.registerEmail = registerEmail;
    }

    public String getRegisterPassword() {
        return registerPassword;
    }

    public void setRegisterPassword(String registerPassword) {
        this.registerPassword = registerPassword;
    }

    public String getLoginEmail() {
        return loginEmail;
    }

    public void setLoginEmail(String loginEmail) {
        this.loginEmail = loginEmail;
    }

    public String getLoginPassword() {
        return loginPassword;
    }

    public void setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword;
    }

    public Student getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(Student currentUser) {
        this.currentUser = currentUser;
    }
}