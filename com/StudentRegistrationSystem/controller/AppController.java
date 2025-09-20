package com.StudentRegistrationSystem.controller;

import javax.swing.*;
import com.StudentRegistrationSystem.model.*;
import com.StudentRegistrationSystem.view.MainFrame;
import java.util.List;
import java.util.stream.Collectors;

/**
 * คลาส Controller หลักของแอปพลิเคชัน
 * ทำหน้าที่เป็นตัวกลางเชื่อมระหว่าง View (ส่วนติดต่อผู้ใช้) และ Model (ส่วนจัดการข้อมูลและตรรกะ)
 * จัดการกับการกระทำทั้งหมดของผู้ใช้ เช่น การล็อกอิน, การลงทะเบียน, และการจัดการเกรด
 */
public class AppController {
    
    // ส่วนประกอบหลักที่ Controller ต้องใช้
    private MainFrame view;
    private DataAccessService dataAccessService;
    private RegistrationService registrationService;
    private AdminService adminService;
    private Student currentUser;

    /**
     * Constructor ของ Controller
     * รับค่า View และ Services ต่างๆ เข้ามาเพื่อเชื่อมการทำงานทั้งหมดเข้าด้วยกัน
     */
    public AppController(MainFrame view, DataAccessService data, RegistrationService registration, AdminService admin) {
        this.view = view;
        this.dataAccessService = data;
        this.registrationService = registration;
        this.adminService = admin;
        initController();
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
        view.getAdminPanel().getSubjectComboBox().addActionListener(e -> handleSubjectSelection());
        view.getAdminPanel().getSaveGradeButton().addActionListener(e -> saveGradeForSelectedStudent());
        view.getAdminPanel().getLogoutButton().addActionListener(e -> logout());
    }

    /**
     * จัดการกระบวนการล็อกอิน
     * ตรวจสอบว่า ID ที่ผู้ใช้กรอกเข้ามาเป็นของแอดมินหรือของนักเรียน
     */
    private void login() {
        String inputId = view.getLoginPanel().getStudentId();
        
        if (inputId.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Please enter an ID.", "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (inputId.equalsIgnoreCase("admin")) {
            loginAsAdmin();
            return;
        }

        currentUser = dataAccessService.findStudentById(inputId).orElse(null);
        
        if (currentUser != null) {
            refreshStudentDashboard();
            view.showRegistrationPanel();
        } else {
            JOptionPane.showMessageDialog(view, "Student ID not found.", "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * เตรียมและแสดงหน้าจอสำหรับผู้ดูแลระบบ (Admin)
     */
    private void loginAsAdmin() {
        view.getAdminPanel().populateSubjectList(dataAccessService.getAllSubjects());
        view.getAdminPanel().getTableModel().setRowCount(0);
        view.showAdminPanel();
    }

    /**
     * จัดการ Event เมื่อแอดมินเลือกรายวิชาจาก ComboBox
     * ทำการอัปเดตตารางแสดงรายชื่อนักเรียนที่ลงทะเบียนในวิชานั้นๆ
     */
    private void handleSubjectSelection() {
        JComboBox<String> combo = view.getAdminPanel().getSubjectComboBox();
        
        if (combo.getSelectedIndex() <= 0) {
            view.getAdminPanel().getTableModel().setRowCount(0);
            return;
        }
        
        String selectedItem = (String) combo.getSelectedItem();
        String subjectId = selectedItem.split(" - ")[0];
        
        List<Enrollment> enrollments = dataAccessService.getEnrollmentsBySubjectId(subjectId);
        
        view.getAdminPanel().updateStudentsTable(enrollments, dataAccessService);
    }
    
    /**
     * จัดการการบันทึกเกรดสำหรับนักเรียนที่ถูกเลือกในตาราง
     */
    private void saveGradeForSelectedStudent() {
        int selectedRow = view.getAdminPanel().getStudentsTable().getSelectedRow();
        
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(view, "Please select a student from the table.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String subjectId = ((String) view.getAdminPanel().getSubjectComboBox().getSelectedItem()).split(" - ")[0];
        String studentId = (String) view.getAdminPanel().getTableModel().getValueAt(selectedRow, 0);
        String grade = view.getAdminPanel().getGrade();

        String result = adminService.updateGrade(studentId, subjectId, grade);

        if (result.startsWith("Success")) {
            JOptionPane.showMessageDialog(view, result, "Success", JOptionPane.INFORMATION_MESSAGE);
            handleSubjectSelection(); // รีเฟรชตารางเพื่อแสดงเกรดใหม่
        } else {
            JOptionPane.showMessageDialog(view, result, "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * จัดการกระบวนการลงทะเบียนเรียนของนักเรียน
     */
    private void register() {
        int selectedRow = view.getRegistrationPanel().getSelectedAvailableSubjectRow();
        
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(view, "Please select a subject to register.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String subjectId = (String) view.getRegistrationPanel().getAvailableSubjectsTableModel().getValueAt(selectedRow, 0);
        Subject selectedSubject = dataAccessService.findSubjectById(subjectId).orElse(null);

        if (currentUser != null && selectedSubject != null) {
            String result = registrationService.registerStudent(currentUser, selectedSubject);
            
            if (result.startsWith("Success")) {
                 JOptionPane.showMessageDialog(view, result, "Success", JOptionPane.INFORMATION_MESSAGE);
                 refreshStudentDashboard();
            } else {
                 JOptionPane.showMessageDialog(view, result, "Registration Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * จัดการการออกจากระบบของผู้ใช้
     */
    private void logout() {
        currentUser = null;
        view.getLoginPanel().clearFields();
        view.showLoginPanel();
    }

    /**
     * รีเฟรชข้อมูลทั้งหมดที่แสดงบนหน้าจอของนักเรียน (Dashboard)
     * เมธอดนี้จะถูกเรียกใช้หลังจากการล็อกอินสำเร็จ และหลังจากการกระทำที่ทำให้ข้อมูลของนักเรียนเปลี่ยนแปลง
     */
    private void refreshStudentDashboard() {
        if (currentUser == null) return;
        
        view.getRegistrationPanel().updateStudentInformation(currentUser);
        
        List<Enrollment> studentEnrollments = dataAccessService.getEnrollmentsByStudentId(currentUser.getStudentId());
        view.getRegistrationPanel().updateRegisteredCoursesTable(studentEnrollments, dataAccessService);

        List<String> enrolledSubjectIds = studentEnrollments.stream()
                .map(Enrollment::getSubjectId)
                .collect(Collectors.toList());
        
        List<Subject> availableSubjects = dataAccessService.getAllSubjects().stream()
                .filter(subject -> !enrolledSubjectIds.contains(subject.getSubjectId()))
                .collect(Collectors.toList());

        view.getRegistrationPanel().updateAvailableSubjectsTable(availableSubjects);
    }
}