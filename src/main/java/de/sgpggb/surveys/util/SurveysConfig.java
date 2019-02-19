package de.sgpggb.surveys.util;

import de.sgpggb.pluginutilitieslibbungee.CustomConfig;
import de.sgpggb.pluginutilitieslibbungee.CustomJavaPlugin;
import net.cubespace.Yamler.Config.Comment;
import net.cubespace.Yamler.Config.Path;

public class SurveysConfig extends CustomConfig {

    public SurveysConfig(CustomJavaPlugin plugin) {
        super(plugin);
    }
    
    @Path("database.mysql.tables.surveys")
    public String database_mysql_table_surveys = "Surveys_Surveys";

    @Path("database.mysql.tables.answers")
    public String database_mysql_table_answers = "Surveys_Answers";

    @Path("database.mysql.tables.useranswers")
    public String database_mysql_table_useranswers = "Surveys_Useranswers";

}
