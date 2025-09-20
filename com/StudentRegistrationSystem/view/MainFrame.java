package com.example.view;

import javax.swing.*;
import java.awt.*;

/**
 * คลาส View หลักของโปรแกรม ซึ่งเป็นหน้าต่าง (JFrame) ที่จะแสดงผลทั้งหมด
 * ทำหน้าที่เป็น Container สำหรับ Panel ต่างๆ และใช้ CardLayout ในการสลับหน้าจอการทำงาน
 * เช่น สลับระหว่างหน้าล็อกอิน, หน้าลงทะเบียน, และหน้าแอดมิน
 */
public class MainFrame extends JFrame {

    // --- Fields: ส่วนประกอบ UI (Components) หลักของหน้าต่าง ---

    /**
     * Layout Manager ที่ใช้ในการสลับการแสดงผลของ Panel ต่างๆ เหมือนสำรับไพ่
     */
    private CardLayout cardLayout;

    /**
     * Panel หลักที่ทำหน้าที่เป็น Container สำหรับเก็บ Panel ย่อยทั้งหมด (Login, Registration, Admin)
     */
    private JPanel mainPanel;

    /**
     * Instance ของหน้าจอสำหรับเข้าสู่ระบบ
     */
    private LoginPanel loginPanel;

    /**
     * Instance ของหน้าจอสำหรับลงทะเบียนของนักเรียน
     */
    private RegistrationPanel registrationPanel;
    
    /**
     * Instance ของหน้าจอสำหรับผู้ดูแลระบบ
     */
    private AdminPanel adminPanel;

    /**
     * Constructor ของ MainFrame
     * ทำหน้าที่ตั้งค่าหน้าต่างหลัก, สร้าง Panel ทั้งหมด และจัดวางลงใน CardLayout
     */
    public MainFrame() {
        // --- 1. การตั้งค่าคุณสมบัติพื้นฐานของหน้าต่าง (JFrame) ---
        setTitle("Student Registration System"); // ตั้งชื่อ Title Bar
        setSize(800, 600); // กำหนดขนาดหน้าต่าง
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // กำหนดให้โปรแกรมปิดเมื่อกดปุ่ม X
        setLocationRelativeTo(null); // ตั้งค่าให้หน้าต่างแสดงผลอยู่กึ่งกลางของจอภาพ

        // --- 2. การสร้างและกำหนดค่า Layout ---
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout); // กำหนดให้ mainPanel ใช้ CardLayout

        // --- 3. การสร้าง Instance ของ Panel ย่อยทั้งหมด ---
        loginPanel = new LoginPanel();
        registrationPanel = new RegistrationPanel();
        adminPanel = new AdminPanel();

        // --- 4. การเพิ่ม Panel ย่อยเข้าไปใน mainPanel พร้อมตั้งชื่อสำหรับอ้างอิง ---
        // แต่ละ Panel จะถูกเพิ่มเข้าไปเป็น "การ์ด" หนึ่งใบในสำรับ
        mainPanel.add(loginPanel, "Login");
        mainPanel.add(registrationPanel, "Registration");
        mainPanel.add(adminPanel, "Admin");

        // --- 5. การนำ mainPanel มาแสดงผลบน JFrame ---
        add(mainPanel);
        
        // --- 6. กำหนดหน้าจอเริ่มต้น ---
        showLoginPanel(); // แสดงหน้าล็อกอินเป็นหน้าแรกเมื่อโปรแกรมเริ่มทำงาน
    }

    // --- เมธอดสาธารณะสำหรับให้ Controller ใช้ในการสลับหน้าจอ (Card) ---

    /**
     * สลับไปแสดงผลหน้าจอ Login
     */
    public void showLoginPanel() {
        cardLayout.show(mainPanel, "Login");
    }

    /**
     * สลับไปแสดงผลหน้าจอ Registration ของนักเรียน
     */
    public void showRegistrationPanel() {
        cardLayout.show(mainPanel, "Registration");
    }

    /**
     * สลับไปแสดงผลหน้าจอ Admin
     */
    public void showAdminPanel() {
        cardLayout.show(mainPanel, "Admin");
    }
    
    // --- Getters: เมธอดสำหรับให้ Controller เข้าถึง Instance ของ Panel ต่างๆ ---

    public LoginPanel getLoginPanel() {
        return loginPanel;
    }

    public RegistrationPanel getRegistrationPanel() {
        return registrationPanel;
    }

    public AdminPanel getAdminPanel() {
        return adminPanel;
    }
}