package com.example.gestionprojets.dao;

import com.example.gestionprojets.entity.Project;
import com.example.gestionprojets.entity.Student;
import com.example.gestionprojets.entity.Task;
import com.example.gestionprojets.enums.TaskStatus;
import com.example.gestionprojets.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class TaskDao {

    public void save(Task task) {
        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(task);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    public void update(Task task) {
        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(task);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    public Task findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "select t from Task t " +
                                    "left join fetch t.project " +
                                    "left join fetch t.creator " +
                                    "left join fetch t.assignedStudent " +
                                    "where t.id = :id", Task.class)
                    .setParameter("id", id)
                    .uniqueResult();
        }
    }

    public List<Task> findByProject(Project project) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Task> query = session.createQuery(
                    "select t from Task t " +
                            "left join fetch t.assignedStudent " +
                            "left join fetch t.creator " +
                            "where t.project = :project " +
                            "order by t.id desc", Task.class);
            query.setParameter("project", project);
            return query.list();
        }
    }

    public List<Task> findAssignedToStudent(Student student) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Task> query = session.createQuery(
                    "select t from Task t " +
                            "left join fetch t.project " +
                            "left join fetch t.creator " +
                            "where t.assignedStudent = :student " +
                            "order by t.id desc", Task.class);
            query.setParameter("student", student);
            return query.list();
        }
    }

    public Long countAssignedTasks(Student student) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select count(t.id) from Task t where t.assignedStudent = :student",
                    Long.class
            ).setParameter("student", student).uniqueResult();
        }
    }

    public Long countAssignedTasksByStatus(Student student, TaskStatus status) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "select count(t.id) from Task t where t.assignedStudent = :student and t.status = :status",
                            Long.class
                    ).setParameter("student", student)
                    .setParameter("status", status)
                    .uniqueResult();
        }
    }
}