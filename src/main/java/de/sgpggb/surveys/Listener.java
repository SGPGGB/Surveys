package de.sgpggb.surveys;

import de.sgpggb.surveys.models.Answer;
import de.sgpggb.surveys.models.Survey;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.event.PostLoginEvent;

public class Listener {

    public void onPlayerJoin(PostLoginEvent e) {
        ProxyServer.getInstance().getScheduler().schedule(SurveysPlugin.getInstance(), new Runnable() {
            @Override
            public void run() {
                UUID senderuuid = e.getPlayer().getUniqueId();
                List<Survey> activeUnanswered = SurveysPlugin.getInstance().getManager().getActiveUnanswered(senderuuid);
                if (!activeUnanswered.isEmpty()) {
                    ComponentBuilder cb = new ComponentBuilder(SurveysPlugin.CHATPREFIX);
                    cb.append("Neue Umfrage verfügbar!").event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/surveys"));
                    e.getPlayer().sendMessage(cb.create());
                }
            }
        }, 30, TimeUnit.SECONDS);
    }
}
