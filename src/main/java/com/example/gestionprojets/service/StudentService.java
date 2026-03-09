package com.example.gestionprojets.service;

import com.example.gestionprojets.dao.StudentDao;
import com.example.gestionprojets.entity.Student;
import com.example.gestionprojets.util.PasswordUtil;

public class StudentService {

    private StudentDao studentDao = new StudentDao();

    public boolean register(Student student) {

        Student existing = studentDao.findByEmail(student.getEmail());

        if (existing != null) {
            return false;
        }

        String hashedPassword = PasswordUtil.hashPassword(student.getPassword());
        student.setPassword(hashedPassword);

        studentDao.save(student);

        return true;
    }

    public Student login(String email, String password) {

        Student student = studentDao.findByEmail(email);

        if (student == null) {
            return null;
        }

        boolean valid = PasswordUtil.checkPassword(password, student.getPassword());

        if (valid) {
            return student;
        }

        return null;
    }
}