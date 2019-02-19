package de.sgpggb.surveys.util;

import de.sgpggb.pluginutilitieslibbungee.Logging;
import de.sgpggb.pluginutilitieslibbungee.sql.DBMigration;
import de.sgpggb.pluginutilitieslibbungee.sql.SQLConnection;
import de.sgpggb.surveys.SurveysPlugin;
import java.sql.SQLException;
import java.util.List;

public class DBMigrationSurveys extends DBMigration {

    public DBMigrationSurveys(SQLConnection sqlmanager, Logging logger) {
        super(sqlmanager, logger);
    }

    @Override
    protected int detectVersion() {
        SurveysConfig config = SurveysPlugin.getInstance().getConfig();
        int version = 0;

        List<String> tables;
        try {
            tables = this.getTables();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
        if (tables.contains(config.database_mysql_table_useranswers)) {
            version = Math.max(version, 1);
        }

        return version;
    }

    @Override
    protected boolean migrateFrom(int fromVersion) {
        switch (fromVersion) {
            case 0:
                if (!createDBV1()) {
                    return false;
                }
            // fallthrough
            default:
                // nothing to do
                return true;
        }
    }

    private boolean createDBV1() {
        SurveysConfig config = SurveysPlugin.getInstance().getConfig();
        boolean res = false;
        this.logger.info("Creating database V1");

        res = query("CREATE TABLE IF NOT EXISTS `" + config.database_mysql_table_surveys + "` ("
                + "`id` 			int(32) 		NOT NULL AUTO_INCREMENT,"
                + "`uuidCreator` 	varchar(40) 	NOT NULL,"
                + "`nameCreator`        varchar(20)     NOT NULL,"
                + "`text`               text     	NOT NULL,"
                + "`timefrom` 		timestamp 		NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                + "`timeto` 		timestamp 		NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                + "PRIMARY KEY (`id`)"
                + ");");
        if (!res) {
            return false;
        }

        res = query("CREATE TABLE IF NOT EXISTS `" + config.database_mysql_table_answers + "` ("
                + "`id` 			int(32) 		NOT NULL AUTO_INCREMENT,"
                + "`surveyid` 			int(32) 	NOT NULL,"
                + "`answer` 			text 	NOT NULL,"
                + "PRIMARY KEY (`id`),"
                + "CONSTRAINT survey_answer_fk FOREIGN KEY (surveyid) REFERENCES " + config.database_mysql_table_surveys + "(id)"
                + ");");
        if (!res) {
            return false;
        }

        res = query("CREATE TABLE IF NOT EXISTS `" + config.database_mysql_table_useranswers + "` ("
                + "`id` 			int(32) 		NOT NULL AUTO_INCREMENT,"
                + "`surveyid` 			int(32) 	NOT NULL,"
                + "`answerid` 			int(32),"
                + "`uuid`                       varchar(40)     NOT NULL,"
                + "`name`                       varchar(20)     NOT NULL,"
                + "`timestamp`                  timestamp       NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                + "PRIMARY KEY (`id`),"
                + "CONSTRAINT survey_useranswer_fk FOREIGN KEY (surveyid) REFERENCES " + config.database_mysql_table_surveys + "(id),"
                + "CONSTRAINT answer_useranswer_fk FOREIGN KEY (answerid) REFERENCES " + config.database_mysql_table_answers + "(id),"
                + "CONSTRAINT answer_useranswer_unique UNIQUE (surveyid, uuid)"
                + ");");
        if (!res) {
            return false;
        }

        this.logger.debug("Creating database V1 DONE");
        return res;
    }

}
