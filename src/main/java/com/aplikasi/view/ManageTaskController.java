package com.aplikasi.view;

import com.aplikasi.dao.TasksDAO;
import com.aplikasi.dao.TrackingDAO;
import com.aplikasi.dao.ReportDAO;
import com.aplikasi.model.Tasks;
import com.aplikasi.model.User;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * Controller untuk Manage Task
 * Menampilkan daftar tugas user dan operasi CRUD
 * UPDATED: Modern styling dengan kategori, filter, dan SEARCH
 */
public class ManageTaskController implements Initializable {

    @FXML private TableColumn<Tasks, String> colTitle;
    @FXML private TableColumn<Tasks, String> colDescription;
    @FXML private TableColumn<Tasks, Boolean> colCompleted;
    @FXML private TableView<Tasks> tvTasks;
    @FXML private TableColumn<Tasks, LocalDate> colDeadline;
    @FXML private TableColumn<Tasks, String> colCategory;
    @FXML private TableColumn<Tasks, Void> colEditDelete;
    @FXML private Button btnGoToAdd;
    @FXML private Button btnGoToTimer;
    @FXML private Button btnGoToManageTask;
    @FXML private Button btnReport;
    @FXML private Button btnMissions;
    @FXML private Button btnBackToMenu;
    @FXML private ComboBox<String> cbFilterCategory;
    @FXML private Label lblTaskCount;
    
    // ✅ BARU: Search components
    @FXML private TextField txtSearch;
    @FXML private Button btnSearch;
    @FXML private Button btnClearSearch;
    
    private ObservableList<Tasks> dataTasks;
    private ObservableList<Tasks> allTasks;
    private User currentUser;
    private Tasks currentTask;  // Task yang sedang dipilih/diedit (untuk konsistensi dengan TimerController)
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dataTasks = FXCollections.observableArrayList();
        allTasks = FXCollections.observableArrayList();
        
