package lk.icbt.findit.outlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import lk.icbt.findit.controller.OutletController;
import lk.icbt.findit.dto.OutletAddDTO;
import lk.icbt.findit.request.OutletAddRequest;
import lk.icbt.findit.request.OutletScheduleRequest;
import lk.icbt.findit.response.*;
import lk.icbt.findit.service.DiscountService;
import lk.icbt.findit.service.FeedbackService;
import lk.icbt.findit.service.OutletScheduleService;
import lk.icbt.findit.service.OutletService;
import lk.icbt.findit.config.ApiRequestLoggingFilter;
import lk.icbt.findit.security.CustomUserDetailsService;
import lk.icbt.findit.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import lk.icbt.findit.config.TestJacksonConfig;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * JUnit tests for Outlet module APIs.
 * Covers: List outlets, Search nearby/assigned, Get details, Add outlet, Update outlet, Schedules, Status, Discounts, Feedbacks.
 */
@Tag("unit")
@WebMvcTest(OutletController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Import(TestJacksonConfig.class)
class OutletModuleTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OutletService outletService;

    @MockitoBean
    private OutletScheduleService outletScheduleService;

    @MockitoBean
    private DiscountService discountService;

    @MockitoBean
    private FeedbackService feedbackService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private ApiRequestLoggingFilter apiRequestLoggingFilter;

    // ---------- UT11: List outlets ----------
    @Test
    @WithMockUser(roles = "MERCHANT")
    @DisplayName("UT11 - List outlets with optional filters")
    void listOutlets_returnsList() throws Exception {
        OutletListResponse out = new OutletListResponse();
        out.setId(1L);
        out.setName("Main Outlet");
        out.setCurrentStatus("OPEN");
        when(outletService.listOutlets(null, null)).thenReturn(List.of(out));

        mockMvc.perform(get("/api/outlets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Main Outlet"))
                .andExpect(jsonPath("$[0].currentStatus").value("OPEN"));
    }

    // ---------- UT12: Search nearby / assigned outlets ----------
    @Test
    @WithMockUser(roles = "MERCHANT")
    @DisplayName("UT12 - List outlets assigned to merchant")
    void listOutletsAssigned_returnsList() throws Exception {
        OutletAssignedItemResponse out = new OutletAssignedItemResponse();
        out.setOutletId(1L);
        out.setOutletName("Assigned Outlet");
        when(outletService.listOutletsByMerchantOrSubMerchant(eq(1L), isNull(), anyString()))
                .thenReturn(List.of(out));

        mockMvc.perform(get("/api/outlets/assigned").param("merchantId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].outletId").value(1))
                .andExpect(jsonPath("$[0].outletName").value("Assigned Outlet"));
    }

    // ---------- UT13: Get outlet details ----------
    @Test
    @WithMockUser(roles = "MERCHANT")
    @DisplayName("UT13 - Get outlet details by id")
    void getOutletDetails_returnsDetails() throws Exception {
        OutletListItemResponse outletItem = new OutletListItemResponse();
        outletItem.setOutletId(1L);
        outletItem.setOutletName("Detail Outlet");
        OutletDetailResponse detail = new OutletDetailResponse();
        detail.setOutlet(outletItem);
        when(outletService.getOutletDetails(1L)).thenReturn(detail);

        mockMvc.perform(get("/api/outlets/1/details"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.outlet.outletId").value(1))
                .andExpect(jsonPath("$.outlet.outletName").value("Detail Outlet"));
    }

    // ---------- UT14: Add outlet ----------
    @Test
    @WithMockUser(roles = "MERCHANT")
    @DisplayName("UT14 - Add new outlet")
    void addOutlet_returnsCreated() throws Exception {
        OutletAddRequest request = new OutletAddRequest();
        request.setMerchantId(1L);
        request.setOutletName("New Outlet");
        request.setAddressLine1("123 Street");

        OutletAddDTO dtoResult = new OutletAddDTO();
        dtoResult.setOutletId(1L);
        dtoResult.setOutletName("New Outlet");
        when(outletService.addOutlet(any(OutletAddDTO.class), anyString())).thenReturn(dtoResult);

        mockMvc.perform(post("/api/outlets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.outletId").value(1))
                .andExpect(jsonPath("$.outletName").value("New Outlet"));
    }

    // ---------- UT15: Get outlet status (open/closed) ----------
    @Test
    @WithMockUser(roles = "MERCHANT")
    @DisplayName("UT15 - Get outlet open/closed status")
    void getOutletStatus_returnsStatus() throws Exception {
        OutletStatusResponse statusResp = new OutletStatusResponse();
        statusResp.setOutletId(1L);
        statusResp.setStatus("OPEN");
        when(outletScheduleService.getOutletStatus(eq(1L), any())).thenReturn(statusResp);

        mockMvc.perform(get("/api/outlets/1/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.outletId").value(1))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    // ---------- UT16: Get outlet schedules ----------
    @Test
    @WithMockUser(roles = "MERCHANT")
    @DisplayName("UT16 - Get outlet schedules grouped by type")
    void getOutletSchedules_returnsGrouped() throws Exception {
        OutletSchedulesGroupedResponse grouped = new OutletSchedulesGroupedResponse();
        when(outletScheduleService.getSchedulesGroupedByType(eq(1L), isNull(), isNull())).thenReturn(grouped);

        mockMvc.perform(get("/api/outlets/1/schedules"))
                .andExpect(status().isOk());
    }

    // ---------- UT17: Get outlet discounts ----------
    @Test
    @WithMockUser(roles = "CUSTOMER")
    @DisplayName("UT17 - Get current discounts for outlet")
    void getOutletDiscounts_returnsList() throws Exception {
        DiscountListItemResponse disc = new DiscountListItemResponse();
        disc.setDiscountId(1L);
        when(discountService.listCurrentByOutletId(1L)).thenReturn(List.of(disc));

        mockMvc.perform(get("/api/outlets/1/discounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].discountId").value(1));
    }

    // ---------- UT18: Get outlet feedbacks ----------
    @Test
    @WithMockUser(roles = "CUSTOMER")
    @DisplayName("UT18 - Get feedbacks for outlet")
    void getOutletFeedbacks_returnsList() throws Exception {
        FeedbackResponse fb = new FeedbackResponse();
        fb.setFeedbackId(1L);
        when(feedbackService.listByOutletId(1L)).thenReturn(List.of(fb));

        mockMvc.perform(get("/api/outlets/1/feedbacks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].feedbackId").value(1));
    }
}
