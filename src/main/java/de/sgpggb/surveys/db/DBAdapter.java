package de.sgpggb.surveys.db;

import de.sgpggb.pluginutilitieslibbungee.Logging;
import de.sgpggb.pluginutilitieslibbungee.sql.SQLConnection;
import de.sgpggb.pluginutilitieslibbungee.sql.adv.Table;
import de.sgpggb.pluginutilitieslibbungee.utils.Util;
import de.sgpggb.surveys.Manager;
import de.sgpggb.surveys.SurveysPlugin;
import de.sgpggb.surveys.misc.Utils;
import de.sgpggb.surveys.model.Answer;
import de.sgpggb.surveys.model.AnswerType;
import de.sgpggb.surveys.model.Group;
import de.sgpggb.surveys.model.Question;
import de.sgpggb.surveys.model.Reward;
import de.sgpggb.surveys.model.RewardType;
import de.sgpggb.surveys.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DBAdapter {

    private final SQLConnection conn;
    private DBMigrationSurveys dbMig;

    private final Table questionsTable;
    private final Table answersTable;
    private final Table rewardsTable;
    private final Table groupsTable;
    private final Table usersTable;

    Logging log = SurveysPlugin.getInstance().getLog();
    Manager manager = SurveysPlugin.getInstance().getManager();

    public DBAdapter(SQLConnection conn, Logging logger) {
        this.conn = conn;
        this.dbMig = new DBMigrationSurveys(conn, logger);
        this.dbMig.checkDB();
        questionsTable = dbMig.getTableQuestions();
        answersTable = dbMig.getTableAnswers();
        rewardsTable = dbMig.getTableRewards();
        groupsTable = dbMig.getTableGroups();
        usersTable = dbMig.getTableUsers();
    }

    public Map<Integer, Reward> loadAllRewards() {
        Map<Integer, Reward> rewards = new HashMap<>();
        PreparedStatement stmt = null;
        ResultSet res = null;
        try {
            stmt = conn.getConnection().prepareStatement("SELECT * FROM " + rewardsTable.getName());
            res = stmt.executeQuery();
            while (res.next()) {
                String s = res.getString("rewardType");
                RewardType rewardType = RewardType.valueOf(s);

                Reward reward = new Reward(
                        res.getInt("id"),
                        res.getString("claimText"),
                        res.getString("infoText"),
                        res.getString("reward"),
                        rewardType
                );
                rewards.put(reward.getId(), reward);
            }
        } catch (Throwable t) {
            throw new RuntimeException(t);
        } finally {
            Util.close(res);
            Util.close(stmt);
        }
        log.debug("loaded " + rewards.size() + " rewards!");
        return rewards;
    }

    public Map<Integer, Question> loadAllQuestions() {
        Map<Integer, Question> questions = new HashMap<>();
        PreparedStatement stmt = null;
        ResultSet res = null;
        try {
            stmt = conn.getConnection().prepareStatement("SELECT * FROM " + questionsTable.getName());
            res = stmt.executeQuery();
            while (res.next()) {
                Question question = new Question(
                        res.getInt("id"),
                        res.getString("text"),
                        res.getString("choices"),
                        res.getInt("nextID"),
                        AnswerType.valueOf(res.getString("answerType")),
                    res.getInt("groupID")
                );
                questions.put(question.getId(), question);
            }
        } catch (Throwable t) {
            throw new RuntimeException(t);
        } finally {
            Util.close(res);
            Util.close(stmt);
        }
        log.debug("loaded " + questions.size() + " questions!");
        return questions;
    }

    public Map<Integer, Group> loadAllGroups() {
        Map<Integer, Group> groups = new HashMap<>();
        PreparedStatement stmt = null;
        ResultSet res = null;
        try {
            stmt = conn.getConnection().prepareStatement("SELECT * FROM " + groupsTable.getName());
            res = stmt.executeQuery();
            while (res.next()) {
                int id = res.getInt("id");
                String name = res.getString("name");
                int order = res.getInt("order");
                String permission = res.getString("permission");
                int firstQuestionID = res.getInt("firstQuestionID");
                int rewardID = res.getInt("rewardID");

                Reward reward = SurveysPlugin.getInstance().getManager().getReward(rewardID);
                if (reward == null) {
                    log.error("could not find reward with id " + rewardID);
                }

                Group group = new Group(id, name, order, permission, firstQuestionID, rewardID);
                groups.put(group.getId(), group);
            }
        } catch (Throwable t) {
            throw new RuntimeException(t);
        } finally {
            Util.close(res);
            Util.close(stmt);
        }
        log.debug("loaded " + groups.size() + " groups!");
        return groups;
    }

    /**
     * returns the last given answer
     * @param uuid the uuid
     * @param questionID questionID or -1 for general search
     * @return the last answer
     */
    public Answer loadLastAnswer(UUID uuid, int questionID) {
        Answer answer = null;
        PreparedStatement stmt = null;
        ResultSet res = null;
        try {
            stmt = conn.getConnection().prepareStatement("SELECT * FROM " + answersTable.getName() +
                    " WHERE uuid = ? " + (questionID == -1 ? "" : "AND questionID = ? ") +
                    "ORDER BY id DESC LIMIT 1");
            stmt.setString(1, uuid.toString());
            if (questionID != -1) {
                stmt.setInt(2, questionID);
            }
            res = stmt.executeQuery();

            if (res.next()) {
                answer = new Answer(
                        res.getInt("id"),
                        res.getInt("questionID"),
                        UUID.fromString(res.getString("uuid")),
                        res.getString("answer"),
                        res.getTimestamp("timestamp")
                );
            }
        } catch (Throwable t) {
            throw new RuntimeException(t);
        } finally {
            Util.close(res);
            Util.close(stmt);
        }
        return answer;
    }

    public Answer loadAnswer(UUID uuid, Question question) {
        Answer answer;
        ResultSet res = null;
        PreparedStatement stmt = null;
        try {
            stmt = conn.getConnection().prepareStatement("SELECT * FROM " + answersTable.getName()
                    + " WHERE uuid = ? AND questionID = ?");
            stmt.setString(1, uuid.toString());
            stmt.setInt(2, question.getId());
            res = stmt.executeQuery();
            if (res.next()) {
                int id = res.getInt("id");
                int questionID = res.getInt("questionID");
                UUID u = UUID.fromString(res.getString("uuid"));
                String text = res.getString("answer");
                Timestamp timestamp = res.getTimestamp("timestamp");
                return new Answer(id, questionID, u, text, timestamp);
            }
        } catch (Throwable t) {
            throw new RuntimeException(t);
        } finally {
            Util.close(res);
            Util.close(stmt);
        }
        return null;
    }

    public User loadUser(UUID uuid) {
        User user;
        ResultSet res = null;
        PreparedStatement stmt = null;
        try {
            stmt = conn.getConnection().prepareStatement("SELECT * FROM " + usersTable.getName()
                + " WHERE uuid = ?");
            stmt.setString(1, uuid.toString());
            res = stmt.executeQuery();
            if (res.next()) {
                int id = res.getInt("id");
                UUID u = UUID.fromString(res.getString("uuid"));
                int currentGroup = res.getInt("currentGroup");
                int currentQuestion = res.getInt("currentQuestion");
                List<Integer> list = Utils.stringToIntList(res.getString("completedGroups"));
                return new User(id, u, currentQuestion, currentGroup, list);
            }
        } catch (Throwable t) {
            throw new RuntimeException(t);
        } finally {
            Util.close(res);
            Util.close(stmt);
        }
        return null;
    }



    public void saveUser(User user) {
        PreparedStatement stmt = null;
        ResultSet res = null;
        try {
            String sql;
            if (user.getId() == -1) {
                sql = "INSERT INTO " + usersTable.getName() + " (uuid, currentGroup, currentQuestion, completedGroups) VALUES (?, ?, ?, ?)";
                stmt = conn.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            } else {
                sql = "UPDATE " + usersTable.getName() + " SET uuid = ?, currentGroup = ?, currentQuestion = ?, completedGroups = ? WHERE id = ?";
                stmt = conn.getConnection().prepareStatement(sql);
            }

            stmt.setString(1, user.getUuid().toString());
            stmt.setInt(2, user.getCurrentGroupID());
            stmt.setInt(3, user.getCurrentQuestionID());
            stmt.setString(4, Utils.intListToString(user.getCompletedGroups()));

            if (user.getId() == -1) {
                stmt.executeUpdate();
                res = stmt.getGeneratedKeys();
                if (res.next()) {
                    user.setId(res.getInt(1));
                }
            } else {
                stmt.setInt(5, user.getId());
                stmt.executeUpdate();
            }
        } catch (Throwable t) {
            throw new RuntimeException(t);
        } finally {
            Util.close(res);
            Util.close(stmt);
        }
    }

    public void saveReward(Reward reward) {
        PreparedStatement stmt = null;
        ResultSet res = null;
        try {
            String sql;
            if (reward.getId() == -1) {
                sql = "INSERT INTO " + rewardsTable.getName() + " (claimText, infoText, reward, rewardType) VALUES (?, ?, ?, ?)";
                stmt = conn.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            } else {
                sql = "UPDATE " + rewardsTable.getName() + " SET claimText = ?, infoText = ?, reward = ?, rewardType = ? WHERE id = ?";
                stmt = conn.getConnection().prepareStatement(sql);
            }

            stmt.setString(1, reward.getClaimText());
            stmt.setString(2, reward.getInfoText());
            stmt.setString(3, reward.getReward());
            stmt.setString(4, reward.getRewardType().name());

            if (reward.getId() == -1) {
                stmt.executeUpdate();
                res = stmt.getGeneratedKeys();
                if (res.next()) {
                    reward.setId(res.getInt(1));
                }
            } else {
                stmt.setInt(5, reward.getId());
                stmt.executeUpdate();
            }
        } catch (Throwable t) {
            throw new RuntimeException(t);
        } finally {
            Util.close(res);
            Util.close(stmt);
        }
    }


    public void saveAnswer(Answer answer) {
        PreparedStatement stmt = null;
        ResultSet res = null;
        try {
            stmt = conn.getConnection().prepareStatement("INSERT INTO " + answersTable.getName() +
                    " (questionID, uuid, answer, timestamp) VALUES (?, ?, ?, CURRENT_TIMESTAMP)", Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, answer.getQuestionID());
            stmt.setString(2, answer.getUuid().toString());
            stmt.setString(3, answer.getAnswer());
            stmt.executeUpdate();

            res = stmt.getGeneratedKeys();
            if (res.next()) {
                answer.setId(res.getInt(1));
            }
        } catch (Throwable t) {
            throw new RuntimeException(t);
        } finally {
            Util.close(res);
            Util.close(stmt);
        }
    }

    public void saveQuestion(Question question) {
        PreparedStatement stmt = null;
        ResultSet res = null;
        try {
            String sql;
            if (question.getId() == -1) {
                sql = "INSERT INTO " + questionsTable.getName() + " (text, choices, groupID, nextID, answerType) VALUES (?, ?, ?, ?, ?)";
                stmt = conn.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            } else {
                sql = "UPDATE " + questionsTable.getName() + " SET text = ?, choices = ?, groupID = ?, nextID = ?, answerType = ? WHERE id = ?";
                stmt = conn.getConnection().prepareStatement(sql);
            }

            stmt.setString(1, question.getText());
            stmt.setString(2, question.getChoices());
            stmt.setInt(3, question.getGroupID());
            stmt.setInt(4, question.getNextID());
            stmt.setString(5, question.getAnswerType().name());

            if (question.getId() == -1) {
                stmt.executeUpdate();
                res = stmt.getGeneratedKeys();
                if (res.next()) {
                    question.setId(res.getInt(1));
                }
            } else {
                stmt.setInt(6, question.getId());
                stmt.executeUpdate();
            }
        } catch (Throwable t) {
            throw new RuntimeException(t);
        } finally {
            Util.close(res);
            Util.close(stmt);
        }
    }

    public void saveGroup(Group group) {
        PreparedStatement stmt = null;
        ResultSet res = null;
        String sql;
        try {
            if (group.getId() == -1) {
                sql = "INSERT INTO " + groupsTable.getName() + " (name, `order`, permission, firstQuestionID, rewardID) VALUES (?, ?, ?, ?, ?)";
                stmt = conn.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            } else {
                sql = "UPDATE " + groupsTable.getName() + " SET name = ?, `order` = ?, permission = ?, firstQuestionID = ?, rewardID = ? WHERE id = ?";
                stmt = conn.getConnection().prepareStatement(sql);
            }

            stmt.setString(1, group.getName());
            stmt.setInt(2, group.getOrder());
            stmt.setString(3, group.getPermission());
            stmt.setInt(4, group.getFirstQuestionID());
            stmt.setInt(5, group.getRewardID());

            if (group.getId() == -1) {
                stmt.executeUpdate();
                res = stmt.getGeneratedKeys();
                if (res.next()) {
                    group.setId(res.getInt(1));
                }
            } else {
                stmt.setInt(6, group.getId());
                stmt.executeUpdate();
            }
        } catch (Throwable t) {
            throw new RuntimeException(t);
        } finally {
            Util.close(res);
            Util.close(stmt);
        }
    }
}
