package edutrack.model;

import edutrack.core.MyArrayList;

/**
 * Represents a student record in EduTrack.
 */
public class Student implements Comparable<Student> {
    private String id;
    private String name;
    private String department;
    private double gpa;
    private int completedCredits;
    private double attendanceRate;
    private MyArrayList<String> enrolledCourses;

    public Student(String id, String name, String department, double gpa, int completedCredits, double attendanceRate) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.gpa = gpa;
        this.completedCredits = completedCredits;
        this.attendanceRate = attendanceRate;
        this.enrolledCourses = new MyArrayList<>();
    }

    public void enroll(String courseCode) {
        if (!enrolledCourses.contains(courseCode)) {
            enrolledCourses.add(courseCode);
        }
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public double getGpa() { return gpa; }
    public void setGpa(double gpa) { this.gpa = gpa; }
    public int getCompletedCredits() { return completedCredits; }
    public double getAttendanceRate() { return attendanceRate; }
    public MyArrayList<String> getEnrolledCourses() { return enrolledCourses; }

    @Override
    public int compareTo(Student other) {
        // Default ranking: Higher GPA first. If tie, higher credits first.
        if (Double.compare(other.gpa, this.gpa) != 0) {
            return Double.compare(other.gpa, this.gpa);
        }
        return Integer.compare(other.completedCredits, this.completedCredits);
    }

    @Override
    public String toString() {
        return id + " | " + name + " (" + department + ") | GPA: " + String.format("%.2f", gpa) + " | Credits: " + completedCredits;
    }
}
