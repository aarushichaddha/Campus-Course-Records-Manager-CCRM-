package edu.ccrm.domain;

import java.time.LocalDateTime;
import java.util.*;

public class Student extends Person {
    public enum Status {
        ACTIVE, INACTIVE, GRADUATED, SUSPENDED
    }

    private final String regNo;
    private Status status;
    private final Set<String> enrolledCourses;
    private LocalDateTime enrollmentDate;

    private Student(Builder builder) {
        super(builder.id, builder.fullName, builder.email);
        this.regNo = builder.regNo;
        this.status = builder.status;
        this.enrolledCourses = new HashSet<>(builder.enrolledCourses);
        this.enrollmentDate = builder.enrollmentDate;
    }

    public static class Builder {
        private final String id;
        private final String regNo;
        private Name fullName;
        private String email;
        private Status status = Status.ACTIVE;
        private Set<String> enrolledCourses = new HashSet<>();
        private LocalDateTime enrollmentDate = LocalDateTime.now();

        public Builder(String id, String regNo) {
            this.id = Objects.requireNonNull(id, "ID cannot be null");
            this.regNo = Objects.requireNonNull(regNo, "Registration number cannot be null");
        }

        public Builder fullName(Name fullName) {
            this.fullName = fullName;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder status(Status status) {
            this.status = status;
            return this;
        }

        public Builder enrolledCourses(Set<String> courses) {
            this.enrolledCourses = new HashSet<>(courses);
            return this;
        }

        public Builder enrollmentDate(LocalDateTime date) {
            this.enrollmentDate = date;
            return this;
        }

        public Student build() {
            return new Student(this);
        }
    }

    public String getRegNo() { return regNo; }
    public Status getStatus() { return status; }
    public Set<String> getEnrolledCourses() { return new HashSet<>(enrolledCourses); }
    public LocalDateTime getEnrollmentDate() { return enrollmentDate; }

    public void setStatus(Status status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public void addCourse(String courseCode) {
        enrolledCourses.add(courseCode);
        this.updatedAt = LocalDateTime.now();
    }

    public void removeCourse(String courseCode) {
        enrolledCourses.remove(courseCode);
        this.updatedAt = LocalDateTime.now();
    }


    @Override
    public String getDisplayName() {
        return fullName + " (" + regNo + ")";
    }

    @Override
    public String getRole() {
        return "Student";
    }

    public String getDetailedProfile() {
        StringBuilder sb = new StringBuilder();
        sb.append("Student Profile:\n");
        sb.append("ID: ").append(id).append("\n");
        sb.append("Registration No: ").append(regNo).append("\n");
        sb.append("Name: ").append(fullName).append("\n");
        sb.append("Email: ").append(email).append("\n");
        sb.append("Status: ").append(status).append("\n");
        sb.append("Enrollment Date: ").append(enrollmentDate).append("\n");
        sb.append("Enrolled Courses: ").append(enrolledCourses.size()).append("\n");
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("Student[id=%s, regNo=%s, name=%s, status=%s, courses=%d]",
                id, regNo, fullName, status, enrolledCourses.size());
    }
}
