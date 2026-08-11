"""
Seed data generator for the Ghana Courier/Delivery Service Operations Optimizer.

Column names and ID conventions follow the team's shared CSV templates
(locations_template.csv, roads_template.csv, resources_template.csv,
service_requests_template.csv) so every teammate's data lines up in the
same database.

Generates CSVs meeting the brief's minimums:
  locations        >= 50
  roads             >= 100
  service_requests >= 300
  resources         >= 30
  algorithm_runs    >= 30

Run: python3 generate_seed_data.py
Output CSVs are written into this same folder.
"""

import csv
import random
from datetime import datetime, timedelta

random.seed(830)  # derived from team index numbers: sum of last 3 digits of
                  # 22243130 (130) + 22198544 (544) + 22046156 (156) = 830
                  # See gh.dso.ProjectParameters.RANDOM_SEED for full documentation.

OUT_DIR = "."

# ---------------------------------------------------------------------
# Real Accra-area neighbourhoods used to localise the dataset
# ---------------------------------------------------------------------
AREAS = [
    "Osu", "Adenta", "Madina", "East Legon", "Tema", "Circle", "Achimota",
    "Dansoman", "Labone", "Cantonments", "Spintex", "Lapaz", "Kaneshie",
    "Nungua", "Teshie", "Ashaiman", "Tesano", "Abelenkpe", "Dzorwulu",
    "Airport Residential", "Legon", "Haatso", "Ogbojo", "Sowutuom",
    "Mallam", "Weija", "McCarthy Hill", "Ablekuma", "Darkuman", "Kasoa",
]

VENDOR_NAMES = [
    "Auntie Muni's Kitchen", "Papaye Express", "Osu Night Market Grill",
    "Kwame's Waakye Spot", "Frankie's Bakery", "Chicken Republic",
    "Champs Sports Bar Kitchen", "KFC Delivery Hub", "Santoku Express",
    "Sanaa Lounge Kitchen", "Buka Restaurant", "Coco Vanilla Cafe",
    "Rhapsody's Kitchen", "Container Restaurant", "Pinocchio Pizza",
    "Kokrobite Fish Grill", "Vendor Mart Groceries", "MaxMart Express",
    "Melcom Pharmacy", "Ernest Chemist", "Palace Pharmacy",
]

RIDER_HUB_SUFFIX = "Dispatch Hub"
LANDMARKS = [
    "Accra Mall", "37 Military Hospital Junction", "Tetteh Quarshie Circle",
    "Kwame Nkrumah Circle", "Kotoka Airport Roundabout", "Achimota Overhead",
    "Nungua Barrier", "Ashaiman Station", "Kaneshie Market", "Lapaz Station",
]

# location_type values follow the template's single-word, title-case style
# (the template used "Library", "Academic", "Health"); adapted to our context.
LOCATION_TYPES = {
    "VENDOR": "Vendor",
    "CUSTOMER_ZONE": "CustomerZone",
    "RIDER_HUB": "RiderHub",
    "LANDMARK": "Landmark",
}

CATEGORIES = ["Food", "Parcel", "Document", "Grocery", "Pharmacy", "Medical"]
RESOURCE_TYPES = ["Rider", "Bicycle", "Tricycle", "Van"]  # matches template's "Van"/"Rider"
RESOURCE_PREFIX = {"Rider": "R", "Bicycle": "B", "Tricycle": "T", "Van": "V"}
STATUSES = ["NEW", "ASSIGNED", "IN_TRANSIT", "DELIVERED", "CANCELLED"]

# Real Accra bounding box (matches the template's Legon-area coordinate style)
LAT_RANGE = (5.55, 5.75)
LON_RANGE = (-0.30, 0.10)


def rand_coord():
    return round(random.uniform(*LAT_RANGE), 3), round(random.uniform(*LON_RANGE), 3)


def loc_id(n):
    return f"L{n:03d}"


def build_locations(n_min=55):
    rows = []
    n = 1

    for name in VENDOR_NAMES:
        area = random.choice(AREAS)
        x, y = rand_coord()
        rows.append([loc_id(n), name, area, LOCATION_TYPES["VENDOR"], x, y])
        n += 1

    for area in AREAS:
        x, y = rand_coord()
        rows.append([loc_id(n), f"{area} Residential Zone", area, LOCATION_TYPES["CUSTOMER_ZONE"], x, y])
        n += 1

    for area in random.sample(AREAS, 6):
        x, y = rand_coord()
        rows.append([loc_id(n), f"{area} {RIDER_HUB_SUFFIX}", area, LOCATION_TYPES["RIDER_HUB"], x, y])
        n += 1

    for landmark in LANDMARKS:
        x, y = rand_coord()
        rows.append([loc_id(n), landmark, "Accra", LOCATION_TYPES["LANDMARK"], x, y])
        n += 1

    while len(rows) < n_min:
        area = random.choice(AREAS)
        x, y = rand_coord()
        rows.append([loc_id(n), f"{area} Extra Zone {n}", area, LOCATION_TYPES["CUSTOMER_ZONE"], x, y])
        n += 1

    return rows


