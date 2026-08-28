# Dataset Localisation Note & Geographic Context

This document explains the local context, geographical mapping, and dataset construction details for the **Ghana Smart Service Operations Optimizer (Courier/Delivery Service)**, in accordance with the project requirements.

---

## 1. Geographical Scope & Context
The project operates across a real-world subset of **Accra, the capital city of Ghana**. The network models key intersections, hubs, and landmarks across several prominent Accra sub-metro regions:
- **Circle (Kofi Annan Avenue / Ring Road West)**: Central dispatch transit point.
- **Osu (Oxford Street)**: High density of commercial vendors (food, fashion).
- **East Legon (Bawaleshie / Boundary Road)**: Premium residential/business customer zones.
- **Madina (Zongo Junction)**: Major market area and transport hub.
- **Airport Residential Area (Liberation Road)**: Residential and corporate landmark zone.
- **Achimota (Achimota Forest / Retail Centre)**: Transit landmark connecting north Accra.

These hubs are populated as nodes in the database (`locations` table), mapped using realistic local naming conventions, coordinates, and types (`Vendor`, `CustomerZone`, `RiderHub`, `Landmark`).

---

## 2. Dataset Construction & Anonymisation
To comply with strict academic integrity and privacy guidelines:
1. **Zero Personal Data**: No real customer names, phone numbers, or exact residential addresses were used. All customer identities are completely omitted, and destinations are generalized to broader community zones (e.g., `"Osu Customer Zone C"`).
2. **Synthetic Generation**: The operational service requests, roads, and resources were generated synthetically using a python generator script [`database/seed/generate_seed_data.py`](file:///k:/3008/DSA_JOINT/New%20folder/ghana-courier-dso/database/seed/generate_seed_data.py).
3. **Reproducibility**: The dataset generation is locked using the team's custom parameter `RANDOM_SEED = 830`. This ensures that all locations, coordinates, road weights, and service requests are fully reproducible on any machine.

---

## 3. Local Constraints & Parameters
The road weights (`roads` table) are modeled using realistic Accra-specific factors:
- **`distance`**: Calculated using Euclidean coordinates mapped onto approximate relative mileage.
- **`travelTime`**: Estimated using average driving times between the regions.
- **`roadConditionWeight`**: A multiplier factor from `1.0` (smooth highway, e.g., N1 highway) to `2.0` (heavily congested or poorly surfaced routes, e.g., local feeder roads in densely populated markets like Madina or Circle during rush hour).

---

## 4. Derived Algorithm Parameters
To link the algorithms to the project team:
- **Hash Table Size (`HASH_TABLE_SIZE = 31`)**: Derived from the prime rounding of Immanuel Debe's index number ending (`30` rounded up to prime `31`).
- **Random Seed (`RANDOM_SEED = 830`)**: Sum of the last 3 digits of index numbers of all three core developers (130 + 544 + 156 = 830).
- **Default Vehicle Capacity (`DEFAULT_VEHICLE_CAPACITY = 7`)**: Derived from Jonas Amuzu's index digits (`44 % 10 = 4` + base capacity of `3` = `7`).
