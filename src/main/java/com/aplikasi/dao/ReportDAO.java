package com.aplikasi.dao;

import com.aplikasi.model.Report;
import com.aplikasi.model.Report.CompletedTaskInfo;
import com.aplikasi.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {

    
    public static void insertReport(Report report) throws SQLException {
        String sql = """
            INSERT INTO report (
                user_id, start_date, end_date,
                completed_tasks, average_focus_hours, generated_at
            ) VALUES (?, ?, ?, ?, ?, NOW())
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, report.getUserId());
            ps.setDate(2, Date.valueOf(report.getStartDate()));
            ps.setDate(3, Date.valueOf(report.getEndDate()));
            ps.setInt(4, report.getCompletedTasks());
            ps.setDouble(5, report.getAverageFocusHours());
            ps.executeUpdate();
        }
        
        
    }

   public static List<Report> getDailyReportsByMonth(int userId, int month, int year) throws SQLException {
    List<Report> list = new ArrayList<>();
    String sql = """
        SELECT DATE(deadline) AS tgl, 
               COUNT(task_id) AS total_tasks
        FROM tasks
        WHERE user_id = ? 
          AND completed = true 
          AND MONTH(deadline) = ? 
          AND YEAR(deadline) = ?
        GROUP BY tgl
        ORDER BY tgl ASC
    """;

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, userId);
        ps.setInt(2, month);
        ps.setInt(3, year);

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(new Report(
                rs.getDate("tgl").toLocalDate(),
                rs.getInt("total_tasks"),
                0.0 // Fokus hours bisa dihitung nanti jika perlu
            ));
        }
    }
    return list;
}


public static List<Report> getWeeklyReportsByMonth(int userId, int month, int year) throws SQLException {
    List<Report> list = new ArrayList<>();
    String sql = """
        SELECT MIN(DATE(deadline)) AS week_start, 
               COUNT(task_id) AS total_tasks
        FROM tasks
        WHERE user_id = ? 
          AND completed = true 
          AND MONTH(deadline) = ? 
          AND YEAR(deadline) = ?
        GROUP BY YEARWEEK(deadline, 1)
        ORDER BY week_start ASC
    """;

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, userId);
        ps.setInt(2, month);
        ps.setInt(3, year);

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(new Report(
                rs.getDate("week_start").toLocalDate(),
                rs.getInt("total_tasks"),
                0.0
            ));
        }
    }
    return list;
}


public static List<Report> getMonthlyReportsByYear(int userId, int year) throws SQLException {
    List<Report> list = new ArrayList<>();
    String sql = """
        SELECT DATE_FORMAT(deadline, '%Y-%m-01') AS month_start, 
               COUNT(task_id) AS total_tasks
        FROM tasks
        WHERE user_id = ? 
          AND completed = true 
          AND YEAR(deadline) = ?
        GROUP BY month_start
        ORDER BY month_start ASC
    """;

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, userId);
        ps.setInt(2, year);

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(new Report(
                rs.getDate("month_start").toLocalDate(),
                rs.getInt("total_tasks"),
                0.0
            ));
        }
    }
    return list;
}

  
    public static List<CompletedTaskInfo> getCompletedTasksHistory(
            int userId, int month, int year) throws SQLException {

        List<CompletedTaskInfo> tasks = new ArrayList<>();

        String sql = """
            SELECT t.task_id, t.title, t.category, t.deadline,
                   t.description, t.created_at,
                   COALESCE(tr.total_focus_hours, 0) AS focus_hours
            FROM tasks t
            LEFT JOIN tracking_productivity tr
                   ON t.task_id = tr.task_id
            WHERE t.user_id = ?
              AND t.completed = true
              AND MONTH(t.deadline) = ?
              AND YEAR(t.deadline) = ?
            ORDER BY t.deadline DESC
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, month);
            ps.setInt(3, year);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                CompletedTaskInfo task = new CompletedTaskInfo();
                task.setTaskId(rs.getInt("task_id"));
                task.setTaskTitle(rs.getString("title"));
                task.setCategory(rs.getString("category"));
                task.setCompletedDate(rs.getTimestamp("deadline").toLocalDateTime().toLocalDate());
                task.setCompletedAt(rs.getTimestamp("created_at").toLocalDateTime());
                task.setDescription(rs.getString("description"));
                task.setFocusHours(rs.getDouble("focus_hours"));
                tasks.add(task);
            }
        }
        return tasks;
    }

   
    public static Report getMonthlyReportWithTasks(int userId, int month, int year) throws SQLException {
        Report report = new Report();

        String summarySql = """
            SELECT COUNT(t.task_id) AS completed_count,
                   COALESCE(AVG(tr.total_focus_hours), 0) AS avg_focus
            FROM tasks t
            LEFT JOIN tracking_productivity tr
                   ON t.task_id = tr.task_id
            WHERE t.user_id = ?
              AND t.completed = true
              AND MONTH(t.deadline) = ?
              AND YEAR(t.deadline) = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(summarySql)) {

            ps.setInt(1, userId);
            ps.setInt(2, month);
            ps.setInt(3, year);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                report.setUserId(userId);
                report.setStartDate(LocalDate.of(year, month, 1));
                report.setEndDate(LocalDate.of(year, month, 1).plusMonths(1).minusDays(1));
                report.setCompletedTasks(rs.getInt("completed_count"));
                report.setAverageFocusHours(rs.getDouble("avg_focus"));
                report.setPeriodType("MONTHLY");
            }
        }

        report.setCompletedTasksList(
                getCompletedTasksHistory(userId, month, year)
        );

        return report;
    }

   
    public static List<String> getAvailableMonths(int userId) throws SQLException {
        List<String> months = new ArrayList<>();

        String sql = """
            SELECT DISTINCT YEAR(deadline) AS year,
                            MONTH(deadline) AS month
            FROM tasks
            WHERE user_id = ?
              AND completed = true
            ORDER BY year DESC, month DESC
        """;

        String[] monthNames = {
            "January","February","March","April","May","June",
            "July","August","September","October","November","December"
        };

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int year = rs.getInt("year");
                int month = rs.getInt("month");
                months.add(monthNames[month - 1] + " " + year);
            }
        }
        return months;
    }

   
    public static List<CategoryStats> getCategoryStatsForMonth(
            int userId, int month, int year) throws SQLException {

        List<CategoryStats> stats = new ArrayList<>();

        String sql = """
            SELECT t.category,
                   COUNT(t.task_id) AS task_count,
                   COALESCE(SUM(tr.total_focus_hours), 0) AS total_focus_hours
            FROM tasks t
            LEFT JOIN tracking_productivity tr
                   ON t.task_id = tr.task_id
            WHERE t.user_id = ?
              AND t.completed = true
              AND MONTH(t.deadline) = ?
              AND YEAR(t.deadline) = ?
            GROUP BY t.category
            ORDER BY task_count DESC
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, month);
            ps.setInt(3, year);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                CategoryStats cs = new CategoryStats();
                cs.category = rs.getString("category");
                cs.taskCount = rs.getInt("task_count");
                cs.totalFocusHours = rs.getDouble("total_focus_hours");
                stats.add(cs);
            }
        }
        return stats;
    }

  
    public static class CategoryStats {
        private String category;
        private int taskCount;
        private double totalFocusHours;

        public String getCategory() { return category; }
        public int getTaskCount() { return taskCount; }
        public double getTotalFocusHours() { return totalFocusHours; }
    }
}

