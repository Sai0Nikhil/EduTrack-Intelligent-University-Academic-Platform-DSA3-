package edutrack.model;

import edutrack.core.MyArrayList;

/**
 * Represents a faculty member in EduTrack.
 */
public class Faculty {
    private String id;
    private String name;
    private String department;
    private int maxWorkload;
    private MyArrayList<String> eligibleCourses;

    public Faculty(String id, String name, String department, int maxWorkload) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.maxWorkload = maxWorkload;
        this.eligibleCourses = new MyArrayList<>();
    }

    public void addEligibleCourse(String courseCode) {
        if (!eligibleCourses.contains(courseCode)) {
            eligibleCourses.add(courseCode);
        }
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public int getMaxWorkload() { return maxWorkload; }
    public MyArrayList<String> getEligibleCourses() { return eligibleCourses; }

    @Override
    public String toString() {
        return id + " | " + name + " (" + department + ") [Cap: " + maxWorkload + " courses]";
    }
}
