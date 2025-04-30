package de.sgpggb.surveys.db;


import de.sgpggb.pluginutilitieslibbungee.Logging;
import de.sgpggb.pluginutilitieslibbungee.sql.DBMigration;
import de.sgpggb.pluginutilitieslibbungee.sql.SQLConnection;
import de.sgpggb.pluginutilitieslibbungee.sql.adv.SQLColumn;
import de.sgpggb.pluginutilitieslibbungee.sql.adv.Table;
import de.sgpggb.pluginutilitieslibbungee.sql.adv.TableBuilder;
import de.sgpggb.pluginutilitieslibbungee.sql.adv.TableEngine;
import de.sgpggb.pluginutilitieslibbungee.sql.adv.constraints.PrimaryKey;

import java.sql.SQLException;
import java.util.List;

public class DBMigrationSurveys extends DBMigration {
    public DBMigrationSurveys(SQLConnection sqlmanager, Logging logger) {
        super(sqlmanager, logger);
        this.questions = "surveys_questions";
        this.answers = "surveys_answers";
        this.rewards = "surveys_rewards";
        this.groups = "surveys_groups";
    }

    private Table tableQuestions;
    private Table tableAnswers;
    private Table tableRewards;
    private Table tableGroups;
    private Table tableUsers;
    private String questions;
    private String answers;
    private String rewards;
    private String groups;
    private String users;

    @Override
    protected int detectVersion() {
        int version = 0;
        List<String> tables;
        try {
            tables = this.getTables();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
        List<String> columns;
        if (tables.contains(questions)) {
            version = Math.max(version, 1);
            try {
                columns = this.getColumns(questions);
            } catch (SQLException e) {
                e.printStackTrace();
                return -1;
            }
            /*if (columns.contains("column"))
                version = Math.max(version, 2);*/
        }

        return version;
    }

    @Override
    protected boolean migrateFrom(int fromVersion) {
        if (!createDBV1(fromVersion >= 1))
            return false;
        return true;
    }

    private boolean createDBV1(boolean execute) {
        if (!execute)
            logger.info("Creating database V1");

        tableQuestions = new TableBuilder(questions, TableEngine.INNODB)
                .column(new SQLColumn("id", "INT", 11, false, "AUTO_INCREMENT"))
                .column(new SQLColumn("text", "TEXT", 0, true, ""))
                .column(new SQLColumn("choices", "TEXT", 0, true, ""))
                .column(new SQLColumn("groupID", "INT", 11, true, ""))
                .column(new SQLColumn("nextID", "INT", 11, true, ""))
                .column(new SQLColumn("answerType", "VARCHAR", 64, true, ""))
                .constraint(new PrimaryKey("id"))
                .build();

        tableAnswers = new TableBuilder(answers, TableEngine.INNODB)
                .column(new SQLColumn("id", "INT", 11, false, "AUTO_INCREMENT"))
                .column(new SQLColumn("questionID", "INT", 11, false, ""))
                .column(new SQLColumn("uuid", "CHAR", 36, false, ""))
                .column(new SQLColumn("answer", "TEXT", 0, false, ""))
                .column(new SQLColumn("timestamp", "TIMESTAMP", 0, false, "DEFAULT CURRENT_TIMESTAMP"))
                .constraint(new PrimaryKey("id"))
                .build();

        tableRewards = new TableBuilder(rewards, TableEngine.INNODB)
                .column(new SQLColumn("id", "INT", 11, false, "AUTO_INCREMENT"))
                .column(new SQLColumn("claimText", "TEXT", 0, false, ""))
                .column(new SQLColumn("infoText", "TEXT", 0, false, ""))
                .column(new SQLColumn("reward", "VARCHAR", 64, false, ""))
                .column(new SQLColumn("rewardType", "VARCHAR", 64, false, ""))
                .constraint(new PrimaryKey("id"))
                .build();

        tableGroups = new TableBuilder(groups, TableEngine.INNODB)
                .column(new SQLColumn("id", "INT", 11, false, "AUTO_INCREMENT"))
                .column(new SQLColumn("name", "VARCHAR", 64, true, ""))
                .column(new SQLColumn("order", "INT", 11, true, ""))
                .column(new SQLColumn("permission", "VARCHAR", 64, true, ""))
                .column(new SQLColumn("questionIDs", "TEXT", 0, true, ""))
                .column(new SQLColumn("rewardID", "INT", 11, true, ""))
                .constraint(new PrimaryKey("id"))
                .build();

        tableUsers = new TableBuilder(users, TableEngine.INNODB)
            .column(new SQLColumn("id", "INT", 11, false, "AUTO_INCREMENT"))
            .column(new SQLColumn("uuid", "CHAR", 36, false, ""))
            .column(new SQLColumn("currentGroup", "INT", 11, true, ""))
            .column(new SQLColumn("currentQuestion", "INT", 11, true, ""))
            .constraint(new PrimaryKey("id"))
            .build();


        if (!execute) {
            tableQuestions.createTable(sqlmanager);
            tableAnswers.createTable(sqlmanager);
            tableRewards.createTable(sqlmanager);
            tableGroups.createTable(sqlmanager);
            tableUsers.createTable(sqlmanager);
        }

        if (!execute)
            logger.info("Creating database V1 DONE");
        return true;
    }

    public Table getTableQuestions() {
        return tableQuestions;
    }

    public Table getTableAnswers() {
        return tableAnswers;
    }

    public Table getTableRewards() {
        return tableRewards;
    }

    public Table getTableGroups() {
        return tableGroups;
    }

    public Table getTableUsers() {
        return tableUsers;
    }
}
