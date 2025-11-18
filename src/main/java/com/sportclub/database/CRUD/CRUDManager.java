package com.sportclub.database.CRUD;

import com.sportclub.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.io.Serializable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A central manager for handling Hibernate sessions and transactions to avoid boilerplate code.
 */
public class CRUDManager {

    /**
     * Executes a transactional operation (e.g., save, update, delete).
     * @param action The action to perform within the transaction.
     */
    private static void executeTransaction(Consumer<Session> action) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            action.accept(session);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Transaction failed. Rolling back.");
            e.printStackTrace();
        }
    }

    /**
     * Executes a query operation that returns a result.
     * @param func The function to execute that returns a result.
     * @return The result of the query.
     */
    private static <T> T executeQuery(Function<Session, T> func) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return func.apply(session);
        } catch (Exception e) {
            System.err.println("Query failed.");
            e.printStackTrace();
            return null;
        }
    }

    public static void save(Object obj) {
        executeTransaction(session -> session.save(obj));
        System.out.println("Saved: " + obj.getClass().getSimpleName());
    }

    public static void update(Object obj) {
        executeTransaction(session -> session.update(obj));
        System.out.println("Updated: " + obj.getClass().getSimpleName());
    }

    public static void delete(Object obj) {
        executeTransaction(session -> session.delete(obj));
        System.out.println("Deleted: " + obj.getClass().getSimpleName());
    }

    public static <T> T get(Class<T> type, Serializable id) {
        return executeQuery(session -> session.get(type, id));
    }

    public static <T> List<T> getAll(Class<T> type) {
        String hql = "FROM " + type.getName();
        return executeQuery(session -> session.createQuery(hql, type).list());
    }
}
