package com.StudentRegistrationSystem.controller;

import javax.swing.*;

import com.StudentRegistrationSystem.model.*;
import com.StudentRegistrationSystem.view.MainFrame;

import java.util.List;
import java.util.stream.Collectors;

/**
 * คลาส Controller หลักของแอปพลิเคชัน
 * ทำหน้าที่เป็นตัวกลางเชื่อมระหว่าง View (ส่วนติดต่อผู้ใช้) และ Model (ส่วนจัดการข้อมูลและตรรกะ)
 * จัดการกับการกระทำทั้งหมดของผู้ใช้ เช่น การล็อกอิน การลงทะเบียน และการจัดการเกรด
 */
public class AppController {
    
    // ส่วนประกอบหลักที่ Controller ต้องใช้
    private MainFrame view;                     // อ้างอิงไปยังหน้าต่างหลักของโปรแกรม (View)
    private DataAccessService dataAccessService;  // Service สำหรับจัดการการเข้าถึงข้อมูล (อ่าน/เขียนไฟล์ CSV)
    private RegistrationService registrationService; // Service สำหรับจัดการตรรกะการลงทะเบียนของนักเรียน
    private AdminService adminService;          // Service สำหรับจัดการตรรกะของผู้ดูแลระบบ
    private Student currentUser;                // เก็บข้อมูลนักเรียนที่กำลังล็อกอินอยู่

    /**
     * Constructor ของ Controller
     * รับค่า View และ Services ต่างๆ เข้ามาเพื่อเชื่อมการทำงานทั้งหมดเข้าด้วยกัน
     *
     * @param view         หน้าต่างหลักของโปรแกรม
     * @param data         Service สำหรับการเข้าถึงข้อมูล
     * @param registration Service สำหรับการลงทะเบียน
     * @param admin        Service สำหรับผู้ดูแลระบบ
     */
    public AppController(MainFrame view, DataAccessService data, RegistrationService registration, AdminService admin) {
        this.view = view;
        this.dataAccessService = data;
        this.registrationService = registration;
        this.adminService = admin;
        initController(); // เรียกใช้เมธอดเพื่อกำหนดการทำงานเริ่มต้น
    }

    /**
     * กำหนดการทำงาน (Action Listener) ให้กับ Component ต่างๆ ในหน้า UI
     * เพื่อดักจับการกระทำของผู้ใช้ เช่น การคลิกปุ่ม
     */
    private void initController() {
        // --- กำหนดการทำงานในหน้า Login ---
        view.getLoginPanel().getLoginButton().addActionListener(e -> login());

        // --- กำหนดการทำงานในหน้าลงทะเบียนของนักเรียน ---
        view.getRegistrationPanel().getRegisterButton().addActionListener(e -> register());
        view.getRegistrationPanel().getLogoutButton().addActionListener(e -> logout());
        
        // --- กำหนดการทำงานในหน้าจัดการของผู้ดูแลระบบ ---
        view.getAdminPanel().getStudentComboBox().addActionListener(e -> handleStudentSelection());
        view.getAdminPanel().getSaveGradeButton().addActionListener(e -> saveGrade());
        view.getAdminPanel().getLogoutButton().addActionListener(e -> logout());
    }

