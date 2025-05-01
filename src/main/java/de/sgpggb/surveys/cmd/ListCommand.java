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
import net.md_5.bungee.api.CommandSender;

import java.util.List;
import java.util.stream.Collectors;

public class ListCommand extends CustomCommand {
    @Override
    protected boolean checkPermission(CommandSender sender) {
        return Permissions.MOD.check(sender);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        String prefix = SurveysPlugin.CHATPREFIX;
        Manager manager = SurveysPlugin.getInstance().getManager();
        if (args.length == 0) {
            ChatUtils.send(sender, prefix + "<red>Falsche Argumente, Siehe Doku");
            return;
        }

        int id = -1;
        if (args.length > 1) {
            try {
                id = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                ChatUtils.send(sender, prefix + "<red>Keine Nummer: " + args[1]);
                return;
            }
        }

        String s = prefix; //"<click:show_command:/surveys list ><green>";

        switch (args[0].toLowerCase()) {
            case "reward" -> {
                if (id > -1) {
                    Reward reward = manager.getReward(id);
                    if (reward == null) {
                        ChatUtils.send(sender, prefix + "<red>Kein Reward mit der ID " + id + " gefunden!");
                        return;
                    }
                    ChatUtils.send(sender, s + "Infos zu Reward " + reward.getId() + ":");
                    ChatUtils.send(sender, s + "RewardType: " + reward.getRewardType().name());
                    ChatUtils.send(sender, s + "ClaimText: " + reward.getClaimText());
                    ChatUtils.send(sender, s + "InfoText: " + reward.getInfoText());
                    ChatUtils.send(sender, s + "Reward: " + reward.getReward());
                    return;
                }

                if (manager.getRewardsMap().isEmpty()) {
                    ChatUtils.send(sender, prefix + "<red>Nichts gefunden :(");
                    return;
                }

                for (Reward reward : manager.getRewardsMap().values()) {
                    ChatUtils.send(sender, prefix + "[" + reward.getId() + "] " +
                            reward.getRewardType() + " - " + reward.getReward());
                }
            }
            case "question" -> {
                if (id > -1) {
                    Question question = manager.getQuestion(id);
                    if (question == null) {
                        ChatUtils.send(sender, prefix + "<red>Keine Question mit der ID " + id + " gefunden");
                        return;
                    }
                    ChatUtils.send(sender, s + "Infos zu Question " + question.getId() + ":");
                    ChatUtils.send(sender, s + "Text: " + question.getText());
                    ChatUtils.send(sender, s + "NextID: " + question.getNextID());
                    ChatUtils.send(sender, s + "Group: " + question.getGroupID());
                    ChatUtils.send(sender, s + "AnswerType: " + question.getAnswerType().name());
                    ChatUtils.send(sender, s + "Choices: " + question.getChoices());
                    return;
                }

                if (manager.getQuestionsMap().isEmpty()) {
                    ChatUtils.send(sender, prefix + "<red>Nichts gefunden :(");
                    return;
                }

                for (Question question : manager.getQuestionsMap().values()) {
                    ChatUtils.send(sender, prefix + "[" + question.getId() + "] " + question.getText());
                }
            }
            case "group" -> {
                if (id > -1) {
                    Group group = manager.getGroup(id);
                    if (group == null) {
                        ChatUtils.send(sender, prefix + "<red>Keine Group mit der ID " + id + " gefunden!");
                        return;
                    }
                    ChatUtils.send(sender, s + "Infos zu Group " + group.getId() + ":");
                    ChatUtils.send(sender, s + "Name: " + group.getName());
                    ChatUtils.send(sender, s + "RewardID: " + group.getRewardID());
                    ChatUtils.send(sender, s + "Order: " + group.getOrder());
                    ChatUtils.send(sender, s + "Firstquestion: " + group.getFirstQuestionID());
                    return;
                }

                if (manager.getGroupsMap().isEmpty()) {
                    ChatUtils.send(sender, prefix + "<red>Nichts gefunden :(");
                    return;
                }

                for (Group group : manager.getGroupsMap().values()) {
                    ChatUtils.send(sender, prefix + "[" + group.getId() + "] " + group.getName());
                }
            }
        }
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> list = Util.list("question", "reward", "group");
        return switch (args.length) {
            case 0, 1 -> list.stream()
                .filter(e -> args.length == 0 || e.toLowerCase().startsWith(args[0].toLowerCase()))
                .sorted(String.CASE_INSENSITIVE_ORDER).collect(Collectors.toList());
            case 2 -> Util.list("<id>");
            default -> super.tabComplete(sender, args);
        };
    }

    @Override
    public List<String> getCommandNames() {
        return List.of("list");
    }

    @Override
    public void printHelp(CommandSender sender) {
        ChatUtils.send(sender, SurveysPlugin.CHATPREFIX + "/surveys list <group/question/reward> [id] - Listet Sachen auf");
    }
}
