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

    // Find subject by name
    public static Subject findSubjectByName(String name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Subject WHERE name = :name AND isDeleted = false";
            return session.createQuery(hql, Subject.class)
                          .setParameter("name", name)
                          .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Find all users joined a subject by subject name
    public static List<User> findUsersBySubjectName(String subjectName) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT DISTINCT u FROM User u " +
                        "JOIN Join j ON u.id = j.id.uId " +
                        "JOIN Subject s ON j.id.subjectId = s.id " +
                        "WHERE s.name = :subjectName AND u.isDeleted = false AND j.isDeleted = 0";
            return session.createQuery(hql, User.class)
                          .setParameter("subjectName", subjectName)
                          .list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Find all joins for a subject by subject name
    public static List<Join> findJoinsBySubjectName(String subjectName) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Join j WHERE j.id.subjectId IN " +
                        "(SELECT s.id FROM Subject s WHERE s.name = :subjectName) " +
                        "AND j.isDeleted = 0";
            return session.createQuery(hql, Join.class)
                          .setParameter("subjectName", subjectName)
                          .list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Find joins with user and subject details by subject name
    public static List<Object[]> findUserDetailsInSubject(String subjectName) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT u.id, u.name, u.phone, u.account, j.id.participated, j.comment, j.manageId " +
                        "FROM User u " +
                        "JOIN Join j ON u.id = j.id.uId " +
                        "JOIN Subject s ON j.id.subjectId = s.id " +
                        "WHERE s.name = :subjectName AND u.isDeleted = false AND j.isDeleted = 0 " +
                        "ORDER BY u.id";
            return session.createQuery(hql, Object[].class)
                          .setParameter("subjectName", subjectName)
                          .list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
