package pa.bibliotech.report;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class CsvReportExporter implements ReportExporter {
    private static final char UTF8_BOM = '\uFEFF';

    @Override
    public void export(List<String[]> rows, Path destination) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(destination, StandardCharsets.UTF_8)) {
            writer.write(UTF8_BOM);
            for (String[] row : rows) {
                writer.write(String.join(";", row));
                writer.newLine();
            }
        }
    }
}