package com.StudentRegistrationSystem.model;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service Class สำหรับจัดการการเข้าถึงข้อมูล (Data Access Layer)
 * ทำหน้าที่เป็นตัวกลางในการอ่านและเขียนข้อมูลจากไฟล์ CSV
 * เปรียบเสมือนการจำลองฐานข้อมูล โดยเก็บข้อมูลทั้งหมดไว้ในหน่วยความจำ (In-memory) ระหว่างที่โปรแกรมทำงาน
 */
public class DataAccessService {

    // --- ค่าคงที่สำหรับระบุชื่อไฟล์ CSV ---
    private static final String STUDENTS_CSV = "students.csv";
    private static final String SUBJECTS_CSV = "subjects.csv";
    private static final String ENROLLMENTS_CSV = "enrollments.csv";

    // --- ที่เก็บข้อมูลในหน่วยความจำ (In-memory Storage) ---
    private List<Student> students;
    private List<Subject> subjects;
    private List<Enrollment> enrollments;

    /**
     * Constructor ของ DataAccessService
     * เมื่อ Service นี้ถูกสร้างขึ้น จะทำการโหลดข้อมูลทั้งหมดจากไฟล์ CSV เข้ามาเก็บใน List ทันที
     */
    public DataAccessService() {
        this.students = loadData(STUDENTS_CSV, this::mapToStudent);
        this.subjects = loadData(SUBJECTS_CSV, this::mapToSubject);
        this.enrollments = loadData(ENROLLMENTS_CSV, this::mapToEnrollment);
    }

    /**
     * เมธอดทั่วไป (Generic Method) สำหรับโหลดข้อมูลจากไฟล์ CSV
     */
    private <T> List<T> loadData(String filePath, Function<String[], T> mapper) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            return reader.lines()
                         .skip(1)
                         .map(line -> line.split(","))
                         .map(mapper)
                         .collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("Error loading file: " + filePath);
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // --- กลุ่มเมธอดสำหรับแปลงข้อมูล (Mappers) ---

    private Student mapToStudent(String[] data) {
        Student student = new Student();
        student.setStudentId(data[0].trim());
        student.setPrefix(data[1].trim());
        student.setFirstName(data[2].trim());
        student.setLastName(data[3].trim());
        student.setDateOfBirth(LocalDate.parse(data[4].trim()));
        student.setSchool(data[5].trim());
        student.setEmail(data[6].trim());
        return student;
    }

    private Subject mapToSubject(String[] data) {
        Subject subject = new Subject();
        subject.setSubjectId(data[0].trim());
        subject.setSubjectName(data[1].trim());
        subject.setCredits(Integer.parseInt(data[2].trim()));
        subject.setInstructorName(data[3].trim());
        subject.setPrerequisiteId(data[4].trim().isEmpty() ? null : data[4].trim());
        subject.setMaxCapacity(Integer.parseInt(data[5].trim()));
        subject.setCurrentEnrollment(Integer.parseInt(data[6].trim()));
        return subject;
    }

    private Enrollment mapToEnrollment(String[] data) {
        Enrollment enrollment = new Enrollment();
        enrollment.setEnrollmentId(Integer.parseInt(data[0].trim()));
        enrollment.setStudentId(data[1].trim());
        enrollment.setSubjectId(data[2].trim());
        enrollment.setEnrollmentDate(LocalDateTime.parse(data[3].trim()));
        enrollment.setGrade(data.length > 4 ? data[4].trim() : "");
        return enrollment;
    }

    /**
     * เมธอดทั่วไป (Generic Method) สำหรับบันทึกข้อมูลลงไฟล์ CSV
     */
    private <T> void saveData(String filePath, List<T> data, String header, Function<T, String> formatter) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println(header);
            data.stream()
                .map(formatter)
                .forEach(writer::println);
        } catch (IOException e) {
            System.err.println("Error saving file: " + filePath);
            e.printStackTrace();
        }
    }

    /**
     * บันทึกข้อมูลวิชาทั้งหมดจากหน่วยความจำลงไฟล์ subjects.csv
     */
    public void saveSubjects() {
        String header = "subject_id,subject_name,credits,instructor_name,prerequisite_id,max_capacity,current_enrollment";
        saveData(SUBJECTS_CSV, subjects, header, s -> String.join(",",
                s.getSubjectId(), s.getSubjectName(), String.valueOf(s.getCredits()),
                s.getInstructorName(), s.getPrerequisiteId() == null ? "" : s.getPrerequisiteId(),
                String.valueOf(s.getMaxCapacity()), String.valueOf(s.getCurrentEnrollment())
        ));
    }

    /**
     * บันทึกข้อมูลการลงทะเบียนทั้งหมดจากหน่วยความจำลงไฟล์ enrollments.csv
     */
    public void saveEnrollments() {
        String header = "enrollment_id,student_id,subject_id,enrollment_date,grade";
        saveData(ENROLLMENTS_CSV, enrollments, header, e -> String.join(",",
                String.valueOf(e.getEnrollmentId()), e.getStudentId(), e.getSubjectId(),
                e.getEnrollmentDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                e.getGrade() == null ? "" : e.getGrade()
        ));
    }

    // --- กลุ่มเมธอดสาธารณะสำหรับให้ Service อื่นๆ เรียกใช้ข้อมูล ---

    public Optional<Student> findStudentById(String id) {
        return students.stream().filter(s -> s.getStudentId().equals(id)).findFirst();
    }
    
    public Optional<Subject> findSubjectById(String id) {
        return subjects.stream().filter(s -> s.getSubjectId().equals(id)).findFirst();
    }

    public List<Subject> getAllSubjects() {
        return new ArrayList<>(subjects);
    }

    public List<Enrollment> getAllEnrollments() {
        return new ArrayList<>(enrollments);
    }
    
    public List<Enrollment> getEnrollmentsByStudentId(String studentId) {
        return enrollments.stream()
                .filter(e -> e.getStudentId().equals(studentId))
                .collect(Collectors.toList());
    }

    /**
     * ค้นหาและคืนค่า List ของการลงทะเบียนทั้งหมดที่เกี่ยวข้องกับรหัสวิชาที่กำหนด
     * @param subjectId รหัสวิชาที่ต้องการค้นหา
     * @return List ของ Enrollment
     */
    public List<Enrollment> getEnrollmentsBySubjectId(String subjectId) {
        return enrollments.stream()
                .filter(e -> e.getSubjectId().equals(subjectId))
                .collect(Collectors.toList());
    }

    public void addEnrollment(Enrollment enrollment) {
        this.enrollments.add(enrollment);
    }

    public Optional<Enrollment> findEnrollment(String studentId, String subjectId) {
        return enrollments.stream()
            .filter(e -> e.getStudentId().equals(studentId) && e.getSubjectId().equals(subjectId))
            .findFirst();
    }

    public List<Student> getAllStudents() {
        return new ArrayList<>(students);
    }
}