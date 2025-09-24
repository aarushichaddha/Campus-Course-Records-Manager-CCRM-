package edu.ccrm.cli;

import edu.ccrm.config.AppConfig;
import edu.ccrm.domain.*;
import edu.ccrm.service.*;
import edu.ccrm.io.*;
import edu.ccrm.util.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.io.IOException;
import java.nio.file.*;

public class CCRMApplication {
    private static final Scanner scanner = new Scanner(System.in);
    private static StudentService studentService;
    private static CourseService courseService;
    private static EnrollmentService enrollmentService;
    private static TranscriptService transcriptService;
    private static ImportExportService ioService;
    private static BackupService backupService;

    public static void main(String[] args) {
        try {
            System.out.println("=== Campus Course & Records Manager (CCRM) ===");
            System.out.println("Java Platform: " + getJavaPlatformInfo());


            AppConfig config = AppConfig.getInstance();
            System.out.println("Data Directory: " + config.getDataDirectory());

            initializeServices();
            runMainMenu();

        }
        catch (Exception e)
        {
            System.err.println("Application error: " + e.getMessage());
            e.printStackTrace();
        }
        finally
        {
            scanner.close();
        }
    }

    private static void initializeServices() throws IOException {
        studentService = new StudentService();
        courseService = new CourseService();
        enrollmentService = new EnrollmentService(studentService, courseService);
        transcriptService = new TranscriptService();
        ioService = new ImportExportService();
        backupService = new BackupService();
    }

