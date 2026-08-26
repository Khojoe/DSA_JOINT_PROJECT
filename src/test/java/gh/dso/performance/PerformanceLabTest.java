package gh.dso.performance;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class PerformanceLabTest {

    @Test
    void runSortBenchmarks_normalCase_recordsMeasurementsForEachAlgorithmAndSize() {
        PerformanceLab lab = new PerformanceLab();
        lab.runSortBenchmarks(new Random(830));

        assertFalse(lab.getResults().isEmpty());
        assertTrue(lab.getResults().stream().anyMatch(r -> r.algorithmName().equals("QuickSort")));
        assertTrue(lab.getResults().stream().anyMatch(r -> r.algorithmName().equals("MergeSort")));
        assertTrue(lab.getResults().stream().anyMatch(r -> r.algorithmName().equals("SelectionSort")));
        assertTrue(lab.getResults().stream().anyMatch(r -> r.algorithmName().equals("InsertionSort")));
    }

    @Test
    void runSearchBenchmarks_normalCase_recordsBothAlgorithms() {
        PerformanceLab lab = new PerformanceLab();
        lab.runSearchBenchmarks(new Random(830));

        assertTrue(lab.getResults().stream().anyMatch(r -> r.algorithmName().equals("LinearSearch")));
        assertTrue(lab.getResults().stream().anyMatch(r -> r.algorithmName().equals("BinarySearch")));
    }

    @Test
    void runGraphBenchmarks_normalCase_recordsAllFiveAlgorithms() {
        PerformanceLab lab = new PerformanceLab();
        lab.runGraphBenchmarks(new Random(830));

        for (String algo : new String[]{"BFS", "DFS", "Dijkstra", "Kruskal", "Prim"}) {
            assertTrue(lab.getResults().stream().anyMatch(r -> r.algorithmName().equals(algo)),
                    "Expected a measurement for " + algo);
        }
    }

    @Test
    void allTimings_boundaryCase_areNonNegative() {
        PerformanceLab lab = new PerformanceLab();
        lab.runSortBenchmarks(new Random(830));

        assertTrue(lab.getResults().stream().allMatch(r -> r.timeNs() >= 0));
    }

    @Test
    void exportCsv_normalCase_createsFileWithHeaderAndRows(@org.junit.jupiter.api.io.TempDir java.nio.file.Path tempDir) throws Exception {
        PerformanceLab lab = new PerformanceLab();
        lab.runSearchBenchmarks(new Random(830));

        String path = tempDir.resolve("nested/results.csv").toString();
        lab.exportCsv(path);

        java.util.List<String> lines = java.nio.file.Files.readAllLines(java.nio.file.Path.of(path));
        assertEquals("algorithm_name,input_size,run_number,time_ns,average_time_ns", lines.get(0));
        assertEquals(lab.getResults().size() + 1, lines.size()); // header + raw repetition rows
    }

    @Test
    void eachExperimentUsesThreeRuns() {
        PerformanceLab lab = new PerformanceLab();
        lab.runSearchBenchmarks(new Random(830));
        assertEquals(3, lab.getResults().stream().filter(r -> r.algorithmName().equals("BinarySearch") && r.inputSize() == 100).count());
        assertEquals(3, lab.repeatCount());
    }
}
