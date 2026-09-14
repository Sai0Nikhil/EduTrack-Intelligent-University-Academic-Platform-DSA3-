package edutrack.model;

/**
 * Represents a continuous streaming activity event in EduTrack.
 */
public class ActivityLog {
    private String eventId;
    private String studentId;
    private String eventType;
    private String details;
    private long timestamp;

    public ActivityLog(String eventId, String studentId, String eventType, String details, long timestamp) {
        this.eventId = eventId;
        this.studentId = studentId;
        this.eventType = eventType;
        this.details = details;
        this.timestamp = timestamp;
    }

    public String getEventId() { return eventId; }
    public String getStudentId() { return studentId; }
    public String getEventType() { return eventType; }
    public String getDetails() { return details; }
    public long getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return "[" + timestamp + "] " + studentId + " -> " + eventType + " (" + details + ")";
    }
}
