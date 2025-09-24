package edu.ccrm.util;

import edu.ccrm.domain.*;
import java.util.regex.Pattern;

public class ValidationUtil {
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern ID_PATTERN =
            Pattern.compile("^[A-Za-z0-9]{3,20}$");

    public static void validateStudent(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }

        validateId(student.getId());

        if (student.getRegNo() == null || student.getRegNo().trim().isEmpty()) {
            throw new IllegalArgumentException("Registration number cannot be null or empty");
        }

        if (student.getFullName() == null) {
            throw new IllegalArgumentException("Full name cannot be null");
        }

        if (student.getEmail() != null && !student.getEmail().isEmpty()) {
            validateEmail(student.getEmail());
        }
    }

    public static void validateCourse(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Course cannot be null");
        }

        if (course.getCode() == null || course.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Course code cannot be null or empty");
        }

        if (course.getTitle() == null || course.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Course title cannot be null or empty");
        }

        if (course.getCredits() <= 0) {
            throw new IllegalArgumentException("Course credits must be positive");
        }

        if (course.getCredits() > 10) {
            throw new IllegalArgumentException("Course credits cannot exceed 10");
        }
    }

    public static void validateId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID cannot be null or empty");
        }

        if (!ID_PATTERN.matcher(id).matches()) {
            throw new IllegalArgumentException("ID must be 3-20 alphanumeric characters");
        }
    }

    public static void validateEmail(String email) {
        if (email != null && !email.trim().isEmpty()) {
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                throw new IllegalArgumentException("Invalid email format");
            }
        }
    }


    public static boolean hasPermission(int userPermissions, int requiredPermission) {
        return (userPermissions & requiredPermission) == requiredPermission;
    }

    public static int addPermission(int currentPermissions, int newPermission) {
        return currentPermissions | newPermission;
    }

    public static int removePermission(int currentPermissions, int permission) {
        return currentPermissions & ~permission;
    }
}
