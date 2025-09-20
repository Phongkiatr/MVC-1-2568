package com.StudentRegistrationSystem.view;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;

import com.StudentRegistrationSystem.model.DataAccessService;
import com.StudentRegistrationSystem.model.Enrollment;
import com.StudentRegistrationSystem.model.Student;
import com.StudentRegistrationSystem.model.Subject;

import java.awt.*;
import java.util.List;

/**
 * คลาส View สำหรับสร้างหน้าจอการทำงานของนักเรียน (Registration Panel)
 * หรือที่เรียกว่า Dashboard ของนักเรียน ประกอบด้วยข้อมูลส่วนตัว,
 * รายวิชาที่สามารถลงทะเบียนได้, และรายวิชาที่ลงทะเบียนไปแล้ว
 */
public class RegistrationPanel extends JPanel {

    // --- Fields: ส่วนประกอบ UI (Components) ของหน้าจอ ---

    // ส่วนแสดงข้อมูลนักเรียน
    private JLabel studentIdLabel;
    private JLabel nameLabel;
    private JLabel schoolLabel;
    private JLabel emailLabel;
    private JLabel ageLabel;

    // ส่วนแสดงวิชา (ใช้ Tabbed Pane เพื่อแบ่งเป็นสัดส่วน)
    private JTabbedPane tabbedPane;

    // ส่วนประกอบของ Tab "วิชาที่ลงทะเบียนได้"
    private JTable availableSubjectsTable;
    private DefaultTableModel availableSubjectsTableModel;
    private JButton registerButton;
    
    // ส่วนประกอบของ Tab "วิชาของฉัน"
    private JTable registeredCoursesTable;
    private DefaultTableModel registeredCoursesTableModel;

    // ปุ่ม Logout (ใช้ร่วมกัน)
    private JButton logoutButton;

