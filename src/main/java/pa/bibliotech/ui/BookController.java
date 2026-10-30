package pa.bibliotech.ui;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import pa.bibliotech.domain.Book;
import pa.bibliotech.exception.BusinessRuleException;
import pa.bibliotech.exception.DataAccessException;
import pa.bibliotech.exception.DomainException;
import pa.bibliotech.service.BookService;


public class BookController {
    @FXML private TableView<Book> tblBooks;
    @FXML private TableColumn<Book, String> colIsbn;
    @FXML private TableColumn<Book, String> colTitle;
    @FXML private TableColumn<Book, String> colAuthor;
    @FXML private TableColumn<Book, Integer> colYear;
    @FXML private TableColumn<Book, Integer> colTotal;
    @FXML private TableColumn<Book, Integer> colAvailable;
    @FXML private TextField txtSearch;
    @FXML private TextField txtIsbn;
    @FXML private TextField txtTitle;
    @FXML private TextField txtAuthor;
    @FXML private TextField txtYear;
    @FXML private TextField txtCopies;
    @FXML private Label lblMessage;

    private final BookService bookService;
    private final Runnable onBack;        

    public BookController(BookService bookService, Runnable onBack) {
        this.bookService = bookService;
        this.onBack = onBack;
    }

    @FXML
    private void initialize() {
        colIsbn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getIsbn()));
        colTitle.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitle()));
        colAuthor.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAuthor()));
        colYear.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getPublicationYear()));
        colTotal.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getTotalCopies()));
        colAvailable.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getAvailableCopies()));
        tblBooks.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldBook, book) -> fillForm(book));
        run(this::refresh);
    }

    @FXML
    private void onSearch() {
        run(() -> tblBooks.setItems(FXCollections.observableArrayList(
                bookService.search(txtSearch.getText()))));
    }

    @FXML
    private void onNew() {
        tblBooks.getSelectionModel().clearSelection();
        clearForm();
    }

    @FXML
    private void onSave() {
        run(() -> {
            Book selected = tblBooks.getSelectionModel().getSelectedItem();
            int year = parseNumber(txtYear, "El año");
            int copies = parseNumber(txtCopies, "Los ejemplares");
            if (selected == null) {
                bookService.register(txtIsbn.getText(), txtTitle.getText(),
                        txtAuthor.getText(), year, copies);
                refresh();
                showOk("Libro registrado");
            } else {
                bookService.update(selected.getId(), txtTitle.getText(),
                        txtAuthor.getText(), year, copies);
                refresh();
                showOk("Libro actualizado");
            }
        });
    }

    @FXML
    private void onDelete() {
        Book selected = tblBooks.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Seleccione un libro de la tabla");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Eliminar \"" + selected.getTitle() + "\"?", ButtonType.YES, ButtonType.NO);
        if (confirm.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            run(() -> {
                bookService.delete(selected.getId());
                refresh();
                showOk("Libro eliminado");
            });
        }
    }

    @FXML
    private void onBack() {
        onBack.run();
    }

     
    private void run(Runnable action) {
        lblMessage.setText("");
        try {
            action.run();
        } catch (DomainException e) {                  
            showError(e.getMessage());
        } catch (DataAccessException e) {
            showError("Error con la base de datos. Intente de nuevo.");
        }
    }

    private void refresh() {
        tblBooks.setItems(FXCollections.observableArrayList(bookService.listAll()));
        clearForm();
    }

    private void fillForm(Book book) {
        if (book == null) {
            clearForm();
            return;
        }
        txtIsbn.setText(book.getIsbn());
        txtIsbn.setDisable(true);                      
        txtTitle.setText(book.getTitle());
        txtAuthor.setText(book.getAuthor());
        txtYear.setText(String.valueOf(book.getPublicationYear()));
        txtCopies.setText(String.valueOf(book.getTotalCopies()));
    }

    private void clearForm() {
        txtIsbn.clear();
        txtIsbn.setDisable(false);
        txtTitle.clear();
        txtAuthor.clear();
        txtYear.clear();
        txtCopies.clear();
    }

    private static int parseNumber(TextField field, String name) {
        try {
            return Integer.parseInt(field.getText().trim());
        } catch (NumberFormatException e) {
            throw new BusinessRuleException(name + " debe ser un número entero");
        }
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