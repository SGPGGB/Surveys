package de.sgpggb.surveys.cmd;

import de.sgpggb.pluginutilitieslibbungee.cmd.CustomCommand;
import de.sgpggb.pluginutilitieslibbungee.utils.ChatUtils;
import de.sgpggb.surveys.Manager;
import de.sgpggb.surveys.SurveysPlugin;
import de.sgpggb.surveys.misc.Permissions;
import de.sgpggb.surveys.model.Question;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;
import java.util.UUID;


public class AnswerCommand extends CustomCommand {

    @Override
    protected void execute(CommandSender sender, String[] args) {
        //surveys answer <antwort>
        String prefix = SurveysPlugin.CHATPREFIX;
        if (!(sender instanceof ProxiedPlayer player)) {
            ChatUtils.send(sender, prefix + "<red>Nur ingame!");
            return;
        }

        Manager manager = SurveysPlugin.getInstance().getManager();

        Question question = manager.getCurrentQuestion(player.getUniqueId());
        if (question == null) {
            ChatUtils.send(player,prefix + "<red>Du hast aktuell keine Umfrage!");
            return;
        }

        List<String> list = manager.getAnswerList(player.getUniqueId());

        //answer the question
        if (args[0].equalsIgnoreCase("#confirm")) {
            manager.answer(player, String.join(";", list));
            manager.removeAnswerCache(player.getUniqueId());
            return;
        }

        String answer = String.join(" ", args);
        String additional = "";
        switch (question.getAnswerType()) {
            case FREE_TEXT -> {
                manager.addAnswerToCache(player.getUniqueId(), answer);
            }
            case SINGLE_CHOICE -> {
                list.clear();
                list.add(answer);
                manager.addAnswerToCache(player.getUniqueId(), String.join(";", list));
            }
            case MULTIPLE_CHOICE -> {
                if (list.contains(answer))
                    list.remove(answer);
                else
                    list.add(answer);
                manager.addAnswerToCache(player.getUniqueId(), String.join(";", list));
            }
        }

        ChatUtils.send(player, prefix + "Deine Antwort: ");
        ChatUtils.send(player, prefix + list.stream().map(s -> "[" + s + "] ").toString());
        ChatUtils.send(player, prefix + "<green><click:run_command:/surveys answer #confirm>" +
            "Klicke um deine Antwort zu bestätigen" + additional + "!");
    }

    @Override
    protected boolean checkPermission(CommandSender sender) {
        return Permissions.USE.check(sender);
    }

    @Override
    public List<String> getCommandNames() {
        return List.of("answer");
    }

    @Override
    public void printHelp(CommandSender sender) {
        ChatUtils.send(sender, SurveysPlugin.CHATPREFIX + "/surveys answer - Antworten");
    }
}
