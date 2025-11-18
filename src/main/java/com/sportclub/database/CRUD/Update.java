package com.sportclub.database.CRUD;

import com.sportclub.database.models.Join;
import com.sportclub.database.models.JoinId;
import com.sportclub.database.models.Subject;
import com.sportclub.database.models.User;

public class Update {

    public static void updateUser(int userId, String newName, String newPhone) {
        User user = Query.findById(User.class, userId);
        if (user != null) {
            user.setName(newName);
            user.setPhone(newPhone);
            CRUDManager.update(user);
            System.out.println("User with ID " + userId + " updated.");
        } else {
            System.out.println("User with ID " + userId + " not found.");
        }
    }

    public static void updateSubjectDescription(int subjectId, String newDescription) {
        Subject subject = Query.findById(Subject.class, subjectId);
        if (subject != null) {
            subject.setDescription(newDescription);
            CRUDManager.update(subject);
            System.out.println("Subject with ID " + subjectId + " updated.");
        } else {
            System.out.println("Subject with ID " + subjectId + " not found.");
        }
    }

    public static void updateJoinParticipation(int userId, int timelineId, int subjectId, int participation, String comment, String manageId) {
        JoinId joinId = new JoinId(userId, timelineId, subjectId);
        Join join = CRUDManager.get(Join.class, joinId);
        if (join != null) {
            join.setParticipated(participation);
            join.setComment(comment);
            join.setManageId(manageId);
            CRUDManager.update(join);
            System.out.println("Join for user " + userId + " updated.");
        } else {
            System.out.println("Join for user " + userId + " not found.");
        }
    }
}
