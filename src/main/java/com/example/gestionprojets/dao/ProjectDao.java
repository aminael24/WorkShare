package com.example.gestionprojets.dao;

import com.example.gestionprojets.entity.Project;
import com.example.gestionprojets.entity.Student;
import com.example.gestionprojets.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class ProjectDao {

    public void save(Project project) {
        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(project);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    public void update(Project project) {
        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(project);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    public void delete(Project project) {
        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Project managed = session.find(Project.class, project.getId());
            if (managed != null) {
                session.remove(managed);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    public Project findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "select distinct p from Project p " +
                                    "left join fetch p.members " +
                                    "left join fetch p.tasks " +
                                    "left join fetch p.creator " +
                                    "where p.id = :id", Project.class)
                    .setParameter("id", id)
                    .uniqueResult();
        }
    }

    public List<Project> findByStudent(Student student) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Project> query = session.createQuery(
                    "select distinct p from Project p " +
                            "left join fetch p.members " +
                            "left join fetch p.tasks " +
                            "left join fetch p.creator " +
                            "where :student member of p.members " +
                            "order by p.id desc", Project.class);
            query.setParameter("student", student);
            return query.list();
        }
    }

    public List<Project> findCreatedByStudent(Student student) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Project> query = session.createQuery(
                    "select distinct p from Project p " +
                            "left join fetch p.members " +
                            "left join fetch p.tasks " +
                            "where p.creator = :student " +
                            "order by p.id desc", Project.class);
            query.setParameter("student", student);
            return query.list();
        }
    }

    public Long countProjectsByStudent(Student student) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select count(distinct p.id) from Project p where :student member of p.members",
                    Long.class
            ).setParameter("student", student).uniqueResult();
        }
    }
    public List<Project> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select distinct p from Project p left join fetch p.creator order by p.id desc",
                    Project.class
            ).list();
        }
    }
    public Long countCreatedProjectsByStudent(Student student) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select count(p.id) from Project p where p.creator = :student",
                    Long.class
            ).setParameter("student", student).uniqueResult();
        }
    }
}