package me.txmc.illegalcreator.command;

import me.txmc.illegalcreator.IllegalCreator;
import me.txmc.illegalcreator.Utils;
import me.txmc.illegalcreator.command.commands.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.*;

public class IllegalCreateCommand implements TabExecutor {
    private final IllegalCreator plugin;
    private final Map<String, BaseCommand> subCommands;

    public IllegalCreateCommand(IllegalCreator plugin) {
        this.plugin = plugin;
        subCommands = new HashMap<>();
        subCommands.put("everyench", new EveryEnchant(plugin));
        subCommands.put("stackall", new StackAllCommand(plugin));
        subCommands.put("fillall", new FillAllCommand(plugin));
        subCommands.put("stack", new StackCommand(plugin));
        subCommands.put("attribute", new AttributesCommand(plugin));
        subCommands.put("statistics", new StatisticsCommand(plugin));
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length >= 1) {
            BaseCommand sub = subCommands.getOrDefault(args[0], null);
            if (sub == null) {
                Utils.sendMessage(commandSender, "&cUnknown SubCommand. The commands are as follows");
                Utils.sendMessage(commandSender, Utils.formatList(new ArrayList<>(subCommands.keySet())));
                return true;
            }
            try {
                sub.execute(commandSender, Arrays.copyOfRange(args, 1, args.length));
            } catch (Throwable t) {
                Utils.sendMessage(commandSender, "&cFailed to execute command due to &r%s:%s&r&c. This is most likely due to user error", t.getClass().getSimpleName(), t.getMessage());
                t.printStackTrace();
            }
        } else {
            Utils.sendMessage(commandSender, "&cYou must provide a SubCommand. The commands are as follows");
            Utils.sendMessage(commandSender, Utils.formatList(new ArrayList<>(subCommands.keySet())));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
        List<String> buf = new ArrayList<>();
        List<String> commands = new ArrayList<>(subCommands.keySet());
        commands.sort(String::compareToIgnoreCase);
        if (args.length == 1) {
            String first = args[0];
            commands.forEach(s -> {
                if (s.toLowerCase().startsWith(first)) buf.add(s);
            });
            return buf;
        } else if (args.length > 1) {
            BaseCommand sub = subCommands.getOrDefault(args[0], null);
            if (sub == null) return commands;
            return sub.tabComplete(commandSender, Arrays.copyOfRange(args, 1, args.length));
        } else return commands;
    }
}
