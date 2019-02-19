package de.sgpggb.surveys.cmd;

import de.sgpggb.pluginutilitieslibbungee.cmd.CustomCommand;
import de.sgpggb.pluginutilitieslibbungee.utils.StringUtils;
import de.sgpggb.surveys.SurveysPlugin;
import java.util.Arrays;
import java.util.List;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class VoteCommand extends CustomCommand {

    public VoteCommand() {
        super(true);
    }

    @Override
    protected boolean checkPermission(CommandSender sender) {
        return sender.hasPermission("surveys.use") && sender instanceof ProxiedPlayer;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        ProxiedPlayer p = (ProxiedPlayer) sender;
        if (args.length != 2 || !StringUtils.isInt(args[0]) || !StringUtils.isInt(args[1])) {
            return;
        }
        int survid = Integer.parseInt(args[0]);
        int ansid = Integer.parseInt(args[1]);
        SurveysPlugin.getInstance().getManager().addUserAnswer(p, survid, ansid);

    }

    @Override
    public List<String> getCommandNames() {
        return Arrays.asList("vote");
    }

    @Override
    public void printHelp(CommandSender sender) {
    }

}
