package edu.ccrm.io;

import edu.ccrm.domain.*;
import java.io.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ImportExportService implements Persistable<Object> {
    private static final String CSV_DELIMITER = ",";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    public List<Student> importStudents(Path filePath) throws IOException {
        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("File not found: " + filePath);
        }

        List<Student> students = new ArrayList<>();

        try (Stream<String> lines = Files.lines(filePath, StandardCharsets.UTF_8)) {
            List<String> lineList = lines.collect(Collectors.toList());

            if (lineList.isEmpty()) {
                return students;
            }

            for (int i = 1; i < lineList.size(); i++) {
                String line = lineList.get(i).trim();
                if (line.isEmpty()) continue;

                try {
                    String[] parts = line.split(CSV_DELIMITER);
                    if (parts.length >= 4) {
                        String id = parts[0].trim();
                        String regNo = parts[1].trim();
                        String firstName = parts[2].trim();
                        String lastName = parts[3].trim();
                        String email = parts.length > 4 ? parts[4].trim() : "";

                        Name fullName = new Name(firstName, lastName);
                        Student student = new Student.Builder(id, regNo)
                                .fullName(fullName)
                                .email(email)
                                .build();

                        students.add(student);
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing line " + (i + 1) + ": " + e.getMessage());
                }
            }
        }

        return students;
    }

    public void exportStudents(List<Student> students, Path exportDir) throws IOException {
        Files.createDirectories(exportDir);
        Path filePath = exportDir.resolve("students_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv");

        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {

            writer.write("ID,RegNo,FirstName,LastName,Email,Status,CreatedAt");
            writer.newLine();

            for (Student student : students) {
                writer.write(String.format("%s,%s,%s,%s,%s,%s,%s",
                        student.getId(),
                        student.getRegNo(),
                        student.getFullName().getFirstName(),
                        student.getFullName().getLastName(),
                        student.getEmail() != null ? student.getEmail() : "",
                        student.getStatus(),
                        student.getCreatedAt().format(DATE_FORMATTER)));
                writer.newLine();
            }
        }

        System.out.println("Students exported to: " + filePath);
    }

    public List<Course> importCourses(Path filePath) throws IOException {
        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("File not found: " + filePath);
        }

        List<Course> courses = new ArrayList<>();

        try (Stream<String> lines = Files.lines(filePath, StandardCharsets.UTF_8)) {
            List<String> lineList = lines.collect(Collectors.toList());

            if (lineList.isEmpty()) {
                return courses;
            }

            for (int i = 1; i < lineList.size(); i++) {
                String line = lineList.get(i).trim();
                if (line.isEmpty()) continue;

                try {
                    String[] parts = line.split(CSV_DELIMITER);
                    if (parts.length >= 6) {
                        String code = parts[0].trim();
                        String title = parts[1].trim();
                        int credits = Integer.parseInt(parts[2].trim());
                        String instructor = parts[3].trim();
                        String department = parts[4].trim();
                        Semester semester = Semester.valueOf(parts[5].trim().toUpperCase());

                        Course course = new Course.Builder(code, title)
                                .credits(credits)
                                .instructor(instructor)
                                .department(department)
                                .semester(semester)
                                .build();

                        courses.add(course);
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing line " + (i + 1) + ": " + e.getMessage());
                }
            }
        }

        return courses;
    }

    public void exportCourses(List<Course> courses, Path exportDir) throws IOException {
        Files.createDirectories(exportDir);
        Path filePath = exportDir.resolve("courses_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv");

        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {

            writer.write("Code,Title,Credits,Instructor,Department,Semester,Active,CreatedAt");
            writer.newLine();

            for (Course course : courses) {
                writer.write(String.format("%s,%s,%d,%s,%s,%s,%s,%s",
                        course.getCode(),
                        course.getTitle(),
                        course.getCredits(),
                        course.getInstructor(),
                        course.getDepartment(),
                        course.getSemester(),
                        course.isActive(),
                        course.getCreatedAt().format(DATE_FORMATTER)));
                writer.newLine();
            }
        }

        System.out.println("Courses exported to: " + filePath);
    }

    @Override
    public void save(List<Object> items, Path filePath) throws IOException {

        Files.createDirectories(filePath.getParent());

        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
            for (Object item : items) {
                writer.write(item.toString());
                writer.newLine();
            }
        }
    }

    @Override
    public List<Object> load(Path filePath) throws IOException {
        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }

        try (Stream<String> lines = Files.lines(filePath, StandardCharsets.UTF_8)) {
            return lines
                    .filter(line -> !line.trim().isEmpty())
                    .collect(Collectors.toList())
                    .stream()
                    .map(Object.class::cast)
                    .collect(Collectors.toList());
        }
    }
}

