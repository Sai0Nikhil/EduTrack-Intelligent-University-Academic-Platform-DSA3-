package edutrack.model;

/**
 * Represents a university lecture hall or laboratory classroom.
 */
public class Classroom {
    private String roomId;
    private String building;
    private int capacity;
    private boolean isLab;

    public Classroom(String roomId, String building, int capacity, boolean isLab) {
        this.roomId = roomId;
        this.building = building;
        this.capacity = capacity;
        this.isLab = isLab;
    }

    public String getRoomId() { return roomId; }
    public String getBuilding() { return building; }
    public int getCapacity() { return capacity; }
    public boolean isLab() { return isLab; }

    @Override
    public String toString() {
        return roomId + " (" + building + ", Cap: " + capacity + (isLab ? ", Lab" : ", Hall") + ")";
    }
}
