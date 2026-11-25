package pa.bibliotech.ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import pa.bibliotech.domain.Member;
import pa.bibliotech.exception.DataAccessException;
import pa.bibliotech.exception.DomainException;
import pa.bibliotech.service.MemberService;

public class MemberController {
    @FXML
    private TableView<Member> tblMembers;
    @FXML
    private TableColumn<Member, String> colDocument;
    @FXML
    private TableColumn<Member, String> colName;
    @FXML
    private TableColumn<Member, String> colEmail;
    @FXML
    private TableColumn<Member, String> colPhone;
    @FXML
    private TableColumn<Member, String> colStatus;
    @FXML
    private TextField txtSearch;
    @FXML
    private TextField txtDocument;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtPhone;
    @FXML
    private CheckBox chkActive;
    @FXML
    private Label lblMessage;

    private final MemberService memberService;
    private final Runnable onBack;
    private final ObservableList<Member> masterData = FXCollections.observableArrayList();
    private final FilteredList<Member> filteredData = new FilteredList<>(masterData, p -> true);

    public MemberController(MemberService memberService, Runnable onBack) {
        this.memberService = memberService;
        this.onBack = onBack;
    }

    @FXML
    private void initialize() {
        colDocument.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDocumentId()));
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFullName()));
        colEmail.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));
        colPhone.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPhone()));
        colStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isActive() ? "Activo" : "Inactivo"));

        tblMembers.setItems(filteredData);
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> applyFilter(newVal));

        tblMembers.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldMember, member) -> fillForm(member));
        run(this::refresh);
    }

    @FXML
    private void onSearch() {
        applyFilter(txtSearch.getText());
    }

    @FXML
    private void onNew() {
        tblMembers.getSelectionModel().clearSelection();
        clearForm();
    }

    @FXML
    private void onSave() {
        run(() -> {
            Member selected = tblMembers.getSelectionModel().getSelectedItem();
            if (selected == null) {
                memberService.register(txtDocument.getText(), txtName.getText(),
                        txtEmail.getText(), txtPhone.getText());
                refresh();
                showOk("Socio registrado");
            } else {
                memberService.update(selected.getId(), txtName.getText(), txtEmail.getText(),
                        txtPhone.getText(), chkActive.isSelected());
                refresh();
                showOk("Socio actualizado");
            }
        });
    }

    @FXML
    private void onDelete() {
        Member selected = tblMembers.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Seleccione un socio de la tabla");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Eliminar al socio " + selected.getFullName() + "?", ButtonType.YES, ButtonType.NO);
        if (confirm.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            run(() -> {
                memberService.delete(selected.getId());
                refresh();
                showOk("Socio eliminado");
            });
        }
    }

    @FXML
    private void onBack() {
        onBack.run();
    }

    private void applyFilter(String query) {
        String filter = query == null ? "" : query.trim().toLowerCase();
        filteredData.setPredicate(member -> {
            if (member == null) {
                return false;
            }
            if (filter.isEmpty()) {
                return true;
            }
            String fullName = member.getFullName() != null ? member.getFullName().toLowerCase() : "";
            String documentId = member.getDocumentId() != null ? member.getDocumentId().toLowerCase() : "";
            String email = member.getEmail() != null ? member.getEmail().toLowerCase() : "";
            return fullName.contains(filter)
                    || documentId.contains(filter)
                    || email.contains(filter);
        });
    }

    private void run(Runnable action) {
        lblMessage.setText("");
        try {
            action.run();
        } catch (DataAccessException e) {
            showError("Error con la base de datos. Intente de nuevo.");
        } catch (DomainException e) {
            showError(e.getMessage());
        }
    }

    private void refresh() {
        masterData.setAll(memberService.listAll());
        applyFilter(txtSearch.getText());
        clearForm();
    }

    private void fillForm(Member member) {
        if (member == null) {
            clearForm();
            return;
        }
        txtDocument.setText(member.getDocumentId());
        txtDocument.setDisable(true);
        txtName.setText(member.getFullName());
        txtEmail.setText(member.getEmail() == null ? "" : member.getEmail());
        txtPhone.setText(member.getPhone() == null ? "" : member.getPhone());
        chkActive.setSelected(member.isActive());
        chkActive.setDisable(false);
    }

    private void clearForm() {
        txtDocument.clear();
        txtDocument.setDisable(false);
        txtName.clear();
        txtEmail.clear();
        txtPhone.clear();
        chkActive.setSelected(true);
        chkActive.setDisable(true);
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