package com.sportclub.database.CRUD;

import com.sportclub.database.models.Subject;
import com.sportclub.database.models.User;

public class Delete {

    /**
     * Soft deletes a user by setting their 'isDeleted' flag to true.
     * @param userId The ID of the user to delete.
     */
    public static void softDeleteUser(int userId) {
        User user = Query.findById(User.class, userId);
        if (user != null) {
            if (!user.isDeleted()) {
                user.setDeleted(true);
                CRUDManager.update(user);
                System.out.println("Soft deleted user with ID: " + userId);
            } else {
                System.out.println("User with ID: " + userId + " was already deleted.");
            }
        } else {
            System.out.println("User with ID " + userId + " not found for deletion.");
        }
    }

    /**
     * Soft deletes a subject by setting its 'isDeleted' flag to true.
     * @param subjectId The ID of the subject to delete.
     */
    public static void softDeleteSubject(int subjectId) {
        Subject subject = Query.findById(Subject.class, subjectId);
        if (subject != null) {
            if (!subject.isDeleted()) {
                subject.setDeleted(true);
                CRUDManager.update(subject);
                System.out.println("Soft deleted subject with ID: " + subjectId);
            } else {
                System.out.println("Subject with ID: " + subjectId + " was already deleted.");
            }
        } else {
            System.out.println("Subject with ID " + subjectId + " not found for deletion.");
        }
    }

    /**
     * Permanently deletes any object from the database. Use with caution.
     * @param obj The object to be deleted.
     */
    public static void hardDelete(Object obj) {
        CRUDManager.delete(obj);
        System.out.println("Permanently deleted object: " + obj.getClass().getSimpleName());
    }
}