def build_roads(locations, n_min=110):
    """Build a connected-ish graph: ring + random extra edges."""
    ids = [r[0] for r in locations]
    rows = []
    road_n = 1

    for i in range(len(ids)):
        a, b = ids[i], ids[(i + 1) % len(ids)]
        dist = round(random.uniform(0.8, 9.5), 2)
        time_min = round(dist * random.uniform(2.2, 4.0), 2)
        weight = round(random.uniform(1.0, 3.0), 2)
        rows.append([f"R{road_n:03d}", a, b, dist, time_min, weight])
        road_n += 1

    while len(rows) < n_min:
        a, b = random.sample(ids, 2)
        if a == b:
            continue
        dist = round(random.uniform(0.5, 12.0), 2)
        time_min = round(dist * random.uniform(2.2, 4.5), 2)
        weight = round(random.uniform(1.0, 3.5), 2)
        rows.append([f"R{road_n:03d}", a, b, dist, time_min, weight])
        road_n += 1

    return rows


def build_service_requests(locations, n_min=310):
    vendor_ids = [r[0] for r in locations if r[3] == LOCATION_TYPES["VENDOR"]]
    customer_ids = [r[0] for r in locations if r[3] == LOCATION_TYPES["CUSTOMER_ZONE"]]
    rows = []
    base_time = datetime(2026, 7, 1, 8, 0, 0)

    for i in range(1, n_min + 1):
        source = random.choice(vendor_ids)
        dest = random.choice(customer_ids)
        category = random.choice(CATEGORIES)
        urgency = random.randint(1, 5)
        submitted = base_time + timedelta(minutes=random.randint(0, 60 * 24 * 20))
        deadline = submitted + timedelta(minutes=random.randint(20, 180))
        status = random.choices(STATUSES, weights=[15, 15, 15, 50, 5], k=1)[0]
        rows.append([
            f"Q{i:03d}", source, dest, category, urgency,
            submitted.strftime("%Y-%m-%dT%H:%M"),
            deadline.strftime("%Y-%m-%dT%H:%M"),
            status,
        ])
    return rows


def build_resources(locations, n_min=32):
    hub_ids = [r[0] for r in locations if r[3] == LOCATION_TYPES["RIDER_HUB"]]
    if not hub_ids:
        hub_ids = [r[0] for r in locations[:5]]
    rows = []
    counters = {"Rider": 0, "Bicycle": 0, "Tricycle": 0, "Van": 0}
    total = 0
    while total < n_min:
        rtype = random.choices(RESOURCE_TYPES, weights=[55, 15, 15, 15], k=1)[0]
        counters[rtype] += 1
        rid = f"{RESOURCE_PREFIX[rtype]}{counters[rtype]:03d}"
        home = random.choice(hub_ids)
        capacity = {"Rider": 3, "Bicycle": 1, "Tricycle": 6, "Van": 15}[rtype]
        status = random.choices(["AVAILABLE", "BUSY", "OFFLINE"], weights=[60, 30, 10], k=1)[0]
        rows.append([rid, rtype, home, capacity, status])
        total += 1
    return rows


def build_algorithm_runs(n_min=32):
    algos = [
        "LinearSearch", "BinarySearch", "SelectionSort", "InsertionSort",
        "MergeSort", "QuickSort", "BFS", "DFS", "Dijkstra", "Prim", "Kruskal",
    ]
    sizes = [100, 500, 1000, 5000, 10000]
    rows = []
    rid = 1
    while len(rows) < n_min:
        algo = random.choice(algos)
        size = random.choice(sizes)
        time_ns = int(size * random.uniform(50, 500))
        mem_kb = int(size * random.uniform(0.5, 4))
        rows.append([rid, algo, size, time_ns, mem_kb,
                     (datetime(2026, 7, 1) + timedelta(days=rid)).strftime("%Y-%m-%d %H:%M:%S")])
        rid += 1
    return rows


def write_csv(filename, header, rows):
    path = f"{OUT_DIR}/{filename}"
    with open(path, "w", newline="", encoding="utf-8") as f:
        writer = csv.writer(f)
        writer.writerow(header)
        writer.writerows(rows)
    print(f"Wrote {len(rows)} rows -> {path}")


if __name__ == "__main__":
    locations = build_locations()
    roads = build_roads(locations)
    requests = build_service_requests(locations)
    resources = build_resources(locations)
    algo_runs = build_algorithm_runs()

    write_csv("locations.csv",
              ["location_id", "name", "area", "location_type", "x_coord", "y_coord"],
              locations)
    write_csv("roads.csv",
              ["road_id", "from_location_id", "to_location_id", "distance_km",
               "travel_time_min", "condition_weight"],
              roads)
    write_csv("service_requests.csv",
              ["request_id", "source_location_id", "destination_location_id", "category",
               "urgency", "time_submitted", "deadline", "status"],
              requests)
    write_csv("resources.csv",
              ["resource_id", "resource_type", "home_location_id", "capacity", "availability_status"],
              resources)
    write_csv("algorithm_runs.csv",
              ["run_id", "algorithm_name", "input_size", "time_ns", "memory_kb", "date_run"],
              algo_runs)
