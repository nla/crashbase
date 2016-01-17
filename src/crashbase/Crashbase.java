package crashbase;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.skife.jdbi.v2.DBI;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class Crashbase {

    private final Path dumpsDir;
    private final DBI dbi;

    public Crashbase(Path dataDir) throws SQLException, IOException {
        this.dumpsDir = dataDir.resolve("dumps");
        if (!Files.exists(dumpsDir)) {
            Files.createDirectories(dumpsDir);
        }

        String jdbcUrl = "jdbc:sqlite:" + dataDir.resolve("crashbase.db");
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setMinimumIdle(1);
        this.dbi = new DBI(new HikariDataSource(config));
    }

    private Db takeDb() {
        return dbi.open(Db.class);
    }

    public long storeDump(String app, InputStream in) throws IOException {
        Path tmpFile = Files.createTempFile(dumpsDir, "incoming", ".tmp");
        try (Db db = takeDb()) {
            Files.copy(in, tmpFile, REPLACE_EXISTING);

            Long appId = db.insertOrIgnoreApp(app);
            if (appId == null) {
                appId = db.findAppId(app);
            }
            long dumpId = db.insertDump(appId);

            Path dir = dumpsDir.resolve(Long.toString(dumpId));
            Files.createDirectory(dir);
            Files.move(tmpFile, dir.resolve(dumpId + ".hprof"));

            return dumpId;
        } finally {
            Files.deleteIfExists(tmpFile);
        }
    }
}
