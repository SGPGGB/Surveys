package de.sgpggb.surveys.cmd;

import de.sgpggb.pluginutilitieslibbungee.Logging;
import de.sgpggb.pluginutilitieslibbungee.cmd.CustomCommand;
import de.sgpggb.pluginutilitieslibbungee.utils.ChatUtils;
import de.sgpggb.surveys.Manager;
import de.sgpggb.surveys.SurveysPlugin;
import de.sgpggb.surveys.misc.Permissions;
import de.sgpggb.surveys.misc.Utils;
import de.sgpggb.surveys.model.AnswerType;
import de.sgpggb.surveys.model.Question;
import de.sgpggb.surveys.model.User;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


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
        Logging log = SurveysPlugin.getInstance().getLog();

        User user = manager.getUser(player.getUniqueId());
        if (user == null) {
            log.error("no user object on player " + player.getName());
            return;
        }


        Question question = user.getCurrentQuestion();
        if (question == null) {
            ChatUtils.send(player,prefix + "<red>Du hast aktuell keine Umfrage!");
            return;
        }

        List<String> list = new ArrayList<>(manager.getAnswerList(user.getUuid()));

        //answer the question
        if (args[0].equalsIgnoreCase("#confirm")) {
            if (list.isEmpty()) {
                ChatUtils.send(player, prefix + "<red>Du hast noch keine Antwort abgegeben.");
                return;
            }

            manager.answer(user, String.join(";", list));
            manager.removeAnswerCache(user.getUuid());
            return;
        }

        //remove all color codes from answer
        String answer = Utils.plain(String.join(" ", args));
        String additional = "";

        switch (question.getAnswerType()) {
            case FREE_TEXT, SINGLE_CHOICE, NUMERICAL -> {
                manager.addAnswerToCache(user, answer);
            }
            case MULTIPLE_CHOICE -> {
                if (list.contains(answer)) {
                    list.remove(answer);
                } else {
                    if (list.size() >= question.getChoicesAmount()) {
                        ChatUtils.send(player, prefix + "<red>Du darfst maximal " + question.getChoicesAmount() + " Antworten auswählen!");
                        return;
                    }
                    list.add(answer);
                }
                manager.addAnswerToCache(user, Utils.stringListToString(list));
            }
        }

        manager.sendQuestion(user);

        //stop if no answer is set
        if (list.isEmpty())
            return;

        ChatUtils.send(player, prefix + "Deine Antwort: ");

        String text = switch (question.getAnswerType()) {
            case FREE_TEXT -> manager.getAnswerCache(user.getUuid());
            case NUMERICAL, SINGLE_CHOICE, MULTIPLE_CHOICE -> list.stream().map(s -> "[" + s + "] ").collect(Collectors.joining());
        };

        ChatUtils.send(player, prefix + text);
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
