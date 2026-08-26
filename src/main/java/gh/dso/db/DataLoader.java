package gh.dso.db;

import gh.dso.datastructures.list.MyLinkedList;
import gh.dso.model.AlgorithmRun;
import gh.dso.model.Location;
import gh.dso.model.Resource;
import gh.dso.model.Road;
import gh.dso.model.ServiceRequest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Reads seed CSVs into the MySQL database, and reloads database rows into
 * our custom data structures (MyLinkedList) for the rest of the system to use.
 *
 * CSV column names and ID formats match the team's shared templates
 * (locations_template.csv, roads_template.csv, resources_template.csv,
 * service_requests_template.csv): string IDs like "L001", "R001", "Q001",
 * and ISO-style timestamps ("yyyy-MM-dd'T'HH:mm").
 *
 * CSV import is a convenience for seeding; per the brief, the running
 * program must read from and write to the database, not the CSVs directly.
 */
public class DataLoader {

    // service_requests_template.csv uses ISO-ish "yyyy-MM-dd'T'HH:mm" (no seconds)
    private static final DateTimeFormatter CSV_TS_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    // -------------------------------------------------------------
    // CSV -> DATABASE
    // -------------------------------------------------------------

    public void importLocationsFromCsv(Connection conn, String csvPath) throws SQLException, IOException {
        String sql = "INSERT INTO locations (location_id, name, area, location_type, x_coord, y_coord) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (BufferedReader br = new BufferedReader(new FileReader(csvPath));
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] f = splitCsv(line);
                ps.setString(1, f[0]);
                ps.setString(2, f[1]);
                ps.setString(3, f[2]);
                ps.setString(4, f[3]);
                ps.setDouble(5, Double.parseDouble(f[4]));
                ps.setDouble(6, Double.parseDouble(f[5]));
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public void importRoadsFromCsv(Connection conn, String csvPath) throws SQLException, IOException {
        String sql = "INSERT INTO roads (road_id, from_location_id, to_location_id, distance_km, " +
                "travel_time_min, condition_weight) VALUES (?, ?, ?, ?, ?, ?)";
        try (BufferedReader br = new BufferedReader(new FileReader(csvPath));
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] f = splitCsv(line);
                ps.setString(1, f[0]);
                ps.setString(2, f[1]);
                ps.setString(3, f[2]);
                ps.setDouble(4, Double.parseDouble(f[3]));
                ps.setDouble(5, Double.parseDouble(f[4]));
                ps.setDouble(6, Double.parseDouble(f[5]));
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public void importServiceRequestsFromCsv(Connection conn, String csvPath) throws SQLException, IOException {
        String sql = "INSERT INTO service_requests (request_id, source_location_id, destination_location_id, " +
                "category, urgency, time_submitted, deadline, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (BufferedReader br = new BufferedReader(new FileReader(csvPath));
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] f = splitCsv(line);
                ps.setString(1, f[0]);
                ps.setString(2, f[1]);
                ps.setString(3, f[2]);
                ps.setString(4, f[3]);
                ps.setInt(5, Integer.parseInt(f[4]));
                ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.parse(f[5], CSV_TS_FORMAT)));
                ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.parse(f[6], CSV_TS_FORMAT)));
                ps.setString(8, f[7]);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public void importResourcesFromCsv(Connection conn, String csvPath) throws SQLException, IOException {
        String sql = "INSERT INTO resources (resource_id, resource_type, home_location_id, capacity, " +
                "availability_status) VALUES (?, ?, ?, ?, ?)";
        try (BufferedReader br = new BufferedReader(new FileReader(csvPath));
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] f = splitCsv(line);
                ps.setString(1, f[0]);
                ps.setString(2, f[1]);
                ps.setString(3, f[2]);
                ps.setInt(4, Integer.parseInt(f[3]));
                ps.setString(5, f[4]);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public void importAlgorithmRunsFromCsv(Connection conn, String csvPath) throws SQLException, IOException {
        String sql = "INSERT INTO algorithm_runs (run_id, algorithm_name, input_size, time_ns, " +
                "memory_kb, date_run) VALUES (?, ?, ?, ?, ?, ?)";
        try (BufferedReader br = new BufferedReader(new FileReader(csvPath));
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] f = splitCsv(line);
                ps.setInt(1, Integer.parseInt(f[0]));
                ps.setString(2, f[1]);
                ps.setInt(3, Integer.parseInt(f[2]));
                ps.setLong(4, Long.parseLong(f[3]));
                ps.setLong(5, Long.parseLong(f[4]));
                ps.setString(6, f[5]);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    // -------------------------------------------------------------
    // DATABASE -> CUSTOM DATA STRUCTURES
    // -------------------------------------------------------------

    public MyLinkedList<Location> loadLocations(Connection conn) throws SQLException {
        MyLinkedList<Location> list = new MyLinkedList<>();
        String sql = "SELECT location_id, name, area, location_type, x_coord, y_coord FROM locations";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.addLast(new Location(
                        rs.getString("location_id"), rs.getString("name"), rs.getString("area"),
                        rs.getString("location_type"), rs.getDouble("x_coord"), rs.getDouble("y_coord")));
            }
        }
        return list;
    }

    public MyLinkedList<Road> loadRoads(Connection conn) throws SQLException {
        MyLinkedList<Road> list = new MyLinkedList<>();
        String sql = "SELECT road_id, from_location_id, to_location_id, distance_km, " +
                "travel_time_min, condition_weight FROM roads";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.addLast(new Road(
                        rs.getString("road_id"), rs.getString("from_location_id"), rs.getString("to_location_id"),
                        rs.getDouble("distance_km"), rs.getDouble("travel_time_min"),
                        rs.getDouble("condition_weight")));
            }
        }
        return list;
    }

    public MyLinkedList<ServiceRequest> loadServiceRequests(Connection conn) throws SQLException {
        MyLinkedList<ServiceRequest> list = new MyLinkedList<>();
        String sql = "SELECT request_id, source_location_id, destination_location_id, category, urgency, " +
                "time_submitted, deadline, status FROM service_requests";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.addLast(new ServiceRequest(
                        rs.getString("request_id"), rs.getString("source_location_id"),
                        rs.getString("destination_location_id"), rs.getString("category"),
                        rs.getInt("urgency"),
                        rs.getTimestamp("time_submitted").toLocalDateTime(),
                        rs.getTimestamp("deadline").toLocalDateTime(),
                        rs.getString("status")));
            }
        }
        return list;
    }

    public MyLinkedList<Resource> loadResources(Connection conn) throws SQLException {
        MyLinkedList<Resource> list = new MyLinkedList<>();
        String sql = "SELECT resource_id, resource_type, home_location_id, capacity, availability_status FROM resources";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.addLast(new Resource(
                        rs.getString("resource_id"), rs.getString("resource_type"),
                        rs.getString("home_location_id"),
                        rs.getInt("capacity"), rs.getString("availability_status")));
            }
        }
        return list;
    }

    public MyLinkedList<AlgorithmRun> loadAlgorithmRuns(Connection conn) throws SQLException {
        MyLinkedList<AlgorithmRun> list = new MyLinkedList<>();
        String sql = "SELECT run_id, algorithm_name, input_size, time_ns, memory_kb, date_run FROM algorithm_runs";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.addLast(new AlgorithmRun(
                        rs.getInt("run_id"), rs.getString("algorithm_name"), rs.getInt("input_size"),
                        rs.getLong("time_ns"), rs.getLong("memory_kb"),
                        rs.getTimestamp("date_run").toLocalDateTime()));
            }
        }
        return list;
    }

    /** Records a new empirical performance measurement (used heavily in Phase 3). */
    public void recordAlgorithmRun(Connection conn, String algorithmName, int inputSize,
                                    long timeNs, long memoryKb) throws SQLException {
        String sql = "INSERT INTO algorithm_runs (algorithm_name, input_size, time_ns, memory_kb) " +
                "VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, algorithmName);
            ps.setInt(2, inputSize);
            ps.setLong(3, timeNs);
            ps.setLong(4, memoryKb);
            ps.executeUpdate();
        }
    }

    // -------------------------------------------------------------
    // WRITE-BACK: used by the Phase 3 integrated dispatch console
    // -------------------------------------------------------------

    /** Updates a service request's status (e.g. NEW -> ASSIGNED). */
    public void updateServiceRequestStatus(Connection conn, String requestId, String newStatus) throws SQLException {
        String sql = "UPDATE service_requests SET status = ? WHERE request_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setString(2, requestId);
            ps.executeUpdate();
        }
    }

    /** Updates a resource's availability (e.g. AVAILABLE -> BUSY once assigned). */
    public void updateResourceStatus(Connection conn, String resourceId, String newStatus) throws SQLException {
        String sql = "UPDATE resources SET availability_status = ? WHERE resource_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setString(2, resourceId);
            ps.executeUpdate();
        }
    }

    /** Logs a dispatch decision to audit_events (status change / assignment trail). */
    public void logAuditEvent(Connection conn, String requestId, String eventType,
                               String oldValue, String newValue) throws SQLException {
        String sql = "INSERT INTO audit_events (request_id, event_type, old_value, new_value) " +
                "VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, requestId);
            ps.setString(2, eventType);
            ps.setString(3, oldValue);
            ps.setString(4, newValue);
            ps.executeUpdate();
        }
    }

    // Minimal CSV splitter (our seed data has no embedded commas/quotes).
    private String[] splitCsv(String line) {
        return line.split(",", -1);
    }
}
