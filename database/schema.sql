-- =====================================================================
-- Ghana Smart Service Operations Optimizer
-- Context: Courier / Food Delivery (Accra, Ghana)
-- DCIT 204/308 Joint DSA Semester Project
-- =====================================================================

CREATE DATABASE IF NOT EXISTS ghana_courier_dso
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE ghana_courier_dso;

-- ---------------------------------------------------------------------
-- locations: nodes in the delivery network (vendors, customer zones,
-- rider hubs, landmarks used for routing)
-- Column names/ID convention follow the team's shared CSV template
-- (locations_template.csv) so every teammate's data lines up.
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS locations (
    location_id     VARCHAR(10) PRIMARY KEY,           -- e.g. L001
    name             VARCHAR(120) NOT NULL,
    area             VARCHAR(80)  NOT NULL,             -- e.g. Osu, Madina, East Legon
    location_type    ENUM('Vendor', 'CustomerZone', 'RiderHub', 'Landmark') NOT NULL,
    x_coord          DECIMAL(9,6) NOT NULL,             -- latitude
    y_coord          DECIMAL(9,6) NOT NULL,             -- longitude
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------------------
-- roads: weighted edges between locations (used to build the graph)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS roads (
    road_id               VARCHAR(10) PRIMARY KEY,      -- e.g. R001
    from_location_id      VARCHAR(10) NOT NULL,
    to_location_id        VARCHAR(10) NOT NULL,
    distance_km           DECIMAL(6,2) NOT NULL,
    travel_time_min       DECIMAL(6,2) NOT NULL,
    condition_weight      DECIMAL(4,2) NOT NULL,        -- 1.0 = good road, higher = worse/slower
    CONSTRAINT fk_roads_from FOREIGN KEY (from_location_id) REFERENCES locations(location_id),
    CONSTRAINT fk_roads_to   FOREIGN KEY (to_location_id)   REFERENCES locations(location_id)
);

-- ---------------------------------------------------------------------
-- service_requests: delivery jobs (queued, prioritised, searched, sorted)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS service_requests (
    request_id               VARCHAR(10) PRIMARY KEY,   -- e.g. Q001
    source_location_id       VARCHAR(10) NOT NULL,      -- vendor location
    destination_location_id  VARCHAR(10) NOT NULL,      -- customer zone location
    category                 ENUM('Food', 'Parcel', 'Document', 'Grocery', 'Pharmacy', 'Medical') NOT NULL,
    urgency                  TINYINT NOT NULL,           -- 1 (low) - 5 (critical)
    time_submitted           DATETIME NOT NULL,
    deadline                 DATETIME NOT NULL,
    status                   ENUM('NEW', 'ASSIGNED', 'IN_TRANSIT', 'DELIVERED', 'CANCELLED') DEFAULT 'NEW',
    CONSTRAINT fk_req_source FOREIGN KEY (source_location_id) REFERENCES locations(location_id),
    CONSTRAINT fk_req_dest   FOREIGN KEY (destination_location_id) REFERENCES locations(location_id)
);

-- ---------------------------------------------------------------------
-- resources: riders/vehicles that can be assigned to requests
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS resources (
    resource_id          VARCHAR(10) PRIMARY KEY,       -- e.g. V001, R001, B001, T001
    resource_type        ENUM('Rider', 'Bicycle', 'Tricycle', 'Van') NOT NULL,
    home_location_id      VARCHAR(10) NOT NULL,
    capacity             INT NOT NULL,                   -- number of parcels it can carry
    availability_status   ENUM('AVAILABLE', 'BUSY', 'OFFLINE') DEFAULT 'AVAILABLE',
    CONSTRAINT fk_res_home FOREIGN KEY (home_location_id) REFERENCES locations(location_id)
);

-- ---------------------------------------------------------------------
-- algorithm_runs: empirical performance measurements
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS algorithm_runs (
    run_id            INT AUTO_INCREMENT PRIMARY KEY,
    algorithm_name    VARCHAR(80) NOT NULL,            -- e.g. 'MergeSort', 'Dijkstra'
    input_size        INT NOT NULL,
    time_ns           BIGINT NOT NULL,
    memory_kb         BIGINT,
    date_run          DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------------------
-- audit_events: stack-based undo/audit log of important system events
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS audit_events (
    event_id       INT AUTO_INCREMENT PRIMARY KEY,
    request_id     VARCHAR(10),
    event_type     VARCHAR(60) NOT NULL,               -- e.g. 'STATUS_CHANGE', 'ASSIGNMENT', 'UNDO'
    old_value      VARCHAR(120),
    new_value      VARCHAR(120),
    event_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_request FOREIGN KEY (request_id) REFERENCES service_requests(request_id)
);

-- Helpful indexes for the indexing engine / search experiments
CREATE INDEX idx_requests_urgency ON service_requests(urgency);
CREATE INDEX idx_requests_status  ON service_requests(status);
CREATE INDEX idx_roads_from       ON roads(from_location_id);
CREATE INDEX idx_roads_to         ON roads(to_location_id);
