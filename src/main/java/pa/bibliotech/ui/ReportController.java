package pa.bibliotech.ui;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import pa.bibliotech.exception.DataAccessException;
import pa.bibliotech.exception.DomainException;
import pa.bibliotech.report.ReportExporter;
import pa.bibliotech.report.ReportExporterFactory;
import pa.bibliotech.report.ReportFormat;
import pa.bibliotech.report.ReportType;
import pa.bibliotech.service.ReportService;

public class ReportController {
    private static final Logger LOGGER = Logger.getLogger(ReportController.class.getName());

    @FXML private ComboBox<ReportType> cmbReport;
    @FXML private TableView<String[]> tblReport;
    @FXML private Label lblMessage;

    private final ReportService reportService;
    private final Runnable onBack;
    private List<String[]> currentRows = List.of();
    private ReportType currentType;

    public ReportController(ReportService reportService, Runnable onBack) {
        this.reportService = reportService;
        this.onBack = onBack;
    }

        @FXML
    private void initialize() {
        cmbReport.setItems(FXCollections.observableArrayList(ReportType.values()));
        cmbReport.getSelectionModel().selectFirst();
        tblReport.setPlaceholder(new Label("Elija un reporte y presione Generar"));
    }

    @FXML
    private void onGenerate() {
        ReportType type = cmbReport.getValue();
        if (type == null) {
            showError("Elija un reporte");
            return;
        }
        lblMessage.setText("");
        try {
            currentRows = reportService.buildRows(type, LocalDate.now());
            currentType = type;
            showTable(currentRows);
            int records = currentRows.size() - 1;
            if (records == 0) {
                showOk("El reporte no tiene datos por ahora");
            } else {
                showOk(type.getTitle() + ": " + records + " registros");
            }
        } catch (DataAccessException e) {
            showError("Error con la base de datos. Intente de nuevo.");
        } catch (DomainException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void onExport() {
        if (currentRows.isEmpty()) {
            showError("Primero genere un reporte");
            return;
        }
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Guardar reporte");
        chooser.setInitialFileName(currentType.getFileName());
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo CSV", "*.csv"));
        File file = chooser.showSaveDialog(tblReport.getScene().getWindow());
        if (file == null) {
            showError("Exportación cancelada");
            return;
        }
        try {
            ReportExporter exporter = ReportExporterFactory.create(ReportFormat.CSV);
            exporter.export(currentRows, file.toPath());
            showOk("Reporte guardado en " + file.getName());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "No se pudo guardar el reporte", e);
            showError("No se pudo guardar el archivo. Revise la carpeta elegida.");
        }
    }

    @FXML
    private void onBack() {
        onBack.run();
    }

    private void showTable(List<String[]> rows) {
        tblReport.getColumns().clear();
        String[] header = rows.get(0);
        for (int i = 0; i < header.length; i++) {
            int index = i;
            TableColumn<String[], String> column = new TableColumn<>(header[i]);
            column.setCellValueFactory(c -> new SimpleStringProperty(c.getValue()[index]));
            column.setPrefWidth(180);
            tblReport.getColumns().add(column);
        }
        tblReport.setItems(FXCollections.observableArrayList(rows.subList(1, rows.size())));
    }

    private void showOk(String text) {
        lblMessage.setStyle("-fx-text-fill: #2f7d4a;");
        lblMessage.setText(text);
    }

    private void showError(String text) {
        lblMessage.setStyle("-fx-text-fill: #b7412f;");
        lblMessage.setText(text);
    }
}