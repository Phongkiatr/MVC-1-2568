package com.example.model;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service Class สำหรับจัดการตรรกะทางธุรกิจ (Business Logic) ที่เกี่ยวข้องกับการลงทะเบียนเรียนของนักเรียน
 * คลาสนี้จะรวบรวมกฎเกณฑ์และเงื่อนไขทั้งหมดที่จำเป็นสำหรับการลงทะเบียน
 */
public class RegistrationService {

    /**
     * อ้างอิงไปยัง Service ที่จัดการการเข้าถึงข้อมูล
     * ใช้ final เพื่อให้แน่ใจว่าจะถูกกำหนดค่าเพียงครั้งเดียวใน Constructor
     */
    private final DataAccessService dataAccess;
    
    /**
     * ตัวนับสำหรับสร้าง ID ของการลงทะเบียนรายการใหม่ (Enrollment ID)
     * ใช้ `AtomicInteger` เพื่อให้แน่ใจว่าการเพิ่มค่า ID จะปลอดภัยในสภาพแวดล้อมที่มีหลายเธรด (Thread-safe)
     * `static` หมายความว่า ID นี้จะถูกใช้ร่วมกันในทุกๆ instance ของคลาสนี้ (มีตัวนับเพียงตัวเดียว)
     */
    private static final AtomicInteger lastEnrollmentId = new AtomicInteger(0);

    /**
     * Constructor ของ RegistrationService
     *
     * @param dataAccess Service สำหรับการเข้าถึงข้อมูลที่จะถูกรับเข้ามา
     */
    public RegistrationService(DataAccessService dataAccess) {
        this.dataAccess = dataAccess;
        
        // --- การกำหนดค่าเริ่มต้นให้กับตัวนับ ID ---
        // ค้นหาค่า enrollmentId สูงสุดที่มีอยู่เดิมในระบบ เพื่อให้ตัวนับเริ่มต้นจากค่าที่ถูกต้อง
        // ป้องกันปัญหา ID ซ้ำซ้อนหลังจากโปรแกรมถูกปิดและเปิดใหม่
        dataAccess.getAllEnrollments().stream()
                .mapToInt(Enrollment::getEnrollmentId) // แปลง Stream ของ Enrollment เป็น Stream ของ int (ID)
                .max() // ค้นหาค่าที่มากที่สุด
                .ifPresent(lastEnrollmentId::set); // หากมีค่าสูงสุดอยู่ ให้กำหนดค่านั้นเป็นค่าเริ่มต้นของตัวนับ
    }

    /**
     * ดำเนินการลงทะเบียนเรียนให้นักเรียนหนึ่งคนสำหรับรายวิชาหนึ่งวิชา
     * โดยจะมีการตรวจสอบกฎเกณฑ์ต่างๆ ก่อนทำการลงทะเบียน
     *
     * @param student นักเรียนที่ต้องการลงทะเบียน
     * @param subject รายวิชาที่ต้องการลงทะเบียน
     * @return ข้อความ (String) ที่บ่งบอกผลลัพธ์การทำงาน: สำเร็จ หรือ ข้อผิดพลาดพร้อมเหตุผล
     */
    public String registerStudent(Student student, Subject subject) {
        
        // --- กฎข้อที่ 1: ตรวจสอบว่านักเรียนเคยลงทะเบียนวิชานี้แล้วหรือไม่ ---
        boolean isAlreadyEnrolled = dataAccess.getEnrollmentsByStudentId(student.getStudentId())
            .stream().anyMatch(e -> e.getSubjectId().equals(subject.getSubjectId()));
        if (isAlreadyEnrolled) {
            return "Error: You are already enrolled in this subject.";
        }

        // --- กฎข้อที่ 2: ตรวจสอบอายุของนักเรียนต้องไม่ต่ำกว่า 15 ปี ---
        if (student.getAge() < 15) { // เรียกใช้ getAge() จาก Student Model โดยตรง
            return "Error: Student must be at least 15 years old.";
        }

        // --- กฎข้อที่ 3: ตรวจสอบว่ารายวิชาเต็มแล้วหรือไม่ ---
        // เงื่อนไขนี้จะทำงานก็ต่อเมื่อวิชามีการจำกัดจำนวนคน (maxCapacity != -1)
        if (subject.getMaxCapacity() != -1 && subject.getCurrentEnrollment() >= subject.getMaxCapacity()) {
            return "Error: Course is full.";
        }

        // --- กฎข้อที่ 4: ตรวจสอบว่านักเรียนผ่านวิชาบังคับก่อน (Prerequisite) แล้วหรือยัง ---
        if (subject.getPrerequisiteId() != null && !subject.getPrerequisiteId().trim().isEmpty()) {
            // ค้นหาในรายการลงทะเบียนของนักเรียน ว่ามีรหัสวิชาที่ตรงกับวิชาบังคับก่อนหรือไม่
            boolean hasPrerequisite = dataAccess.getEnrollmentsByStudentId(student.getStudentId())
                    .stream()
                    .anyMatch(e -> e.getSubjectId().equals(subject.getPrerequisiteId()));
            if (!hasPrerequisite) {
                 return "Error: Prerequisite '" + subject.getPrerequisiteId() + "' not met.";
            }
        }
        
        // --- หากผ่านการตรวจสอบทุกข้อ: ดำเนินการลงทะเบียน ---
        Enrollment newEnrollment = new Enrollment();
        newEnrollment.setEnrollmentId(lastEnrollmentId.incrementAndGet()); // เพิ่มค่า ID อย่างปลอดภัยและนำค่าใหม่มาใช้
        newEnrollment.setStudentId(student.getStudentId());
        newEnrollment.setSubjectId(subject.getSubjectId());
        newEnrollment.setEnrollmentDate(LocalDateTime.now()); // กำหนดวันที่และเวลาที่ลงทะเบียนเป็นปัจจุบัน
        
        // เพิ่มข้อมูลการลงทะเบียนใหม่เข้าไปใน List ที่อยู่ในหน่วยความจำ
        dataAccess.addEnrollment(newEnrollment);
        // อัปเดตจำนวนคนลงทะเบียนปัจจุบันใน Object ของวิชานั้นๆ
        subject.setCurrentEnrollment(subject.getCurrentEnrollment() + 1);

        // --- บันทึกการเปลี่ยนแปลงทั้งหมดลงไฟล์ CSV ---
        dataAccess.saveEnrollments(); // บันทึกข้อมูลการลงทะเบียนใหม่
        dataAccess.saveSubjects();    // บันทึกจำนวนคนที่อัปเดตแล้วของวิชา
        
        // ส่งคืนข้อความว่าทำรายการสำเร็จ
        return "Success: Registered successfully in " + subject.getSubjectName() + "!";
    }
}