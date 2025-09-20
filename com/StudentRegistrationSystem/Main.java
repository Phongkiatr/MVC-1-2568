package com.StudentRegistrationSystem;

import javax.swing.*;

import com.StudentRegistrationSystem.controller.AppController;
import com.StudentRegistrationSystem.model.AdminService;
import com.StudentRegistrationSystem.model.DataAccessService;
import com.StudentRegistrationSystem.model.RegistrationService;
import com.StudentRegistrationSystem.view.MainFrame;

/**
 * คลาสหลัก (Main Class) ที่เป็นจุดเริ่มต้นของการทำงานทั้งหมดของโปรแกรม
 * ทำหน้าที่สร้างและเชื่อมต่อส่วนประกอบหลักตามสถาปัตยกรรม MVC (Model-View-Controller)
 */
public class Main {

    /**
     * เมธอด main ซึ่งเป็นจุดแรกที่ Java Virtual Machine (JVM) จะเรียกใช้งานเมื่อโปรแกรมเริ่มทำงาน
     *
     * @param args อาร์กิวเมนต์ที่รับมาจาก Command Line (ในโปรแกรมนี้ไม่ได้ใช้งาน)
     */
    public static void main(String[] args) {
        
        // ใช้ SwingUtilities.invokeLater เพื่อให้แน่ใจว่าการสร้างและจัดการส่วนประกอบ UI ทั้งหมด
        // จะเกิดขึ้นบน Event Dispatch Thread (EDT) ซึ่งเป็นกฎสำคัญของการเขียนโปรแกรม Swing
        SwingUtilities.invokeLater(() -> {

            // --- 1. การสร้างส่วนประกอบของ Model ---
            // Model ในที่นี้คือกลุ่มของคลาส Service ที่จัดการข้อมูลและตรรกะทางธุรกิจ
            
            // สร้าง Service สำหรับจัดการการเข้าถึงข้อมูล (อ่าน/เขียนไฟล์ CSV)
            DataAccessService dataAccess = new DataAccessService();
            // สร้าง Service สำหรับจัดการตรรกะการลงทะเบียน โดยส่ง DataAccessService เข้าไป
            RegistrationService registrationService = new RegistrationService(dataAccess);
            // สร้าง Service สำหรับจัดการตรรกะของแอดมิน โดยส่ง DataAccessService เข้าไป
            AdminService adminService = new AdminService(dataAccess);
            
            // --- 2. การสร้างส่วนประกอบของ View ---
            // View คือส่วนที่ผู้ใช้มองเห็นและโต้ตอบด้วย
            MainFrame view = new MainFrame();
            
            // --- 3. การสร้างส่วนประกอบของ Controller และเชื่อมทุกอย่างเข้าด้วยกัน ---
            // Controller ทำหน้าที่เป็นตัวกลางรับคำสั่งจาก View และเรียกใช้ Model
            
            // สร้าง Controller และส่ง "Model" (Services) และ "View" (MainFrame) เข้าไป
            // เพื่อให้ Controller สามารถทำหน้าที่เป็นตัวกลางประสานงานได้
            new AppController(view, dataAccess, registrationService, adminService);
            
            // --- 4. แสดงหน้าต่างโปรแกรม ---
            // ทำให้หน้าต่าง GUI ปรากฏขึ้นบนหน้าจอของผู้ใช้
            view.setVisible(true);
        });
    }
}