package gh.dso.model;

import java.time.LocalDateTime;

/**
 * A single empirical performance measurement, used by the Phase 3
 * efficiency lab to build time-vs-input-size graphs.
 */
public class AlgorithmRun {
    private final int runId;
    private final String algorithmName;
    private final int inputSize;
    private final long timeNs;
    private final long memoryKb;
    private final LocalDateTime dateRun;

    public AlgorithmRun(int runId, String algorithmName, int inputSize,
                         long timeNs, long memoryKb, LocalDateTime dateRun) {
        this.runId = runId;
        this.algorithmName = algorithmName;
        this.inputSize = inputSize;
        this.timeNs = timeNs;
        this.memoryKb = memoryKb;
        this.dateRun = dateRun;
    }

    public int getRunId() { return runId; }
    public String getAlgorithmName() { return algorithmName; }
    public int getInputSize() { return inputSize; }
    public long getTimeNs() { return timeNs; }
    public long getMemoryKb() { return memoryKb; }
    public LocalDateTime getDateRun() { return dateRun; }

    @Override
    public String toString() {
        return algorithmName + " n=" + inputSize + " time=" + timeNs + "ns";
    }
}
