package de.sgpggb.surveys.cmd;

import de.sgpggb.pluginutilitieslibbungee.cmd.CustomCommand;
import de.sgpggb.pluginutilitieslibbungee.utils.StringUtils;
import de.sgpggb.surveys.SurveysPlugin;
import java.util.Arrays;
import java.util.List;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ReloadCommand extends CustomCommand{

    public ReloadCommand() {
        super(true);
    }
    
    @Override
    protected boolean checkPermission(CommandSender sender) {
        return sender.hasPermission("surveys.admin") && sender instanceof ProxiedPlayer;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        SurveysPlugin.getInstance().getManager().reloadSurveys();
    }

    @Override
    public List<String> getCommandNames() {
        return Arrays.asList("reload");
    }

    @Override
    public void printHelp(CommandSender sender) {
    }

}
