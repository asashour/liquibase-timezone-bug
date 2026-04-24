package com.example.liquibase;

import liquibase.command.CommandScope;
import liquibase.command.core.DiffChangelogCommandStep;
import liquibase.command.core.helpers.DbUrlConnectionArgumentsCommandStep;
import liquibase.command.core.helpers.ReferenceDbUrlConnectionCommandStep;
import liquibase.database.DatabaseFactory;
import liquibase.database.core.OracleDatabase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.datatype.DataTypeFactory;
import liquibase.exception.DatabaseException;
import liquibase.structure.core.DataType;
import org.junit.jupiter.api.Test;

import java.sql.DriverManager;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertTrue;

class OracleTest {

    @Test
    void test() throws Exception {
        //reference: CREATE TABLE telegram (message_time timestamp with time zone);
        //target:    CREATE TABLE telegram (message_time varchar(200));
        try (var refConn = DriverManager.getConnection(
                "jdbc:oracle:thin:@//localhost:1521/db", "liqr", "liqr");
             var targetConn = DriverManager.getConnection(
                     "jdbc:oracle:thin:@//localhsot:1521/cb", "liqt", "liqt")) {

            var refDb = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(refConn));
            var targetDb = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(targetConn));

            var changeLog = "abc.yaml";
            new CommandScope(DiffChangelogCommandStep.COMMAND_NAME)
                    .addArgumentValue(ReferenceDbUrlConnectionCommandStep.REFERENCE_DATABASE_ARG, refDb)
                    .addArgumentValue(DbUrlConnectionArgumentsCommandStep.DATABASE_ARG, targetDb)
                    .addArgumentValue(DiffChangelogCommandStep.CHANGELOG_FILE_ARG, changeLog)

                    .execute();

            // output is "timestamp(6)"
        }
    }

    @Test
    void factory() throws DatabaseException {
        try (var db = new OracleDatabase()) {
            var type = new DataType("TIMESTAMP(6) WITH TIME ZONE");
            type.setColumnSize(13);
            var output = DataTypeFactory.getInstance().from(type, db).toString();
            assertTrue(output.toLowerCase(Locale.ROOT).contains("zone"));
        }
    }
}
