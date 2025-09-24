package edu.ccrm.service;

import edu.ccrm.domain.*;
import edu.ccrm.service.exceptions.*;
import edu.ccrm.util.ValidationUtil;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class StudentService {
    private final Map<String, Student> students;

    public static class ServiceStats {
        private final int totalStudents;
        private final int activeStudents;
        private final int inactiveStudents;

        public ServiceStats(int total, int active, int inactive) {
            this.totalStudents = total;
            this.activeStudents = active;
            this.inactiveStudents = inactive;
        }

        public int getTotalStudents() { return totalStudents; }
        public int getActiveStudents() { return activeStudents; }
        public int getInactiveStudents() { return inactiveStudents; }

        @Override
        public String toString() {
            return String.format("Stats[Total: %d, Active: %d, Inactive: %d]",
                    totalStudents, activeStudents, inactiveStudents);
        }
    }

    public StudentService() {
        this.students = new ConcurrentHashMap<>();
    }

    public void addStudent(Student student) throws DuplicateStudentException {
        ValidationUtil.validateStudent(student);

        if (students.containsKey(student.getId())) {
            throw new DuplicateStudentException("Student with ID " + student.getId() + " already exists");
        }

        boolean regNoExists = students.values().stream()
                .anyMatch(s -> s.getRegNo().equals(student.getRegNo()));

        if (regNoExists) {
            throw new DuplicateStudentException("Student with registration number " + student.getRegNo() + " already exists");
        }

        students.put(student.getId(), student);
    }

    public Student findStudentById(String id) {
        return students.get(id);
    }

    public Student findStudentByRegNo(String regNo) {
        return students.values().stream()
                .filter(s -> s.getRegNo().equals(regNo))
                .findFirst()
                .orElse(null);
    }

    public List<Student> getAllStudents() {
        return new ArrayList<>(students.values());
    }

    public List<Student> getActiveStudents() {
        return students.values().stream()
                .filter(s -> s.getStatus() == Student.Status.ACTIVE)
                .collect(Collectors.toList());
    }

    public void updateStudent(Student student) throws StudentNotFoundException {
        if (!students.containsKey(student.getId())) {
            throw new StudentNotFoundException("Student with ID " + student.getId() + " not found");
        }
        students.put(student.getId(), student);
    }

    public void deactivateStudent(String studentId) throws StudentNotFoundException {
        Student student = findStudentById(studentId);
        if (student == null) {
            throw new StudentNotFoundException("Student with ID " + studentId + " not found");
        }
        student.setStatus(Student.Status.INACTIVE);
    }

    public ServiceStats getServiceStats() {
        List<Student> allStudents = getAllStudents();
        int total = allStudents.size();
        int active = (int) allStudents.stream()
                .filter(s -> s.getStatus() == Student.Status.ACTIVE)
                .count();
        int inactive = total - active;

        return new ServiceStats(total, active, inactive);
    }

    public class SearchCriteria {
        private String namePattern;
        private Student.Status status;
        private String department;

        public SearchCriteria setNamePattern(String pattern) {
            this.namePattern = pattern;
            return this;
        }

        public SearchCriteria setStatus(Student.Status status) {
            this.status = status;
            return this;
        }

        public SearchCriteria setDepartment(String department) {
            this.department = department;
            return this;
        }

        public List<Student> search() {
            return students.values().stream()
                    .filter(s -> namePattern == null ||
                            s.getFullName().toString().toLowerCase().contains(namePattern.toLowerCase()))
                    .filter(s -> status == null || s.getStatus() == status)
                    .collect(Collectors.toList());
        }
    }

    public SearchCriteria createSearchCriteria() {
        return new SearchCriteria();
    }
}

