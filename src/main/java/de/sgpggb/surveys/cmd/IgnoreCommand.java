package de.sgpggb.surveys.cmd;

import de.sgpggb.pluginutilitieslibbungee.cmd.CustomCommand;
import de.sgpggb.pluginutilitieslibbungee.utils.StringUtils;
import de.sgpggb.surveys.SurveysPlugin;
import java.util.Arrays;
import java.util.List;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class IgnoreCommand extends CustomCommand{

    public IgnoreCommand() {
        super(true);
    }
    
    @Override
    protected boolean checkPermission(CommandSender sender) {
        return sender.hasPermission("surveys.use") && sender instanceof ProxiedPlayer;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        ProxiedPlayer p = (ProxiedPlayer) sender;
        if (args.length != 1 || !StringUtils.isInt(args[0])) {
            return;
        }
        int survid = Integer.parseInt(args[0]);
        Integer answerid = null;
        SurveysPlugin.getInstance().getManager().addUserAnswer(p, survid, answerid);
    }

    @Override
    public List<String> getCommandNames() {
        return Arrays.asList("ignore");
    }

    @Override
    public void printHelp(CommandSender sender) {
    }

}
