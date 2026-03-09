package com.example.gestionprojets.dao;

import com.example.gestionprojets.entity.Student;
import com.example.gestionprojets.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

public class StudentDao {

    public void save(Student student){

        Transaction tx = null;

        try(Session session = HibernateUtil.getSessionFactory().openSession()){

            tx = session.beginTransaction();

            session.persist(student);

            tx.commit();

        }catch(Exception e){

            if(tx != null) tx.rollback();

            e.printStackTrace();

        }

    }

    public Student findByEmail(String email){

        try(Session session = HibernateUtil.getSessionFactory().openSession()){

            Query<Student> query = session.createQuery(
                    "FROM Student WHERE email = :email", Student.class);

            query.setParameter("email", email);

            return query.uniqueResult();

        }

    }

}