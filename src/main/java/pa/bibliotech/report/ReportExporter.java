package pa.bibliotech.report;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface ReportExporter {
    void export(List<String[]> rows, Path destination) throws IOException;
}