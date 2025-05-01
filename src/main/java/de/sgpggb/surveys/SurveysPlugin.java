package de.sgpggb.surveys;

import de.sgpggb.pluginutilitieslibbungee.CustomConfigurationConstants;
import de.sgpggb.pluginutilitieslibbungee.CustomJavaPlugin;
import de.sgpggb.pluginutilitieslibbungee.cmd.CustomCommandHandler;
import de.sgpggb.surveys.cmd.AddCommand;
import de.sgpggb.surveys.cmd.AnswerCommand;
import de.sgpggb.surveys.cmd.EditCommand;
import de.sgpggb.surveys.cmd.ListCommand;
import de.sgpggb.surveys.cmd.NextCommand;
import de.sgpggb.surveys.cmd.ReloadCommand;
import de.sgpggb.surveys.cmd.VersionCommand;
import de.sgpggb.surveys.db.DBAdapter;
import de.sgpggb.surveys.listener.PlayerListener;
import net.md_5.bungee.api.ChatColor;

public class SurveysPlugin extends CustomJavaPlugin {

    private static SurveysPlugin instance = null;
    public final static String CHATPREFIX = ChatColor.WHITE + "[" + ChatColor.RED + "Su" + ChatColor.GRAY + "rveys"
            + ChatColor.WHITE + "] " + ChatColor.RESET;

    public static SurveysPlugin getInstance() {
        return instance;
    }

    private Manager manager;
    private DBAdapter dbAdapter;

    public SurveysPlugin() {
        super();
        instance = this;
        this.APIVERSION_MAJOR_REQ = 4;
        this.APIVERSION_MINOR_REQ = 0;
    }

    public void onEnable() {
        super.onEnable();
        if (!this.initLib())
            return;

        dbAdapter = new DBAdapter(getSQLConnection(), getLog());

        manager = new Manager();
        manager.loadAll();

        CustomCommandHandler cmd = new CustomCommandHandler("surveys", null, this.getLog());
        cmd.registerCmd(new AddCommand());
        cmd.registerCmd(new AnswerCommand());
        cmd.registerCmd(new EditCommand());
        cmd.registerCmd(new ListCommand());
        cmd.registerCmd(new NextCommand());
        cmd.registerCmd(new ReloadCommand());
        cmd.registerCmd(new VersionCommand());
        this.getProxy().getPluginManager().registerCommand(this, cmd);

        this.getProxy().getPluginManager().registerListener(this, new PlayerListener());
    }

    public static class ConfigurationConstants extends CustomConfigurationConstants {
        public static final String CONFIGKEY_DATABASE_MYSQL_TABLES_REQUESTS = "database.mysql.tables.requests";
    }

    public void onDisable() {

    }

    @Override
    public void reload() {
        super.reload();

    }

    @Override
    protected String getName() {
        return "Surveys";
    }

    @Override
    public void initConfig() {
        super.initConfig();
        //addConfigDefaults(ConfigurationConstants.CONFIGKEY_DEBUG, true);
    }

    public Manager getManager() {
        return manager;
    }

    public DBAdapter getDbAdapter() {
        return dbAdapter;
    }
}
