package edutrack.io;

import edutrack.core.MyArrayList;
import edutrack.model.Course;
import edutrack.model.Faculty;
import edutrack.model.Student;
import edutrack.model.AssignmentSubmission;
import edutrack.model.ActivityLog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Loads and parses datasets from the data/ directory into EduTrack domain models.
 * Zero java.util.* dependencies.
 */
public class DatasetLoader {

    public static MyArrayList<Course> loadCourses(String filepath) {
        MyArrayList<Course> list = new MyArrayList<>();
        File file = new File(filepath);
        if (!file.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine(); // Header
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = splitCsvLine(line);
                if (parts.length >= 6) {
                    String code = parts[0].trim();
                    String title = parts[1].trim();
                    String dept = parts[2].trim();
                    int credits = Integer.parseInt(parts[3].trim());
                    int rows = Integer.parseInt(parts[4].trim());
                    int cols = Integer.parseInt(parts[5].trim());

                    Course c = new Course(code, title, dept, credits, rows, cols);
                    if (parts.length >= 7 && !parts[6].trim().isEmpty()) {
                        String[] prereqs = parts[6].trim().split(";");
                        for (String pr : prereqs) {
                            c.addPrerequisite(pr.trim());
                        }
                    }
                    list.add(c);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading courses: " + e.getMessage());
        }
        return list;
    }

    public static MyArrayList<Student> loadStudents(String filepath) {
        MyArrayList<Student> list = new MyArrayList<>();
        File file = new File(filepath);
        if (!file.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine(); // Header
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = splitCsvLine(line);
                if (parts.length >= 6) {
                    String id = parts[0].trim();
                    String name = parts[1].trim();
                    String dept = parts[2].trim();
                    double gpa = Double.parseDouble(parts[3].trim());
                    int credits = Integer.parseInt(parts[4].trim());
                    double att = Double.parseDouble(parts[5].trim());

                    Student s = new Student(id, name, dept, gpa, credits, att);
                    if (parts.length >= 7 && !parts[6].trim().isEmpty()) {
                        String[] courses = parts[6].trim().split(";");
                        for (String cr : courses) {
                            s.enroll(cr.trim());
                        }
                    }
                    list.add(s);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading students: " + e.getMessage());
        }
        return list;
    }

    public static MyArrayList<Faculty> loadFaculty(String filepath) {
        MyArrayList<Faculty> list = new MyArrayList<>();
        File file = new File(filepath);
        if (!file.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine(); // Header
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = splitCsvLine(line);
                if (parts.length >= 4) {
                    String id = parts[0].trim();
                    String name = parts[1].trim();
                    String dept = parts[2].trim();
                    int maxWorkload = Integer.parseInt(parts[3].trim());

                    Faculty f = new Faculty(id, name, dept, maxWorkload);
                    if (parts.length >= 5 && !parts[4].trim().isEmpty()) {
                        String[] eligible = parts[4].trim().split(";");
                        for (String el : eligible) {
                            f.addEligibleCourse(el.trim());
                        }
                    }
                    list.add(f);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading faculty: " + e.getMessage());
        }
        return list;
    }

    public static MyArrayList<AssignmentSubmission> loadSubmissions(String dirPath) {
        MyArrayList<AssignmentSubmission> list = new MyArrayList<>();
        File dir = new File(dirPath);
        if (!dir.exists() || !dir.isDirectory()) return list;

        File[] files = dir.listFiles();
        if (files == null) return list;

        for (File f : files) {
            if (f.isFile() && f.getName().endsWith(".txt")) {
                try {
                    String content = readFileToString(f);
                    String filename = f.getName();
                    String id = filename.replace(".txt", "");
                    AssignmentSubmission sub = new AssignmentSubmission(id, "STU", "CS", filename, content, System.currentTimeMillis());
                    list.add(sub);
                } catch (IOException e) {
                    System.err.println("Error reading submission file: " + f.getName());
                }
            }
        }
        return list;
    }

    public static String readDocument(String filepath) {
        try {
            return readFileToString(new File(filepath));
        } catch (IOException e) {
            return "Document could not be read: " + e.getMessage();
        }
    }

    public static MyArrayList<ActivityLog> loadActivityLogs(String filepath) {
        MyArrayList<ActivityLog> list = new MyArrayList<>();
        File file = new File(filepath);
        if (!file.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    long ts = Long.parseLong(parts[0].trim());
                    String stuId = parts[1].trim();
                    String eventType = parts[2].trim();
                    String details = parts[3].trim();
                    list.add(new ActivityLog("EVT_" + ts, stuId, eventType, details, ts));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading activity stream: " + e.getMessage());
        }
        return list;
    }

    private static String readFileToString(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }

    private static String[] splitCsvLine(String line) {
        return line.split(",");
    }
}
