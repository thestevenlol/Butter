package me.stevenlol.butter.utils;

import me.stevenlol.butter.Main;

public class Config {

    public Config() {
        Main.getPlugin().getConfig().options().copyDefaults(true);
        Main.getPlugin().saveDefaultConfig();
    }

    public String getPrefix() {
        return Main.getPlugin().getConfig().getString("prefix");
    }

}
