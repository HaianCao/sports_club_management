package com.sportclub.database.CRUD;

import com.sportclub.database.models.*;
import com.sportclub.util.HibernateUtil;
import org.hibernate.Session;
import java.util.List;

public class Query {

    // Generic find by ID
    public static <T> T findById(Class<T> type, int id) {
        return CRUDManager.get(type, id);
    }
    
    // Generic find all
    public static <T> List<T> findAll(Class<T> type) {
        return CRUDManager.getAll(type);
    }

    // Specific queries
    public static User findUserByAccount(String account) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM User WHERE account = :account AND isDeleted = false";
            return session.createQuery(hql, User.class)
                          .setParameter("account", account)
                          .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Subject> findActiveSubjects() {
         try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Subject WHERE isDeleted = false";
            return session.createQuery(hql, Subject.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
