package com.example.model;

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
     * สามารถใช้กับไฟล์ข้อมูลประเภทใดก็ได้ โดยรับ 'mapper' function เข้ามาเพื่อแปลงข้อมูลแต่ละแถวให้เป็น Object ที่ต้องการ
     *
     * @param filePath เส้นทางไปยังไฟล์ CSV
     * @param mapper   ฟังก์ชันสำหรับแปลงอาร์เรย์ของ String (ข้อมูลในแถว) ให้เป็น Object ประเภท T
     * @param <T>      ประเภทของ Object ที่ต้องการ (เช่น Student, Subject)
     * @return List ของ Object ที่โหลดมาจากไฟล์
     */
    private <T> List<T> loadData(String filePath, Function<String[], T> mapper) {
        // ใช้ try-with-resources เพื่อให้แน่ใจว่า BufferedReader จะถูกปิดโดยอัตโนมัติ
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            return reader.lines()
                         .skip(1) // ข้ามแถวแรกซึ่งเป็น header
                         .map(line -> line.split(",")) // ตัดแต่ละบรรทัดด้วยจุลภาค (,) ให้เป็นอาร์เรย์ของ String
                         .map(mapper) // เรียกใช้ mapper function ที่รับเข้ามาเพื่อแปลงอาร์เรย์เป็น Object
                         .collect(Collectors.toList()); // รวบรวม Object ทั้งหมดเก็บใน List
        } catch (IOException e) {
            System.err.println("Error loading file: " + filePath);
            e.printStackTrace();
            return new ArrayList<>(); // หากเกิดข้อผิดพลาด ให้คืนค่าเป็น List ว่าง
        }
    }

    // --- กลุ่มเมธอดสำหรับแปลงข้อมูล (Mappers) ---

    /**
     * แปลงข้อมูลจากอาร์เรย์ของ String ให้เป็น Object 'Student'
     * @param data อาร์เรย์ของ String ที่ได้จากการแบ่งแถวในไฟล์ students.csv
     * @return Object 'Student' ที่มีข้อมูลครบถ้วน
     */
    private Student mapToStudent(String[] data) {
        Student student = new Student();
        student.setStudentId(data[0]);
        student.setPrefix(data[1]);
        student.setFirstName(data[2]);
        student.setLastName(data[3]);
        student.setDateOfBirth(LocalDate.parse(data[4]));
        student.setSchool(data[5]);
        student.setEmail(data[6]);
        return student;
    }

    /**
     * แปลงข้อมูลจากอาร์เรย์ของ String ให้เป็น Object 'Subject'
     * @param data อาร์เรย์ของ String ที่ได้จากการแบ่งแถวในไฟล์ subjects.csv
     * @return Object 'Subject' ที่มีข้อมูลครบถ้วน
     */
    private Subject mapToSubject(String[] data) {
        Subject subject = new Subject();
        subject.setSubjectId(data[0]);
        subject.setSubjectName(data[1]);
        subject.setCredits(Integer.parseInt(data[2]));
        subject.setInstructorName(data[3]);
        // ตรวจสอบว่ามีวิชาบังคับก่อนหรือไม่ ถ้าไม่มีให้เป็น null
        subject.setPrerequisiteId(data[4].isEmpty() ? null : data[4]);
        subject.setMaxCapacity(Integer.parseInt(data[5].trim()));
        subject.setCurrentEnrollment(Integer.parseInt(data[6]));
        return subject;
    }

    /**
     * แปลงข้อมูลจากอาร์เรย์ของ String ให้เป็น Object 'Enrollment'
     * @param data อาร์เรย์ของ String ที่ได้จากการแบ่งแถวในไฟล์ enrollments.csv
     * @return Object 'Enrollment' ที่มีข้อมูลครบถ้วน
     */
    private Enrollment mapToEnrollment(String[] data) {
        Enrollment enrollment = new Enrollment();
        enrollment.setEnrollmentId(Integer.parseInt(data[0]));
        enrollment.setStudentId(data[1]);
        enrollment.setSubjectId(data[2]);
        enrollment.setEnrollmentDate(LocalDateTime.parse(data[3]));
        // ตรวจสอบว่ามีคอลัมน์เกรดหรือไม่ (เพื่อรองรับไฟล์เก่า) ถ้าไม่มีให้เป็นค่าว่าง
        enrollment.setGrade(data.length > 4 ? data[4] : "");
        return enrollment;
    }

    /**
     * เมธอดทั่วไป (Generic Method) สำหรับบันทึกข้อมูลลงไฟล์ CSV
     * @param filePath  เส้นทางไปยังไฟล์ CSV ที่ต้องการบันทึก
     * @param data      List ของ Object ที่ต้องการบันทึก
     * @param header    ข้อความส่วนหัวของไฟล์ CSV
     * @param formatter ฟังก์ชันสำหรับแปลง Object ประเภท T กลับไปเป็น String ในรูปแบบ CSV
     * @param <T>       ประเภทของ Object ที่ต้องการบันทึก
     */
    private <T> void saveData(String filePath, List<T> data, String header, Function<T, String> formatter) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println(header); // เขียน header ก่อน
            data.stream()
                .map(formatter) // แปลง Object แต่ละตัวใน List เป็น String
                .forEach(writer::println); // เขียน String แต่ละบรรทัดลงไฟล์
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
                s.getSubjectId(),
                s.getSubjectName(),
                String.valueOf(s.getCredits()),
                s.getInstructorName(),
                s.getPrerequisiteId() == null ? "" : s.getPrerequisiteId(), // แปลงค่า null เป็นสตริงว่าง
                String.valueOf(s.getMaxCapacity()),
                String.valueOf(s.getCurrentEnrollment())
        ));
    }

    /**
     * บันทึกข้อมูลการลงทะเบียนทั้งหมดจากหน่วยความจำลงไฟล์ enrollments.csv
     */
    public void saveEnrollments() {
        String header = "enrollment_id,student_id,subject_id,enrollment_date,grade";
        saveData(ENROLLMENTS_CSV, enrollments, header, e -> String.join(",",
                String.valueOf(e.getEnrollmentId()),
                e.getStudentId(),
                e.getSubjectId(),
                e.getEnrollmentDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), // จัดรูปแบบวันที่และเวลามาตรฐาน
                e.getGrade() == null ? "" : e.getGrade() // แปลงค่า null เป็นสตริงว่าง
        ));
    }

    // --- กลุ่มเมธอดสำหรับให้ Service อื่นๆ เรียกใช้ข้อมูล ---

    public Optional<Student> findStudentById(String id) {
        return students.stream().filter(s -> s.getStudentId().equals(id)).findFirst();
    }
    
    public Optional<Subject> findSubjectById(String id) {
        return subjects.stream().filter(s -> s.getSubjectId().equals(id)).findFirst();
    }

    public List<Subject> getAllSubjects() {
        return new ArrayList<>(subjects); // คืนค่าเป็น new ArrayList เพื่อป้องกันการแก้ไข List หลักจากภายนอก
    }

    public List<Enrollment> getAllEnrollments() {
        return new ArrayList<>(enrollments);
    }
    
    public List<Enrollment> getEnrollmentsByStudentId(String studentId) {
        return enrollments.stream()
                .filter(e -> e.getStudentId().equals(studentId))
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