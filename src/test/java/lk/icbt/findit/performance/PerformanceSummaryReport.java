package lk.icbt.findit.performance;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public final class PerformanceSummaryReport {

    
    private static final PerformanceSummaryReport SHARED = new PerformanceSummaryReport();

    private static final String REPORT_DIR = "target";
    private static final String REPORT_FILENAME = "api-performance-summary.txt";

    private final List<PerformanceSummaryRow> rows = new CopyOnWriteArrayList<>();

    public void record(String endpoint,
                       double avgResponseTimeMs,
                       long p95ResponseTimeMs,
                       int status,
                       long totalTimeMs,
                       int requestCount,
                       int usersCount,
                       int warmupCount) {
        rows.add(new PerformanceSummaryRow(
                endpoint,
                avgResponseTimeMs,
                p95ResponseTimeMs,
                status,
                totalTimeMs,
                requestCount,
                usersCount,
                warmupCount
        ));
    }

    public static PerformanceSummaryReport getShared() {
        return SHARED;
    }

    
    public void record(String endpoint,
                       double avgResponseTimeMs,
                       int status,
                       long totalTimeMs,
                       int requestCount,
                       int usersCount,
                       int warmupCount) {
        record(endpoint, avgResponseTimeMs, -1, status, totalTimeMs, requestCount, usersCount, warmupCount);
    }

    public List<PerformanceSummaryRow> getRows() {
        return new ArrayList<>(rows);
    }

    
    public void printAndWriteTable() {
        String table = buildTableString();
        System.out.println(table);
        writeToFile(table);
    }

    public String buildTableString() {
        if (rows.isEmpty()) {
            return "--- API / System Performance Summary ---\n(No records)";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("================================================================================\n");
        sb.append("           API RESPONSE TIME / SYSTEM PERFORMANCE SUMMARY\n");
        sb.append("================================================================================\n");
        sb.append(String.format("Generated: %s%n", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
        sb.append("--------------------------------------------------------------------------------\n");

        String header = String.format("%-45s | %8s | %6s | %6s | %10s | %6s | %5s | %6s",
                "Endpoint", "Avg(ms)", "P95(ms)", "Status", "Total(ms)", "ReqCnt", "Users", "Warmup");
        String separator = "-".repeat(header.length());
        sb.append(header).append("\n");
        sb.append(separator).append("\n");

        for (PerformanceSummaryRow r : rows) {
            String p95Str = r.p95ResponseTimeMs >= 0 ? String.valueOf(r.p95ResponseTimeMs) : "-";
            sb.append(String.format("%-45s | %8.2f | %6s | %6d | %10d | %6d | %5d | %6d",
                    truncate(r.endpoint, 45),
                    r.avgResponseTimeMs,
                    p95Str,
                    r.status,
                    r.totalTimeMs,
                    r.requestCount,
                    r.usersCount,
                    r.warmupCount)).append("\n");
        }
        sb.append("================================================================================\n");
        return sb.toString();
    }

    private static String truncate(String s, int maxLen) {
        if (s == null) return "";
        return s.length() <= maxLen ? s : s.substring(0, maxLen - 3) + "...";
    }

    private void writeToFile(String content) {
        try {
            Path dir = Paths.get(REPORT_DIR);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
            Path file = dir.resolve(REPORT_FILENAME);
            Files.writeString(file, content);
            System.out.println("Performance summary written to: " + file.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Could not write performance summary file: " + e.getMessage());
        }
    }

    public static final class PerformanceSummaryRow {
        public final String endpoint;
        public final double avgResponseTimeMs;
        public final long p95ResponseTimeMs;
        public final int status;
        public final long totalTimeMs;
        public final int requestCount;
        public final int usersCount;
        public final int warmupCount;

        public PerformanceSummaryRow(String endpoint, double avgResponseTimeMs, long p95ResponseTimeMs,
                                    int status, long totalTimeMs, int requestCount, int usersCount, int warmupCount) {
            this.endpoint = endpoint;
            this.avgResponseTimeMs = avgResponseTimeMs;
            this.p95ResponseTimeMs = p95ResponseTimeMs;
            this.status = status;
            this.totalTimeMs = totalTimeMs;
            this.requestCount = requestCount;
            this.usersCount = usersCount;
            this.warmupCount = warmupCount;
        }
    }
}
