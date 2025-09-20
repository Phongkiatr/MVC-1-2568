package com.StudentRegistrationSystem.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.StudentRegistrationSystem.model.DataAccessService;
import com.StudentRegistrationSystem.model.Enrollment;
import com.StudentRegistrationSystem.model.Student;
import com.StudentRegistrationSystem.model.Subject;

import java.awt.*;
import java.util.List;

/**
 * คลาส View สำหรับสร้างหน้าจอการทำงานของผู้ดูแลระบบ (Admin Panel)
 * ประกอบด้วยส่วนประกอบ UI สำหรับเลือกรายวิชา, ดูรายชื่อนักเรียนที่ลงทะเบียนในวิชานั้น, และแก้ไขเกรด
 */
public class AdminPanel extends JPanel {

    // --- Fields: ส่วนประกอบ UI (Components) ของหน้าจอ ---

    private JComboBox<String> subjectComboBox;    // Dropdown สำหรับเลือกรายวิชา
    private JTable studentsTable;                 // ตารางสำหรับแสดงรายชื่อนักเรียนในวิชาที่เลือก
    private DefaultTableModel tableModel;         // โมเดลสำหรับจัดการข้อมูลในตาราง
    private JComboBox<String> gradeComboBox;      // Dropdown สำหรับเลือกเกรดที่จะบันทึก
    private JButton saveGradeButton;              // ปุ่มสำหรับบันทึกเกรด
    private JButton logoutButton;                 // ปุ่มสำหรับออกจากระบบ

    /**
     * Constructor ของ AdminPanel
     * ทำหน้าที่สร้างและจัดวางส่วนประกอบ UI ทั้งหมดในหน้าจอ
     */
    public AdminPanel() {
        // กำหนด Layout หลักของ Panel เป็น BorderLayout และเพิ่มระยะห่างรอบๆ
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- 1. ส่วนบน (North): สำหรับการเลือกรายวิชา ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Select Subject:"));
        subjectComboBox = new JComboBox<>();
        topPanel.add(subjectComboBox);

        // --- 2. ส่วนกลาง (Center): สำหรับตารางแสดงข้อมูลนักเรียน ---
        String[] columnNames = {"Student ID", "Student Name", "Current Grade"};
        // สร้าง Table Model โดยป้องกันการแก้ไขข้อมูลในเซลล์โดยตรง
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        studentsTable = new JTable(tableModel);
        studentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(studentsTable);

        // --- 3. ส่วนล่าง (South): สำหรับการเลือกเกรดและปุ่มคำสั่ง ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(new JLabel("Select Grade:"));
        String[] validGrades = {"", "A", "B+", "B", "C+", "C", "D+", "D", "F"};
        gradeComboBox = new JComboBox<>(validGrades);
        bottomPanel.add(gradeComboBox);
        
        saveGradeButton = new JButton("Save Grade");
        logoutButton = new JButton("Logout");
        bottomPanel.add(saveGradeButton);
        bottomPanel.add(logoutButton);
        
        // --- 4. ประกอบ Panel ทั้งหมดเข้าด้วยกัน ---
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // --- Public Methods: เมธอดสาธารณะสำหรับให้ Controller เรียกใช้ ---

    public JComboBox<String> getSubjectComboBox() {
        return subjectComboBox;
    }

    public JTable getStudentsTable() {
        return studentsTable;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JButton getSaveGradeButton() {
        return saveGradeButton;
    }

    public JButton getLogoutButton() {
        return logoutButton;
    }

    public String getGrade() {
        return (String) gradeComboBox.getSelectedItem();
    }
    
    /**
     * เติมรายชื่อวิชาทั้งหมดลงใน ComboBox สำหรับการเลือก
     * @param subjects List ของวิชาทั้งหมดในระบบ
     */
    public void populateSubjectList(List<Subject> subjects) {
        subjectComboBox.removeAllItems();
        subjectComboBox.addItem("-- Select a Subject --");
        for (Subject subject : subjects) {
            subjectComboBox.addItem(subject.getSubjectId() + " - " + subject.getSubjectName());
        }
    }
    
    /**
     * อัปเดตข้อมูลในตารางให้แสดงรายชื่อนักเรียนของวิชาที่ถูกเลือก
     * @param enrollments List ของการลงทะเบียนทั้งหมดของวิชานั้น
     * @param dataAccess  Service สำหรับใช้ค้นหาชื่อนักเรียนจากรหัสนักเรียน
     */
    public void updateStudentsTable(List<Enrollment> enrollments, DataAccessService dataAccess) {
        tableModel.setRowCount(0);
        
        for (Enrollment e : enrollments) {
            // ค้นหาชื่อนักเรียนจากรหัสนักเรียน เพื่อการแสดงผลที่เป็นมิตรต่อผู้ใช้
            String studentName = dataAccess.findStudentById(e.getStudentId())
                                           .map(s -> s.getFirstName() + " " + s.getLastName())
                                           .orElse("Unknown Student");
            
            Object[] row = {e.getStudentId(), studentName, e.getGrade()};
            tableModel.addRow(row);
        }
    }
}