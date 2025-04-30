package de.sgpggb.surveys.cmd;

import de.sgpggb.pluginutilitieslibbungee.cmd.CustomCommand;
import de.sgpggb.pluginutilitieslibbungee.utils.ChatUtils;
import de.sgpggb.surveys.SurveysPlugin;
import de.sgpggb.surveys.misc.Permissions;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;

public class NextCommand extends CustomCommand {
    @Override
    protected boolean checkPermission(CommandSender sender) {
        return Permissions.USE.check(sender);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        String prefix = SurveysPlugin.CHATPREFIX;
        if (!(sender instanceof ProxiedPlayer player)) {
            ChatUtils.send(sender, prefix + "<red>Nur ingame!");
            return;
        }

        SurveysPlugin.getInstance().getManager().sendQuestion(player);
    }

    @Override
    public List<String> getCommandNames() {
        return List.of("next", "go");
    }

    @Override
    public void printHelp(CommandSender sender) {
        ChatUtils.send(sender, SurveysPlugin.CHATPREFIX + "/surveys next - Mit Umfrage weiter machen");
    }
}
