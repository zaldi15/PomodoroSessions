package com.aplikasi.view;

import com.aplikasi.dao.ReportDAO;
import com.aplikasi.dao.ReportDAO.CategoryStats;
import com.aplikasi.dao.TasksDAO;
import com.aplikasi.dao.TrackingDAO;
import com.aplikasi.model.Report;
import com.aplikasi.model.Report.CompletedTaskInfo;
import com.aplikasi.model.Tasks;
import com.aplikasi.model.TrackingProductivity;
import com.aplikasi.model.User;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Controller untuk Report dengan Task History
 * UPDATED: Menambahkan fitur untuk melihat history task yang diselesaikan
 */
public class ReportController implements Initializable {

    // ==================== CHART COMPONENTS ====================
    @FXML private LineChart<String, Number> lineChartProgress;
    @FXML private BarChart<String, Number> barChartCategory;
    
    // ==================== NAVIGATION BUTTONS ====================
    @FXML private Button btnGoToTimer;
    @FXML private Button btnGoToManageTask;
    @FXML private Button btnMissions;
    @FXML private Button btnGoToReport;
    @FXML private Button btnBackToMenu;
    
    // ==================== FILTER COMPONENTS ====================
    @FXML private ComboBox<String> cbPeriodType; // Daily, Weekly, Monthly
    @FXML private ComboBox<String> cbMonthFilter; // Filter bulan untuk history
    @FXML private ComboBox<String> cbCategoryFilter; // Filter kategori
    
    // ==================== SUMMARY LABELS ====================
    @FXML private Label lblTotalTasks;
    @FXML private Label lblCompletedTasks;
    @FXML private Label lblAverageFocus;
    @FXML private Label lblTotalSessions;
    @FXML private Label lblSelectedPeriod;
    
    // ==================== TASK HISTORY TABLE ====================
    @FXML private TableView<CompletedTaskInfo> tvTaskHistory;
    @FXML private TableColumn<CompletedTaskInfo, String> colTaskTitle;
    @FXML private TableColumn<CompletedTaskInfo, String> colCategory;
    @FXML private TableColumn<CompletedTaskInfo, LocalDate> colCompletedDate;
    @FXML private TableColumn<CompletedTaskInfo, String> colDescription;
    @FXML private TableColumn<CompletedTaskInfo, Double> colFocusHours;
    
    // ==================== DATA ====================
    private User currentUser;
    private ObservableList<CompletedTaskInfo> taskHistoryData;
    private int selectedMonth;
    private int selectedYear;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        taskHistoryData = FXCollections.observableArrayList();
        
        // Setup period type ComboBox
        if (cbPeriodType != null) {
            cbPeriodType.getItems().addAll("Daily", "Weekly", "Monthly");
            cbPeriodType.setValue("Monthly");
            cbPeriodType.setOnAction(e -> {
                if (currentUser != null) {
                     loadChartData();
                     }
                });
        }
        
        // Setup category filter
        if (cbCategoryFilter != null) {
            cbCategoryFilter.getItems().addAll("All", "Academic", "Project", "Development");
            cbCategoryFilter.setValue("All");
            cbCategoryFilter.setOnAction(e -> {
            if (currentUser != null) {
                filterTaskHistory();
            }
        });
        }
        
        // Setup task history table
        setupTaskHistoryTable();
        
        // Set default period (current month)
        LocalDate now = LocalDate.now();
        selectedMonth = now.getMonthValue();
        selectedYear = now.getYear();
    }
    
    /**
     * Initialize controller dengan user yang sedang login
     */
