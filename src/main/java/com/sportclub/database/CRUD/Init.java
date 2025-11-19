package com.sportclub.database.CRUD;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.sportclub.database.IConfig;
import com.sportclub.database.models.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date;

public class Init {
    private static volatile SessionFactory sessionFactory;
    private static final Object lock = new Object();

    public static void initialize(SessionFactory factory) {
        if (sessionFactory == null) {
            synchronized (lock) {
                if (sessionFactory == null) {
                    initDatabase();
                    sessionFactory = factory;
                }
            }
        }
    }

    public static void initialize() {
        if (sessionFactory == null) {
            synchronized (lock) {
                if (sessionFactory == null) {
                    try {
                        initDatabase();

                        Configuration configuration = new Configuration();
                        configuration.configure("hibernate.cfg.xml");

                        configuration.setProperty("hibernate.connection.url", IConfig.databaseUrl);
                        configuration.setProperty("hibernate.connection.username", IConfig.username);
                        configuration.setProperty("hibernate.connection.password", IConfig.password);

                        sessionFactory = configuration.buildSessionFactory();
                        System.out.println("SessionFactory has been initialized successfully.");
                    } catch (Exception e) {
                        System.err.println("Error initializing SessionFactory: " + e.getMessage());
                        e.printStackTrace();
                        throw new RuntimeException("Unable to initialize SessionFactory", e);
                    }
                }
            }
        }
    }

    public static void initDatabase() {
        String serverUrl = "jdbc:mysql://localhost:3306/?useSSL=false&serverTimezone=Asia/Ho_Chi_Minh&allowPublicKeyRetrieval=true";
        String databaseName = "sport_club_db";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL Driver not found!");
            e.printStackTrace();
            throw new RuntimeException("MySQL Driver not found", e);
        }

        try (Connection conn = DriverManager.getConnection(serverUrl, IConfig.username, IConfig.password);
                Statement stmt = conn.createStatement()) {

            String sql = "CREATE DATABASE IF NOT EXISTS " + databaseName +
                    " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci";
            stmt.executeUpdate(sql);
            System.out.println("Database '" + databaseName + "' has been initialized successfully");

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Unable to initialize database", e);
        }
    }

    public static void initSampleData() {
        try {
            // Create sample members
            Member member1 = new Member("Nguyen Van A", Date.valueOf("1995-05-15"), "Nam", "0901234567",
                    "vana@email.com");
            Member member2 = new Member("Tran Thi B", Date.valueOf("1996-08-20"), "Nữ", "0907654321", "thib@email.com");
            Member member3 = new Member("Le Van C", Date.valueOf("1994-12-10"), "Nam", "0903456789", "vanc@email.com");

            CRUDManager.save(member1);
            CRUDManager.save(member2);
            CRUDManager.save(member3);

            // Create sample subjects
            Subject subject1 = new Subject("Bóng đá", "Môn thể thao đồng đội", "HLV Duc");
            Subject subject2 = new Subject("Bơi lội", "Môn thể thao cá nhân", "HLV Mai");
            Subject subject3 = new Subject("Cầu lông", "Môn thể thao vợt", "HLV Hải");

            CRUDManager.save(subject1);
            CRUDManager.save(subject2);
            CRUDManager.save(subject3);

            System.out.println("Sample data has been created successfully!");

        } catch (Exception e) {
            System.err.println("Error creating sample data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            initialize();
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            synchronized (lock) {
                if (sessionFactory != null) {
                    try {
                        sessionFactory.close();
                        System.out.println("SessionFactory has been closed");
                    } catch (Exception e) {
                        System.err.println("Error closing SessionFactory: " + e.getMessage());
                    } finally {
                        sessionFactory = null;
                    }
                }
            }
        }
    }
}
