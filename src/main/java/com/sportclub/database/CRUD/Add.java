package com.sportclub.database.CRUD;

import com.sportclub.database.models.*;
import java.sql.Time;

public class Add {

    public static User addUser(String name, String phone, String account, String passwd, String gender, int role) {
        User user = new User();
        user.setName(name);
        user.setPhone(phone);
        user.setAccount(account);
        user.setPasswd(passwd);
        user.setGender(gender);
        user.setRole(role);
        user.setDeleted(false);
        CRUDManager.save(user);
        return user;
    }

    public static Subject addSubject(String name, String description) {
        Subject subject = new Subject();
        subject.setName(name);
        subject.setDescription(description);
        subject.setDeleted(false);
        CRUDManager.save(subject);
        return subject;
    }

    public static Timeline addTimeline(Time start, Time end) {
        Timeline timeline = new Timeline();
        timeline.setStart(start);
        timeline.setEnd(end);
        timeline.setDeleted(false);
        CRUDManager.save(timeline);
        return timeline;
    }

    public static Join addJoin(int userId, int timelineId, int subjectId, String manageId) {
        JoinId joinId = new JoinId(userId, timelineId, subjectId);
        Join join = new Join();
        join.setId(joinId);
        join.setParticipated(0); // Default to not participated
        join.setIsDeleted(0);
        join.setManageId(manageId);
        CRUDManager.save(join);
        return join;
    }
}
