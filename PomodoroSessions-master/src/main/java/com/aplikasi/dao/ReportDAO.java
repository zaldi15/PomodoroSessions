package com.aplikasi.dao;

import com.aplikasi.model.Report;
import com.aplikasi.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {

    // ✅ Simpan report ke database
    public static void insertReport(Report report) throws SQLException {
        String sql = """
            INSERT INTO report (
                user_id, start_date, end_date, 
                completed_tasks, average_focus_hours, generated_at
            )
            VALUES (?, ?, ?, ?, ?, NOW())
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

    // ✅ Ambil semua report user
    public static List<Report> getReportsByUser(int userId) throws SQLException {
        List<Report> list = new ArrayList<>();
        String sql = "SELECT * FROM report WHERE user_id = ? ORDER BY generated_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Report r = new Report();
                r.setReportId(rs.getInt("report_id"));
                r.setUserId(rs.getInt("user_id"));
                r.setStartDate(rs.getDate("start_date").toLocalDate());
                r.setEndDate(rs.getDate("end_date").toLocalDate());
                r.setCompletedTasks(rs.getInt("completed_tasks"));
                r.setAverageFocusHours(rs.getDouble("average_focus_hours"));
                r.setGeneratedAt(rs.getTimestamp("generated_at").toLocalDateTime());
                list.add(r);
            }
        }
        return list;
    }

    // ✅ Ambil report paling terbaru
    public static Report getLatestReport(int userId) throws SQLException {
        String sql = "SELECT * FROM report WHERE user_id = ? ORDER BY generated_at DESC LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Report r = new Report();
                r.setReportId(rs.getInt("report_id"));
                r.setUserId(rs.getInt("user_id"));
                r.setStartDate(rs.getDate("start_date").toLocalDate());
                r.setEndDate(rs.getDate("end_date").toLocalDate());
                r.setCompletedTasks(rs.getInt("completed_tasks"));
                r.setAverageFocusHours(rs.getDouble("average_focus_hours"));
                r.setGeneratedAt(rs.getTimestamp("generated_at").toLocalDateTime());
                return r;
            }
        }
        return null;
    }
}
