package com.sportclub.database.CRUD;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.sportclub.database.IConfig;
import com.sportclub.database.models.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
        String serverUrl = "jdbc:mysql://localhost:3306/?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
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

    /**
     * Creates the root account if it does not exist in the database.
     */
    public static void initRootAccount() {
        try {
            // Query to check if root account already exists
            User existingRoot = Query.findUserByAccount(IConfig.acc_root);
            
            if (existingRoot == null) {
                // Root account does not exist, create it
                System.out.println("Creating root account...");
                User rootUser = new User();
                rootUser.setName("Administrator");
                rootUser.setPhone("0000000000");
                rootUser.setAccount(IConfig.acc_root);
                rootUser.setPasswd(IConfig.passwd_root);
                rootUser.setGender("Unknown");
                rootUser.setRole(0); // Role 0 = root
                rootUser.setDeleted(false);
                
                CRUDManager.save(rootUser);
                System.out.println("Root account created successfully with account: " + IConfig.acc_root);
            } else {
                System.out.println("Root account already exists.");
            }
        } catch (Exception e) {
            System.err.println("Error creating root account: " + e.getMessage());
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