    /**
     * จัดการกระบวนการล็อกอิน
     * ตรวจสอบว่า ID ที่ผู้ใช้กรอกเข้ามาเป็นของแอดมินหรือของนักเรียน
     */
    private void login() {
        String inputId = view.getLoginPanel().getStudentId();
        
        // ตรวจสอบว่าผู้ใช้กรอก ID เข้ามาหรือไม่
        if (inputId.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Please enter an ID.", "Login Error", JOptionPane.ERROR_MESSAGE);
            return; // จบการทำงานของเมธอด
        }

        // ตรวจสอบก่อนว่าเป็นแอดมินหรือไม่ (ไม่สนใจตัวพิมพ์เล็ก/ใหญ่)
        if (inputId.equalsIgnoreCase("admin")) {
            loginAsAdmin();
            return; // จบการทำงานของเมธอด
        }

        // หากไม่ใช่แอดมิน ให้ค้นหาข้อมูลนักเรียนจาก ID ที่กรอกเข้ามา
        currentUser = dataAccessService.findStudentById(inputId).orElse(null);
        
        // ถ้านักเรียนมีตัวตนในระบบ
        if (currentUser != null) {
            refreshStudentDashboard(); // โหลดข้อมูลทั้งหมดของนักเรียน
            view.showRegistrationPanel(); // แสดงหน้าลงทะเบียน
        } else {
            // ถ้าไม่พบนักเรียนในระบบ
            JOptionPane.showMessageDialog(view, "Student ID not found.", "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * เตรียมและแสดงหน้าจอสำหรับผู้ดูแลระบบ (Admin)
     */
    private void loginAsAdmin() {
        view.getAdminPanel().populateStudentList(dataAccessService.getAllStudents());
        view.getAdminPanel().getTableModel().setRowCount(0); // ล้างข้อมูลในตารางเมื่อล็อกอินเข้ามาใหม่
        view.showAdminPanel(); // แสดงหน้าแอดมิน
    }

    /**
     * จัดการ Event เมื่อแอดมินเลือกนักเรียนจาก ComboBox
     * ทำการอัปเดตตารางแสดงรายวิชาที่ลงทะเบียนของนักเรียนคนนั้นๆ
     */
    private void handleStudentSelection() {
        JComboBox<String> combo = view.getAdminPanel().getStudentComboBox();
        
        // ถ้าแอดมินเลือกตัวเลือกแรกที่ไม่ใช่นักเรียน (เช่น "-- Select a Student --")
        if (combo.getSelectedIndex() <= 0) {
            view.getAdminPanel().getTableModel().setRowCount(0); // ให้ล้างข้อมูลในตาราง
            return;
        }
        
        // ดึงรหัสนักเรียนออกมาจากข้อความใน ComboBox (รูปแบบ: "69000001 - John")
        String selectedItem = (String) combo.getSelectedItem();
        String studentId = selectedItem.split(" - ")[0];
        
        // ดึงข้อมูลการลงทะเบียนทั้งหมดของนักเรียนที่เลือก
        List<Enrollment> enrollments = dataAccessService.getEnrollmentsByStudentId(studentId);
        // อัปเดตตารางเพื่อแสดงข้อมูล
        view.getAdminPanel().updateEnrollmentsTable(enrollments, dataAccessService);
    }
    
    /**
     * จัดการการบันทึกเกรดที่แอดมินเป็นคนกำหนด
     */
    private void saveGrade() {
        int selectedRow = view.getAdminPanel().getEnrollmentsTable().getSelectedRow();
        
        // ตรวจสอบว่าแอดมินได้เลือกรายวิชาจากตารางแล้วหรือยัง
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(view, "Please select a subject from the table.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // ดึงข้อมูลที่จำเป็น (รหัสนักเรียน, รหัสวิชา, เกรด) จากหน้า UI
        String studentId = ((String) view.getAdminPanel().getStudentComboBox().getSelectedItem()).split(" - ")[0];
        String subjectId = (String) view.getAdminPanel().getTableModel().getValueAt(selectedRow, 0);
        String grade = view.getAdminPanel().getGrade();

        // เรียกใช้ Service เพื่อทำการอัปเดตเกรด และรับผลลัพธ์กลับมาเป็นข้อความ
        String result = adminService.updateGrade(studentId, subjectId, grade);

        // ตรวจสอบผลลัพธ์ที่ได้จาก Service
        if (result.startsWith("Success")) {
            JOptionPane.showMessageDialog(view, result, "Success", JOptionPane.INFORMATION_MESSAGE);
            handleStudentSelection(); // หากสำเร็จ ทำการรีเฟรชตารางเพื่อแสดงข้อมูลเกรดล่าสุด
        } else {
            // หากไม่สำเร็จ (เช่น เกรดไม่ถูกต้อง) ให้แสดงข้อความแจ้งเตือนตามที่ Service ส่งมา
            JOptionPane.showMessageDialog(view, result, "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * จัดการกระบวนการลงทะเบียนเรียนของนักเรียน
     */
    private void register() {
        int selectedRow = view.getRegistrationPanel().getSelectedAvailableSubjectRow();
        
        // ตรวจสอบว่านักเรียนได้เลือกวิชาที่ต้องการลงทะเบียนแล้วหรือยัง
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(view, "Please select a subject to register.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // ดึงรหัสวิชาจากแถวที่นักเรียนเลือกในตาราง
        String subjectId = (String) view.getRegistrationPanel().getAvailableSubjectsTableModel().getValueAt(selectedRow, 0);
        Subject selectedSubject = dataAccessService.findSubjectById(subjectId).orElse(null);

        // ตรวจสอบให้แน่ใจว่าข้อมูลผู้ใช้และวิชาที่เลือกไม่เป็นค่า null ก่อนดำเนินการ
        if (currentUser != null && selectedSubject != null) {
            // เรียกใช้ Service เพื่อดำเนินการลงทะเบียนและรับผลลัพธ์กลับมา
            String result = registrationService.registerStudent(currentUser, selectedSubject);
            
            if (result.startsWith("Success")) {
                 JOptionPane.showMessageDialog(view, result, "Success", JOptionPane.INFORMATION_MESSAGE);
                 refreshStudentDashboard(); // รีเฟรชข้อมูลทั้งหมดในหน้าของนักเรียน
            } else {
                 JOptionPane.showMessageDialog(view, result, "Registration Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * จัดการการออกจากระบบของผู้ใช้
     */
    private void logout() {
        currentUser = null; // ล้างข้อมูลผู้ใช้ปัจจุบัน
        view.getLoginPanel().clearFields(); // ล้างช่องกรอกข้อความในหน้าล็อกอิน
        view.showLoginPanel(); // กลับไปยังหน้าล็อกอิน
    }

    /**
     * รีเฟรชข้อมูลทั้งหมดที่แสดงบนหน้าจอของนักเรียน (Dashboard)
     * เมธอดนี้จะถูกเรียกใช้หลังจากการล็อกอินสำเร็จ และหลังจากการกระทำที่ทำให้ข้อมูลของนักเรียนเปลี่ยนแปลง
     */
    private void refreshStudentDashboard() {
        // ป้องกันการทำงานหากไม่มีผู้ใช้ล็อกอินอยู่ (Safety Check)
        if (currentUser == null) return;
        
        // อัปเดตส่วนแสดงข้อมูลส่วนตัวของนักเรียน
        view.getRegistrationPanel().updateStudentInformation(currentUser);
        
        // ดึงข้อมูลวิชาที่ลงทะเบียนแล้ว
        List<Enrollment> studentEnrollments = dataAccessService.getEnrollmentsByStudentId(currentUser.getStudentId());
        // อัปเดตตารางวิชาที่ลงทะเบียนแล้ว
        view.getRegistrationPanel().updateRegisteredCoursesTable(studentEnrollments, dataAccessService);

        // สร้างรายการรหัสวิชาที่ลงทะเบียนไปแล้ว เพื่อใช้ในการกรองวิชาที่ยังไม่ได้ลงทะเบียน
        List<String> enrolledSubjectIds = studentEnrollments.stream()
                .map(Enrollment::getSubjectId)
                .collect(Collectors.toList());
        
        // กรองวิชาทั้งหมดในระบบ ให้เหลือเฉพาะวิชาที่นักเรียนยังไม่ได้ลงทะเบียน
        List<Subject> availableSubjects = dataAccessService.getAllSubjects().stream()
                .filter(subject -> !enrolledSubjectIds.contains(subject.getSubjectId()))
                .collect(Collectors.toList());

        // อัปเดตตารางวิชาที่สามารถลงทะเบียนได้
        view.getRegistrationPanel().updateAvailableSubjectsTable(availableSubjects);
    }
}