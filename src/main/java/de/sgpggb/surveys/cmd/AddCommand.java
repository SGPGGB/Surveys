package de.sgpggb.surveys.cmd;

import de.sgpggb.pluginutilitieslibbungee.cmd.CustomCommand;
import de.sgpggb.pluginutilitieslibbungee.utils.ChatUtils;
import de.sgpggb.pluginutilitieslibbungee.utils.Util;
import de.sgpggb.surveys.SurveysPlugin;
import de.sgpggb.surveys.misc.Permissions;
import de.sgpggb.surveys.model.Group;
import de.sgpggb.surveys.model.Question;
import de.sgpggb.surveys.model.Reward;
import net.md_5.bungee.api.CommandSender;

import java.util.List;
import java.util.stream.Collectors;

public class AddCommand extends CustomCommand {
    @Override
    protected boolean checkPermission(CommandSender sender) {
        return Permissions.ADMIN.check(sender);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        //surveys add <question/reward/group>
        String prefix = SurveysPlugin.CHATPREFIX;
        if (args.length == 0) {
            ChatUtils.send(sender,prefix + "<red>Zu wenig Argumente, siehe Doku!");
            return;
        }
        String value = args[0];
        switch (value.toLowerCase()) {
            case "question" -> {
                Question question = new Question();
                SurveysPlugin.getInstance().getDbAdapter().saveQuestion(question);
                ChatUtils.send(sender, prefix + "<green>Question mit ID " + question.getId() + " erstellt!");
            }
            case "reward" -> {
                Reward reward = new Reward();
                SurveysPlugin.getInstance().getDbAdapter().saveReward(reward);
                ChatUtils.send(sender, prefix + "<green>Reward mit ID " + reward.getId() + " erstellt!");
            }
            case "group" -> {
                Group group = new Group();
                SurveysPlugin.getInstance().getDbAdapter().saveGroup(group);
                ChatUtils.send(sender, prefix + "<green>Group mit ID " + group.getId() + " erstellt!");
            }
            default -> {
                ChatUtils.send(sender, prefix + "<red>Die Option " + value + " gibt es nicht!");
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
            default -> super.tabComplete(sender, args);
        };
    }

    @Override
    public List<String> getCommandNames() {
        return List.of("add");
    }

    @Override
    public void printHelp(CommandSender sender) {
        ChatUtils.send(sender, SurveysPlugin.CHATPREFIX + "/surveys add <options> - Sachen hinzufügen");
    }
}
