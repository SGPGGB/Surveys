package de.sgpggb.surveys;

import de.sgpggb.surveys.models.Answer;
import de.sgpggb.surveys.models.Survey;
import de.sgpggb.surveys.util.SurveysConfig;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DBController {

    private SurveysPlugin plugin;
    private Connection con;
    private SurveysConfig config;

    public DBController() {
        plugin = SurveysPlugin.getInstance();
        con = plugin.getSQLConnection().getConnection();
        config = plugin.getConfig();
    }

    public Map<Integer, Survey> loadSurveys() {
        Map<Integer, Survey> ret = new HashMap<>();
        PreparedStatement stmt = null;
        ResultSet res = null;
        try {
            //LOAD SURVEYS
            stmt = con.prepareStatement("SELECT * FROM " + config.database_mysql_table_surveys + " ORDER BY id");
            res = stmt.executeQuery();
            while (res.next()) {
                int id = res.getInt("id");
                UUID uuidCreator = UUID.fromString(res.getString("uuidCreator"));
                String nameCreator = res.getString("nameCreator");
                String text = res.getString("text");
                Timestamp timefrom = res.getTimestamp("timefrom");
                Timestamp timeto = res.getTimestamp("timeto");
                Survey s = new Survey(id, uuidCreator, nameCreator, text, timefrom, timeto, new HashMap<>());
                ret.put(id, s);
            }
            res.close();
            stmt.close();

            //LOAD ANSWERS
            stmt = con.prepareStatement("SELECT * FROM " + config.database_mysql_table_answers + " ORDER BY id");
            res = stmt.executeQuery();
            while (res.next()) {
                int id = res.getInt("id");
                int surveyid = res.getInt("surveyid");
                String text = res.getString("answer");
                Answer a = new Answer(id, text);
                ret.get(surveyid).getAnswers().put(id, a);
            }
            return ret;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        } finally {
            try {
                res.close();
            } catch (Exception ex) {
            }
            try {
                stmt.close();
            } catch (Exception ex) {
            }
        }
    }

    public void addUserAnswer(UUID uuid, String player, int surveyid, Integer answerid) {
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement("INSERT INTO " + config.database_mysql_table_useranswers
                    + "(surveyid, answerid, uuid, name) VALUES"
                    + "(?,?,?,?)");
            stmt.setInt(1, surveyid);
            if (answerid == null) {
                stmt.setNull(2, Types.INTEGER);
            } else {
                stmt.setInt(2, answerid);
            }
            stmt.setString(3, uuid.toString());
            stmt.setString(4, player);
            stmt.execute();
        } catch (SQLException ex) {
            if (ex.getMessage().startsWith("Duplicate entry")) {
                throw new RuntimeException("Du hast bereits abgestimmt");
            }
            throw new RuntimeException(ex);
        } finally {
            try {
                stmt.close();
            } catch (Exception ex) {
            }
        }
    }

    public boolean hasUserAnswered(UUID uuid, int surveyid) {
        PreparedStatement stmt = null;
        ResultSet res = null;
        try {
            //LOAD ANSWERS
            stmt = con.prepareStatement("SELECT * FROM " + config.database_mysql_table_useranswers + " WHERE uuid=? AND surveyid=? ORDER BY id");
            stmt.setString(1, uuid.toString());
            stmt.setInt(2, surveyid);
            res = stmt.executeQuery();
            return res.last();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        } finally {
            try {
                res.close();
            } catch (Exception ex) {
            }
            try {
                stmt.close();
            } catch (Exception ex) {
            }
        }
    }
}
