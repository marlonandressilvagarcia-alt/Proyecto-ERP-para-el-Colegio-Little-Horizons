package com.littlehorizons.erp.controller;

import com.littlehorizons.erp.config.DatabaseConfig;
import com.littlehorizons.erp.service.*;
import com.littlehorizons.erp.util.ConsoleFormatter;
import com.littlehorizons.erp.view.*;

/**
 * Root Controller orchestrating sub-controllers and the main application lifecycle.
 */
public class MainController {

    private final MainMenuView mainView;
    private final StudentController studentController;
    private final AcademicController academicController;
    private final TeacherController teacherController;
    private final ExtracurricularController extracurricularController;
    private final FinancialController financialController;
    private final ResourceController resourceController;
    private final ReportController reportController;

    public MainController() {
        this.mainView = new MainMenuView();

        // Initialize Services
        StudentService studentService = new StudentService();
        AcademicService academicService = new AcademicService();
        ExtracurricularService extracurricularService = new ExtracurricularService();
        FinancialService financialService = new FinancialService();
        ResourceService resourceService = new ResourceService();
        ReportService reportService = new ReportService(studentService, academicService, extracurricularService, financialService, resourceService);

        // Initialize Views & Controllers
        this.studentController = new StudentController(studentService, new StudentView());
        this.academicController = new AcademicController(academicService, new AcademicView());
        this.teacherController = new TeacherController(academicService, new TeacherView());
        this.extracurricularController = new ExtracurricularController(extracurricularService, new ExtracurricularView());
        this.financialController = new FinancialController(financialService, new FinancialView());
        this.resourceController = new ResourceController(resourceService, new ResourceView());
        this.reportController = new ReportController(reportService, new ReportView());
    }

    public void start() {
        boolean dbConnected = DatabaseConfig.getInstance().testConnection();
        mainView.displayWelcomeBanner(dbConnected);

        boolean running = true;
        while (running) {
            int option = mainView.displayMainMenu();
            switch (option) {
                case 1 -> studentController.run();
                case 2 -> academicController.run();
                case 3 -> teacherController.run();
                case 4 -> extracurricularController.run();
                case 5 -> financialController.run();
                case 6 -> resourceController.run();
                case 7 -> reportController.run();
                case 8 -> {
                    ConsoleFormatter.printHeader("GRACIAS POR UTILIZAR LITTLE HORIZONS ERP");
                    System.out.println("Cerrando sesión de manera segura...");
                    running = false;
                }
            }
        }
    }
}
