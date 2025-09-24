package edu.ccrm.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class Course {
    private final String code;
    private String title;
    private int credits;
    private String instructor;
    private Semester semester;
    private String department;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    private Course(Builder builder) {
        this.code = builder.code;
        this.title = builder.title;
        this.credits = builder.credits;
        this.instructor = builder.instructor;
        this.semester = builder.semester;
        this.department = builder.department;
        this.active = builder.active;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }


    public static class Builder {
        private final String code;
        private final String title;
        private int credits = 3;
        private String instructor = "";
        private Semester semester = Semester.SPRING;
        private String department = "";
        private boolean active = true;

        public Builder(String code, String title) {
            this.code = Objects.requireNonNull(code, "Course code cannot be null");
            this.title = Objects.requireNonNull(title, "Course title cannot be null");
        }

        public Builder credits(int credits) {
            if (credits <= 0) throw new IllegalArgumentException("Credits must be positive");
            this.credits = credits;
            return this;
        }

        public Builder instructor(String instructor) {
            this.instructor = instructor;
            return this;
        }

        public Builder semester(Semester semester) {
            this.semester = semester;
            return this;
        }

        public Builder department(String department) {
            this.department = department;
            return this;
        }

        public Builder active(boolean active) {
            this.active = active;
            return this;
        }

        public Course build() {
            return new Course(this);
        }
    }


    public String getCode() { return code; }
    public String getTitle() { return title; }
    public int getCredits() { return credits; }
    public String getInstructor() { return instructor; }
    public Semester getSemester() { return semester; }
    public String getDepartment() { return department; }
    public boolean isActive() { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setTitle(String title) {
        this.title = title;
        this.updatedAt = LocalDateTime.now();
    }

    public void setCredits(int credits) {
        this.credits = credits;
        this.updatedAt = LocalDateTime.now();
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
        this.updatedAt = LocalDateTime.now();
    }

    public void setSemester(Semester semester) {
        this.semester = semester;
        this.updatedAt = LocalDateTime.now();
    }

    public void setDepartment(String department) {
        this.department = department;
        this.updatedAt = LocalDateTime.now();
    }

    public void setActive(boolean active) {
        this.active = active;
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Course course = (Course) obj;
        return Objects.equals(code, course.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return String.format("Course[%s: %s, Credits: %d, Instructor: %s, Semester: %s]",
                code, title, credits, instructor, semester);
    }
}

