package com.StudentRegistrationSystem.model;

/**
 * คลาส Model สำหรับจัดเก็บข้อมูลของรายวิชา (Subject)
 * แต่ละ Object ของคลาสนี้จะแทนข้อมูลของรายวิชาหนึ่งวิชา
 */
public class Subject {

    // --- Fields: คุณสมบัติของข้อมูลรายวิชา ---

    /**
     * รหัสวิชา (Primary Key)
     */
    private String subjectId;

    /**
     * ชื่อเต็มของรายวิชา
     */
    private String subjectName;

    /**
     * จำนวนหน่วยกิตของรายวิชา
     */
    private int credits;

    /**
     * ชื่ออาจารย์ผู้สอน
     */
    private String instructorName;

    /**
     * รหัสของวิชาบังคับก่อน (Prerequisite)
     * อาจมีค่าเป็น null ได้ หากรายวิชานี้ไม่มีวิชาบังคับก่อน
     */
    private String prerequisiteId;

    /**
     * จำนวนนักเรียนสูงสุดที่สามารถลงทะเบียนได้
     * ค่า -1 หมายถึง "ไม่จำกัดจำนวน"
     */
    private int maxCapacity;

    /**
     * จำนวนนักเรียนที่ลงทะเบียนแล้วในปัจจุบัน
     */
    private int currentEnrollment;


    // --- Getters and Setters: เมธอดสำหรับการเข้าถึงและแก้ไขข้อมูลใน Fields ---

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public String getPrerequisiteId() {
        return prerequisiteId;
    }

    public void setPrerequisiteId(String prerequisiteId) {
        this.prerequisiteId = prerequisiteId;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public int getCurrentEnrollment() {
        return currentEnrollment;
    }

    public void setCurrentEnrollment(int currentEnrollment) {
        this.currentEnrollment = currentEnrollment;
    }
}