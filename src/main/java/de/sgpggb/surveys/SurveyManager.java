package de.sgpggb.surveys;

import de.sgpggb.surveys.models.Survey;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class SurveyManager {

    private DBController dbcontroller;
    private Map<Integer, Survey> surveys;

    public SurveyManager() {
        dbcontroller = new DBController();
        surveys = dbcontroller.loadSurveys();
    }

    public List<Survey> getActiveSurveys() {
        Timestamp curTime = Timestamp.valueOf(LocalDateTime.now());
        return surveys.values().stream()
                .filter(e -> e.getTimefrom().before(curTime))
                .filter(e -> e.getTimeto().after(curTime))
                .collect(Collectors.toList());

    }

    public List<Survey> getActiveUnanswered(UUID uuid) {
        return getActiveSurveys().stream()
                .filter(e -> !dbcontroller.hasUserAnswered(uuid, e.getId()))
                .collect(Collectors.toList());
    }

    public void addUserAnswer(ProxiedPlayer player, int survey, Integer answer) {
        try {
            dbcontroller.addUserAnswer(player.getUniqueId(), player.getName(), survey, answer);
        } catch (Exception e) {
            player.sendMessage(SurveysPlugin.CHATPREFIX + e.getMessage());
        }
    }
}
