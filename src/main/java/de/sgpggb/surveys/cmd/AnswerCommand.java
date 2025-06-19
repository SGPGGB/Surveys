package de.sgpggb.surveys.cmd;

import de.sgpggb.pluginutilitieslibbungee.Logging;
import de.sgpggb.pluginutilitieslibbungee.cmd.CustomCommand;
import de.sgpggb.pluginutilitieslibbungee.utils.ChatUtils;
import de.sgpggb.pluginutilitieslibbungee.utils.Util;
import de.sgpggb.surveys.Manager;
import de.sgpggb.surveys.SurveysPlugin;
import de.sgpggb.surveys.misc.Permissions;
import de.sgpggb.surveys.misc.Utils;
import de.sgpggb.surveys.model.AnswerType;
import de.sgpggb.surveys.model.Choice;
import de.sgpggb.surveys.model.Question;
import de.sgpggb.surveys.model.User;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;


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

        List<Choice> currentAnswers = manager.getAnswerCache(user.getUuid());

        //answer the question
        if (args[0].equalsIgnoreCase("#confirm")) {
            if (currentAnswers.isEmpty()) {
                ChatUtils.send(player, prefix + "<red>Du hast noch keine Antwort abgegeben.");
                return;
            }

            manager.answer(user, currentAnswers);
            manager.removeAnswerCache(user.getUuid());
            return;
        }

        //remove all color codes from answer
        String answer = String.join(" ", args);
        log.info("answer " + answer);

        //get the correct answer choice from question
        Choice answerChoice = null;
        for (Choice c : question.getChoices()) {
            if (c.getText().equalsIgnoreCase(answer))
                answerChoice = c;
        }

        if (question.getAnswerType().equals(AnswerType.FREE_TEXT)) {
            currentAnswers.clear();
            currentAnswers.add(new Choice(answer));
        } else {
            if (answerChoice == null) {
                ChatUtils.send(player, prefix + "<red>Das ist keine passende Antwort!");
                return;
            }
            switch (question.getAnswerType()) {
                case NUMERICAL, SINGLE_CHOICE -> {
                    currentAnswers.clear();
                    currentAnswers.add(answerChoice);
                }
                case MULTIPLE_CHOICE -> {
                    if (currentAnswers.contains(answerChoice)) {
                        currentAnswers.remove(answerChoice);
                    } else {
                        if (currentAnswers.size() >= question.getChoicesAmount()) {
                            ChatUtils.send(player, prefix + "<red>Du darfst maximal " + question.getChoicesAmount() + " Antworten auswählen!");
                            return;
                        }
                        currentAnswers.add(answerChoice);
                    }
                }
                default -> throw new IllegalStateException("default value in answer command");
            }
        }
        manager.addAnswerToCache(user, currentAnswers);

        manager.sendQuestion(user);

        if (currentAnswers.isEmpty())
            return;

        ChatUtils.send(player, prefix + "Deine Antwort: ");

        StringBuilder text = new StringBuilder();
        switch (question.getAnswerType()) {
            case FREE_TEXT -> {
                text.append(currentAnswers.getFirst().getText());
            }
            case NUMERICAL, SINGLE_CHOICE, MULTIPLE_CHOICE -> {
                for (Choice c : currentAnswers) {
                    text.append("<").append(c.getColor()).append(">[").append(c.getText()).append("] ");
                }
            }
        };

        ChatUtils.send(player, prefix + text);
        ChatUtils.send(player, prefix + "<green><click:run_command:/surveys answer #confirm>" +
            "Klicke um deine Antwort zu bestätigen!");
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
        ChatUtils.send(sender, SurveysPlugin.CHATPREFIX + "/surveys answer - Antwort geben");
    }
}
