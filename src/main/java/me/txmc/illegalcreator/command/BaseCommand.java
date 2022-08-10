package me.txmc.illegalcreator.command;

import lombok.RequiredArgsConstructor;
import me.txmc.illegalcreator.IllegalCreator;
import me.txmc.illegalcreator.Utils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public abstract class BaseCommand extends Utils {
    protected final IllegalCreator plugin;

    public abstract void execute(CommandSender sender, String[] args) throws Throwable;

    public abstract List<String> getPossibleArgs();

    public List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> buf = new ArrayList<>();
        List<String> commands = getPossibleArgs();
        commands.sort(String::compareToIgnoreCase);
        if (args.length == 1) {
            String first = args[0];
            commands.forEach(s -> {
                if (s.toLowerCase().startsWith(first)) buf.add(s);
            });
            return buf;
        } else return commands;
    }

    public abstract String getUsage();
}
