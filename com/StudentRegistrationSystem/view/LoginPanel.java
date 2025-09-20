package com.StudentRegistrationSystem.view;

import javax.swing.*;
import java.awt.*;

/**
 * คลาส View สำหรับสร้างหน้าจอสำหรับเข้าสู่ระบบ (Login Panel)
 * ใช้ GridBagLayout เพื่อจัดวางองค์ประกอบต่างๆ ในแนวตั้งและให้อยู่กึ่งกลางของหน้าจอ
 */
public class LoginPanel extends JPanel {

    // --- Fields: ส่วนประกอบ UI (Components) ของหน้าจอ ---
    private JTextField studentIdField; // ช่องสำหรับกรอกรหัสนักเรียน หรือ 'admin'
    private JButton loginButton;       // ปุ่มสำหรับยืนันการเข้าสู่ระบบ

    /**
     * Constructor ของ LoginPanel
     * ทำหน้าที่สร้างและจัดวางส่วนประกอบ UI ทั้งหมดในหน้าจอ
     */
    public LoginPanel() {
        // --- 1. การตั้งค่า Panel หลัก ---
        super(new GridBagLayout());

        // --- 2. การสร้าง Panel สำหรับฟอร์ม ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbcForm = new GridBagConstraints();

        // --- 3. กำหนดฟอนต์และขนาดที่ใหญ่ขึ้น ---
        Font titleFont = new Font("SansSerif", Font.BOLD, 36);
        Font labelFont = new Font("SansSerif", Font.PLAIN, 16);
        Font hintFont = new Font("SansSerif", Font.ITALIC, 12);
        Font inputFont = new Font("SansSerif", Font.PLAIN, 18);
        Font buttonFont = new Font("SansSerif", Font.BOLD, 18);

        // --- 4. การจัดวาง Component ภายใน formPanel (จัดเรียงในแนวตั้ง) ---

        // แถวที่ 0: หัวข้อหลัก "Login"
        JLabel titleLabel = new JLabel("Login");
        titleLabel.setFont(titleFont);
        gbcForm.gridx = 0;
        gbcForm.gridy = 0;
        gbcForm.anchor = GridBagConstraints.CENTER;
        gbcForm.insets = new Insets(10, 10, 30, 10);
        formPanel.add(titleLabel, gbcForm);

        // แถวที่ 1: ป้ายกำกับ (Label) ของช่องกรอกข้อมูล
        JLabel idLabel = new JLabel("Student ID / Admin");
        idLabel.setFont(labelFont);
        gbcForm.gridy = 1;
        gbcForm.anchor = GridBagConstraints.WEST;
        gbcForm.insets = new Insets(0, 10, 5, 10);
        formPanel.add(idLabel, gbcForm);

        // แถวที่ 2: ข้อความกำกับสำหรับ Admin
        JLabel adminHintLabel = new JLabel("For admin access, enter \'admin\'");
        adminHintLabel.setFont(hintFont);        // ใช้ฟอนต์สำหรับคำแนะนำ
        adminHintLabel.setForeground(Color.GRAY); // ตั้งค่าสีข้อความให้เป็นสีเทา
        gbcForm.gridy = 2;
        gbcForm.anchor = GridBagConstraints.WEST;
        gbcForm.insets = new Insets(0, 10, 10, 10);
        formPanel.add(adminHintLabel, gbcForm);

        // แถวที่ 3: ช่องกรอกข้อความ (Text Field)
        studentIdField = new JTextField(20);
        studentIdField.setFont(inputFont);
        studentIdField.setPreferredSize(new Dimension(studentIdField.getPreferredSize().width, 40));
        gbcForm.gridy = 3;
        gbcForm.fill = GridBagConstraints.HORIZONTAL;
        gbcForm.weightx = 1.0;
        gbcForm.insets = new Insets(0, 10, 15, 10);
        formPanel.add(studentIdField, gbcForm);

        // แถวที่ 4: ปุ่ม (Button)
        loginButton = new JButton("Login");
        loginButton.setFont(buttonFont);
        loginButton.setPreferredSize(new Dimension(loginButton.getPreferredSize().width, 50));
        gbcForm.gridy = 4;
        gbcForm.fill = GridBagConstraints.HORIZONTAL;
        gbcForm.insets = new Insets(5, 10, 10, 10);
        formPanel.add(loginButton, gbcForm);

        // --- 5. การนำ formPanel ไปวางบน Panel หลัก ---
        add(formPanel, new GridBagConstraints());
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