        // Setup kolom tabel
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colDeadline.setCellValueFactory(new PropertyValueFactory<>("deadline"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        
        // ✅ MODERN: Custom cell untuk kategori dengan badge style
        setupModernCategoryColumn();
        
        // ✅ MODERN: Custom cell untuk deadline dengan color coding
        setupModernDeadlineColumn();
        
        // ✅ MODERN: Custom cell untuk description dengan truncate
        setupModernDescriptionColumn();
        
        // Setup checkbox completed
        colCompleted.setCellValueFactory(cellData -> cellData.getValue().completedProperty());
        colCompleted.setCellFactory(column -> {
            return new CheckBoxTableCell<>(index -> {
                Tasks task = tvTasks.getItems().get(index);
                task.completedProperty().addListener((obs, oldVal, newVal) -> {
                    try {
                        task.setCompleted(newVal);
                        TasksDAO.updateEntry(task);
                        
                        // Update tracking
                        if (newVal) {
                            TrackingDAO.updateSession(currentUser.getUser_id(), task.getTask_id(), 0, 0);
                        }
                        
                        // Refresh untuk apply style
                        tvTasks.refresh();
                        updateTaskCounter();

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
                return task.completedProperty();
            });
        });

        // ✅ MODERN: Setup tombol Edit & Delete dengan styling
        setupModernActionColumn();
        
        // Setup ComboBox filter
        setupCategoryFilter();
        
        // ✅ BARU: Setup search functionality
        setupSearchFunctionality();
        
        // Handler untuk update checkbox
        colCompleted.setOnEditCommit(event -> {
            Tasks task = event.getRowValue();
            task.setCompleted(event.getNewValue());
            try {
                TasksDAO.updateEntry(task);

                if (event.getNewValue()) {
                    try {
                        TrackingDAO.updateSession(currentUser.getUser_id(), task.getTask_id(), 0, 0);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Status tugas berhasil diupdate");
                tvTasks.refresh();
                updateTaskCounter();
            } catch (SQLException e) {
                System.err.println("Gagal update status completed: " + e.getMessage());
                showAlert(Alert.AlertType.ERROR, "Database Error", "Gagal update status tugas.");
                task.setCompleted(event.getOldValue());
                tvTasks.refresh();
            }
        });
    
        tvTasks.setItems(dataTasks);
        tvTasks.setEditable(true);
        
        // Load CSS untuk styling modern
        loadModernStyling();
    }
    
    /**
     * ✅ BARU: Setup search functionality
     */
    private void setupSearchFunctionality() {
        // Search saat user mengetik (real-time)
        if (txtSearch != null) {
            txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
                performSearch(newValue);
            });
            
            // Search saat Enter ditekan
            txtSearch.setOnAction(event -> performSearch(txtSearch.getText()));
        }
        
        // Button search (alternative)
        if (btnSearch != null) {
            btnSearch.setOnAction(event -> performSearch(txtSearch.getText()));
        }
        
        // Button clear search
        if (btnClearSearch != null) {
            btnClearSearch.setOnAction(event -> clearSearch());
        }
    }
    
    /**
     * ✅ BARU: Perform search berdasarkan keyword
     */
    private void performSearch(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            // Jika search kosong, tampilkan semua (dengan filter kategori jika ada)
            applyFilters();
            return;
        }
        
        String searchTerm = keyword.toLowerCase().trim();
        ObservableList<Tasks> searchResults = FXCollections.observableArrayList();
        
        // Ambil tasks yang sudah difilter kategori (atau semua jika "All")
        ObservableList<Tasks> tasksToSearch;
        if (cbFilterCategory != null && !cbFilterCategory.getValue().equals("All")) {
            tasksToSearch = FXCollections.observableArrayList();
            for (Tasks task : allTasks) {
                if (task.getCategory().equals(cbFilterCategory.getValue())) {
                    tasksToSearch.add(task);
                }
            }
        } else {
            tasksToSearch = allTasks;
        }
        
        // Search dalam title, description, dan category
        for (Tasks task : tasksToSearch) {
            boolean matchTitle = task.getTitle().toLowerCase().contains(searchTerm);
            boolean matchDescription = task.getDescription().toLowerCase().contains(searchTerm);
            boolean matchCategory = task.getCategory().toLowerCase().contains(searchTerm);
            
            if (matchTitle || matchDescription || matchCategory) {
                searchResults.add(task);
            }
        }
        
        dataTasks.setAll(searchResults);
        updateTaskCounter();
        
        System.out.println("🔍 Search: '" + keyword + "' - Found " + searchResults.size() + " tasks");
    }
    
    /**
     * ✅ BARU: Clear search dan tampilkan semua task
     */
    private void clearSearch() {
        if (txtSearch != null) {
            txtSearch.clear();
        }
        applyFilters();
        System.out.println("🔄 Search cleared");
    }
    
    /**
     * ✅ BARU: Apply filters (category + search)
     */
    private void applyFilters() {
        String selectedCategory = cbFilterCategory != null ? cbFilterCategory.getValue() : "All";
        String searchKeyword = txtSearch != null ? txtSearch.getText() : "";
        
        if (selectedCategory.equals("All") && searchKeyword.trim().isEmpty()) {
            // No filters, show all
            dataTasks.setAll(allTasks);
        } else if (!searchKeyword.trim().isEmpty()) {
            // Apply search (which already considers category filter)
            performSearch(searchKeyword);
        } else {
            // Only category filter
            filterTasksByCategory(selectedCategory);
        }
        
        updateTaskCounter();
    }
    
    /**
     * ✅ BARU: Load CSS styling untuk table modern
     */
    private void loadModernStyling() {
        try {
            String css = getClass().getResource("/com/aplikasi/css/Table.css").toExternalForm();
            tvTasks.getStylesheets().add(css);
        } catch (Exception e) {
            System.out.println("⚠️ CSS file not found, using inline styles");
        }
    }
    
    /**
     * ✅ MODERN: Custom cell factory untuk kategori dengan badge
     */
    private void setupModernCategoryColumn() {
        colCategory.setCellFactory(column -> {
            return new TableCell<Tasks, String>() {
                @Override
                protected void updateItem(String category, boolean empty) {
                    super.updateItem(category, empty);
                    
                    if (empty || category == null) {
                        setText(null);
                        setGraphic(null);
                        setStyle("");
                    } else {
                        Tasks task = getTableView().getItems().get(getIndex());
                        
                        // Create badge label
                        Label badge = new Label(task.getCategoryIcon() + " " + category);
                        badge.setStyle(
                            "-fx-background-color: " + task.getCategoryColor() + ";" +
                            "-fx-text-fill: white;" +
                            "-fx-background-radius: 15;" +
                            "-fx-padding: 5 12 5 12;" +
                            "-fx-font-size: 11px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 4, 0, 0, 1);"
                        );
                        
                        // Center the badge
                        HBox container = new HBox(badge);
                        container.setAlignment(Pos.CENTER);
                        
                        setText(null);
                        setGraphic(container);
                        setStyle("-fx-alignment: CENTER;");
                    }
                }
            };
        });
    }
    
    /**
     * ✅ MODERN: Custom cell factory untuk deadline dengan color coding
     */
    private void setupModernDeadlineColumn() {
        colDeadline.setCellFactory(column -> {
            return new TableCell<Tasks, LocalDate>() {
                @Override
                protected void updateItem(LocalDate deadline, boolean empty) {
                    super.updateItem(deadline, empty);
                    
                    if (empty || deadline == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        Tasks task = getTableView().getItems().get(getIndex());
                        setText(deadline.toString());
                        
                        // Apply color based on urgency
                        long daysUntil = ChronoUnit.DAYS.between(LocalDate.now(), deadline);
                        
                        if (task.isCompleted()) {
                            setStyle("-fx-text-fill: #6EACDA; -fx-alignment: CENTER;");
                        } else if (daysUntil < 0) {
                            // Overdue - Red
                            setStyle("-fx-text-fill: #FF6B6B; -fx-font-weight: bold; -fx-alignment: CENTER;");
                        } else if (daysUntil == 0) {
                            // Today - Orange
                            setStyle("-fx-text-fill: #FFA500; -fx-font-weight: bold; -fx-alignment: CENTER;");
                        } else if (daysUntil <= 3) {
                            // Soon - Yellow
                            setStyle("-fx-text-fill: #FFD700; -fx-alignment: CENTER;");
                        } else if (daysUntil <= 7) {
                            // This week - Green
                            setStyle("-fx-text-fill: #50C878; -fx-alignment: CENTER;");
                        } else {
                            // Future - Default
                            setStyle("-fx-text-fill: #E2E2B6; -fx-alignment: CENTER;");
                        }
                    }
                }
            };
        });
    }
    
    /**
     * ✅ MODERN: Custom cell factory untuk description dengan truncate
     */
    private void setupModernDescriptionColumn() {
        colDescription.setCellFactory(column -> {
            return new TableCell<Tasks, String>() {
                @Override
                protected void updateItem(String description, boolean empty) {
                    super.updateItem(description, empty);
                    
                    if (empty || description == null) {
                        setText(null);
                        setTooltip(null);
                    } else {
                        // Truncate long descriptions
                        String displayText = description.length() > 50 
                            ? description.substring(0, 47) + "..." 
                            : description;
                        setText(displayText);
                        
                        // Add tooltip for full text
                        if (description.length() > 50) {
                            javafx.scene.control.Tooltip tooltip = new javafx.scene.control.Tooltip(description);
                            tooltip.setWrapText(true);
                            tooltip.setMaxWidth(300);
                            setTooltip(tooltip);
                        }
                        
                        setStyle("-fx-text-fill: #E2E2B6; -fx-alignment: CENTER-LEFT;");
                    }
                }
            };
        });
    }
    
    /**
     * ✅ MODERN: Setup action column dengan styled buttons
     */
    private void setupModernActionColumn() {
        Callback<TableColumn<Tasks, Void>, TableCell<Tasks, Void>> cellFactory = param -> {
            return new TableCell<>() {
                private final Button btnEdit = new Button("✏️ Edit");
                private final Button btnDelete = new Button("🗑️ Delete");
                private final HBox container = new HBox(8, btnEdit, btnDelete);

                {
                    // Modern Edit Button Style
                    btnEdit.setStyle(
                        "-fx-background-color: linear-gradient(to bottom, #6EACDA 0%, #5090c0 100%);" +
                        "-fx-text-fill: #021526;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;" +
                        "-fx-font-size: 11px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 6 12 6 12;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 3, 0, 0, 1);"
                    );
                    
                    // Modern Delete Button Style
                    btnDelete.setStyle(
                        "-fx-background-color: linear-gradient(to bottom, #FF6B6B 0%, #E05555 100%);" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;" +
                        "-fx-font-size: 11px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 6 12 6 12;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 3, 0, 0, 1);"
                    );
                    
                    // Hover effects
                    btnEdit.setOnMouseEntered(e -> {
                        btnEdit.setStyle(btnEdit.getStyle() + 
                            "-fx-scale-x: 1.05; -fx-scale-y: 1.05;");
                    });
                    btnEdit.setOnMouseExited(e -> {
                        btnEdit.setStyle(btnEdit.getStyle().replace(
                            "-fx-scale-x: 1.05; -fx-scale-y: 1.05;", ""));
                    });
                    
                    btnDelete.setOnMouseEntered(e -> {
                        btnDelete.setStyle(btnDelete.getStyle() + 
                            "-fx-scale-x: 1.05; -fx-scale-y: 1.05;");
                    });
                    btnDelete.setOnMouseExited(e -> {
                        btnDelete.setStyle(btnDelete.getStyle().replace(
                            "-fx-scale-x: 1.05; -fx-scale-y: 1.05;", ""));
                    });

                    btnEdit.setOnAction(event -> {
                        Tasks task = getTableView().getItems().get(getIndex());
                        if (task != null) {
                            currentTask = task;  // Set currentTask saat edit
                            openEditWindow(task);
                        }
                    });

                    btnDelete.setOnAction(event -> {
                        Tasks task = getTableView().getItems().get(getIndex());
                        if (task != null) {
                            currentTask = task;  // Set currentTask saat delete
                            deleteTask(task);
                        }
                    });

                    container.setAlignment(Pos.CENTER);
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(null);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(container);
                    }
                }
            };
        };

        colEditDelete.setCellFactory(cellFactory);
    }
    
    /**
     * Setup ComboBox untuk filter kategori
     */
    private void setupCategoryFilter() {
        if (cbFilterCategory != null) {
            cbFilterCategory.getItems().addAll("All", "Academic", "Project", "Development");
            cbFilterCategory.setValue("All");
            
            cbFilterCategory.setOnAction(event -> {
                applyFilters(); // ✅ UPDATE: Use applyFilters instead
            });
        }
    }
    
    /**
     * Update task counter
     */
    private void updateTaskCounter() {
        if (lblTaskCount != null) {
            int total = dataTasks.size();
            long completed = dataTasks.stream().filter(Tasks::isCompleted).count();
            lblTaskCount.setText(completed + "/" + total + " completed");
        }
    }
    
    /**
     * Filter tasks berdasarkan kategori
     */
    private void filterTasksByCategory(String category) {
        if (category == null || category.equals("All")) {
            dataTasks.setAll(allTasks);
        } else {
            ObservableList<Tasks> filtered = FXCollections.observableArrayList();
            for (Tasks task : allTasks) {
                if (task.getCategory().equals(category)) {
                    filtered.add(task);
                }
            }
            dataTasks.setAll(filtered);
        }
        tvTasks.refresh();
        updateTaskCounter();
    }
    
    /**
     * PENTING: Method ini dipanggil dari MainMenuController
     */
    public void initForUser(User user) {
        this.currentUser = user;
        refreshTableView();
    }
    
    /**
     * Buka window untuk edit task
     */
    private void openEditWindow(Tasks task) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/aplikasi/view/EditTasks.fxml"));
            Stage stage = (Stage) tvTasks.getScene().getWindow();
            
            loader.load();
            
            EditTasksController controller = loader.getController();
            controller.initForUser(currentUser);
            controller.setTask(task);
            controller.setParentController(this);
            
            stage.getScene().setRoot(loader.getRoot());
            
        } catch (IOException e) {
            System.err.println("Gagal memuat EditTasks.fxml: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal membuka window edit");
        }
    }
    
