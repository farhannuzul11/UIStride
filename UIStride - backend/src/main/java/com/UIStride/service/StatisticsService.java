package com.UIStride.service;

import com.UIStride.model.Activity;
import com.UIStride.repository.ActivityRepository;
import com.UIStride.repository.PointsRepository;
import com.UIStride.repository.UserPointsRepository;
import com.UIStride.repository.UserRewardRepository;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.IsoFields;
import java.util.*;

@Service
public class StatisticsService {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private PointsRepository pointsRepository;

    @Autowired
    private UserPointsRepository userPointsRepository;



    // Statistik berdasarkan periode
    public Map<String, Object> getUserStatistics(Long accountId, LocalDateTime startDate, LocalDateTime endDate) {
        double totalDistance = activityRepository.getTotalDistanceByAccountIdAndPeriod(accountId, startDate, endDate);
        int totalSteps = activityRepository.getTotalStepsByAccountIdAndPeriod(accountId, startDate, endDate);

        // Total points dari user_points
        Integer currentTotalPoints = userPointsRepository.findTotalPointsByAccountId(accountId);
        if (currentTotalPoints == null) {
            currentTotalPoints = 0;
        }

        // Atur statistik
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalDistance", totalDistance);
        statistics.put("totalSteps", totalSteps);
        statistics.put("totalPoints", currentTotalPoints);
        return statistics;
    }


    // Statistik yang dikelompokkan berdasarkan hari, minggu, bulan, tahun, atau alltime
    public Map<String, Object> getGroupedStatistics(Long accountId, LocalDateTime startDate, LocalDateTime endDate, String period) {
        List<Activity> activities = activityRepository.findByAccountIdAndStartTimeBetween(accountId, startDate, endDate);

        Map<String, Object> groupedStatistics = new HashMap<>();

        switch (period.toLowerCase()) {
            case "daily":
                groupedStatistics.putAll(getDailyStatistics(activities));
                break;

            case "weekly":
                groupedStatistics.putAll(getWeeklyStatistics(activities));
                break;

            case "monthly":
                groupedStatistics.putAll(getMonthlyStatistics(activities));
                break;

            case "yearly":
                groupedStatistics.putAll(getYearlyStatistics(activities));
                break;

            case "alltime":
                groupedStatistics.putAll(getAllTimeStatistics(accountId));
                break;

            default:
                throw new IllegalArgumentException("Invalid period type. Valid options are: daily, weekly, monthly, yearly, alltime.");
        }

        return groupedStatistics;
    }

    // Menghitung statistik harian
    private Map<String, Object> getDailyStatistics(List<Activity> activities) {
        // Initialize maps for steps, distance, and points by day
        Map<String, Integer> stepsByDay = new HashMap<>();
        Map<String, Double> distanceByDay = new HashMap<>();
        Map<String, Integer> pointsByDay = new HashMap<>();

        // List of all days of the week in order: Monday to Sunday
        List<String> allDays = Arrays.asList("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY");

        // Initialize all days with default values
        for (String day : allDays) {
            stepsByDay.put(day, 0);  // Default 0 steps
            distanceByDay.put(day, 0.0);  // Default 0 distance
            pointsByDay.put(day, 0);  // Default 0 points
        }

        // Aggregate data from activities
        for (Activity activity : activities) {
            String dayOfWeek = activity.getStartTime().getDayOfWeek().toString().toUpperCase(); // Convert to uppercase for consistency
            stepsByDay.put(dayOfWeek, stepsByDay.getOrDefault(dayOfWeek, 0) + activity.getSteps());
            distanceByDay.put(dayOfWeek, distanceByDay.getOrDefault(dayOfWeek, 0.0) + activity.getDistance());
            pointsByDay.put(dayOfWeek, pointsByDay.getOrDefault(dayOfWeek, 0) + calculatePoints(activity));
        }

        // Prepare the result map, ensuring the order is correct
        Map<String, Object> dailyStats = new LinkedHashMap<>();

        // Iterate over allDays to maintain the correct order
        dailyStats.put("stepsByDay", sortMapByOrder(stepsByDay, allDays));
        dailyStats.put("distanceByDay", sortMapByOrder(distanceByDay, allDays));
        dailyStats.put("pointsByDay", sortMapByOrder(pointsByDay, allDays));

        return dailyStats;
    }

    // Helper method to sort map by the predefined order of days
    private Map<String, Object> sortMapByOrder(Map<String, ?> originalMap, List<String> order) {
        Map<String, Object> sortedMap = new LinkedHashMap<>();
        for (String day : order) {
            sortedMap.put(day, originalMap.get(day));
        }
        return sortedMap;
    }



    // Menghitung statistik mingguan
    private Map<String, Object> getWeeklyStatistics(List<Activity> activities) {
        Map<String, Integer> stepsByWeek = new HashMap<>();
        Map<String, Double> distanceByWeek = new HashMap<>();
        Map<String, Integer> pointsByWeek = new HashMap<>();

        for (Activity activity : activities) {
            int weekOfYear = activity.getStartTime().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR); // Week 1, Week 2, etc.
            stepsByWeek.put("Week " + weekOfYear, stepsByWeek.getOrDefault("Week " + weekOfYear, 0) + activity.getSteps());
            distanceByWeek.put("Week " + weekOfYear, distanceByWeek.getOrDefault("Week " + weekOfYear, 0.0) + activity.getDistance());
            pointsByWeek.put("Week " + weekOfYear, pointsByWeek.getOrDefault("Week " + weekOfYear, 0) + calculatePoints(activity));
        }

