package me.txmc.illegalcreator.command.commands;

import me.txmc.illegalcreator.IllegalCreator;
import me.txmc.illegalcreator.command.BaseCommand;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.ItemStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class StackCommand extends BaseCommand {
    List<String> tabs = Arrays.asList("1", "16", "64", "127");

    public StackCommand(IllegalCreator plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws Throwable {
        if (sender instanceof Player) {
            EntityPlayer player = fromSender(sender);
            ItemStack itemStack = player.inventory.getItemInHand();
            if (!itemStack.isEmpty()) {
                if (args.length == 1) {
                    int amount = Integer.parseInt(args[0]);
                    if (amount <= 127) {
                        itemStack.setCount(amount);
                        sendMessage(sender, "&3Successfully stacked your item to &r&a%d&r", amount);
                    } else sendMessage(sender, "&cItems cannot stack above 127");
                } else sendMessage(sender, "&c/ic stack <amount>");
            } else sendMessage(sender, "&cYou must be holding an item");
        } else sendMessage(sender, "&cYou must be a player");

    }

    @Override
    public List<String> getPossibleArgs() {
        return tabs;
    }

    @Override
    public String getUsage() {
        return null;
    }
}
