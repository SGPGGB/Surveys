package de.sgpggb.surveys.listener;


import de.sgpggb.surveys.SurveysPlugin;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(ServerConnectedEvent event) {
        SurveysPlugin.getInstance().getManager().onLogin(event.getPlayer());
    }

    @EventHandler
    public void onPlayerLeave(PlayerDisconnectEvent event) {
        SurveysPlugin.getInstance().getManager().onLogout(event.getPlayer());
    }


}
