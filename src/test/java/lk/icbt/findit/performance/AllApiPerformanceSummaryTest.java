package lk.icbt.findit.performance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.platform.suite.api.AfterSuite;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Single entry point for all API performance tests.
 * Runs all performance test classes and prints the combined
 * "API RESPONSE TIME / SYSTEM PERFORMANCE SUMMARY" table once at the end.
 *
 * Run with: mvn test -Dtest=AllApiPerformanceSummaryTest
 */
@Tag("performance")
@Suite
@DisplayName("API Response Time / System Performance Summary - All APIs")
@SelectClasses({
    ApiPerformanceTest.class,
    CustomerAppNearestOutletPerformanceTest.class,
    LoginAuthPerformanceTest.class,
    MerchantAppPerformanceTest.class,
    CustomerAppPerformanceTest.class,
    ItemCrudPerformanceTest.class,
    DashboardPerformanceTest.class
})
public class AllApiPerformanceSummaryTest {

    @AfterSuite
    static void printPerformanceSummary() {
        PerformanceSummaryReport.getShared().printAndWriteTable();
    }
}
