package com.example.gestionprojets.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_student")
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    // étudiant crée plusieurs projets
    @OneToMany(mappedBy = "creator")
    private List<Project> createdProjects = new ArrayList<>();

    // étudiant membre de plusieurs projets
    @ManyToMany(mappedBy = "members")
    private List<Project> projects = new ArrayList<>();

    // étudiant crée plusieurs tâches
    @OneToMany(mappedBy = "creator")
    private List<Task> createdTasks = new ArrayList<>();

    // étudiant assigné à plusieurs tâches
    @OneToMany(mappedBy = "assignedStudent")
    private List<Task> assignedTasks = new ArrayList<>();

    public Student() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}