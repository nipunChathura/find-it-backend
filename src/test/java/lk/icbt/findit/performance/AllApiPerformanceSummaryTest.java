package lk.icbt.findit.performance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.platform.suite.api.AfterSuite;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;


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
