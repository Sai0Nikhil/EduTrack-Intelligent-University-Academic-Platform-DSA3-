package edutrack.model;

/**
 * Represents a student assignment or academic essay submission.
 */
public class AssignmentSubmission {
    private String submissionId;
    private String studentId;
    private String courseCode;
    private String title;
    private String textContent;
    private long timestamp;

    public AssignmentSubmission(String submissionId, String studentId, String courseCode, String title, String textContent, long timestamp) {
        this.submissionId = submissionId;
        this.studentId = studentId;
        this.courseCode = courseCode;
        this.title = title;
        this.textContent = textContent;
        this.timestamp = timestamp;
    }

    public String getSubmissionId() { return submissionId; }
    public String getStudentId() { return studentId; }
    public String getCourseCode() { return courseCode; }
    public String getTitle() { return title; }
    public String getTextContent() { return textContent; }
    public long getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return submissionId + " [" + courseCode + "] Student: " + studentId + " - " + title + " (" + textContent.length() + " chars)";
    }
}
