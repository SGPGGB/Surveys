package de.sgpggb.surveys;

import de.sgpggb.pluginutilitieslibbungee.Logging;
import de.sgpggb.pluginutilitieslibbungee.utils.ChatUtils;
import de.sgpggb.pluginutilitieslibbungee.utils.Util;
import de.sgpggb.surveys.db.DBAdapter;
import de.sgpggb.surveys.events.SurveysQuestionAnsweredEvent;
import de.sgpggb.surveys.events.SurveysSurveyCompletedEvent;
import de.sgpggb.surveys.misc.Utils;
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
import java.util.concurrent.TimeUnit;


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
     * @param user the user
     */
    private void goNextQuestion(User user) {
        Question current = user.getCurrentQuestion();
        Group group = user.getCurrentGroup();

        if (current == null || group == null) {
            log.error("user " + user.getName() + " went to next question, but current group or question is null!");
            return;
        }

        int nextID = current.getNextID();
        Question next = group.getNextQuestion(current);
        if (next == null) {
            //found no next question -> REWARDTIME
            Reward reward = getReward(group.getRewardID());
            SurveysPlugin.getInstance().getProxy().getPluginManager().callEvent(new SurveysSurveyCompletedEvent(group, reward));

            user.setCurrentGroupID(-1);
            user.setCurrentQuestionID(-1);
            user.addGroupCompleted(group);

            user.save();

            if (reward == null)
                return;
            reward.give(user);
        } else {
            user.setCurrentQuestionID(next.getId());
            user.save();

            sendQuestion(user);
            log.debug("player " + user.getName() + " went to question " + next.getId() + " in group " + next.getGroupID());
        }
    }

    /**
     * saves answer to the db
     * @param user the user
     * @param answer the answer
     */
    public void answer(User user, String answer) {
        Question current = user.getCurrentQuestion();
        if (current == null) {
            log.error("user " + user.getName() + " answered, but question is null!");
            return;
        }
        log.debug("player " + user.getName() + " answered question " + current.getId() + " with " + answer);

        Answer ans = new Answer(-1, current.getId(), user.getUuid(), answer, null);
        db.saveAnswer(ans);
        SurveysPlugin.getInstance().getProxy().getPluginManager().callEvent(new SurveysQuestionAnsweredEvent(current, ans));
        goNextQuestion(user);
    }

    /**
     * sends the player the text for its current question
     * @param user the user
     */
    public void sendQuestion(User user) {
        Question current = user.getCurrentQuestion();
        if (current == null) {
            log.error("tried sending a question, but user " + user.getName() + " has no current question");
            return;
        }

        ChatUtils.send(user.getUuid(), prefix + current.getText());
        StringBuilder text = new StringBuilder();
        List<String> answers = getAnswerList(user.getUuid());
        switch (current.getAnswerType()) {
            case FREE_TEXT -> {
                text = new StringBuilder("<green><click:suggest_command:/surveys answer >[Klicken zum Antworten]");
            }
            case SINGLE_CHOICE, NUMERICAL, MULTIPLE_CHOICE -> {
                List<String> choices = current.getChoicesList();
                boolean b = false;
                for (String s : choices) {
                    if (answers.contains(s))
                        b = true;
                    text.append(getRandomColor())
                        .append(b ? "<b>" : "")
                        .append("[<click:run_command:/surveys answer " + s + ">")
                        .append(s)
                        .append("] ")
                        .append(b ? "</b>" : "");
                }
            }
        }

        ChatUtils.send(user.getUuid(), prefix + text);
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
     * @param user the user
     * @param answer the answer
     */
    public void addAnswerToCache(User user, String answer) {
        Question current = user.getCurrentQuestion();
        if (current == null)
            return;

        switch (current.getAnswerType()) {
            case NUMERICAL, FREE_TEXT, SINGLE_CHOICE -> {
                answersCache.put(user.getUuid(), answer);
            }
            case MULTIPLE_CHOICE -> {
                String s = answersCache.getOrDefault(user.getUuid(), "");
                s = s + answer + ";";
                answersCache.put(user.getUuid(), s);
            }
        }
    }

    public String getAnswerCache(UUID uuid) {
        return answersCache.getOrDefault(uuid, "");
    }

    public List<String> getAnswerList(UUID uuid) {
        return Utils.stringToStringList(getAnswerCache(uuid));
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
        User user = db.loadUser(player.getUniqueId());
        if (user == null) {
            user = new User(-1, player.getUniqueId(), -1, -1, new ArrayList<>());
            db.saveUser(user);
        }

        user.setName(player.getName());

        usersMap.put(player.getUniqueId(), user);

        if (user.getCurrentGroupID() == -1) {
            Group next = getNextGroup(user.getUuid());
            if (next == null) {
                log.debug("found no next group for user " + player.getName() + "! Already completed " + user.getCompletedGroups());
                return;
            }
            user.setCurrentGroupID(next.getId());
            user.setCurrentQuestionID(next.getFirstQuestionID());
        }

        Group group = user.getCurrentGroup();
        if (group == null)
            return;

        if (!player.hasPermission(group.getPermission())) {
            log.error("player " + player.getName() + " has group " + group.getName() + " active, but has no permission");
            return;
        }

        Question question = user.getCurrentQuestion();
        if (question == null)
            return;

        if (question.getGroupID() != group.getId()) {
            log.error("player " + player.getName() + " has question " + question.getId() + " (set groupid=" + question.getGroupID()
                + ") active, but also has group " + group.getId() + " active!");
            return;
        }

        SurveysPlugin.getInstance().getProxy().getScheduler().schedule(SurveysPlugin.getInstance(), () -> {
            if (!player.isConnected())
                return;

            ChatUtils.send(player, prefix + "<click:run_command:/surveys next><gold>Du hast noch eine Umfrage offen!" +
                " Klicke hier, um an dieser weiter zu machen und am Ende deine <b>Belohnung</b> abzuholen!");

            }, 45, TimeUnit.SECONDS);
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
