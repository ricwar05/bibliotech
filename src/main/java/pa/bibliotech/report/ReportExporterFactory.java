package pa.bibliotech.report;

public class ReportExporterFactory {
    public static ReportExporter create(ReportFormat format) {
        if (format == ReportFormat.CSV) {
            return new CsvReportExporter();
        } else if (format == ReportFormat.PDF) {
            return new PdfReportExporter();
        }
        throw new IllegalArgumentException("Formato no soportado: " + format);
    }
}