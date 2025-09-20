package com.StudentRegistrationSystem.model;

import java.time.LocalDateTime;

/**
 * คลาส Model สำหรับจัดเก็บข้อมูลการลงทะเบียนเรียน (Enrollment)
 * แต่ละ Object ของคลาสนี้จะแทนการลงทะเบียนเรียน 1 รายการ
 * ซึ่งเป็นการจับคู่ระหว่างนักเรียนหนึ่งคนกับรายวิชาหนึ่งวิชา
 */
public class Enrollment {

    // --- Fields: คุณสมบัติของข้อมูลการลงทะเบียน ---

    /**
     * รหัสเฉพาะของการลงทะเบียน (Primary Key)
     */
    private int enrollmentId;

    /**
     * รหัสนักเรียนที่ทำการลงทะเบียน (Foreign Key to Student)
     */
    private String studentId;

    /**
     * รหัสวิชาที่ถูกลงทะเบียน (Foreign Key to Subject)
     */
    private String subjectId;

    /**
     * วันที่และเวลาที่ทำการลงทะเบียน
     */
    private LocalDateTime enrollmentDate;

    /**
     * เกรดที่ได้รับในรายวิชานี้ (อาจเป็นค่าว่างได้หากยังไม่มีการให้เกรด)
     */
    private String grade;

    // --- Getters and Setters: เมธอดสำหรับการเข้าถึงและแก้ไขข้อมูลใน Fields ---

    public int getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(int enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public LocalDateTime getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDateTime enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}