package lk.icbt.findit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lk.icbt.findit.controller.ItemController;
import lk.icbt.findit.request.ItemRequest;
import lk.icbt.findit.response.ItemListItemResponse;
import lk.icbt.findit.response.ItemResponse;
import lk.icbt.findit.service.ItemService;
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

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@Tag("unit")
@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Import(TestJacksonConfig.class)
class ItemModuleTest {

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
    @DisplayName("UT05 - Add new item")
    void addNewItem_returnsCreated() throws Exception {
        ItemRequest request = new ItemRequest();
        request.setItemName("Test Item");
        request.setItemDescription("Description");
        request.setCategoryId(1L);
        request.setOutletId(1L);
        request.setPrice(BigDecimal.valueOf(99.99));
        request.setAvailability(true);

        ItemResponse response = new ItemResponse();
        response.setStatus("SUCCESS");
        response.setItemId(1L);
        response.setItemName("Test Item");
        response.setPrice(BigDecimal.valueOf(99.99));

        when(itemService.create(any(ItemRequest.class), anyString())).thenReturn(response);

        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.itemId").value(1))
                .andExpect(jsonPath("$.itemName").value("Test Item"));
    }

    
    @Test
    @WithMockUser(roles = "MERCHANT")
    @DisplayName("UT06 - Get items by outlet id")
    void getItemsByOutletId_returnsList() throws Exception {
        ItemListItemResponse item = new ItemListItemResponse();
        item.setItemId(1L);
        item.setItemName("Item One");
        when(itemService.getByOutletId(1L)).thenReturn(List.of(item));

        mockMvc.perform(get("/api/items/outlet/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].itemId").value(1))
                .andExpect(jsonPath("$[0].itemName").value("Item One"));
    }

    
    @Test
    @WithMockUser(roles = "MERCHANT")
    @DisplayName("UT07 - Get item by id")
    void getItemById_returnsItem() throws Exception {
        ItemResponse response = new ItemResponse();
        response.setItemId(1L);
        response.setItemName("Single Item");
        when(itemService.getById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId").value(1))
                .andExpect(jsonPath("$.itemName").value("Single Item"));
    }

    
    @Test
    @WithMockUser(roles = "MERCHANT")
    @DisplayName("UT08 - Search items with filters")
    void searchItems_returnsList() throws Exception {
        ItemListItemResponse item = new ItemListItemResponse();
        item.setItemId(1L);
        item.setItemName("Searched Item");
        when(itemService.search(eq("coffee"), eq(1L), eq(1L), eq("ACTIVE"), eq(true)))
                .thenReturn(List.of(item));

        mockMvc.perform(get("/api/items")
                        .param("search", "coffee")
                        .param("categoryId", "1")
                        .param("outletId", "1")
                        .param("status", "ACTIVE")
                        .param("availability", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].itemName").value("Searched Item"));
    }

    
    @Test
    @WithMockUser(roles = "MERCHANT")
    @DisplayName("UT09 - Update existing item")
    void updateItem_returnsOk() throws Exception {
        ItemRequest request = new ItemRequest();
        request.setItemName("Updated Item");
        request.setCategoryId(1L);
        request.setOutletId(1L);
        request.setPrice(BigDecimal.valueOf(149.99));

        ItemResponse response = new ItemResponse();
        response.setItemId(1L);
        response.setItemName("Updated Item");
        when(itemService.update(eq(1L), any(ItemRequest.class), anyString())).thenReturn(response);

        mockMvc.perform(put("/api/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemName").value("Updated Item"));
    }

    
    @Test
    @WithMockUser(roles = "MERCHANT")
    @DisplayName("UT10 - Delete item")
    void deleteItem_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/items/1"))
                .andExpect(status().isNoContent());
        verify(itemService).delete(1L);
    }
}
