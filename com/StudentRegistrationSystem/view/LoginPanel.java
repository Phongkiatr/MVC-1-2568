package com.StudentRegistrationSystem.view;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * คลาส View สำหรับสร้างหน้าจอสำหรับเข้าสู่ระบบ (Login Panel)
 * ใช้ GridBagLayout ซ้อนกันเพื่อจัดวางฟอร์มล็อกอินให้อยู่กึ่งกลางของหน้าจออย่างสวยงาม
 */
public class LoginPanel extends JPanel {

    // --- Fields: ส่วนประกอบ UI (Components) ของหน้าจอ ---
    private JTextField studentIdField; // ช่องสำหรับกรอกรหัสนักเรียน หรือ 'admin'
    private JButton loginButton;       // ปุ่มสำหรับยืนยันการเข้าสู่ระบบ

    /**
     * Constructor ของ LoginPanel
     * ทำหน้าที่สร้างและจัดวางส่วนประกอบ UI ทั้งหมดในหน้าจอ
     */
    public LoginPanel() {
        // --- 1. การตั้งค่า Panel หลัก ---
        // กำหนด Layout หลักของ Panel เป็น GridBagLayout เพื่อใช้ในการจัดตำแหน่ง Component ให้อยู่กึ่งกลาง
        setLayout(new GridBagLayout());
        GridBagConstraints gbcMain = new GridBagConstraints();

        // --- 2. การสร้าง Panel สำหรับฟอร์ม ---
        // สร้าง Panel ขึ้นมาอีกชั้นสำหรับจัดวางฟอร์มโดยเฉพาะ ทำให้ง่ายต่อการจัดการ
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbcForm = new GridBagConstraints();

        // --- 3. การตกแต่ง Panel ฟอร์ม ---
        // สร้างเส้นขอบแบบผสม: มีทั้งหัวข้อ "Login" และมีระยะห่างภายใน (Padding) เพื่อความสวยงาม
        Border titledBorder = BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Login");
        Border padding = BorderFactory.createEmptyBorder(20, 20, 20, 20); // ระยะห่าง 20px รอบด้าน
        formPanel.setBorder(BorderFactory.createCompoundBorder(titledBorder, padding));

        // --- 4. การจัดวาง Component ภายใน formPanel ---

        // แถวที่ 0, คอลัมน์ที่ 0: ป้ายกำกับ (Label)
        JLabel idLabel = new JLabel("Student ID / 'admin':");
        gbcForm.gridx = 0; // ตำแหน่งคอลัมน์
        gbcForm.gridy = 0; // ตำแหน่งแถว
        gbcForm.anchor = GridBagConstraints.WEST; // anchor: จัดตำแหน่ง Component ให้ชิดซ้ายของพื้นที่เซลล์
        gbcForm.insets = new Insets(5, 5, 5, 10); // insets: กำหนดระยะห่างรอบ Component (บน, ซ้าย, ล่าง, ขวา)
        formPanel.add(idLabel, gbcForm);

        // แถวที่ 0, คอลัมน์ที่ 1: ช่องกรอกข้อความ (Text Field)
        studentIdField = new JTextField(15); // กำหนดขนาดเริ่มต้นที่เหมาะสม
        gbcForm.gridx = 1;
        gbcForm.gridy = 0;
        gbcForm.fill = GridBagConstraints.HORIZONTAL; // fill: กำหนดให้ Component ขยายเต็มความกว้างของพื้นที่เซลล์
        gbcForm.weightx = 1.0; // weightx: ให้น้ำหนักการขยายในแนวนอนแก่ Component นี้ เมื่อมีการปรับขนาดหน้าต่าง
        gbcForm.insets = new Insets(5, 5, 5, 5);
        formPanel.add(studentIdField, gbcForm);

        // แถวที่ 1, คอลัมน์ที่ 1: ปุ่ม (Button)
        loginButton = new JButton("Login");
        gbcForm.gridx = 1;
        gbcForm.gridy = 1;
        gbcForm.fill = GridBagConstraints.NONE; // ไม่ต้องขยายปุ่ม
        gbcForm.anchor = GridBagConstraints.EAST; // anchor: จัดตำแหน่ง Component ให้ชิดขวาของพื้นที่เซลล์
        gbcForm.weightx = 0; // ไม่ต้องให้น้ำหนักการขยาย
        gbcForm.insets = new Insets(10, 5, 5, 5); // เพิ่มระยะห่างด้านบน เพื่อให้ปุ่มไม่ติดกับช่องกรอกข้อความ
        formPanel.add(loginButton, gbcForm);

        // --- 5. การนำ formPanel ไปวางบน Panel หลัก ---
        // นำ formPanel ที่สร้างเสร็จแล้ว ไปใส่ใน Panel หลัก ซึ่งจะถูกจัดให้อยู่กึ่งกลางโดยอัตโนมัติ
        add(formPanel, gbcMain);
    }

    // --- Public Methods: เมธอดสาธารณะสำหรับให้ Controller เรียกใช้ ---
    
    /**
     * ดึงข้อความ ID ที่ผู้ใช้กรอกเข้ามา
     * @return รหัสนักเรียน หรือ 'admin' ที่ถูกตัดช่องว่างหน้า-หลังแล้ว
     */
    public String getStudentId() {
        return studentIdField.getText().trim();
    }
    
    /**
     * คืนค่า Object ของปุ่ม Login เพื่อให้ Controller สามารถเพิ่ม Action Listener ได้
     * @return JButton ของปุ่มล็อกอิน
     */
    public JButton getLoginButton() {
        return loginButton;
    }
    
    /**
     * ล้างข้อความในช่องกรอก ID
     * ถูกเรียกใช้เมื่อผู้ใช้ทำการ Logout
     */
    public void clearFields() {
        studentIdField.setText("");
    }
}