    private static void runMainMenu() {
        boolean running = true;


        mainLoop: while (running) {
            displayMainMenu();

            try {
                int choice = scanner.nextInt();
                scanner.nextLine();
                switch (choice) {
                    case 1 -> manageStudents();
                    case 2 -> manageCourses();
                    case 3 -> manageEnrollments();
                    case 4 -> manageGrades();
                    case 5 -> fileOperations();
                    case 6 -> generateReports();
                    case 7 -> {
                        System.out.println("Goodbye!");
                        running = false;
                        break mainLoop;
                    }
                    default -> System.out.println("Invalid choice. Please try again.");
                }

            }
            catch (InputMismatchException e)
            {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
            catch (Exception e)
            {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    private static void displayMainMenu()
    {
        System.out.println("\n=== MAIN MENU ===");
        System.out.println("1. Manage Students");
        System.out.println("2. Manage Courses");
        System.out.println("3. Manage Enrollments");
        System.out.println("4. Manage Grades");
        System.out.println("5. File Operations");
        System.out.println("6. Generate Reports");
        System.out.println("7. Exit");
        System.out.print("Enter your choice: ");
    }

    private static void manageStudents()
    {
        System.out.println("\n=== STUDENT MANAGEMENT ===");
        System.out.println("1. Add Student");
        System.out.println("2. List Students");
        System.out.println("3. Update Student");
        System.out.println("4. Deactivate Student");
        System.out.println("5. View Student Profile");
        System.out.print("Enter choice: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                addStudent();
                break;
            case 2:
                listStudents();
                break;
            case 3:
                updateStudent();
                break;
            case 4:
                deactivateStudent();
                break;
            case 5:
                viewStudentProfile();
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private static void addStudent()
    {
        try
        {
            System.out.print("Enter student ID: ");
            String id = scanner.nextLine();

            System.out.print("Enter registration number: ");
            String regNo = scanner.nextLine();

            System.out.print("Enter first name: ");
            String firstName = scanner.nextLine();

            System.out.print("Enter last name: ");
            String lastName = scanner.nextLine();

            // Using immutable Name class
            Name fullName = new Name(firstName, lastName);

            System.out.print("Enter email: ");
            String email = scanner.nextLine();

            Student student = new Student.Builder(id, regNo)
                    .fullName(fullName)
                    .email(email)
                    .build();

            studentService.addStudent(student);
            System.out.println("Student added successfully!");

        }
        catch (Exception e)
        {
            System.err.println("Error adding student: " + e.getMessage());
        }
    }

    private static void listStudents() {
        List<Student> students = studentService.getAllStudents();


        System.out.println("\n=== ALL STUDENTS ===");
        for (Student student : students) {
            System.out.println(student);
        }

        // Stream API demonstration
        long activeStudents = students.stream()
                .filter(s -> s.getStatus() == Student.Status.ACTIVE)
                .count();

        System.out.println("\nTotal Students: " + students.size());
        System.out.println("Active Students: " + activeStudents);
    }

    private static void updateStudent() {
        System.out.print("Enter student ID to update: ");
        String id = scanner.nextLine();

        try {
            Student student = studentService.findStudentById(id);
            if (student != null) {
                System.out.print("Enter new email (current: " + student.getEmail() + "): ");
                String newEmail = scanner.nextLine();

                if (!newEmail.trim().isEmpty()) {
                    student.setEmail(newEmail);
                    studentService.updateStudent(student);
                    System.out.println("Student updated successfully!");
                }
            } else {
                System.out.println("Student not found.");
            }
        } catch (Exception e) {
            System.err.println("Error updating student: " + e.getMessage());
        }
    }

    private static void deactivateStudent() {
        System.out.print("Enter student ID to deactivate: ");
        String id = scanner.nextLine();

        try {
            studentService.deactivateStudent(id);
            System.out.println("Student deactivated successfully!");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void viewStudentProfile() {
        System.out.print("Enter student ID: ");
        String id = scanner.nextLine();

        try {
            Student student = studentService.findStudentById(id);
            if (student != null) {
                System.out.println("\n=== STUDENT PROFILE ===");
                System.out.println(student.getDetailedProfile());

                // Show transcript
                String transcript = transcriptService.generateTranscript(student);
                System.out.println("\n=== TRANSCRIPT ===");
                System.out.println(transcript);
            } else {
                System.out.println("Student not found.");
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void manageCourses() {
        System.out.println("\n=== COURSE MANAGEMENT ===");
        System.out.println("1. Add Course");
        System.out.println("2. List Courses");
        System.out.println("3. Search Courses");
        System.out.println("4. Update Course");
        System.out.print("Enter choice: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1 -> addCourse();
            case 2 -> listCourses();
            case 3 -> searchCourses();
            case 4 -> updateCourse();
            default -> System.out.println("Invalid choice.");
        }
    }

    private static void addCourse() {
        try {
            System.out.print("Enter course code: ");
            String code = scanner.nextLine();

            System.out.print("Enter course title: ");
            String title = scanner.nextLine();

            System.out.print("Enter credits: ");
            int credits = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Enter department: ");
            String department = scanner.nextLine();

            System.out.print("Enter instructor name: ");
            String instructorName = scanner.nextLine();

            // Display semester options using enum
            System.out.println("Available semesters:");
            Semester[] semesters = Semester.values();
            // Traditional for loop with counter
            for (int i = 0; i < semesters.length; i++) {
                System.out.println((i + 1) + ". " + semesters[i]);
            }

            System.out.print("Choose semester (1-" + semesters.length + "): ");
            int semesterChoice = scanner.nextInt() - 1;
            scanner.nextLine();

            if (semesterChoice >= 0 && semesterChoice < semesters.length) {
                Course course = new Course.Builder(code, title)
                        .credits(credits)
                        .department(department)
                        .instructor(instructorName)
                        .semester(semesters[semesterChoice])
                        .build();

                courseService.addCourse(course);
                System.out.println("Course added successfully!");
            } else {
                System.out.println("Invalid semester choice.");
            }

        } catch (Exception e) {
            System.err.println("Error adding course: " + e.getMessage());
        }
    }

    private static void listCourses() {
        List<Course> courses = courseService.getAllCourses();

        System.out.println("\n=== ALL COURSES ===");

        int index = 0;
        while (index < courses.size()) {
            Course course = courses.get(index);
            System.out.println(course);
            index++;


            if (!course.isActive()) {
                continue;
            }
        }
    }

    private static void searchCourses() {
        System.out.println("Search by:");
        System.out.println("1. Department");
        System.out.println("2. Instructor");
        System.out.println("3. Semester");
        System.out.print("Enter choice: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        List<Course> results = new ArrayList<>();

        try {
            switch (choice) {
                case 1:
                    System.out.print("Enter department: ");
                    String dept = scanner.nextLine();
                    results = courseService.searchByDepartment(dept);
                    break;
                case 2:
                    System.out.print("Enter instructor name: ");
                    String instructor = scanner.nextLine();
                    results = courseService.searchByInstructor(instructor);
                    break;
                case 3:
                    System.out.print("Enter semester: ");
                    String semesterName = scanner.nextLine();
                    try {
                        Semester semester = Semester.valueOf(semesterName.toUpperCase());
                        results = courseService.searchBySemester(semester);
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid semester name.");
                        return;
                    }
                    break;
                default:
                    System.out.println("Invalid choice.");
                    return;
            }

            System.out.println("\n=== SEARCH RESULTS ===");
            results.forEach(System.out::println); // Lambda expression
            System.out.println("Found " + results.size() + " courses.");

        } catch (Exception e) {
            System.err.println("Search error: " + e.getMessage());
        }
    }

    private static void updateCourse() {
        System.out.print("Enter course code to update: ");
        String code = scanner.nextLine();

        try {
            Course course = courseService.findCourseByCode(code);
            if (course != null) {
                System.out.print("Enter new instructor (current: " + course.getInstructor() + "): ");
                String newInstructor = scanner.nextLine();

                if (!newInstructor.trim().isEmpty()) {
                    course.setInstructor(newInstructor);
                    courseService.updateCourse(course);
                    System.out.println("Course updated successfully!");
                }
            } else {
                System.out.println("Course not found.");
            }
        } catch (Exception e) {
            System.err.println("Error updating course: " + e.getMessage());
        }
    }

    private static void manageEnrollments() {
        System.out.println("\n=== ENROLLMENT MANAGEMENT ===");
        System.out.println("1. Enroll Student");
        System.out.println("2. Unenroll Student");
        System.out.println("3. View Student Enrollments");
        System.out.print("Enter choice: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1 -> enrollStudent();
            case 2 -> unenrollStudent();
            case 3 -> viewEnrollments();
            default -> System.out.println("Invalid choice.");
        }
    }

    private static void enrollStudent() {
        try {
            System.out.print("Enter student ID: ");
            String studentId = scanner.nextLine();

            System.out.print("Enter course code: ");
            String courseCode = scanner.nextLine();

            enrollmentService.enrollStudent(studentId, courseCode);
            System.out.println("Student enrolled successfully!");

        } catch (Exception e) {
            System.err.println("Enrollment error: " + e.getMessage());
        }
    }

    private static void unenrollStudent() {
        try {
            System.out.print("Enter student ID: ");
            String studentId = scanner.nextLine();

            System.out.print("Enter course code: ");
            String courseCode = scanner.nextLine();

            enrollmentService.unenrollStudent(studentId, courseCode);
            System.out.println("Student unenrolled successfully!");

        } catch (Exception e) {
            System.err.println("Unenrollment error: " + e.getMessage());
        }
    }

    private static void viewEnrollments() {
        System.out.print("Enter student ID: ");
        String studentId = scanner.nextLine();

        try {
            List<Enrollment> enrollments = enrollmentService.getStudentEnrollments(studentId);

            System.out.println("\n=== STUDENT ENROLLMENTS ===");
            // Do-while loop demonstration
            int i = 0;
            if (!enrollments.isEmpty()) {
                do {
                    System.out.println(enrollments.get(i));
                    i++;
                } while (i < enrollments.size());
            } else {
                System.out.println("No enrollments found.");
            }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void manageGrades() {
        System.out.println("\n=== GRADE MANAGEMENT ===");
        System.out.println("1. Record Grade");
        System.out.println("2. View Grades");
        System.out.println("3. Calculate GPA");
        System.out.print("Enter choice: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1 -> recordGrade();
            case 2 -> viewGrades();
            case 3 -> calculateGPA();
            default -> System.out.println("Invalid choice.");
        }
    }

    private static void recordGrade() {
        try {
            System.out.print("Enter student ID: ");
            String studentId = scanner.nextLine();

            System.out.print("Enter course code: ");
            String courseCode = scanner.nextLine();

            System.out.print("Enter marks (0-100): ");
            double marks = scanner.nextDouble();
            scanner.nextLine();

            enrollmentService.recordGrade(studentId, courseCode, marks);
            System.out.println("Grade recorded successfully!");

        } catch (Exception e) {
            System.err.println("Error recording grade: " + e.getMessage());
        }
    }

    private static void viewGrades() {
        System.out.print("Enter student ID: ");
        String studentId = scanner.nextLine();

        try {
            List<Enrollment> enrollments = enrollmentService.getStudentEnrollments(studentId);

            System.out.println("\n=== STUDENT GRADES ===");
            for (Enrollment enrollment : enrollments) {
                if (enrollment.getGrade() != null) {
                    System.out.printf("%s: %.2f (%s)%n",
                            enrollment.getCourse().getCode(),
                            enrollment.getMarks(),
                            enrollment.getGrade());
                }
            }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void calculateGPA() {
        System.out.print("Enter student ID: ");
        String studentId = scanner.nextLine();

        try {
            Student student = studentService.findStudentById(studentId);
            if (student != null) {
                double gpa = transcriptService.calculateGPA(student);
                System.out.printf("Student GPA: %.2f%n", gpa);
            } else {
                System.out.println("Student not found.");
            }
        } catch (Exception e) {
            System.err.println("Error calculating GPA: " + e.getMessage());
        }
    }

    private static void fileOperations() {
        System.out.println("\n=== FILE OPERATIONS ===");
        System.out.println("1. Import Data");
        System.out.println("2. Export Data");
        System.out.println("3. Backup Data");
        System.out.println("4. Show Backup Size");
        System.out.print("Enter choice: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1 -> importData();
            case 2 -> exportData();
            case 3 -> backupData();
            case 4 -> showBackupSize();
            default -> System.out.println("Invalid choice.");
        }
    }

    private static void importData() {
        try {
            System.out.println("1. Import Students");
            System.out.println("2. Import Courses");
            System.out.print("Enter choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Enter file path: ");
            String filePath = scanner.nextLine();

            switch (choice) {
                case 1:
                    List<Student> students = ioService.importStudents(Paths.get(filePath));
                    for (Student student : students) {
                        studentService.addStudent(student);
                    }
                    System.out.println("Students imported successfully!");
                    break;
                case 2:
                    List<Course> courses = ioService.importCourses(Paths.get(filePath));
                    for (Course course : courses) {
                        courseService.addCourse(course);
                    }
                    System.out.println("Courses imported successfully!");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }

        } catch (Exception e) {
            System.err.println("Import error: " + e.getMessage());
        }
    }

    private static void exportData() {
        try {
            AppConfig config = AppConfig.getInstance();
            Path exportDir = Paths.get(config.getDataDirectory(), "exports");

            ioService.exportStudents(studentService.getAllStudents(), exportDir);
            ioService.exportCourses(courseService.getAllCourses(), exportDir);

            System.out.println("Data exported to: " + exportDir);

        } catch (Exception e) {
            System.err.println("Export error: " + e.getMessage());
        }
    }

    private static void backupData() {
        try {
            Path backupPath = backupService.createBackup();
            System.out.println("Backup created at: " + backupPath);

        } catch (Exception e) {
            System.err.println("Backup error: " + e.getMessage());
        }
    }

    private static void showBackupSize() {
        try {
            AppConfig config = AppConfig.getInstance();
            Path backupDir = Paths.get(config.getDataDirectory(), "backups");

            if (Files.exists(backupDir)) {
                long totalSize = RecursiveUtil.calculateDirectorySize(backupDir);
                System.out.printf("Total backup size: %d bytes (%.2f MB)%n",
                        totalSize, totalSize / (1024.0 * 1024.0));


                System.out.println("\nBackup directory structure:");
                RecursiveUtil.printDirectoryStructure(backupDir, 0);
            } else {
                System.out.println("No backup directory found.");
            }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void generateReports() {
        System.out.println("\n=== REPORTS ===");
        System.out.println("1. Top Students by GPA");
        System.out.println("2. GPA Distribution");
        System.out.println("3. Course Enrollment Statistics");
        System.out.print("Enter choice: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1 -> showTopStudents();
            case 2 -> showGPADistribution();
            case 3 -> showEnrollmentStats();
            default -> System.out.println("Invalid choice.");
        }
    }

    private static void showTopStudents() {
        try {
            List<Student> students = studentService.getAllStudents();


            List<Student> topStudents = students.stream()
                    .filter(s -> s.getStatus() == Student.Status.ACTIVE)
                    .sorted((s1, s2) -> Double.compare(
                            transcriptService.calculateGPA(s2),
                            transcriptService.calculateGPA(s1)))
                    .limit(10)
                    .collect(Collectors.toList());

            System.out.println("\n=== TOP 10 STUDENTS BY GPA ===");
            int rank = 1;
            for (Student student : topStudents) {
                double gpa = transcriptService.calculateGPA(student);
                System.out.printf("%d. %s - GPA: %.2f%n",
                        rank++, student.getFullName(), gpa);
            }

        } catch (Exception e) {
            System.err.println("Error generating report: " + e.getMessage());
        }
    }

    private static void showGPADistribution() {
        try {
            List<Student> students = studentService.getAllStudents();

            Map<String, Long> gpaDistribution = students.stream()
                    .filter(s -> s.getStatus() == Student.Status.ACTIVE)
                    .mapToDouble(transcriptService::calculateGPA)
                    .boxed()
                    .collect(Collectors.groupingBy(
                            gpa -> {
                                if (gpa >= 3.5) return "Excellent (3.5-4.0)";
                                else if (gpa >= 3.0) return "Good (3.0-3.49)";
                                else if (gpa >= 2.0) return "Average (2.0-2.99)";
                                else return "Below Average (<2.0)";
                            },
                            Collectors.counting()
                    ));

            System.out.println("\n=== GPA DISTRIBUTION ===");
            gpaDistribution.forEach((range, count) ->
                    System.out.printf("%s: %d students%n", range, count));

        } catch (Exception e) {
            System.err.println("Error generating report: " + e.getMessage());
        }
    }

    private static void showEnrollmentStats() {
        try {
            List<Course> courses = courseService.getAllCourses();

            System.out.println("\n=== COURSE ENROLLMENT STATISTICS ===");
            courses.stream()
                    .filter(Course::isActive)
                    .forEach(course -> {
                        int enrollmentCount = enrollmentService.getCourseEnrollmentCount(course.getCode());
                        System.out.printf("%s: %d students enrolled%n",
                                course.getCode(), enrollmentCount);
                    });

        } catch (Exception e) {
            System.err.println("Error generating report: " + e.getMessage());
        }
    }

    private static String getJavaPlatformInfo() {
        return "Java SE - Standard Edition (Desktop/Server applications)";
    }
}
