package de.sgpggb.surveys.misc;


import net.md_5.bungee.api.CommandSender;

public enum Permissions {

    USE("surveys.use"),
    MOD("surveys.mod"),
    ADMIN("surveys.admin"),
    ;

    String perm;
    Permissions(String perm) {
        this.perm = perm;
    }

    public boolean check(CommandSender sender) {
        return sender.hasPermission(perm);
    }
}
