package de.sgpggb.surveys.cmd;

import de.sgpggb.pluginutilitieslibbungee.cmd.CustomCommand;
import de.sgpggb.pluginutilitieslibbungee.utils.PlayerUtils;
import de.sgpggb.surveys.SurveysPlugin;
import de.sgpggb.surveys.model.User;
import net.md_5.bungee.api.CommandSender;

import java.util.List;
import java.util.UUID;

public class TempCommand extends CustomCommand {
    @Override
    protected boolean checkPermission(CommandSender sender) {
        return true;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length != 1)
            return;
        String name = args[0];
        UUID uuid = PlayerUtils.getUUID(name);
        if (uuid == null) {
            sender.sendMessage("Nicht gefunden: " + name);
            return;
        }
        User user = SurveysPlugin.getInstance().getManager().getUser(uuid);
        SurveysPlugin.getInstance().getDbAdapter().deleteUser(user);
        sender.sendMessage("Gelöscht, bitte reloggen: " + user.getName());
    }

    @Override
    public List<String> getCommandNames() {
        return List.of("temp");
    }

    @Override
    public void printHelp(CommandSender sender) {

    }
}
