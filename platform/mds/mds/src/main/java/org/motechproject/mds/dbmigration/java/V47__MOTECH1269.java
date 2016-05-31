package org.motechproject.mds.dbmigration.java;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Migrates old history to new history.
 */
public class V47__MOTECH1269 { // NO CHECKSTYLE Bad format of member name

    private static final Logger LOGGER = LoggerFactory.getLogger(V47__MOTECH1269.class);
    private static final String MYSQL_ID_TYPE = "bigint(20)";

    public void migrate(JdbcTemplate jdbcTemplate) throws SQLException {
        // for each history table
        for (String table : getHistoryTables(jdbcTemplate)) {
            // for each Foreign Key to a different history table
            for (HistoryFk fk : getHistoryFksToMigrate(table, jdbcTemplate)) {
                migrateHistoryFk(fk, jdbcTemplate);
            }
        }
    }

    private List<String> getHistoryTables(JdbcTemplate jdbc) throws SQLException {
        Connection connection = jdbc.getDataSource().getConnection();
        DatabaseMetaData dbmd = connection.getMetaData();

        ResultSet tableRs = dbmd.getTables(connection.getCatalog(), null, "%__history", null);
        List<String> tables = new ArrayList<>();

        while (tableRs.next()) {
            tables.add(tableRs.getString(3));
        }

        return tables;
    }

    private List<HistoryFk> getHistoryFksToMigrate(String historyTable, JdbcTemplate jdbc) throws SQLException {
        Connection connection = jdbc.getDataSource().getConnection();
        DatabaseMetaData dbmd = connection.getMetaData();

        ResultSet foreignKeys = dbmd.getImportedKeys(connection.getCatalog(), null, historyTable);

        List<HistoryFk> keys = new ArrayList<>();
        while (foreignKeys.next()) {
            String constrainName = foreignKeys.getString("FK_NAME");
            String pkTableName = foreignKeys.getString("PKTABLE_NAME");
            String fkColumnName = foreignKeys.getString("FKCOLUMN_NAME");

            if (pkTableName.endsWith("__history") && fkColumnName.endsWith("id_OID")) {
                String newColumn = fkColumnName.replace("id_OID", "id");

                boolean newColExists = columnExists(foreignKeys.getMetaData(), newColumn);

                HistoryFk historyFk = new HistoryFk(historyTable, pkTableName, fkColumnName,
                        newColumn, newColExists, constrainName);
                keys.add(historyFk);
            }
        }

        return keys;
    }

    private boolean columnExists(ResultSetMetaData rsmd, String columnName) throws SQLException {
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            if (columnName.equals(rsmd.getColumnName(i))) {
                return true;
            }
        }
        return false;
    }

    private void migrateHistoryFk(HistoryFk historyFk, JdbcTemplate jdbc) {
        if (!historyFk.newColumnExists) {
            LOGGER.debug("Adding column {} to {}", historyFk.newColumn, historyFk.table);

            jdbc.execute(String.format("ALTER TABLE %s ADD COLUMN %s %s",
                    historyFk.table, historyFk.newColumn, MYSQL_ID_TYPE));
        }

        LOGGER.debug("Migrating history field. Table: {}, old column: {}, new column: {}, related table: {}",
                historyFk.table, historyFk.oldColumn, historyFk.newColumn, historyFk.relatedTable);

        final String query = String.format("UPDATE %s SET %s = (SELECT id from %s WHERE id = %s.%s)",
                historyFk.table, historyFk.newColumn, historyFk.relatedTable, historyFk.table, historyFk.oldColumn);
        LOGGER.debug("Executing update query: {}", query);

        jdbc.update(query);

        LOGGER.debug("Dropping constraint {} from {}", historyFk.constraintName, historyFk.table);

        jdbc.execute(String.format("ALTER TABLE %s DROP FOREIGN KEY %s", historyFk.table, historyFk.constraintName));

        LOGGER.debug("Dropping column {} from {}", historyFk.oldColumn, historyFk.table);

        jdbc.execute(String.format("ALTER TABLE %s DROP COLUMN %s", historyFk.table, historyFk.oldColumn));
    }

    private class HistoryFk {
        private String constraintName;
        private String table;
        private String relatedTable;
        private String oldColumn;
        private String newColumn;
        private boolean newColumnExists;

        public HistoryFk(String table, String relatedTable, String oldColumn, String newColumn,
                         boolean newColumnExists, String constraintName) {
            this.table = table;
            this.relatedTable = relatedTable;
            this.oldColumn = oldColumn;
            this.newColumn = newColumn;
            this.newColumnExists = newColumnExists;
            this.constraintName = constraintName;
        }
    }
}