public void initForUser(User user) {
    if (user == null) {
        System.err.println("❌ ReportController: user NULL");
        return;
    }

    this.currentUser = user;
    System.out.println("✔ ReportController user: " + user.getUser_id());

    loadAvailableMonths();
    loadChartData();
    loadTaskHistory();
    loadSummaryStatistics();
    loadCategoryChart();
}

    
    /**
     * ✅ BARU: Setup table untuk task history
     */
    private void setupTaskHistoryTable() {
        if (tvTaskHistory == null) return;
        
        // Setup columns
        colTaskTitle.setCellValueFactory(new PropertyValueFactory<>("taskTitle"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colCompletedDate.setCellValueFactory(new PropertyValueFactory<>("completedDate"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colFocusHours.setCellValueFactory(new PropertyValueFactory<>("focusHours"));
        
        // ✅ Custom cell untuk kategori dengan badge
        colCategory.setCellFactory(column -> {
            return new TableCell<CompletedTaskInfo, String>() {
                @Override
                protected void updateItem(String category, boolean empty) {
                    super.updateItem(category, empty);
                    
                    if (empty || category == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        String icon = getCategoryIcon(category);
                        String color = getCategoryColor(category);
                        
                        Label badge = new Label(icon + " " + category);
                        badge.setStyle(
                            "-fx-background-color: " + color + ";" +
                            "-fx-text-fill: white;" +
                            "-fx-background-radius: 12;" +
                            "-fx-padding: 4 10 4 10;" +
                            "-fx-font-size: 11px;" +
                            "-fx-font-weight: bold;"+
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 4, 0, 0, 1);"
                        );
                        
                        HBox container = new HBox(badge);
                        container.setAlignment(Pos.CENTER);
                        
                        setText(null);
                        setGraphic(container);
                    }
                }
            };
        });
        
        // ✅ Custom cell untuk focus hours dengan format
        colFocusHours.setCellFactory(column -> {
            return new TableCell<CompletedTaskInfo, Double>() {
                @Override
                protected void updateItem(Double hours, boolean empty) {
                    super.updateItem(hours, empty);
                    
                    if (empty || hours == null) {
                        setText(null);
                    } else {
                        setText(String.format("%.2f hrs", hours));
                        setStyle("-fx-alignment: CENTER;");
                    }
                }
            };
        });
        
        // ✅ Custom cell untuk description dengan truncate
        colDescription.setCellFactory(column -> {
            return new TableCell<CompletedTaskInfo, String>() {
                @Override
                protected void updateItem(String description, boolean empty) {
                    super.updateItem(description, empty);
                    
                    if (empty || description == null) {
                        setText(null);
                        setTooltip(null);
                    } else {
                        String display = description.length() > 40 
                            ? description.substring(0, 37) + "..." 
                            : description;
                        setText(display);
                        
                        if (description.length() > 40) {
                            Tooltip tooltip = new Tooltip(description);
                            tooltip.setWrapText(true);
                            tooltip.setMaxWidth(300);
                            setTooltip(tooltip);
                        }
                    }
                }
            };
        });
        
        tvTaskHistory.setItems(taskHistoryData);
    }
    
    /**
     * ✅ BARU: Load available months untuk dropdown filter
     */
    private void loadAvailableMonths() {
        if (cbMonthFilter == null || currentUser == null) return;
        
        try {
            List<String> months = ReportDAO.getAvailableMonths(currentUser.getUser_id());
            cbMonthFilter.getItems().clear();
            cbMonthFilter.getItems().addAll(months);
            
            if (!months.isEmpty()) {
                cbMonthFilter.setValue(months.get(0)); // Select most recent
                
                // Parse selected month and year
                parseSelectedMonth(months.get(0));
            }
            
            // Set listener untuk perubahan bulan
            cbMonthFilter.setOnAction(e -> {
                String selected = cbMonthFilter.getValue();
                if (selected != null) {
                    parseSelectedMonth(selected);
                    loadTaskHistory();
                    loadSummaryStatistics();
                    loadCategoryChart();
                }
            });
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal memuat daftar bulan");
        }
    }
    
    /**
     * ✅ BARU: Parse selected month string to month and year
     */
    private void parseSelectedMonth(String monthYear) {
        // Format: "January 2024"
        String[] parts = monthYear.split(" ");
        if (parts.length == 2) {
            String monthName = parts[0];
            selectedYear = Integer.parseInt(parts[1]);
            
            String[] monthNames = {
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
            };
            
            for (int i = 0; i < monthNames.length; i++) {
                if (monthNames[i].equals(monthName)) {
                    selectedMonth = i + 1;
                    break;
                }
            }
            
            if (lblSelectedPeriod != null) {
                lblSelectedPeriod.setText(monthYear);
            }
        }
    }
    
    /**
     * ✅ BARU: Load task history untuk bulan yang dipilih
     */
    private void loadTaskHistory() {
        if (currentUser == null) return;
        
        try {
            taskHistoryData.clear();
            List<CompletedTaskInfo> tasks = ReportDAO.getCompletedTasksHistory(
                currentUser.getUser_id(), selectedMonth, selectedYear
            );
            taskHistoryData.addAll(tasks);
            
            System.out.println("✔ Loaded " + tasks.size() + " completed tasks for " + 
                             selectedMonth + "/" + selectedYear);
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal memuat history task");
        }
    }
    
    /**
     * ✅ BARU: Filter task history berdasarkan kategori
     */
    private void filterTaskHistory() {
        if (currentUser == null || cbCategoryFilter == null) return;
        
        String selectedCategory = cbCategoryFilter.getValue();
        
        try {
            List<CompletedTaskInfo> allTasks = ReportDAO.getCompletedTasksHistory(
                currentUser.getUser_id(), selectedMonth, selectedYear
            );
            
            taskHistoryData.clear();
            
            if ("All".equals(selectedCategory)) {
                taskHistoryData.addAll(allTasks);
            } else {
                for (CompletedTaskInfo task : allTasks) {
                    if (task.getCategory().equals(selectedCategory)) {
                        taskHistoryData.add(task);
                    }
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * ✅ FIXED: Load summary statistics dengan total tasks yang benar
     */
    private void loadSummaryStatistics() {
        
        if (currentUser == null) return;

        try {
            Report report = ReportDAO.getMonthlyReportWithTasks(
                currentUser.getUser_id(), selectedMonth, selectedYear
            );

            // Insert report snapshot
            ReportDAO.insertReport(report);
            System.out.println("✔ Report snapshot inserted");

            // ✅ Get ALL tasks untuk user (completed + uncompleted)
            List<Tasks> allTasks = TasksDAO.getAllTasksByUser(currentUser.getUser_id());
            int totalTasks = allTasks.size();
            int completedTasks = (int) allTasks.stream().filter(Tasks::isCompleted).count();
            
            // ✅ Total Tasks = SEMUA task (completed + uncompleted)
            if (lblTotalTasks != null) {
                lblTotalTasks.setText(String.valueOf(totalTasks));
            }

            // ✅ Completed Tasks
            if (lblCompletedTasks != null) {
                lblCompletedTasks.setText(String.valueOf(completedTasks));
            }

            // ✅ Average Focus Hours
            if (lblAverageFocus != null) {
                lblAverageFocus.setText(
                    String.format("%.2f", report.getAverageFocusHours())
                );
            }

            // ✅ Total Sessions dari tracking productivity
            if (lblTotalSessions != null) {
                lblTotalSessions.setText(
                String.valueOf(TrackingDAO.getTotalSessions(currentUser.getUser_id()))
);

            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal memuat statistik");
        }
    }

    
    /**
     * Load chart data (existing method - keep as is)
     */
    private void loadChartData() {
        if (currentUser == null || lineChartProgress == null) return;
        
        try {
            String periodType = cbPeriodType.getValue();
            lineChartProgress.getData().clear();
            
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Completed Tasks");
            
            if ("Monthly".equals(periodType)) {
                List<Report> reports = ReportDAO.getMonthlyReportsByYear(
                    currentUser.getUser_id(), selectedYear
                );
                
                for (Report report : reports) {
                    String monthName = report.getStartDate().getMonth().toString();
                    series.getData().add(new XYChart.Data<>(
                        monthName, report.getCompletedTasks()
                    ));
                }
            } else if ("Daily".equals(periodType)) {
                List<Report> reports = ReportDAO.getDailyReportsByMonth(
                    currentUser.getUser_id(), selectedMonth, selectedYear
                );
                
                for (Report report : reports) {
                    String day = String.valueOf(report.getStartDate().getDayOfMonth());
                    series.getData().add(new XYChart.Data<>(
                        day, report.getCompletedTasks()
                    ));
                }
            }  else if ("Weekly".equals(periodType)) {

                List<Report> reports = ReportDAO.getWeeklyReportsByMonth(
                    currentUser.getUser_id(), selectedMonth, selectedYear
                );

                for (Report report : reports) {
                    String label = "Week of " +
                        report.getStartDate().format(DateTimeFormatter.ofPattern("dd MMM"));

                    series.getData().add(new XYChart.Data<>(
                        label, report.getCompletedTasks()
                    ));
                }
}

            
            lineChartProgress.getData().add(series);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * ✅ BARU: Load category chart
     */
    private void loadCategoryChart() {
        if (currentUser == null || barChartCategory == null) return;
        
        try {
            barChartCategory.getData().clear();
            
            List<CategoryStats> stats = ReportDAO.getCategoryStatsForMonth(
                currentUser.getUser_id(), selectedMonth, selectedYear
            );
            
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Tasks by Category");
            
            for (CategoryStats stat : stats) {
                String icon = getCategoryIcon(stat.getCategory());
                series.getData().add(new XYChart.Data<>(
                    icon + " " + stat.getCategory(), 
                    stat.getTaskCount()
                ));
            }
            
            barChartCategory.getData().add(series);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // ==================== UTILITY METHODS ====================
    
    private String getCategoryIcon(String category) {
        switch (category) {
            case "Academic": return "📚";
            case "Project": return "🎯";
            case "Development": return "💻";
            default: return "📋";
        }
    }
    
    private String getCategoryColor(String category) {
        switch (category) {
            case "Academic": return "#4A90E2";
            case "Project": return "#50C878";
            case "Development": return "#FF6B6B";
            default: return "#95A5A6";
        }
    }
    
    // ==================== NAVIGATION ====================
    
    @FXML
    private void handleGoToTimer(ActionEvent event) throws IOException {
        navigateWithUser("/com/aplikasi/view/Timer.fxml", btnGoToTimer);
    }

    @FXML
    private void handleGoToManageTask(ActionEvent event) throws IOException {
        navigateWithUser("/com/aplikasi/view/ManageTask.fxml", btnGoToManageTask);
    }

        @FXML
    private void handleMissions(ActionEvent event) throws IOException {
        navigateWithUser("/com/aplikasi/view/MissionsView.fxml", btnMissions);
    }
    
    @FXML
    private void handleGoToReport(ActionEvent event) throws IOException {
        System.out.println("Already in Report");
    }
    
     @FXML
    private void handleBackToMenu(ActionEvent event) {
        try {
            // Gunakan path yang konsisten dengan struktur projectmu
            // Jika filenya bernama MenuUtama.fxml, pastikan tulisannya sama persis (Case Sensitive)
            URL fxmlLocation = getClass().getResource("/com/aplikasi/view/MenuUtama.fxml");
            
            if (fxmlLocation == null) {
                // Debugging: Jika null, berarti file tidak ditemukan di folder tersebut
                System.err.println("❌ FXML Error: MenuUtama.fxml tidak ditemukan!");
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load(); 
            
            // Mengambil controller tujuan (Pastikan namanya sesuai, misal: MenuUtamaController)
            Object controller = loader.getController();
            if (controller != null) {
                // Gunakan reflection atau casting jika ada method initForUser
                try {
                    Method m = controller.getClass().getMethod("initForUser", User.class);
                    m.invoke(controller, currentUser);
                } catch (Exception e) {
                    System.out.println("ℹ️ Controller tujuan tidak memiliki method initForUser");
                }
            }
            
            Stage stage = (Stage) btnBackToMenu.getScene().getWindow();
            stage.getScene().setRoot(root);
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal kembali ke menu utama");
        }
    }
    
    private void navigateWithUser(String fxmlPath, Button sourceButton) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Stage stage = (Stage) sourceButton.getScene().getWindow();
            loader.load();
            
            Object controller = loader.getController();

            if (controller instanceof TimerController) {
                ((TimerController) controller).initForUser(currentUser);

            } else if (controller instanceof ManageTaskController) {
                ((ManageTaskController) controller).initForUser(currentUser);

            } else if (controller instanceof MissionsController) {
                ((MissionsController) controller).initForUser(currentUser);
            }

            
            stage.getScene().setRoot(loader.getRoot());
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal membuka halaman");
        }
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}