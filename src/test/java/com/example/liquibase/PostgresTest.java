package com.example.liquibase;

import liquibase.command.CommandScope;
import liquibase.command.core.DiffChangelogCommandStep;
import liquibase.command.core.helpers.*;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import org.junit.jupiter.api.Test;

import java.sql.DriverManager;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PostgresTest {

    @Test
    void test() throws Exception {
        //reference: CREATE TABLE telegram (message_time timestamp with time zone);
        //target:    CREATE TABLE telegram (message_time varchar);
        try (var refConn = DriverManager.getConnection(
                "jdbc:postgresql://localhost/r", "liq", "liq");
             var targetConn = DriverManager.getConnection(
                     "jdbc:postgresql://localhost/t", "liq", "liq")) {

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
        }
    }
}
