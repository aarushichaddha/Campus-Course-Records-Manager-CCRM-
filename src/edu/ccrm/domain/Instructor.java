package edu.ccrm.domain;

import java.time.LocalDateTime;
import java.util.*;

public class Instructor extends Person {
    private String department;
    private final Set<String> assignedCourses;
    private LocalDateTime hireDate;

    public Instructor(String id, Name fullName, String email, String department) {
        super(id, fullName, email);
        this.department = department;
        this.assignedCourses = new HashSet<>();
        this.hireDate = LocalDateTime.now();
    }

    public String getDepartment() { return department; }
    public Set<String> getAssignedCourses() { return new HashSet<>(assignedCourses); }
    public LocalDateTime getHireDate() { return hireDate; }

    public void setDepartment(String department) {
        this.department = department;
        this.updatedAt = LocalDateTime.now();
    }

    public void assignCourse(String courseCode) {
        assignedCourses.add(courseCode);
        this.updatedAt = LocalDateTime.now();
    }

    public void unassignCourse(String courseCode) {
        assignedCourses.remove(courseCode);
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String getDisplayName() {
        return "Dr. " + fullName;
    }

    @Override
    public String getRole() {
        return "Instructor";
    }

    @Override
    public String toString() {
        return String.format("Instructor[id=%s, name=%s, dept=%s, courses=%d]",
                id, fullName, department, assignedCourses.size());
    }
}