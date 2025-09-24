package edu.ccrm.service;

import edu.ccrm.domain.*;
import edu.ccrm.service.exceptions.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class EnrollmentService {
    private final StudentService studentService;
    private final CourseService courseService;
    private final Map<String, List<Enrollment>> studentEnrollments;
    private final Map<String, List<Enrollment>> courseEnrollments;

    private static final int MAX_CREDITS_PER_SEMESTER = 20;

    public EnrollmentService(StudentService studentService, CourseService courseService) {
        this.studentService = studentService;
        this.courseService = courseService;
        this.studentEnrollments = new ConcurrentHashMap<>();
        this.courseEnrollments = new ConcurrentHashMap<>();
    }

    public void enrollStudent(String studentId, String courseCode)
            throws StudentNotFoundException, CourseNotFoundException,
            DuplicateEnrollmentException, MaxCreditLimitExceededException {

        Student student = studentService.findStudentById(studentId);
        if (student == null) {
            throw new StudentNotFoundException("Student with ID " + studentId + " not found");
        }

        Course course = courseService.findCourseByCode(courseCode);
        if (course == null) {
            throw new CourseNotFoundException("Course with code " + courseCode + " not found");
        }

        if (student.getStatus() != Student.Status.ACTIVE) {
            throw new IllegalStateException("Cannot enroll inactive student");
        }

        if (!course.isActive()) {
            throw new IllegalStateException("Cannot enroll in inactive course");
        }

        List<Enrollment> enrollments = studentEnrollments.getOrDefault(studentId, new ArrayList<>());
        boolean alreadyEnrolled = enrollments.stream()
                .anyMatch(e -> e.getCourse().getCode().equals(courseCode));

        if (alreadyEnrolled) {
            throw new DuplicateEnrollmentException("Student already enrolled in course " + courseCode);
        }

        int currentCredits = enrollments.stream()
                .filter(e -> e.getCourse().getSemester() == course.getSemester())
                .mapToInt(e -> e.getCourse().getCredits())
                .sum();

        if (currentCredits + course.getCredits() > MAX_CREDITS_PER_SEMESTER) {
            throw new MaxCreditLimitExceededException(
                    "Enrollment would exceed maximum credits per semester (" + MAX_CREDITS_PER_SEMESTER + ")");
        }

        Enrollment enrollment = new Enrollment(student, course);


        enrollments.add(enrollment);
        studentEnrollments.put(studentId, enrollments);

        List<Enrollment> courseEnrollmentList = courseEnrollments.getOrDefault(courseCode, new ArrayList<>());
        courseEnrollmentList.add(enrollment);
        courseEnrollments.put(courseCode, courseEnrollmentList);

        student.addCourse(courseCode);
    }

    public void unenrollStudent(String studentId, String courseCode)
            throws StudentNotFoundException, CourseNotFoundException, EnrollmentNotFoundException {

        Student student = studentService.findStudentById(studentId);
        if (student == null) {
            throw new StudentNotFoundException("Student with ID " + studentId + " not found");
        }

        Course course = courseService.findCourseByCode(courseCode);
        if (course == null) {
            throw new CourseNotFoundException("Course with code " + courseCode + " not found");
        }

        List<Enrollment> enrollments = studentEnrollments.get(studentId);
        if (enrollments == null) {
            throw new EnrollmentNotFoundException("No enrollments found for student " + studentId);
        }

        boolean removed = enrollments.removeIf(e -> e.getCourse().getCode().equals(courseCode));
        if (!removed) {
            throw new EnrollmentNotFoundException("Student not enrolled in course " + courseCode);
        }

        List<Enrollment> courseEnrollmentList = courseEnrollments.get(courseCode);
        if (courseEnrollmentList != null) {
            courseEnrollmentList.removeIf(e -> e.getStudent().getId().equals(studentId));
        }

        student.removeCourse(courseCode);
    }

    public void recordGrade(String studentId, String courseCode, double marks)
            throws StudentNotFoundException, CourseNotFoundException, EnrollmentNotFoundException {

        List<Enrollment> enrollments = studentEnrollments.get(studentId);
        if (enrollments == null) {
            throw new EnrollmentNotFoundException("No enrollments found for student " + studentId);
        }

        Enrollment enrollment = enrollments.stream()
                .filter(e -> e.getCourse().getCode().equals(courseCode))
                .findFirst()
                .orElseThrow(() -> new EnrollmentNotFoundException("Student not enrolled in course " + courseCode));

        enrollment.setGrade(marks);
    }

    public List<Enrollment> getStudentEnrollments(String studentId) {
        return studentEnrollments.getOrDefault(studentId, new ArrayList<>());
    }

    public List<Enrollment> getCourseEnrollments(String courseCode) {
        return courseEnrollments.getOrDefault(courseCode, new ArrayList<>());
    }

    public int getCourseEnrollmentCount(String courseCode) {
        List<Enrollment> enrollments = courseEnrollments.get(courseCode);
        return enrollments != null ? enrollments.size() : 0;
    }

    public Map<Semester, Integer> getEnrollmentStatsBySemester() {
        return studentEnrollments.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.groupingBy(
                        e -> e.getCourse().getSemester(),
                        Collectors.collectingAndThen(Collectors.counting(), Math::toIntExact)
                ));
    }
}
