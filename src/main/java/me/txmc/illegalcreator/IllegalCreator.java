package me.txmc.illegalcreator;

import me.txmc.illegalcreator.command.IllegalCreateCommand;
import me.txmc.illegalcreator.listener.ProjectileListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class IllegalCreator extends JavaPlugin {

    @Override
    public void onEnable() {
        getCommand("illegalcreate").setExecutor(new IllegalCreateCommand(this));
        Bukkit.getPluginManager().registerEvents(new ProjectileListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
