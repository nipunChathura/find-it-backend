package lk.icbt.findit.performance;

import com.fasterxml.jackson.databind.ObjectMapper;
import lk.icbt.findit.config.ApiRequestLoggingFilter;
import lk.icbt.findit.config.TestJacksonConfig;
import lk.icbt.findit.controller.ItemController;
import lk.icbt.findit.request.ItemRequest;
import lk.icbt.findit.response.ItemListItemResponse;
import lk.icbt.findit.response.ItemResponse;
import lk.icbt.findit.service.ItemService;
import lk.icbt.findit.security.CustomUserDetailsService;
import lk.icbt.findit.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@Tag("performance")
@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Import(TestJacksonConfig.class)
class ItemCrudPerformanceTest {

    private static final int WARMUP = 2;
    private static final int MEASURED = 5;
    private static final long SLA_MS = 500;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private ItemService itemService;
    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;
    @MockitoBean
    private ApiRequestLoggingFilter apiRequestLoggingFilter;

    @Test
    @WithMockUser(roles = "MERCHANT")
    @DisplayName("Performance: GET /api/items (Get)")
    void getItems_responseTime() throws Exception {
        when(itemService.search(any(), any(), any(), any(), any())).thenReturn(List.of(new ItemListItemResponse()));

        long[] times = runTimed(MEASURED, WARMUP, () ->
                mockMvc.perform(get("/api/items")).andExpect(status().isOk()).andReturn());
        record("GET /api/items (Get)", times, 200);
        assertThat(averageMs(times)).isLessThan(SLA_MS);
    }

    @Test
    @WithMockUser(roles = "MERCHANT")
    @DisplayName("Performance: POST /api/items (Save)")
    void postItem_responseTime() throws Exception {
        ItemRequest req = new ItemRequest();
        req.setItemName("I");
        req.setCategoryId(1L);
        req.setOutletId(1L);
        req.setPrice(BigDecimal.ONE);
        req.setAvailability(true);
        ItemResponse res = new ItemResponse();
        res.setItemId(1L);
        when(itemService.create(any(ItemRequest.class), anyString())).thenReturn(res);

        long[] times = runTimed(MEASURED, WARMUP, () ->
                mockMvc.perform(post("/api/items").contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                        .andExpect(status().isCreated()).andReturn());
        record("POST /api/items (Save)", times, 201);
        assertThat(averageMs(times)).isLessThan(SLA_MS);
    }

    @Test
    @WithMockUser(roles = "MERCHANT")
    @DisplayName("Performance: PUT /api/items/1 (Update)")
    void putItem_responseTime() throws Exception {
        ItemRequest req = new ItemRequest();
        req.setItemName("I");
        req.setCategoryId(1L);
        req.setOutletId(1L);
        req.setPrice(BigDecimal.ONE);
        req.setAvailability(true);
        ItemResponse res = new ItemResponse();
        res.setItemId(1L);
        when(itemService.update(eq(1L), any(ItemRequest.class), anyString())).thenReturn(res);

        long[] times = runTimed(MEASURED, WARMUP, () ->
                mockMvc.perform(put("/api/items/1").contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                        .andExpect(status().isOk()).andReturn());
        record("PUT /api/items/1 (Update)", times, 200);
        assertThat(averageMs(times)).isLessThan(SLA_MS);
    }

    private long[] runTimed(int n, int warmup, RequestRunner r) throws Exception {
        for (int i = 0; i < warmup; i++) r.run();
        long[] t = new long[n];
        for (int i = 0; i < n; i++) {
            long start = System.nanoTime();
            r.run();
            t[i] = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
        }
        return t;
    }

    private void record(String endpoint, long[] times, int status) {
        double avg = averageMs(times);
        long total = 0;
        for (long x : times) total += x;
        PerformanceSummaryReport.getShared().record(endpoint, avg, p95(times), status, total, times.length, 1, WARMUP);
    }

    private static double averageMs(long[] t) {
        long s = 0;
        for (long x : t) s += x;
        return t.length == 0 ? 0 : (double) s / t.length;
    }

    private static long p95(long[] t) {
        long[] c = t.clone();
        java.util.Arrays.sort(c);
        int i = Math.max(0, (int) Math.ceil(0.95 * c.length) - 1);
        return c[i];
    }

    @FunctionalInterface
    private interface RequestRunner { MvcResult run() throws Exception; }
}
