package me.txmc.illegalcreator.command.commands;

import me.txmc.illegalcreator.IllegalCreator;
import me.txmc.illegalcreator.command.BaseCommand;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.ItemShulkerBox;
import net.minecraft.server.v1_12_R1.ItemStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class StackAllCommand extends BaseCommand {
    private final List<String> possibleArgs = Arrays.asList("max", "random", "1", "16", "64", "127");
    private final ThreadLocalRandom random = ThreadLocalRandom.current();

    public StackAllCommand(IllegalCreator plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws Throwable {
        if (sender instanceof Player) {
            EntityPlayer player = fromSender(sender);
            ItemStack itemStack = player.inventory.getItemInHand();
            if (!itemStack.isEmpty()) {
                if (itemStack.getItem() instanceof ItemShulkerBox) {
                    Map<Byte, ItemStack> contents = getShulkerContents(itemStack);
                    if (args.length == 1) {
                        String arg = args[0].toLowerCase();
                        for (ItemStack shulkerItem : contents.values()) {
                            switch (arg) {
                                case "max":
                                    shulkerItem.setCount(shulkerItem.getItem().getMaxStackSize());
                                    break;
                                case "random":
                                    shulkerItem.setCount(random.nextInt(1, 127));
                                    break;
                                default:
                                    int amount = Integer.parseInt(arg);
                                    if (amount <= 127) {
                                        shulkerItem.setCount(amount);
                                    } else {
                                        sendMessage(sender, "&cItems cant stack past 127");
                                        return;
                                    }
                            }
                        }
                    } else {
                        for (ItemStack shulkerItem : contents.values()) {
                            shulkerItem.setCount(127);
                        }
                    }
                    setShulkerContents(itemStack, contents);
                    sendMessage(sender, "&3Successfully stacked everything in your shulkerbox");
                } else sendMessage(sender, "&cYou must be holding a ShulkerBox");
            } else sendMessage(sender, "&cYou must be holding a ShulkerBox");
        } else sendMessage(sender, "&cYou must be a player");
    }

    @Override
    public List<String> getPossibleArgs() {
        return possibleArgs;
    }

    @Override
    public String getUsage() {
        return "null";
    }
}
