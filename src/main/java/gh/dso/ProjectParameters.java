package gh.dso;

/**
 * Algorithm parameters derived from team member index numbers, per the
 * project brief's requirement to derive at least 3 parameters this way.
 *
 * Team members used for derivation:
 *   Immanuel Oheneba Debe  - 22243130
 *   Jonas Kudzo Amuzu      - 22198544
 *   Cedric Dzodzodzi       - 22046156
 *
 * Each derivation is documented inline so it can be reproduced and
 * explained in the report / oral defence.
 */
public final class ProjectParameters {

    private ProjectParameters() { }

    /**
     * HASH_TABLE_SIZE: derived from Immanuel's index number (22243130).
     *   last 2 digits of 22243130 = 30
     *   next prime number >= 30    = 31
     * A prime table size spreads hash values more evenly and reduces
     * clustering, so we round up to the nearest prime rather than using
     * 30 directly.
     */
    public static final int HASH_TABLE_SIZE = 31;

    /**
     * RANDOM_SEED: derived from all three index numbers, summing the
     * last 3 digits of each so no single member's number dominates:
     *   22243130 -> 130
     *   22198544 -> 544
     *   22046156 -> 156
     *   130 + 544 + 156 = 830
     * Used to make the generated seed dataset (locations/roads/requests)
     * reproducible run-to-run.
     */
    public static final long RANDOM_SEED = 830;

    /**
     * DEFAULT_VEHICLE_CAPACITY: derived from Jonas's index number (22198544).
     *   last 2 digits of 22198544 = 44
     *   44 % 10 = 4, plus a base capacity of 3 -> 7
     * Used as the default parcel capacity for the Phase 3 knapsack
     * (greedy vs DP) demo — i.e. how many parcel "slots" a vehicle has
     * when we need to choose which pending requests it can carry.
     */
    public static final int DEFAULT_VEHICLE_CAPACITY = 7;
}
