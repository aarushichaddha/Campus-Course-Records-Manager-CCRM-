package edu.ccrm.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class Enrollment {
    private final Student student;
    private final Course course;
    private final LocalDateTime enrollmentDate;
    private Grade grade;
    private double marks;
    private LocalDateTime gradeDate;

    public Enrollment(Student student, Course course) {
        this.student = Objects.requireNonNull(student, "Student cannot be null");
        this.course = Objects.requireNonNull(course, "Course cannot be null");
        this.enrollmentDate = LocalDateTime.now();
        this.marks = -1; // Indicates no marks recorded
    }

    public Student getStudent() { return student; }
    public Course getCourse() { return course; }
    public LocalDateTime getEnrollmentDate() { return enrollmentDate; }
    public Grade getGrade() { return grade; }
    public double getMarks() { return marks; }
    public LocalDateTime getGradeDate() { return gradeDate; }

    public void setGrade(double marks) {
        if (marks < 0 || marks > 100) {
            throw new IllegalArgumentException("Marks must be between 0 and 100");
        }
        this.marks = marks;
        this.grade = Grade.fromMarks(marks);
        this.gradeDate = LocalDateTime.now();
    }

    public boolean hasGrade() {
        return grade != null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Enrollment that = (Enrollment) obj;
        return Objects.equals(student.getId(), that.student.getId()) &&
                Objects.equals(course.getCode(), that.course.getCode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(student.getId(), course.getCode());
    }

    @Override
    public String toString() {
        if (hasGrade()) {
            return String.format("Enrollment[Student: %s, Course: %s, Grade: %s (%.2f)]",
                    student.getRegNo(), course.getCode(), grade, marks);
        } else {
            return String.format("Enrollment[Student: %s, Course: %s, Grade: Not Recorded]",
                    student.getRegNo(), course.getCode());
        }
    }
}
