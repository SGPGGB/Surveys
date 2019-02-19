package de.sgpggb.surveys;

import de.sgpggb.pluginutilitieslibbungee.CustomConfig;
import de.sgpggb.pluginutilitieslibbungee.CustomJavaPlugin;
import de.sgpggb.pluginutilitieslibbungee.cmd.CustomCommandHandler;
import de.sgpggb.pluginutilitieslibbungee.sql.DBMigration;
import de.sgpggb.surveys.cmd.IgnoreCommand;
import de.sgpggb.surveys.cmd.SurveysCommand;
import de.sgpggb.surveys.cmd.VoteCommand;
import de.sgpggb.surveys.util.DBMigrationSurveys;
import de.sgpggb.surveys.util.SurveysConfig;
import net.md_5.bungee.api.ChatColor;

public class SurveysPlugin extends CustomJavaPlugin {

    private static SurveysPlugin instance = null;
    public final static String CHATPREFIX = ChatColor.WHITE + "[" + ChatColor.RED + "Su" + ChatColor.GRAY + "rveys"
            + ChatColor.WHITE + "] " + ChatColor.RESET;

    public static SurveysPlugin getInstance() {
        return instance;
    }

    private SurveysConfig config;
    private SurveyManager manager;

    public SurveysPlugin() {
        instance = this;
        this.APIVERSION_MAJOR_REQ = 3;
        this.APIVERSION_MINOR_REQ = 0;
    }

    public void onLoad() {
        this.initConfig();
    }

    public void onEnable() {
        super.onEnable();
        if (!this.initLib()) {
            return;
        }
        DBMigration dbmigration = new DBMigrationSurveys(this.getSQLConnection(), this.getLog());
        dbmigration.checkDB();

        manager = new SurveyManager();

        CustomCommandHandler requestscmd = new SurveysCommand("surveys", null, this.getLog());
        requestscmd.registerCmd(new VoteCommand());
        requestscmd.registerCmd(new IgnoreCommand());
        this.getProxy().getPluginManager().registerCommand(this, requestscmd);
    }

    public void onDisable() {

    }

    @Override
    protected String getName() {
        return "Surveys";
    }

    private void initConfig() {
        this.config = new SurveysConfig(this);
    }

    @Override
    public CustomConfig getCustomConfig() {
        return config;
    }

    public SurveysConfig getConfig() {
        return config;
    }

    public SurveyManager getManager() {
        return manager;
    }
}
