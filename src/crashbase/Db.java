package crashbase;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

import java.io.Closeable;

public interface Db extends Closeable {

    void close();


    @SqlUpdate("INSERT OR IGNORE INTO app (name) VALUES (:name)")
    @GetGeneratedKeys
    Long insertOrIgnoreApp(@Bind("name") String name);

    @SqlQuery("SELECT id FROM app WHERE name = :name")
    Long findAppId(@Bind("name") String name);

    @SqlUpdate("INSERT INTO dump (app_id) VALUES (:appId)")
    @GetGeneratedKeys
    long insertDump(long appId);
}
