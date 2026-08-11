package gh.dso.model;

/**
 * A node in the delivery network: a vendor, customer zone, rider hub, or landmark.
 * Field names and the string ID convention (e.g. "L001") follow the team's
 * shared CSV template (locations_template.csv).
 */
public class Location {
    private final String locationId;
    private final String name;
    private final String area;
    private final String locationType; // Vendor, CustomerZone, RiderHub, Landmark
    private final double xCoord;       // latitude
    private final double yCoord;       // longitude

    public Location(String locationId, String name, String area, String locationType,
                     double xCoord, double yCoord) {
        this.locationId = locationId;
        this.name = name;
        this.area = area;
        this.locationType = locationType;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
    }

    public String getLocationId() { return locationId; }
    public String getName() { return name; }
    public String getArea() { return area; }
    public String getLocationType() { return locationType; }
    public double getXCoord() { return xCoord; }
    public double getYCoord() { return yCoord; }

    @Override
    public String toString() {
        return name + " [" + area + ", " + locationType + "] (#" + locationId + ")";
    }
}
