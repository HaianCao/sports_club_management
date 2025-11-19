package com.sportclub.database.CRUD;

import com.sportclub.database.models.*;
import com.sportclub.util.HibernateUtil;
import org.hibernate.Session;
import java.util.List;

public class Query {

    public static <T> T findById(Class<T> type, int id) {
        return CRUDManager.get(type, id);
    }

    public static <T> List<T> findAll(Class<T> type) {
        return CRUDManager.getAll(type);
    }

    public static List<Member> findActiveMembers() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Member";
            return session.createQuery(hql, Member.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Member findMemberByPhone(String phone) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Member WHERE phone = :phone";
            return session.createQuery(hql, Member.class)
                    .setParameter("phone", phone)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Member findMemberByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Member WHERE email = :email";
            return session.createQuery(hql, Member.class)
                    .setParameter("email", email)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Member findMemberById(int memId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Member WHERE memId = :memId";
            return session.createQuery(hql, Member.class)
                    .setParameter("memId", memId)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Subject queries
    public static List<Subject> findActiveSubjects() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Subject";
            return session.createQuery(hql, Subject.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Subject findSubjectByName(String name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Subject WHERE name = :name";
            return session.createQuery(hql, Subject.class)
                    .setParameter("name", name)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Subject findSubjectById(int subjId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Subject WHERE subjId = :subjId";
            return session.createQuery(hql, Subject.class)
                    .setParameter("subjId", subjId)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Timeline> findActiveSchedules() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Timeline ORDER BY weekDay, startTime";
            return session.createQuery(hql, Timeline.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Timeline> findSchedulesBySubject(int subjId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Timeline WHERE subjId = :subjId";
            return session.createQuery(hql, Timeline.class)
                    .setParameter("subjId", subjId)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Timeline> findSchedulesByWeekDay(String weekDay) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Timeline WHERE weekDay = :weekDay";
            return session.createQuery(hql, Timeline.class)
                    .setParameter("weekDay", weekDay)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Regist> findActiveRegistrations() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Regist";
            return session.createQuery(hql, Regist.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Regist> findRegistrationsByMember(int memId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Regist WHERE registId.memId = :memId";
            return session.createQuery(hql, Regist.class)
                    .setParameter("memId", memId)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Regist> findRegistrationsBySubject(int subjId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Regist r WHERE r.id.subjId = :subjId";
            return session.createQuery(hql, Regist.class)
                    .setParameter("subjId", subjId)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Attendance> findActiveAttendance() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Attendance";
            return session.createQuery(hql, Attendance.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Attendance> findAttendanceByMember(int memId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Attendance WHERE memId = :memId";
            return session.createQuery(hql, Attendance.class)
                    .setParameter("memId", memId)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Attendance> findAttendanceByTimeline(int timelineId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Attendance WHERE timelineId = :timelineId";
            return session.createQuery(hql, Attendance.class)
                    .setParameter("timelineId", timelineId)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Attendance> findAllAttendance() {
        return findActiveAttendance();
    }

    public static List<Attendance> findAttendanceByDate(java.sql.Date date) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Attendance WHERE attendDate = :date";
            return session.createQuery(hql, Attendance.class)
                    .setParameter("date", date)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Attendance> findAttendanceByDateRange(java.sql.Date startDate, java.sql.Date endDate) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Attendance WHERE attendDate BETWEEN :startDate AND :endDate";
            return session.createQuery(hql, Attendance.class)
                    .setParameter("startDate", startDate)
                    .setParameter("endDate", endDate)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Attendance> findAttendanceByTimelineAndDate(int timelineId, java.sql.Date date) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Attendance WHERE timelineId = :timelineId AND attendDate = :date";
            return session.createQuery(hql, Attendance.class)
                    .setParameter("timelineId", timelineId)
                    .setParameter("date", date)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
