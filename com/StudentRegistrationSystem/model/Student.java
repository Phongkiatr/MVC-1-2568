package com.example.model;

import java.time.LocalDate;
import java.time.Period;

/**
 * คลาส Model สำหรับจัดเก็บข้อมูลของนักเรียน (Student)
 * แต่ละ Object ของคลาสนี้จะแทนข้อมูลของนักเรียนหนึ่งคน
 */
public class Student {

    // --- Fields: คุณสมบัติของข้อมูลนักเรียน ---

    /**
     * รหัสประจำตัวนักเรียน (Primary Key)
     */
    private String studentId;

    /**
     * คำนำหน้าชื่อ (เช่น นาย, นางสาว)
     */
    private String prefix;

    /**
     * ชื่อจริง
     */
    private String firstName;

    /**
     * นามสกุล
     */
    private String lastName;

    /**
     * วันเกิดของนักเรียน
     */
    private LocalDate dateOfBirth;

    /**
     * ชื่อโรงเรียนปัจจุบัน
     */
    private String school;

    /**
     * อีเมลสำหรับติดต่อ
     */
    private String email;

    /**
     * คำนวณและคืนค่าอายุของนักเรียน ณ ปัจจุบัน โดยอ้างอิงจากวันเกิด
     * นี่คือคุณสมบัติที่คำนวณได้ (Derived Attribute) จึงมีแค่เมธอด getter แต่ไม่มี setter
     *
     * @return อายุของนักเรียน (เป็นปีเต็ม)
     */
    public int getAge() {
        // ตรวจสอบก่อนว่า dateOfBirth มีค่าหรือไม่ เพื่อป้องกัน NullPointerException
        if (this.dateOfBirth == null) {
            return 0;
        }
        // ใช้ Period.between ในการคำนวณหาระยะห่างระหว่างวันเกิดกับวันปัจจุบัน แล้วคืนค่าเป็นจำนวนปี
        return Period.between(this.dateOfBirth, LocalDate.now()).getYears();
    }

    // --- Getters and Setters: เมธอดสำหรับการเข้าถึงและแก้ไขข้อมูลใน Fields ---

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}