    /**
     * Constructor ของ RegistrationPanel
     * ทำหน้าที่สร้างและจัดวางส่วนประกอบ UI ทั้งหมดในหน้าจอ
     */
    public RegistrationPanel() {
        // กำหนด Layout หลักของ Panel เป็น BorderLayout และเพิ่มระยะห่างรอบๆ
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- 1. สร้าง Panel แสดงข้อมูลนักเรียน (ส่วนบน - NORTH) ---
        JPanel studentInfoPanel = new JPanel(new GridLayout(0, 2, 5, 5)); // GridLayout จัดเรียงเป็นตาราง 2 คอลัมน์
        Border titledBorder = BorderFactory.createTitledBorder("Student Information");
        studentInfoPanel.setBorder(titledBorder);

        // เพิ่ม Label และ Field สำหรับแสดงข้อมูลต่างๆ
        studentInfoPanel.add(new JLabel("Student ID:"));
        studentIdLabel = new JLabel("-"); // กำหนดค่าเริ่มต้นเป็น "-"
        studentInfoPanel.add(studentIdLabel);

        studentInfoPanel.add(new JLabel("Full Name:"));
        nameLabel = new JLabel("-");
        studentInfoPanel.add(nameLabel);

        studentInfoPanel.add(new JLabel("Age:"));
        ageLabel = new JLabel("-");
        studentInfoPanel.add(ageLabel);

        studentInfoPanel.add(new JLabel("School:"));
        schoolLabel = new JLabel("-");
        studentInfoPanel.add(schoolLabel);
        
        studentInfoPanel.add(new JLabel("Email:"));
        emailLabel = new JLabel("-");
        studentInfoPanel.add(emailLabel);

        // --- 2. สร้าง JTabbedPane (ส่วนกลาง - CENTER) ---
        tabbedPane = new JTabbedPane();

        // สร้างและเพิ่ม Tab ที่หนึ่ง: รายวิชาที่สามารถลงทะเบียนได้
        JPanel availableSubjectsPanel = createAvailableSubjectsPanel();
        tabbedPane.addTab("Available Subjects for Registration", availableSubjectsPanel);

        // สร้างและเพิ่ม Tab ที่สอง: รายวิชาที่ลงทะเบียนไปแล้ว
        JPanel registeredCoursesPanel = createRegisteredCoursesPanel();
        tabbedPane.addTab("My Registered Courses", registeredCoursesPanel);
        
        // --- 3. สร้าง Panel ปุ่ม Logout (ส่วนล่าง - SOUTH) ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // จัดปุ่มชิดขวา
        logoutButton = new JButton("Logout");
        logoutButton.setBackground(Color.RED);
        logoutButton.setForeground(Color.WHITE);
        bottomPanel.add(logoutButton);

        // --- 4. ประกอบ Panel ทั้งหมดเข้าด้วยกันบน Layout หลัก ---
        add(studentInfoPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * เมธอดช่วย (Helper Method) สำหรับสร้าง Panel ของ Tab "Available Subjects"
     * @return JPanel ที่มีตารางและปุ่มลงทะเบียน
     */
    private JPanel createAvailableSubjectsPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        
        // สร้างตาราง
        String[] columnNames = {"ID", "Name", "Credits", "Instructor", "Capacity", "Enrolled"};
        availableSubjectsTableModel = new DefaultTableModel(columnNames, 0) {
            // Override เมธอดนี้เพื่อป้องกันไม่ให้ผู้ใช้แก้ไขข้อมูลในตารางโดยตรง
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        availableSubjectsTable = new JTable(availableSubjectsTableModel);
        availableSubjectsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // เลือกได้ทีละแถว
        panel.add(new JScrollPane(availableSubjectsTable), BorderLayout.CENTER);
        
        // สร้าง Panel สำหรับปุ่ม
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        registerButton = new JButton("Register for Selected Subject");
        buttonPanel.add(registerButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }
    
    /**
     * เมธอดช่วย (Helper Method) สำหรับสร้าง Panel ของ Tab "My Registered Courses"
     * @return JPanel ที่มีตารางแสดงวิชาที่ลงทะเบียนแล้ว
     */
    private JPanel createRegisteredCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        
        // สร้างตาราง
        String[] columnNames = {"Subject ID", "Subject Name", "Credits", "Grade"};
        registeredCoursesTableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        registeredCoursesTable = new JTable(registeredCoursesTableModel);
        panel.add(new JScrollPane(registeredCoursesTable), BorderLayout.CENTER);
        
        return panel;
    }

    // --- เมธอดสาธารณะสำหรับให้ Controller เรียกใช้ ---

    public JButton getRegisterButton() { return registerButton; }
    public JButton getLogoutButton() { return logoutButton; }
    public int getSelectedAvailableSubjectRow() { return availableSubjectsTable.getSelectedRow(); }
    public DefaultTableModel getAvailableSubjectsTableModel() { return availableSubjectsTableModel; }

    /**
     * อัปเดตข้อมูลที่แสดงในส่วน Student Information Panel
     * @param student Object ของนักเรียนที่กำลังล็อกอินอยู่
     */
    public void updateStudentInformation(Student student) {
        studentIdLabel.setText(student.getStudentId());
        nameLabel.setText(student.getPrefix() + " " + student.getFirstName() + " " + student.getLastName());
        ageLabel.setText(student.getAge() + " years old");
        schoolLabel.setText(student.getSchool());
        emailLabel.setText(student.getEmail());
    }

    /**
     * อัปเดตข้อมูลในตาราง "Available Subjects"
     * @param subjects List ของรายวิชาที่นักเรียนยังไม่ได้ลงทะเบียน
     */
    public void updateAvailableSubjectsTable(List<Subject> subjects) {
        availableSubjectsTableModel.setRowCount(0); // ล้างข้อมูลเก่าในตาราง
        
        // วนลูปเพื่อเพิ่มข้อมูลแต่ละวิชาลงในตาราง
        for (Subject s : subjects) {
            // แปลงค่า maxCapacity ที่เป็น -1 ให้แสดงเป็น "Unlimited" เพื่อให้ผู้ใช้เข้าใจง่าย
            String capacity = s.getMaxCapacity() == -1 ? "Unlimited" : String.valueOf(s.getMaxCapacity());
            Object[] row = {
                s.getSubjectId(), s.getSubjectName(), s.getCredits(), 
                s.getInstructorName(), capacity, s.getCurrentEnrollment()
            };
            availableSubjectsTableModel.addRow(row);
        }
    }

    /**
     * อัปเดตข้อมูลในตาราง "My Registered Courses"
     * @param enrollments List ของการลงทะเบียนทั้งหมดของนักเรียนคนนั้น
     * @param dataAccess  Service สำหรับใช้ค้นหาข้อมูลเพิ่มเติม เช่น ชื่อวิชา
     */
    public void updateRegisteredCoursesTable(List<Enrollment> enrollments, DataAccessService dataAccess) {
        registeredCoursesTableModel.setRowCount(0); // ล้างข้อมูลเก่าในตาราง
        
        // วนลูปเพื่อเพิ่มข้อมูลการลงทะเบียนแต่ละรายการลงในตาราง
        for (Enrollment e : enrollments) {
            // ค้นหา Object ของ Subject เพื่อดึงข้อมูลชื่อวิชาและหน่วยกิตมาแสดง
            Subject subject = dataAccess.findSubjectById(e.getSubjectId()).orElse(null);
            
            if (subject != null) {
                Object[] row = {
                    subject.getSubjectId(),
                    subject.getSubjectName(),
                    subject.getCredits(),
                    // ตรวจสอบค่าเกรด ถ้าเป็น null หรือค่าว่าง ให้แสดงว่า "Not Graded"
                    e.getGrade() == null || e.getGrade().isEmpty() ? "Not Graded" : e.getGrade()
                };
                registeredCoursesTableModel.addRow(row);
            }
        }
    }
}