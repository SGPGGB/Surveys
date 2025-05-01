package de.sgpggb.surveys;

import de.sgpggb.pluginutilitieslibbungee.Logging;
import de.sgpggb.pluginutilitieslibbungee.utils.ChatUtils;
import de.sgpggb.pluginutilitieslibbungee.utils.Util;
import de.sgpggb.surveys.db.DBAdapter;
import de.sgpggb.surveys.events.SurveysQuestionAnsweredEvent;
import de.sgpggb.surveys.events.SurveysSurveyCompletedEvent;
import de.sgpggb.surveys.model.Answer;
import de.sgpggb.surveys.model.Group;
import de.sgpggb.surveys.model.Question;
import de.sgpggb.surveys.model.Reward;
import de.sgpggb.surveys.model.User;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
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
    private Map<UUID, User> usersMap = new HashMap<>();
    private Map<UUID, String> answersCache = new HashMap<>();

    Logging log = SurveysPlugin.getInstance().getLog();
    DBAdapter db = SurveysPlugin.getInstance().getDbAdapter();
    String prefix = SurveysPlugin.CHATPREFIX;

    public Manager() {
    }

    public void loadAll() {
        questionsMap = SurveysPlugin.getInstance().getDbAdapter().loadAllQuestions();
        rewardsMap = SurveysPlugin.getInstance().getDbAdapter().loadAllRewards();
        groupsMap = SurveysPlugin.getInstance().getDbAdapter().loadAllGroups();
    }

    /**
     * gets the current question for the player
     * @param uuid the uuid of user
     * @return current question or null if no question is left
     */
    public Question getCurrentQuestion(UUID uuid) {
        User user = getUser(uuid);
        if (user == null)
            return null;
        int id = user.getCurrentQuestion();
        if (id == -1)
            return null;
        return getQuestion(id);
    }


    /**
     * sets the give question to the current question and saves it.
     * @param uuid the uuid of user
     * @param question the question
     */
    public void setCurrentQuestion(UUID uuid, Question question) {
        User user = getUser(uuid);
        if (user == null)
            return;
        user.setCurrentQuestion(question.getId());
        db.saveUser(user);
    }

    /**
     * sets the current group as finished. saves the user
     * @param uuid the uuid of user
     * @param group the finished group
     */
    public void setGroupAsCompleted(UUID uuid, Group group) {
        User user = getUser(uuid);
        if (user == null)
            return;
        List<Integer> list = user.getCompletedGroups();
        list.add(group.getId());
        user.setCompletedGroups(list);
        db.saveUser(user);
    }


    /**
     * returns the next group for given user
     * @param uuid the uuid of user
     * @return the next group or null
     */
    public Group getNextGroup(UUID uuid) {
        ProxiedPlayer player = SurveysPlugin.getInstance().getProxy().getPlayer(uuid);
        if (player == null)
            return null;

        User user = getUser(uuid);
        if (user == null)
            return null;

        Group next = null;

        for (Group group : groupsMap.values()) {
            //skip if no permission
            if (!player.hasPermission(group.getPermission()))
                continue;

            //skip if already completed
            if (user.hasCompletedGroup(group))
                continue;

            //if no next is found, set the current as next
            if (next == null) {
                next = group;
                continue;
            }

            //if the next is later (higher order number) than the current one
            if (group.getOrder() > next.getOrder()) {
                next = group;
            }
        }

        return next;
    }

    public User getUser(UUID uuid) {
        return usersMap.getOrDefault(uuid, null);
    }

    /**
     * returns the current group of user
     * @param uuid the uuid of user
     * @return the current group or null
     */
    public Group getCurrentGroup(UUID uuid) {
        Question question = getCurrentQuestion(uuid);
        if (question == null)
            return null;
        int gId = question.getGroupID();
        return getGroup(gId);
    }

    /**
     * returns the group by given id
     * @param id the id
     * @return the group or null
     */
    public Group getGroup(int id) {
        return groupsMap.getOrDefault(id, null);
    }

    public void addGroup(Group group) {
        groupsMap.put(group.getId(), group);
    }

    public void addReward(Reward reward) {
        rewardsMap.put(reward.getId(), reward);
    }

    public void addQuestion(Question question) {
        questionsMap.put(question.getId(), question);
    }

    /**
     * sends player the next question, updates cache
     * @param player the player
     */
    private void goNextQuestion(ProxiedPlayer player) {
        Question current = getCurrentQuestion(player.getUniqueId());
        Group group = getCurrentGroup(player.getUniqueId());
        int nextID = current.getNextID();

        Question next = group.getNextQuestion(current);
        if (next == null) {
            //found no next question -> REWARDTIME
            Reward reward = getReward(group.getRewardID());
            SurveysPlugin.getInstance().getProxy().getPluginManager().callEvent(new SurveysSurveyCompletedEvent(group, reward));

            //adds group to completed for user
            setGroupAsCompleted(player.getUniqueId(), group);
            if (reward == null)
                return;
            reward.give(player);
        } else {
            setCurrentQuestion(player.getUniqueId(), next);
            sendQuestion(player);
            log.debug("player " + player.getName() + " went to question " + next.getId() + " in group " + next.getGroupID());
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
        db.saveAnswer(ans);
        SurveysPlugin.getInstance().getProxy().getPluginManager().callEvent(new SurveysQuestionAnsweredEvent(current, ans));
        goNextQuestion(player);
    }

    /**
     * sends the player the text for its current question
     * @param player the player
     */
    public void sendQuestion(ProxiedPlayer player) {
        Question current = getCurrentQuestion(player.getUniqueId());

        if (current == null) {
            log.error("tried sending a question, but user " + player.getName() + " has no current question");
            return;
        }

        ChatUtils.send(player, prefix + current.getText());
        StringBuilder text = new StringBuilder();
        List<String> answers = getAnswerList(player.getUniqueId());
        switch (current.getAnswerType()) {
            case FREE_TEXT -> {
                text = new StringBuilder("<green><click:show_command:/surveys answer >[Klicken zum Antworten]");
            }
            case SINGLE_CHOICE, NUMERICAL, MULTIPLE_CHOICE -> {
                List<String> choices = current.getChoicesList();
                boolean b = false;
                for (String s : choices) {
                    if (answers.contains(s))
                        b = true;
                    text.append(getRandomColor())
                        .append(b ? "<b>" : "")
                        .append("[<click:run_command:/surveys answer ")
                        .append(s)
                        .append(">")
                        .append(s)
                        .append("] ")
                        .append(b ? "</b>" : "");
                }
            }
        }

        ChatUtils.send(player, prefix + text);
        //ChatUtils.send(player, prefix + "<green><click:run_command:/surveys answer #confirm>[Klicken um Antwort abzuschicken]");
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

    public void onLogin(ProxiedPlayer player) {
        if (player == null || !player.isConnected())
            return;

        User user = db.loadUser(player.getUniqueId());
        if (user == null) {
            user = new User(-1, player.getUniqueId(), -1, -1, new ArrayList<>());
            db.saveUser(user);
        }

        usersMap.put(player.getUniqueId(), user);

        if (user.getCurrentGroup() == -1) {
            Group next = getNextGroup(user.getUuid());
            if (next == null) {
                log.debug("found no next group for user " + player.getName() + "! Already completed " + user.getCompletedGroups());
                return;
            }
            user.setCurrentGroup(next.getId());
            user.setCurrentQuestion(next.getFirstQuestionID());
        }

        Group group = getCurrentGroup(player.getUniqueId());
        if (group == null)
            return;

        if (!player.hasPermission(group.getPermission())) {
            log.error("player " + player.getName() + " has group " + group.getName() + " active, but has no permission");
            return;
        }

        Question question = getCurrentQuestion(player.getUniqueId());
        if (question == null)
            return;

        if (question.getGroupID() != group.getId()) {
            log.error("player " + player.getName() + " has question " + question.getId() + " (set groupid=" + question.getGroupID()
                + ") active, but also has group " + group.getId() + " active!");
            return;
        }

        ChatUtils.send(player, prefix + "<click:run_command:/surveys next><gold>Du hast noch deine Umfrage offen!" +
                " Klicke hier, um an dieser weiter zu machen und am Ende deine <b>Belohnung</b> abzuholen!");
    }


    public void onLogout(ProxiedPlayer player) {
        User user = getUser(player.getUniqueId());
        if (user == null)
            return;
        db.saveUser(user);
        usersMap.remove(player.getUniqueId());
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
