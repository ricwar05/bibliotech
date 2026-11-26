package pa.bibliotech.report;

public enum ReportType {
    TOP_BORROWED_BOOKS("Libros más prestados", "libros-mas-prestados.csv"),
    OVERDUE_LOANS("Préstamos vencidos", "prestamos-vencidos.csv"),
    MEMBER_FINES("Socios con multas", "socios-con-multas.csv");

    private final String title;
    private final String fileName;

    ReportType(String title, String fileName) {
        this.title = title;
        this.fileName = fileName;
    }

    public String getTitle() {
        return title;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public String toString() {
        return title;
    }
}