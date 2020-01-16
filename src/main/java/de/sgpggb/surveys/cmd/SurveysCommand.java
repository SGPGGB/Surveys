package de.sgpggb.surveys.cmd;

import de.sgpggb.pluginutilitieslibbungee.Logging;
import de.sgpggb.pluginutilitieslibbungee.cmd.CustomCommandHandler;
import de.sgpggb.surveys.SurveysPlugin;
import de.sgpggb.surveys.models.Answer;
import de.sgpggb.surveys.models.Survey;
import de.sgpggb.surveys.util.Util;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class SurveysCommand extends CustomCommandHandler {

    public SurveysCommand(String name, String permission, Logging logger) {
        super(name, permission, logger);
    }

    public void execute(CommandSender sender, String[] args) {
        SurveysPlugin plugin = SurveysPlugin.getInstance();
        UUID senderuuid = (sender instanceof ProxiedPlayer) ? ((ProxiedPlayer) sender).getUniqueId() : null;

        if (args.length == 1 && "help".startsWith(args[0].toLowerCase())) {
            printHelp(sender);
            return;
        }

        if (handleCommand(sender, args)) {
            return;
        }

        if (sender.hasPermission("surveys.use") && args.length == 0) {
            //get all active surveys
            List<Survey> activeUnanswered = plugin.getManager().getActiveUnanswered(senderuuid);
            if (activeUnanswered.isEmpty()) {
                sender.sendMessage(SurveysPlugin.CHATPREFIX + "Derzeit sind keine Umfragen verfügbar");
                return;
            }
            sender.sendMessage(SurveysPlugin.CHATPREFIX + "Folgende Umfragen sind verfügbar:");
            for (Survey s : activeUnanswered) {
                sender.sendMessage(SurveysPlugin.CHATPREFIX
                        + ChatColor.GRAY
                        + "[noch "
                        + Util.duration(Timestamp.valueOf(LocalDateTime.now()), s.getTimeto())
                        + "] "
                        + ChatColor.RESET
                        + s.getText());
                ComponentBuilder cb = new ComponentBuilder(SurveysPlugin.CHATPREFIX);
                for (Answer a : s.getAnswers().values()) {
                    cb.append("[" + a.getText() + "] ")
                            .event(new ClickEvent(Action.RUN_COMMAND, "/surveys vote " + s.getId() + " " + a.getId()));
                }
                cb.append("[Umfrage ignorieren]").color(ChatColor.RED).event(new ClickEvent(Action.RUN_COMMAND, "/surveys ignore " + s.getId()));
                sender.sendMessage(cb.create());
            }
        } else {
            sender.sendMessage(new ComponentBuilder("Unbekannter Befehl!").color(ChatColor.RED).create());
        }
    }
}
