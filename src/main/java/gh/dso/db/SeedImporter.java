package gh.dso.db;

import java.sql.Connection;

/**
 * One-shot seed data importer: loads all four CSVs into MySQL in the
 * correct order (locations must exist before roads/resources/requests
 * reference them via foreign keys).
 *
 * Run this ONCE after creating the schema (database/schema.sql) and
 * before using Main.java's demo menu — it's what actually puts real
 * data into the tables that everything else reads from.
 *
 * Run directly from IntelliJ: right-click this file -> Run 'SeedImporter.main()'.
 * Adjust CSV_DIR below if your working directory differs.
 */
public class SeedImporter {

    // Relative to the project root (where you run this from in IntelliJ).
    private static final String CSV_DIR = "database/seed/";

    public static void main(String[] args) {
        DataLoader loader = new DataLoader();

        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("Connected to database. Importing seed data...");

            loader.importLocationsFromCsv(conn, CSV_DIR + "locations.csv");
            System.out.println("  locations.csv imported.");

            loader.importRoadsFromCsv(conn, CSV_DIR + "roads.csv");
            System.out.println("  roads.csv imported.");

            loader.importResourcesFromCsv(conn, CSV_DIR + "resources.csv");
            System.out.println("  resources.csv imported.");

            loader.importServiceRequestsFromCsv(conn, CSV_DIR + "service_requests.csv");
            System.out.println("  service_requests.csv imported.");

            loader.importAlgorithmRunsFromCsv(conn, CSV_DIR + "algorithm_runs.csv");
            System.out.println("  algorithm_runs.csv imported.");

            System.out.println("\nAll seed data imported successfully.");
            System.out.println("You can now run Main.java and try options 1, 2, 9-13.");

        } catch (Exception e) {
            System.out.println("Import failed: " + e.getMessage());
            System.out.println("\nCommon causes:");
            System.out.println("  - schema.sql hasn't been run yet (tables don't exist)");
            System.out.println("  - DatabaseConnection.java still has placeholder credentials");
            System.out.println("  - CSV_DIR path doesn't match your working directory");
            System.out.println("  - Running this a second time (rows already exist -> duplicate key error;");
            System.out.println("    truncate the tables first if you need to re-import)");
            e.printStackTrace();
        }
    }
}
