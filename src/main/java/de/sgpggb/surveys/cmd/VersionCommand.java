package de.sgpggb.surveys.cmd;

import de.sgpggb.pluginutilitieslibbungee.cmd.CustomCommand;
import de.sgpggb.pluginutilitieslibbungee.utils.ChatUtils;
import de.sgpggb.surveys.SurveysPlugin;
import de.sgpggb.surveys.misc.Permissions;
import net.md_5.bungee.api.CommandSender;

import java.util.List;

public class VersionCommand extends CustomCommand {
    @Override
    protected boolean checkPermission(CommandSender sender) {
        return Permissions.ADMIN.check(sender);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        ChatUtils.send(sender, SurveysPlugin.CHATPREFIX + "<green>Pluginversion: " + SurveysPlugin.getInstance().getDescription().getVersion());
    }

    @Override
    public List<String> getCommandNames() {
        return List.of("version");
    }

    @Override
    public void printHelp(CommandSender sender) {
        ChatUtils.send(sender, SurveysPlugin.CHATPREFIX + "/surveys version - Pluginversion anzeigen");
    }
}
