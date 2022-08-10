package me.txmc.illegalcreator;

import me.txmc.illegalcreator.command.IllegalCreateCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class IllegalCreator extends JavaPlugin {

    @Override
    public void onEnable() {
        getCommand("illegalcreate").setExecutor(new IllegalCreateCommand(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
