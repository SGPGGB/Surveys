package de.sgpggb.surveys.cmd;

import de.sgpggb.pluginutilitieslibbungee.cmd.CustomCommand;
import de.sgpggb.pluginutilitieslibbungee.utils.ChatUtils;
import de.sgpggb.pluginutilitieslibbungee.utils.Util;
import de.sgpggb.surveys.Manager;
import de.sgpggb.surveys.SurveysPlugin;
import de.sgpggb.surveys.misc.Permissions;
import de.sgpggb.surveys.model.Group;
import de.sgpggb.surveys.model.Question;
import de.sgpggb.surveys.model.Reward;
import de.sgpggb.surveys.model.RewardType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EditCommand extends CustomCommand {

    @Override
    protected boolean checkPermission(CommandSender sender) {
        return Permissions.ADMIN.check(sender);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        //surveys edit reward id infoText/claimText/reward TEXT
        //surveys edit group id name/order/... TEXT
        //surveys edit question id text/rewardID/nextID/addChoice/removeChoice/permission/groupID
        String prefix = SurveysPlugin.getInstance().CHATPREFIX;
        Manager manager = SurveysPlugin.getInstance().getManager();
        if (!(sender instanceof ProxiedPlayer player)) {
            ChatUtils.send(sender, prefix + "<red>Nur ingame!");
            return;
        }

        if (args.length < 4) {
            ChatUtils.send(player, prefix + "<red>Falsche Argumente, siehe Doku");
            return;
        }
        int id;
        try {
            id = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            ChatUtils.send(player, prefix + "<red>Keine Nummer angegeben: " + args[1]);
            return;
        }
        String option = args[2];
        String value = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
        if (args[0].equalsIgnoreCase("reward")) {
            Reward reward = manager.getReward(id);
            if (reward == null) {
                ChatUtils.send(player, prefix + "<red>Kein Reward mit der ID " + id + " gefunden!");
                return;
            }

            switch (option.toLowerCase()) {
                case "infotext" -> {
                    reward.setInfoText(value);
                    ChatUtils.send(player, prefix + "<green>Infotext angepasst");
                }
                case "claimtext" -> {
                    reward.setClaimText(value);
                    ChatUtils.send(player, prefix + "<green>Claimtext angepasst");
                }
                case "rewardtype" -> {
                    RewardType rewardType = RewardType.valueOf(value);
                    if (rewardType == null) {
                        ChatUtils.send(player, prefix + "<red>Rewardtype " + value + " gibt es nicht!");
                        return;
                    }
                    reward.setRewardType(rewardType);
                    ChatUtils.send(player, prefix + "<green>Rewardtype angepasst");
                }
                case "reward" -> {
                    switch (reward.getRewardType()) {
                        case MONEY, REWARD -> {
                            int i;
                            try {
                                i = Integer.parseInt(value);
                            } catch (NumberFormatException e) {
                                ChatUtils.send(player, prefix + "<red>Das ist keine Nummer: " + value);
                                return;
                            }
                            reward.setReward(String.valueOf(i));
                            ChatUtils.send(player, prefix + "<green>Reward angepasst");
                        }
                        default -> {
                            ChatUtils.send(player, prefix + "<red>Kein Rewardtype festgelegt!");
                            return;
                        }
                    }
                }
                default -> {
                    ChatUtils.send(player, prefix + "Falsche Option angegeben! Siehe Doku");
                    return;
                }
            }
            SurveysPlugin.getInstance().getDbAdapter().saveReward(reward);
        } else if (args[0].equalsIgnoreCase("question")) {
            Question question = manager.getQuestion(id);
            if (question == null) {
                ChatUtils.send(player, prefix + "<red>Keine Question mit der ID " + id + " gefunden!");
                return;
            }

            switch (option.toLowerCase()) {
                case "text" -> {
                    question.setText(value);
                    ChatUtils.send(player, prefix + "<green>Text angepasst");
                }
                case "nextid" -> {
                    int nextId;
                    try {
                        nextId = Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        ChatUtils.send(player, prefix + "<red>Keine Nummer: " + value);
                        return;
                    }
                    Question next = manager.getQuestion(nextId);
                    if (next == null) {
                        ChatUtils.send(player, prefix + "<red>Keine Question mit der ID " + nextId + " gefunden!");
                        return;
                    }
                    question.setNextID(next.getId());
                    ChatUtils.send(player, prefix + "<green>Next angepasst");
                }
                case "groupid" -> {
                    int groupId;
                    try {
                        groupId = Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        ChatUtils.send(player, prefix + "<red>Keine Nummer: " + value);
                        return;
                    }
                    question.setGroupID(groupId);
                    ChatUtils.send(player, prefix + "<green>Group angepasst");
                }
                case "addchoice" -> {
                    List<String> choices = question.getChoicesList();
                    if (choices.contains(value)) {
                        ChatUtils.send(player, prefix + "<red>Wert " + value + " ist bereits eine Option");
                        return;
                    }
                    choices.add(value);
                    question.setChoicesList(choices);
                    ChatUtils.send(player, prefix + "<green>Choices angepasst!");
                }
                case "removechoice" -> {
                    List<String> choices = question.getChoicesList();
                    if (!choices.contains(value)) {
                        ChatUtils.send(player, prefix + "<red>Wert " + value + " ist keine Option");
                        return;
                    }
                    choices.remove(value);
                    question.setChoicesList(choices);
                    ChatUtils.send(player, prefix + "<green>Choices angepasst!");
                }
                default -> {
                    ChatUtils.send(player, prefix + "Falsche Option angegeben! Siehe Doku");
                    return;
                }
            }
            SurveysPlugin.getInstance().getDbAdapter().saveQuestion(question);
        } else if (args[0].equalsIgnoreCase("group")) {
            Group group = manager.getGroup(id);
            if (group == null) {
                ChatUtils.send(player, prefix + "<red>Keine Group mit der ID " + id + " gefunden!");
                return;
            }

            switch (option.toLowerCase()) {
                case "name" -> {
                    group.setName(value);
                    ChatUtils.send(player, prefix + "<green>Name angepasst");
                }
                case "order" -> {
                    int order;
                    try {
                        order = Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        ChatUtils.send(player, prefix + "<red>Keine Nummer: " + value);
                        return;
                    }
                    group.setOrder(order);
                    ChatUtils.send(player, prefix + "<green>Order angepasst");
                }
                case "rewardid" -> {
                    int rewardId;
                    try {
                        rewardId = Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        ChatUtils.send(player, prefix + "<red>Keine Nummer: " + value);
                        return;
                    }
                    Reward reward = manager.getReward(rewardId);
                    if (reward == null) {
                        ChatUtils.send(player, prefix + "<red>Kein Reward mit der ID " + rewardId + " gefunden!");
                        return;
                    }
                    group.setRewardID(reward.getId());
                    ChatUtils.send(player, prefix + "<green>Reward angepasst");
                }
                case "permission" -> {
                    group.setPermission(value);
                    ChatUtils.send(player, prefix + "<green>Permission angepasst");
                }
                case "addquestion" -> {
                    List<Integer> questions = group.getQuestions();
                    int add;
                    try {
                        add = Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        ChatUtils.send(player, prefix + "<red>Keine Nummer: " + value);
                        return;
                    }
                    if (questions.contains(add)) {
                        ChatUtils.send(player, prefix + "<red>Wert " + value + " ist bereits eine Option");
                        return;
                    }
                    questions.add(add);
                    group.setQuestions(questions);
                    ChatUtils.send(player, prefix + "<green>Questions angepasst!");
                }
                case "removequestion" -> {
                    List<Integer> questions = group.getQuestions();
                    int rem;
                    try {
                        rem = Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        ChatUtils.send(player, prefix + "<red>Keine Nummer: " + value);
                        return;
                    }
                    if (!questions.contains(rem)) {
                        ChatUtils.send(player, prefix + "<red>Wert " + value + " ist keine Option");
                        return;
                    }
                    questions.remove(rem);
                    group.setQuestions(questions);
                    ChatUtils.send(player, prefix + "<green>Questions angepasst!");
                }
                default -> {
                    ChatUtils.send(player, prefix + "Falsche Option angegeben! Siehe Doku");
                    return;
                }
            }
            SurveysPlugin.getInstance().getDbAdapter().saveGroup(group);
        }
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> list = Util.list("question", "reward", "group");
        return switch (args.length) {
            //TODO: tabcompletes
            //surveys edit reward id infoText/claimText/reward TEXT
            //surveys edit group id name/order/... TEXT
            //surveys edit question id text/rewardID/nextID/addChoice/removeChoice/permission/groupID
            case 0, 1 -> list.stream()
                .filter(e -> args.length == 0 || e.toLowerCase().startsWith(args[0].toLowerCase()))
                .sorted(String.CASE_INSENSITIVE_ORDER).collect(Collectors.toList());
            default -> super.tabComplete(sender, args);
        };
    }

    @Override
    public List<String> getCommandNames() {
        return List.of("edit");
    }

    @Override
    public void printHelp(CommandSender sender) {
        ChatUtils.send(sender, SurveysPlugin.CHATPREFIX + "/surveys edit - Sachen editieren");
    }
}
