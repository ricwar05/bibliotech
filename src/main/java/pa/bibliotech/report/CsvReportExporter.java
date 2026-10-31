package pa.bibliotech.report;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class CsvReportExporter implements ReportExporter {
    @Override
    public void export(List<String[]> rows, Path destination) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(destination, StandardCharsets.UTF_8)) {
            for (String[] row : rows) {
                writer.write(String.join(";", row));
                writer.newLine();
            }
        }
    }
}