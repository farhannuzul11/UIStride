package com.UIStride;

import com.UIStride.model.UserPoints;
import com.UIStride.service.UserPointsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserPointsTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserPointsService userPointsService;

    @Test
    void testGetUserPointsSuccess() throws Exception {
        UserPoints mockUserPoints = new UserPoints(1L, 1001L, 150);

        when(userPointsService.getUserPoints(1001L)).thenReturn(mockUserPoints);

        mockMvc.perform(get("/user-points/1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User points fetched successfully"))
                .andExpect(jsonPath("$.payload.id").value(1))
                .andExpect(jsonPath("$.payload.accountId").value(1001))
                .andExpect(jsonPath("$.payload.totalPoints").value(150));
    }

    @Test
    void testCheckAndRewardSuccess() throws Exception {
        doNothing().when(userPointsService).checkAndRewardMilestones(1001L, 5.0, 5000);

        mockMvc.perform(post("/user-points/check-reward")
                        .param("accountId", "1001")
                        .param("distance", "5.0")
                        .param("steps", "5000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Milestones checked and points rewarded"));
    }
}

