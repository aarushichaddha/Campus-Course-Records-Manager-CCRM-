package edu.ccrm.service;

import edu.ccrm.domain.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class TranscriptService {
    private final EnrollmentService enrollmentService;

    public TranscriptService() {

        this.enrollmentService = null;
    }

    public String generateTranscript(Student student) {
        List<Enrollment> enrollments = getStudentEnrollments(student.getId());

        StringBuilder transcript = new StringBuilder();
        transcript.append("OFFICIAL TRANSCRIPT\n");
        transcript.append("===================\n\n");
        transcript.append("Student: ").append(student.getFullName()).append("\n");
        transcript.append("Registration No: ").append(student.getRegNo()).append("\n");
        transcript.append("Generated: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))).append("\n\n");

        Map<Semester, List<Enrollment>> bySemester = enrollments.stream()
                .filter(e -> e.hasGrade())
                .collect(Collectors.groupingBy(e -> e.getCourse().getSemester()));

        double totalGradePoints = 0.0;
        int totalCredits = 0;

        for (Semester semester : Semester.values()) {
            List<Enrollment> semesterEnrollments = bySemester.get(semester);
            if (semesterEnrollments == null || semesterEnrollments.isEmpty()) {
                continue;
            }

            transcript.append(semester.getDisplayName()).append("\n");
            transcript.append("-".repeat(semester.getDisplayName().length())).append("\n");

            double semesterGradePoints = 0.0;
            int semesterCredits = 0;

            for (Enrollment enrollment : semesterEnrollments) {
                Course course = enrollment.getCourse();
                Grade grade = enrollment.getGrade();

                transcript.append(String.format("%-8s %-30s %2d %5.1f %s\n",
                        course.getCode(),
                        course.getTitle(),
                        course.getCredits(),
                        enrollment.getMarks(),
                        grade.name()));

                double gradePoints = grade.getGradePoint() * course.getCredits();
                semesterGradePoints += gradePoints;
                semesterCredits += course.getCredits();
            }

            double semesterGPA = semesterCredits > 0 ? semesterGradePoints / semesterCredits : 0.0;

            transcript.append(String.format("\nSemester GPA: %.2f\n\n", semesterGPA));

            totalGradePoints += semesterGradePoints;
            totalCredits += semesterCredits;
        }

        double overallGPA = totalCredits > 0 ? totalGradePoints / totalCredits : 0.0;
        transcript.append(String.format("Overall GPA: %.2f\n", overallGPA));
        transcript.append(String.format("Total Credits: %d\n", totalCredits));

        return transcript.toString();
    }

    public double calculateGPA(Student student) {
        List<Enrollment> enrollments = getStudentEnrollments(student.getId());

        double totalGradePoints = 0.0;
        int totalCredits = 0;

        for (Enrollment enrollment : enrollments) {
            if (enrollment.hasGrade()) {
                double gradePoints = enrollment.getGrade().getGradePoint() * enrollment.getCourse().getCredits();
                totalGradePoints += gradePoints;
                totalCredits += enrollment.getCourse().getCredits();
            }
        }

        return totalCredits > 0 ? totalGradePoints / totalCredits : 0.0;
    }

    public double calculateSemesterGPA(Student student, Semester semester) {
        List<Enrollment> enrollments = getStudentEnrollments(student.getId());

        double totalGradePoints = 0.0;
        int totalCredits = 0;

        for (Enrollment enrollment : enrollments) {
            if (enrollment.hasGrade() && enrollment.getCourse().getSemester() == semester) {
                double gradePoints = enrollment.getGrade().getGradePoint() * enrollment.getCourse().getCredits();
                totalGradePoints += gradePoints;
                totalCredits += enrollment.getCourse().getCredits();
            }
        }

        return totalCredits > 0 ? totalGradePoints / totalCredits : 0.0;
    }


    private List<Enrollment> getStudentEnrollments(String studentId) {

        return new ArrayList<>();
    }
}
