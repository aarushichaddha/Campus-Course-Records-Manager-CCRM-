package edu.ccrm.service;

import edu.ccrm.domain.*;
import edu.ccrm.service.exceptions.*;
import edu.ccrm.util.ValidationUtil;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class CourseService {
    private final Map<String, Course> courses;

    public CourseService() {
        this.courses = new ConcurrentHashMap<>();
    }

    public void addCourse(Course course) throws DuplicateCourseException {
        ValidationUtil.validateCourse(course);

        if (courses.containsKey(course.getCode())) {
            throw new DuplicateCourseException("Course with code " + course.getCode() + " already exists");
        }

        courses.put(course.getCode(), course);
    }

    public Course findCourseByCode(String code) {
        return courses.get(code);
    }

    public List<Course> getAllCourses() {
        return new ArrayList<>(courses.values());
    }

    public List<Course> getActiveCourses() {
        return courses.values().stream()
                .filter(Course::isActive)
                .collect(Collectors.toList());
    }

    public void updateCourse(Course course) throws CourseNotFoundException {
        if (!courses.containsKey(course.getCode())) {
            throw new CourseNotFoundException("Course with code " + course.getCode() + " not found");
        }
        courses.put(course.getCode(), course);
    }

    public void deactivateCourse(String courseCode) throws CourseNotFoundException {
        Course course = findCourseByCode(courseCode);
        if (course == null) {
            throw new CourseNotFoundException("Course with code " + courseCode + " not found");
        }
        course.setActive(false);
    }


    public List<Course> searchByDepartment(String department) {
        return courses.values().stream()
                .filter(c -> c.getDepartment().toLowerCase().contains(department.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Course> searchByInstructor(String instructor) {
        return courses.values().stream()
                .filter(c -> c.getInstructor().toLowerCase().contains(instructor.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Course> searchBySemester(Semester semester) {
        return courses.values().stream()
                .filter(c -> c.getSemester() == semester)
                .collect(Collectors.toList());
    }

    public List<Course> searchByCredits(int minCredits, int maxCredits) {
        return courses.values().stream()
                .filter(c -> c.getCredits() >= minCredits && c.getCredits() <= maxCredits)
                .collect(Collectors.toList());
    }
}