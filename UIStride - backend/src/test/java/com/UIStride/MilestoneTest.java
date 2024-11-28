package com.UIStride;

import com.UIStride.model.Milestone;
import com.UIStride.service.MilestoneService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class MilestoneTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MilestoneService milestoneService;

    @Test
    void testGetAllMilestonesSuccess() throws Exception {
        List<Milestone> mockMilestones = Arrays.asList(
                new Milestone(1L, "5KM Walk", 5.0, 5000, 50, MilestoneType.DISTANCE,
                        LocalDateTime.of(2024, 11, 24, 10, 0),
                        LocalDateTime.of(2024, 11, 24, 12, 0)),
                new Milestone(2L, "10KM Walk", 10.0, 10000, 100, MilestoneType.DISTANCE,
                        LocalDateTime.of(2024, 11, 24, 8, 0),
                        LocalDateTime.of(2024, 11, 24, 10, 0))
        );

        when(milestoneService.getAllMilestones()).thenReturn(mockMilestones);

        mockMvc.perform(get("/milestones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Milestones fetched successfully"))
                .andExpect(jsonPath("$.payload", hasSize(2)))
                .andExpect(jsonPath("$.payload[0].id").value(1))
                .andExpect(jsonPath("$.payload[0].name").value("5KM Walk"))
                .andExpect(jsonPath("$.payload[0].requiredDistance").value(5.0))
                .andExpect(jsonPath("$.payload[0].requiredSteps").value(5000))
                .andExpect(jsonPath("$.payload[0].points").value(50))
                .andExpect(jsonPath("$.payload[0].type").value("DISTANCE"))
                .andExpect(jsonPath("$.payload[0].createdAt").value("2024-11-24T10:00:00"))
                .andExpect(jsonPath("$.payload[0].updatedAt").value("2024-11-24T12:00:00"))
                .andExpect(jsonPath("$.payload[1].id").value(2))
                .andExpect(jsonPath("$.payload[1].name").value("10KM Walk"))
                .andExpect(jsonPath("$.payload[1].requiredDistance").value(10.0))
                .andExpect(jsonPath("$.payload[1].requiredSteps").value(10000))
                .andExpect(jsonPath("$.payload[1].points").value(100))
                .andExpect(jsonPath("$.payload[1].type").value("DISTANCE"))
                .andExpect(jsonPath("$.payload[1].createdAt").value("2024-11-24T08:00:00"))
                .andExpect(jsonPath("$.payload[1].updatedAt").value("2024-11-24T10:00:00"));
    }
}

