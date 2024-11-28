package com.UIStride;

import com.UIStride.model.Activity;
import com.UIStride.service.ActivityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class ActivityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ActivityService activityService;

    @Test
    public void testSaveActivitySuccess() throws Exception {
        Activity mockActivity = new Activity(1L, 1001L, 2.5, 300,
                LocalDateTime.now().minusMinutes(30), LocalDateTime.now(), "00:30:00");

        when(activityService.addActivity(anyLong(), anyDouble(), anyInt(), any(LocalDateTime.class), any(LocalDateTime.class), anyString()))
                .thenReturn(mockActivity);

        mockMvc.perform(post("/activity/save")
                        .param("accountId", "1001")
                        .param("distance", "2.5")
                        .param("steps", "300")
                        .param("startTime", LocalDateTime.now().minusMinutes(30).toString())
                        .param("endTime", LocalDateTime.now().toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Activity saved successfully"))
                .andExpect(jsonPath("$.payload.distance").value(2.5));
    }

    @Test
    public void testSaveActivityInvalidTimeFormat() throws Exception {
        mockMvc.perform(post("/activity/save")
                        .param("accountId", "1001")
                        .param("distance", "2.5")
                        .param("steps", "300")
                        .param("startTime", "invalid-date-format")
                        .param("endTime", "2024-11-25T10:15:30"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid time format"));
    }

    @Test
    public void testGetActivitiesByAccountIdSuccess() throws Exception {
        List<Activity> mockActivities = Arrays.asList(
                new Activity(1L, 1001L, 3.0, 400, LocalDateTime.now().minusHours(1), LocalDateTime.now(), "01:00:00"),
                new Activity(2L, 1001L, 2.5, 350, LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), "01:00:00")
        );

        when(activityService.getActivitiesByAccountId(1001L)).thenReturn(mockActivities);

        mockMvc.perform(get("/activity/1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Activities fetched successfully"))
                .andExpect(jsonPath("$.payload[0].distance").value(3.0))
                .andExpect(jsonPath("$.payload[1].steps").value(350));
    }

    @Test
    public void testGetActivitiesByAccountIdNotFound() throws Exception {
        when(activityService.getActivitiesByAccountId(1002L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/activity/1002"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("No activities found for the given accountId"));
    }
}
