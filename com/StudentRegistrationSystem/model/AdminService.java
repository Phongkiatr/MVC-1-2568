package com.StudentRegistrationSystem.model;

import java.util.Optional;
import java.util.Set;

/**
 * Service Class สำหรับจัดการตรรกะทางธุรกิจ (Business Logic) ที่เกี่ยวข้องกับผู้ดูแลระบบ (Admin)
 * เช่น การตรวจสอบและบันทึกเกรดของนักเรียน
 */
public class AdminService {

    /**
     * อ้างอิงไปยัง Service ที่จัดการการเข้าถึงข้อมูล
     * ใช้ final เพื่อให้แน่ใจว่าจะถูกกำหนดค่าเพียงครั้งเดียวใน Constructor
     */
    private final DataAccessService dataAccess;

    /**
     * กลุ่มของค่าเกรดที่ระบบอนุญาตให้ใช้งานได้
     * ใช้ `Set` เพื่อประสิทธิภาพในการค้นหาสูงสุด
     * `final` และ `static` เพราะเป็นค่าคงที่ที่ไม่เปลี่ยนแปลงและใช้ร่วมกันในทุก instance ของคลาสนี้
     * การมีค่า `""` (สตริงว่าง) หมายถึงระบบอนุญาตให้ "ลบเกรด" หรือ "ยังไม่ระบุเกรด" ได้
     */
    private static final Set<String> VALID_GRADES = Set.of(
        "A", "B+", "B", "C+", "C", "D+", "D", "F", ""
    );

    /**
     * Constructor ของ AdminService
     *
     * @param dataAccess Service สำหรับการเข้าถึงข้อมูล (Data Access Layer) ที่จะถูกฉีด (inject) เข้ามา
     */
    public AdminService(DataAccessService dataAccess) {
        this.dataAccess = dataAccess;
    }

    /**
     * อัปเดตเกรดสำหรับการลงทะเบียน (Enrollment) รายการใดรายการหนึ่ง หลังจากตรวจสอบความถูกต้องของค่าเกรดแล้ว
     *
     * @param studentId รหัสนักเรียน
     * @param subjectId รหัสวิชา
     * @param grade     เกรดใหม่ที่ต้องการบันทึก
     * @return ข้อความ (String) ที่บ่งบอกผลลัพธ์การทำงาน: สำเร็จ, เกรดไม่ถูกต้อง, หรือเกิดข้อผิดพลาด
     */
    public String updateGrade(String studentId, String subjectId, String grade) {
        
        // --- 1. การตรวจสอบและจัดรูปแบบข้อมูล (Validation & Formatting) ---
        // จัดรูปแบบเกรดที่รับเข้ามา: ตัดช่องว่างที่ไม่จำเป็นออก และแปลงเป็นตัวพิมพ์ใหญ่ทั้งหมด
        // เพื่อให้การตรวจสอบไม่ขึ้นอยู่กับตัวพิมพ์เล็ก/ใหญ่ (case-insensitive) เช่น 'a' หรือ 'b+'
        String formattedGrade = grade.trim().toUpperCase();
        
        // ตรวจสอบว่าเกรดที่จัดรูปแบบแล้วนั้น อยู่ในกลุ่มของเกรดที่ถูกต้อง (VALID_GRADES) หรือไม่
        if (!VALID_GRADES.contains(formattedGrade)) {
            // หากไม่อยู่ ให้ส่งคืนข้อความแจ้งเตือนและจบการทำงานทันที
            return "Error: Invalid grade. Please use A, B+, B, C+, C, D+, D, F.";
        }
        
        // --- 2. การดำเนินการกับข้อมูล (Data Manipulation) ---
        // หากเกรดถูกต้อง ให้ค้นหาข้อมูลการลงทะเบียนของนักเรียนในรายวิชานั้นๆ
        Optional<Enrollment> enrollmentOpt = dataAccess.findEnrollment(studentId, subjectId);
        
        // ตรวจสอบว่าค้นหาข้อมูลการลงทะเบียนเจอหรือไม่
        if (enrollmentOpt.isPresent()) {
            // หากเจอข้อมูล
            Enrollment enrollment = enrollmentOpt.get();
            enrollment.setGrade(formattedGrade); // อัปเดตค่าเกรดใน object
            dataAccess.saveEnrollments();        // เรียกใช้ DataAccessService เพื่อบันทึกการเปลี่ยนแปลงทั้งหมดลงไฟล์ CSV
            
            return "Success: Grade has been updated successfully!"; // ส่งคืนข้อความว่าทำรายการสำเร็จ
        } else {
            // หากไม่เจอข้อมูลการลงทะเบียน (กรณีนี้ไม่ควรเกิดขึ้นหาก UI ทำงานถูกต้อง แต่เป็นการป้องกันข้อผิดพลาดไว้)
            return "Error: Could not find the enrollment record.";
        }
    }
}