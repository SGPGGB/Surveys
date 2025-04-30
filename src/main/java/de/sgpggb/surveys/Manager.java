package de.sgpggb.surveys;

import de.sgpggb.pluginutilitieslibbungee.Logging;
import de.sgpggb.pluginutilitieslibbungee.utils.ChatUtils;
import de.sgpggb.pluginutilitieslibbungee.utils.Util;
import de.sgpggb.surveys.model.Answer;
import de.sgpggb.surveys.model.Group;
import de.sgpggb.surveys.model.Question;
import de.sgpggb.surveys.model.Reward;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;


public class Manager {

    //          db-id    object
    private Map<Integer, Group> groupsMap;
    private Map<Integer, Question> questionsMap;
    private Map<Integer, Reward> rewardsMap;

    //          uuid  object
    private Map<UUID, Question> currentQuestion = new HashMap<>();
    private Map<UUID, String> answersCache = new HashMap<>();

    Logging log = SurveysPlugin.getInstance().getLog();
    String prefix = SurveysPlugin.CHATPREFIX;

    public Manager() {
        questionsMap = SurveysPlugin.getInstance().getDbAdapter().loadAllQuestions();
        rewardsMap = SurveysPlugin.getInstance().getDbAdapter().loadAllRewards();
        groupsMap = SurveysPlugin.getInstance().getDbAdapter().loadAllGroups();
    }

    /**
     * gets the current question for the player
     * @param uuid the uuid
     * @return current question
     */
    public Question getCurrentQuestion(UUID uuid) {
        return currentQuestion.getOrDefault(uuid, null);
    }

    public Group getCurrentGroup(UUID uuid) {
        Question question = getCurrentQuestion(uuid);
        if (question == null)
            return null;
        int gId = question.getGroupID();
        return getGroup(gId);
    }

    public Group getGroup(int id) {
        return groupsMap.getOrDefault(id, null);
    }

    /**
     * sends player the next question, updates cache
     * @param player the player
     */
    private void goNextQuestion(ProxiedPlayer player) {
        Question current = currentQuestion.get(player.getUniqueId());
        Group group = getCurrentGroup(player.getUniqueId());
        int nextID = current.getNextID();

        Question next = group.getNextQuestion(current);
        if (next == null) {
            //found no next question -> REWARDTIME
            Reward reward = getReward(group.getRewardID());
            if (reward == null)
                return;
            reward.give(player);
        } else {
            currentQuestion.put(player.getUniqueId(), next);
            sendQuestion(player);
            log.debug("player " + player.getName() + " went to question " + next.getId()
                    + " in group " + next.getGroupID());
        }
    }

    /**
     * saves answer to the db
     * @param player the player
     * @param answer the answer
     */
    public void answer(ProxiedPlayer player, String answer) {
        Question current = getCurrentQuestion(player.getUniqueId());
        log.debug("player " + player.getName() + " answered question " + current.getId() + " with " + answer);

        Answer ans = new Answer(-1, current.getId(), player.getUniqueId(), answer, null);
        SurveysPlugin.getInstance().getDbAdapter().saveAnswer(ans);
        goNextQuestion(player);
    }

    /**
     * sends the player the text for its current question
     * @param player the player
     */
    public void sendQuestion(ProxiedPlayer player) {
        Question current = currentQuestion.get(player.getUniqueId());
        ChatUtils.send(player, prefix + current.getText());
        String text = "";
        String bold = "";
        List<String> answers = getAnswerList(player.getUniqueId());
        switch (current.getAnswerType()) {
            case FREE_TEXT -> {
                text = "<green><click:show_command:/surveys answer >[Klicken zum Antworten]";
            }
            case SINGLE_CHOICE, NUMERICAL, MULTIPLE_CHOICE -> {
                List<String> choices = current.getChoicesList();
                for (String s : choices) {
                    if (answers.contains(s))
                        bold = "<b>";
                    else
                        bold = "</b>";
                    text += getRandomColor() + bold + "[<click:run_command:/surveys answer " + s + ">" + s + "] ";
                }
            }
        }

        ChatUtils.send(player, prefix + text);
        ChatUtils.send(player, prefix + "<green><click:run_command:/surveys answer #confirm>[Klicken um Antwort abzuschicken]");
    }

    /**
     *
     * @return returns a random color
     */
    private String getRandomColor() {
        List<String> list = Util.list(
                "<black>", "<dark_blue>", "<dark_green>", "<dark_aqua>", "<dark_red>", "<dark_purple>", "<gold>", "<gray>",
                "<dark_gray>", "<blue>", "<green>", "<aqua>", "<red>", "<light_purple>", "<yellow>", "<white>"
        );
        Random random = new Random();
        return list.get(random.nextInt(list.size()));
    }

    /**
     * saves current answer to the cache
     * @param uuid the uuid
     * @param answer the answer
     */
    public void addAnswerToCache(UUID uuid, String answer) {
        Question current = getCurrentQuestion(uuid);
        if (current == null)
            return;

        switch (current.getAnswerType()) {
            case NUMERICAL, FREE_TEXT, SINGLE_CHOICE -> {
                answersCache.put(uuid, answer);
            }
            case MULTIPLE_CHOICE -> {
                String s = answersCache.getOrDefault(uuid, "");
                s = s + answer + ";";
                answersCache.put(uuid, s);
            }
        }
    }

    /**
     *
     * @param uuid the uuid
     * @return the answer cached
     */
    public String getAnswerCache(UUID uuid) {
        return answersCache.getOrDefault(uuid, "");
    }

    public List<String> getAnswerList(UUID uuid) {
        String s = getAnswerCache(uuid);
        return Arrays.asList(s.split(";"));
    }

    public Question getQuestion(int id) {
        return questionsMap.getOrDefault(id, null);
    }

    public Reward getReward(int id) {
        return rewardsMap.getOrDefault(id, null);
    }

    public void removeAnswerCache(UUID uuid) {
        answersCache.remove(uuid);
    }

    public void checkLogin(ProxiedPlayer player) {
        if (!player.isConnected())
            return;

        Answer answer = SurveysPlugin.getInstance().getDbAdapter().loadLastAnswer(player.getUniqueId(), -1);
        if (answer == null) {
            //no answer given yet
            return;
        }

        ChatUtils.send(player, prefix + "<click:run_command:/surveys next><gold>Du hast noch deine Umfrage offen!" +
                " Klicke hier, um an dieser weiter zu machen und deine <b>Belohnung</b> abzuholen!");
    }

    public Map<UUID, Question> getCurrentQuestion() {
        return currentQuestion;
    }

    public void setCurrentQuestion(Map<UUID, Question> currentQuestion) {
        this.currentQuestion = currentQuestion;
    }

    public Map<UUID, String> getAnswersCache() {
        return answersCache;
    }

    public void setAnswersCache(Map<UUID, String> answersCache) {
        this.answersCache = answersCache;
    }

    public Map<Integer, Group> getGroupsMap() {
        return groupsMap;
    }

    public void setGroupsMap(Map<Integer, Group> groupsMap) {
        this.groupsMap = groupsMap;
    }

    public Map<Integer, Question> getQuestionsMap() {
        return questionsMap;
    }

    public void setQuestionsMap(Map<Integer, Question> questionsMap) {
        this.questionsMap = questionsMap;
    }

    public Map<Integer, Reward> getRewardsMap() {
        return rewardsMap;
    }

    public void setRewardsMap(Map<Integer, Reward> rewardsMap) {
        this.rewardsMap = rewardsMap;
    }
}