        Map<String, Object> weeklyStats = new HashMap<>();
        weeklyStats.put("stepsByWeek", stepsByWeek);
        weeklyStats.put("distanceByWeek", distanceByWeek);
        weeklyStats.put("pointsByWeek", pointsByWeek);

        return weeklyStats;
    }

    // Menghitung statistik bulanan
    private Map<String, Object> getMonthlyStatistics(List<Activity> activities) {
        Map<String, Integer> stepsByMonth = new HashMap<>();
        Map<String, Double> distanceByMonth = new HashMap<>();
        Map<String, Integer> pointsByMonth = new HashMap<>();

        // Proses data aktivitas
        for (Activity activity : activities) {
            String month = activity.getStartTime().getMonth().toString(); // January, February, etc.
            stepsByMonth.put(month, stepsByMonth.getOrDefault(month, 0) + activity.getSteps());
            distanceByMonth.put(month, distanceByMonth.getOrDefault(month, 0.0) + activity.getDistance());
            pointsByMonth.put(month, pointsByMonth.getOrDefault(month, 0) + calculatePoints(activity));
        }

        // Hapus bulan tanpa data
        stepsByMonth.entrySet().removeIf(entry -> entry.getValue() == 0);
        distanceByMonth.entrySet().removeIf(entry -> entry.getValue() == 0.0);
        pointsByMonth.entrySet().removeIf(entry -> entry.getValue() == 0);

        // Urutkan bulan berdasarkan urutan kalender
        List<String> monthsOrder = Arrays.asList(
                "JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE",
                "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"
        );

        Map<String, Integer> sortedStepsByMonth = new LinkedHashMap<>();
        Map<String, Double> sortedDistanceByMonth = new LinkedHashMap<>();
        Map<String, Integer> sortedPointsByMonth = new LinkedHashMap<>();

        for (String month : monthsOrder) {
            if (stepsByMonth.containsKey(month)) {
                sortedStepsByMonth.put(month, stepsByMonth.get(month));
            }
            if (distanceByMonth.containsKey(month)) {
                sortedDistanceByMonth.put(month, distanceByMonth.get(month));
            }
            if (pointsByMonth.containsKey(month)) {
                sortedPointsByMonth.put(month, pointsByMonth.get(month));
            }
        }

        // Gabungkan hasil ke dalam Map final
        Map<String, Object> monthlyStats = new LinkedHashMap<>();
        monthlyStats.put("stepsByMonth", sortedStepsByMonth);
        monthlyStats.put("distanceByMonth", sortedDistanceByMonth);
        monthlyStats.put("pointsByMonth", sortedPointsByMonth);

        return monthlyStats;
    }

    // Menghitung statistik tahunan
    private Map<String, Object> getYearlyStatistics(List<Activity> activities) {
        Map<String, Integer> stepsByYear = new HashMap<>();
        Map<String, Double> distanceByYear = new HashMap<>();
        Map<String, Integer> pointsByYear = new HashMap<>();

        for (Activity activity : activities) {
            int year = activity.getStartTime().getYear(); // 2023, 2024, etc.
            stepsByYear.put("Year " + year, stepsByYear.getOrDefault("Year " + year, 0) + activity.getSteps());
            distanceByYear.put("Year " + year, distanceByYear.getOrDefault("Year " + year, 0.0) + activity.getDistance());
            pointsByYear.put("Year " + year, pointsByYear.getOrDefault("Year " + year, 0) + calculatePoints(activity));
        }

        Map<String, Object> yearlyStats = new HashMap<>();
        yearlyStats.put("stepsByYear", stepsByYear);
        yearlyStats.put("distanceByYear", distanceByYear);
        yearlyStats.put("pointsByYear", pointsByYear);

        return yearlyStats;
    }

    // Statistik all-time
    private Map<String, Object> getAllTimeStatistics(Long accountId) {
        int totalStepsAllTime = activityRepository.getTotalStepsByAccountId(accountId);
        double totalDistanceAllTime = activityRepository.getTotalDistanceByAccountId(accountId);
        int totalPointsAllTime = pointsRepository.getTotalPointsByAccountId(accountId);

        Map<String, Object> allTimeStats = new HashMap<>();
        allTimeStats.put("totalSteps", totalStepsAllTime);
        allTimeStats.put("totalDistance", totalDistanceAllTime);
        allTimeStats.put("totalPoints", totalPointsAllTime);

        return allTimeStats;
    }

    // Fungsi untuk menghitung poin berdasarkan aktivitas
    private int calculatePoints(Activity activity) {
        int distancePoints = (int) (activity.getDistance()) * 10; // 10 points per km
        int stepPoints = (activity.getSteps() / 10) * 5; // 5 points per 1000 steps
        return distancePoints + stepPoints;
    }
}
