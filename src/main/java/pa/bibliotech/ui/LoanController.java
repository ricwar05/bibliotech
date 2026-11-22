package pa.bibliotech.ui;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import pa.bibliotech.domain.Book;
import pa.bibliotech.domain.Loan;
import pa.bibliotech.domain.Member;
import pa.bibliotech.exception.DataAccessException;
import pa.bibliotech.exception.DomainException;
import pa.bibliotech.service.BookService;
import pa.bibliotech.service.LoanService;
import pa.bibliotech.service.MemberService;

public class LoanController {
    @FXML
    private ComboBox<Book> cmbBook;
    @FXML
    private ComboBox<Member> cmbMember;
    @FXML
    private TableView<Loan> tblLoans;
    @FXML
    private TableColumn<Loan, Long> colId;
    @FXML
    private TableColumn<Loan, String> colBook;
    @FXML
    private TableColumn<Loan, String> colMember;
    @FXML
    private TableColumn<Loan, LocalDate> colLoanDate;
    @FXML
    private TableColumn<Loan, LocalDate> colDueDate;
    @FXML
    private TextField txtSearch;
    @FXML
    private Label lblMessage;

    private final LoanService loanService;
    private final BookService bookService;
    private final MemberService memberService;
    private final Runnable onBack;
    private final Map<Long, String> bookTitles = new HashMap<>();
    private final Map<Long, String> memberNames = new HashMap<>();

    private final ObservableList<Loan> loans = FXCollections.observableArrayList();
    private final FilteredList<Loan> filteredLoans = new FilteredList<>(loans, loan -> true);

    public LoanController(LoanService loanService, BookService bookService, MemberService memberService,
            Runnable onBack) {
        this.loanService = loanService;
        this.bookService = bookService;
        this.memberService = memberService;
        this.onBack = onBack;
    }

    @FXML
    private void initialize() {
        cmbBook.setConverter(new StringConverter<>() {
            @Override
            public String toString(Book book) {
                return book == null ? "" : book.getTitle() + " (" + book.getAvailableCopies() + " disponibles)";
            }

            @Override
            public Book fromString(String text) {
                return null;
            }
        });
        cmbMember.setConverter(new StringConverter<>() {
            @Override
            public String toString(Member member) {
                return member == null ? "" : member.getFullName() + " · " + member.getDocumentId();
            }

            @Override
            public Member fromString(String text) {
                return null;
            }
        });
        colId.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getId()));
        colBook.setCellValueFactory(c -> new SimpleStringProperty(bookTitles.get(c.getValue().getBookId())));
        colMember.setCellValueFactory(c -> new SimpleStringProperty(memberNames.get(c.getValue().getMemberId())));
        colLoanDate.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getLoanDate()));
        colDueDate.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getDueDate()));

        tblLoans.setItems(filteredLoans);
        txtSearch.textProperty().addListener((obs, oldText, text) -> applyFilter());
        run(this::refresh);
    }

    private void applyFilter() {
        String needle = txtSearch.getText() == null ? "" : txtSearch.getText().trim().toLowerCase();
        filteredLoans.setPredicate(loan -> needle.isEmpty()
                || bookTitles.getOrDefault(loan.getBookId(), "").toLowerCase().contains(needle)
                || memberNames.getOrDefault(loan.getMemberId(), "").toLowerCase().contains(needle));
    }

    @FXML
    private void onLend() {
        Book book = cmbBook.getValue();
        Member member = cmbMember.getValue();
        if (book == null || member == null) {
            showError("Elija un libro y un socio");
            return;
        }
        run(() -> {
            Loan loan = loanService.registerLoan(book.getId(), member.getId());
            refresh();
            showOk("Préstamo registrado. Vence el " + loan.getDueDate());
        });
    }

    @FXML
    private void onReturn() {
        Loan selected = tblLoans.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Seleccione un préstamo de la tabla");
            return;
        }
        run(() -> {
            BigDecimal fine = loanService.returnLoan(selected.getId(), LocalDate.now());
            refresh();
            if (fine.signum() == 0) {
                showOk("Libro devuelto a tiempo, sin multa");
            } else {
                showOk("Libro devuelto con multa de B/. " + fine);
            }
        });
    }

    @FXML
    private void onBack() {
        onBack.run();
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
        bookTitles.clear();
        memberNames.clear();
        for (Book book : bookService.listAll()) {
            bookTitles.put(book.getId(), book.getTitle());
        }
        for (Member member : memberService.listAll()) {
            memberNames.put(member.getId(), member.getFullName());
        }
        cmbBook.setItems(FXCollections.observableArrayList(
                bookService.listAll().stream().filter(Book::isAvailable).toList()));
        cmbMember.setItems(FXCollections.observableArrayList(
                memberService.listAll().stream().filter(Member::isActive).toList()));
        loans.setAll(loanService.listActive());
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