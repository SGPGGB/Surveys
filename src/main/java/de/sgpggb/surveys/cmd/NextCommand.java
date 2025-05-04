package de.sgpggb.surveys.cmd;

import de.sgpggb.pluginutilitieslibbungee.cmd.CustomCommand;
import de.sgpggb.pluginutilitieslibbungee.utils.ChatUtils;
import de.sgpggb.surveys.SurveysPlugin;
import de.sgpggb.surveys.misc.Permissions;
import de.sgpggb.surveys.model.User;
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

        User user = SurveysPlugin.getInstance().getManager().getUser(player.getUniqueId());
        if (user == null) {
            SurveysPlugin.getInstance().getLog().error("no user object on player " + player.getName());
            return;
        }

        SurveysPlugin.getInstance().getManager().sendQuestion(user);
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
