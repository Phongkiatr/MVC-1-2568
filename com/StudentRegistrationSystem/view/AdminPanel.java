package com.StudentRegistrationSystem.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.StudentRegistrationSystem.model.DataAccessService;
import com.StudentRegistrationSystem.model.Enrollment;
import com.StudentRegistrationSystem.model.Student;

import java.awt.*;
import java.util.List;

/**
 * คลาส View สำหรับสร้างหน้าจอการทำงานของผู้ดูแลระบบ (Admin Panel)
 * ประกอบด้วยส่วนประกอบ UI สำหรับเลือกนักเรียน, ดูรายวิชาที่ลงทะเบียน, และแก้ไขเกรด
 */
public class AdminPanel extends JPanel {

    // --- Fields: ส่วนประกอบ UI (Components) ของหน้าจอ ---

    private JComboBox<String> studentComboBox;    // Dropdown สำหรับเลือกนักเรียน
    private JTable enrollmentsTable;              // ตารางสำหรับแสดงรายวิชาที่นักเรียนลงทะเบียน
    private DefaultTableModel tableModel;         // โมเดลสำหรับจัดการข้อมูลในตาราง enrollmentsTable
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

        // --- 1. ส่วนบน (North): สำหรับการเลือกนักเรียน ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // จัดชิดซ้าย
        topPanel.add(new JLabel("Select Student:"));
        studentComboBox = new JComboBox<>();
        topPanel.add(studentComboBox);

        // --- 2. ส่วนกลาง (Center): สำหรับตารางแสดงข้อมูล ---
        String[] columnNames = {"Subject ID", "Subject Name", "Current Grade"};
        // สร้าง Table Model โดยป้องกันการแก้ไขข้อมูลในเซลล์โดยตรง
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        enrollmentsTable = new JTable(tableModel);
        enrollmentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // อนุญาตให้เลือกได้ทีละแถวเท่านั้น
        JScrollPane scrollPane = new JScrollPane(enrollmentsTable); // เพิ่ม Scroll bar ให้ตาราง

        // --- 3. ส่วนล่าง (South): สำหรับการกรอกเกรดและปุ่มคำสั่ง ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // จัดชิดขวา
        bottomPanel.add(new JLabel("Select Grade:"));

        // สร้าง Dropdown (ComboBox) สำหรับเกรด เพื่อป้องกันการกรอกข้อมูลผิดพลาด
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

    public JComboBox<String> getStudentComboBox() {
        return studentComboBox;
    }

    public JTable getEnrollmentsTable() {
        return enrollmentsTable;
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
    
    /**
     * ดึงค่าเกรดที่ถูกเลือกจาก ComboBox
     * @return ค่าเกรดที่เลือก (เช่น "A", "B+")
     */
    public String getGrade() {
        return (String) gradeComboBox.getSelectedItem();
    }
    
    /**
     * เติมรายชื่อนักเรียนทั้งหมดลงใน ComboBox สำหรับการเลือก
     * @param students List ของนักเรียนทั้งหมดในระบบ
     */
    public void populateStudentList(List<Student> students) {
        studentComboBox.removeAllItems(); // ล้างข้อมูลเก่าออกก่อน
        studentComboBox.addItem("-- Select a Student --"); // เพิ่มตัวเลือกเริ่มต้น
        for (Student student : students) {
            // เพิ่มนักเรียนแต่ละคนในรูปแบบ "ID - ชื่อ"
            studentComboBox.addItem(student.getStudentId() + " - " + student.getFirstName());
        }
    }
    
    /**
     * อัปเดตข้อมูลในตาราง enrollmentsTable ให้แสดงรายวิชาของนักเรียนที่ถูกเลือก
     * @param enrollments List ของการลงทะเบียนทั้งหมดของนักเรียนคนนั้น
     * @param dataAccess  Service สำหรับใช้ค้นหาชื่อวิชาจากรหัสวิชา
     */
    public void updateEnrollmentsTable(List<Enrollment> enrollments, DataAccessService dataAccess) {
        tableModel.setRowCount(0); // ล้างข้อมูลเก่าในตารางออกทั้งหมด
        
        // วนลูปเพื่อเพิ่มข้อมูลการลงทะเบียนแต่ละรายการลงในตาราง
        for (Enrollment e : enrollments) {
            // ค้นหาชื่อวิชาจากรหัสวิชา เพื่อการแสดงผลที่เป็นมิตรต่อผู้ใช้
            String subjectName = dataAccess.findSubjectById(e.getSubjectId())
                                           .map(s -> s.getSubjectName())
                                           .orElse("Unknown Subject"); // หากไม่พบชื่อวิชา
            
            Object[] row = {e.getSubjectId(), subjectName, e.getGrade()};
            tableModel.addRow(row); // เพิ่มแถวใหม่ลงในตาราง
        }
    }
}