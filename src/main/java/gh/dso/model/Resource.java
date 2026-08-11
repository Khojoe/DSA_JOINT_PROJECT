package gh.dso.model;

/**
 * A rider or vehicle that can be assigned to service requests.
 * Field names and string ID convention follow resources_template.csv.
 */
public class Resource {
    private final String resourceId;
    private final String resourceType;   // Rider, Bicycle, Tricycle, Van
    private final String homeLocationId;
    private final int capacity;
    private String availabilityStatus;   // AVAILABLE, BUSY, OFFLINE

    public Resource(String resourceId, String resourceType, String homeLocationId,
                     int capacity, String availabilityStatus) {
        this.resourceId = resourceId;
        this.resourceType = resourceType;
        this.homeLocationId = homeLocationId;
        this.capacity = capacity;
        this.availabilityStatus = availabilityStatus;
    }

    public String getResourceId() { return resourceId; }
    public String getResourceType() { return resourceType; }
    public String getHomeLocationId() { return homeLocationId; }
    public int getCapacity() { return capacity; }
    public String getAvailabilityStatus() { return availabilityStatus; }
    public void setAvailabilityStatus(String availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    @Override
    public String toString() {
        return resourceType + "#" + resourceId + " (capacity=" + capacity + ", " + availabilityStatus + ")";
    }
}
