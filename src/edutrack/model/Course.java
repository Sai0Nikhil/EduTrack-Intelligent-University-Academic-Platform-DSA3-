package edutrack.model;

import edutrack.core.MyArrayList;

/**
 * Represents an academic course in EduTrack.
 */
public class Course {
    private String code;
    private String title;
    private String department;
    private int credits;
    private MyArrayList<String> prerequisites;
    private int matrixDimensionRows;
    private int matrixDimensionCols;

    public Course(String code, String title, String department, int credits, int matrixRows, int matrixCols) {
        this.code = code;
        this.title = title;
        this.department = department;
        this.credits = credits;
        this.prerequisites = new MyArrayList<>();
        this.matrixDimensionRows = matrixRows;
        this.matrixDimensionCols = matrixCols;
    }

    public void addPrerequisite(String prereqCode) {
        if (!prerequisites.contains(prereqCode)) {
            prerequisites.add(prereqCode);
        }
    }

    public String getCode() { return code; }
    public String getTitle() { return title; }
    public String getDepartment() { return department; }
    public int getCredits() { return credits; }
    public MyArrayList<String> getPrerequisites() { return prerequisites; }
    public int getMatrixDimensionRows() { return matrixDimensionRows; }
    public int getMatrixDimensionCols() { return matrixDimensionCols; }

    @Override
    public String toString() {
        return "[" + code + "] " + title + " (" + department + ", " + credits + " cr)";
    }
}