    /**
     * Hapus task dari database dan tabel
     */
    private void deleteTask(Tasks task) {
        try {
            TasksDAO.removeEntry(task);
            dataTasks.remove(task);
            allTasks.remove(task);
            currentTask = null;  // Clear currentTask setelah delete
            updateTaskCounter();
            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Tugas berhasil dihapus");
        } catch (SQLException e) { 
            System.err.println("Database Error saat menghapus: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Gagal menghapus tugas dari database.");
        }
    }

    @FXML
    private void handleAddTask(ActionEvent event) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/aplikasi/view/AddTask.fxml"));
            Stage stage = (Stage) btnGoToAdd.getScene().getWindow();
            
            loader.load();
            
            AddTaskController controller = loader.getController();
            controller.initForUser(currentUser);
            controller.setParentController(this);
            
            stage.getScene().setRoot(loader.getRoot());
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal membuka halaman Add Task");
        }
    }
   
    /**
     * Refresh tabel dengan data terbaru
     */
    public void refreshTableView() {
        if (currentUser == null) {
            System.err.println("❌ ManageTask: currentUser is null!");
            return;
        }
        
        dataTasks.clear();
        allTasks.clear();
        currentTask = null;  // Clear currentTask saat refresh
        
        try {
            allTasks.addAll(TasksDAO.getAllTasksByUser(currentUser.getUser_id()));
            
            // Apply current filters (category + search)
            applyFilters();
            
            System.out.println("✔ ManageTask: Loaded " + allTasks.size() + " tasks for user " + currentUser.getUsername());
        } catch (SQLException e) {
            System.err.println("Gagal memuat data tugas dari database: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Database Error", "Gagal memuat daftar tugas.");
        }
        tvTasks.refresh();
    }
    
    /**
     * Tambah task ke tabel dan refresh
     */
    public void addTaskAndRefresh(Tasks task) {
        if (task != null) {
            allTasks.add(task);
            
            // Apply current filters
            applyFilters();
            
            tvTasks.refresh();
        }
    }

    @FXML
    private void handleGoToTimer(ActionEvent event) throws IOException {
        navigateWithUser("/com/aplikasi/view/Timer.fxml", btnGoToTimer);
    }

    @FXML
    private void handleGoToManageTask(ActionEvent event) throws IOException {
        System.out.println("Already in Task Management");
    }

    @FXML
    private void handleMissions(ActionEvent event) throws IOException {
        navigateWithUser("/com/aplikasi/view/MissionsView.fxml", btnMissions);
    }
    
     @FXML
    private void handleReport(ActionEvent event) throws IOException {
        navigateWithUser("/com/aplikasi/view/Report.fxml", btnReport);
    }
    
    @FXML
    private void handleBackToMenu(ActionEvent event) {
        try {
            URL fxmlLocation = getClass().getResource("/com/aplikasi/view/MenuUtama.fxml");
            
            if (fxmlLocation == null) {
                System.err.println("❌ FXML Error: MenuUtama.fxml tidak ditemukan!");
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load(); 
            
            Object controller = loader.getController();
            if (controller != null) {
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

    private void navigateWithUser(String fxmlPath, Button sourceButton) {
    try {
        URL resource = getClass().getResource(fxmlPath);
        if (resource == null) {
            System.err.println("❌ FXML tidak ditemukan: " + fxmlPath);
            return;
        }

        FXMLLoader loader = new FXMLLoader(resource);
        Parent root = loader.load();

        Object controller = loader.getController();

        if (controller instanceof TimerController) {
            ((TimerController) controller).initForUser(currentUser);

        }  else if (controller instanceof ReportController) {   // 👈 tambahkan ini
            ((ReportController) controller).initForUser(currentUser);
        } else if (controller instanceof MissionsController) {
            ((MissionsController) controller).initForUser(currentUser);

        }

        Stage stage = (Stage) sourceButton.getScene().getWindow();
        stage.getScene().setRoot(root);

    } catch (IOException e) {
        e.printStackTrace();
        showAlert(Alert.AlertType.ERROR, "Error",
                "Gagal memuat halaman: " + fxmlPath);
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
