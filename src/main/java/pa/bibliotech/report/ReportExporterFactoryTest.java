package pa.bibliotech.report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ReportExporterFactoryTest {

    @Test
    void creaElExportadorSegunElFormato() {
        assertInstanceOf(CsvReportExporter.class,
                ReportExporterFactory.create(ReportFormat.CSV));
        assertInstanceOf(PdfReportExporter.class,
                ReportExporterFactory.create(ReportFormat.PDF));
    }

    @Test
    void exportaCsvConPuntoYComa(@TempDir Path dir) throws Exception {
        Path file = dir.resolve("libros.csv");
        ReportExporterFactory.create(ReportFormat.CSV).export(
                List.of(new String[] { "isbn", "titulo" }, new String[] { "9789962000011",
                        "Ficciones" }),
                file);
        assertEquals(List.of("isbn;titulo", "9789962000011;Ficciones"),
                Files.readAllLines(file));
    }
}