package de.sgpggb.surveys.listener;


import de.sgpggb.surveys.SurveysPlugin;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.concurrent.TimeUnit;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(ServerConnectedEvent event) {
        SurveysPlugin.getInstance().getProxy().getScheduler().schedule(SurveysPlugin.getInstance(), () -> {
            SurveysPlugin.getInstance().getManager().checkLogin(event.getPlayer());
        }, 45, TimeUnit.SECONDS);
    }


}
