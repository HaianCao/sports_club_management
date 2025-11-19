package com.sportclub.util;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import com.sportclub.database.CRUD.Init;

public final class HibernateUtil {

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            Init.initDatabase();

            final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                    .configure()
                    .build();
            try {
                System.out.println("✓ Initializing Hibernate SessionFactory...");
                return new MetadataSources(registry).buildMetadata().buildSessionFactory();
            } catch (Exception e) {
                System.err.println("✗ Error initializing SessionFactory: " + e.getMessage());
                StandardServiceRegistryBuilder.destroy(registry);
                throw new ExceptionInInitializerError(e);
            }
        } catch (Exception e) {
            System.err.println("✗ Error initializing database: " + e.getMessage());
            throw new ExceptionInInitializerError(e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            getSessionFactory().close();
            System.out.println("✓ Hibernate SessionFactory has been closed safely.");
        }
    }

    private HibernateUtil() {
    }
}
