package pa.bibliotech.report;

public final class ReportExporterFactory {
    private ReportExporterFactory() {
    }

    public static ReportExporter create(ReportFormat format) {
        return switch (format) {
            case CSV -> new CsvReportExporter();
            case PDF -> new PdfReportExporter();
        };
    }
}