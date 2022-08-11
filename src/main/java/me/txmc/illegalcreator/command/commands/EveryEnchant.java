package me.txmc.illegalcreator.command.commands;

import me.txmc.illegalcreator.IllegalCreator;
import me.txmc.illegalcreator.command.BaseCommand;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class EveryEnchant extends BaseCommand {
    private final List<String> possibleArgs = Arrays.asList("maxlevel", "32k", "random");
    private final ThreadLocalRandom random = ThreadLocalRandom.current();

    public EveryEnchant(IllegalCreator plugin) {
        super(plugin);
        possibleArgs.sort(String::compareToIgnoreCase);
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws Throwable {
        if (sender instanceof Player) {
            if (args.length == 1) {
                EntityPlayer player = fromSender(sender);
                ItemStack item = player.inventory.getItemInHand();
                if (!item.isEmpty()) {
                    if (!item.hasTag()) item.setTag(new NBTTagCompound());
                    NBTTagList ench = new NBTTagList();
                    Enchantment.enchantments.iterator().forEachRemaining(enchantment -> {
                        NBTTagCompound tag = new NBTTagCompound();
                        tag.setShort("id", (short) Enchantment.getId(enchantment));
                        switch (args[0].toLowerCase()) {
                            case "maxlevel":
                                tag.setShort("lvl", (short) enchantment.getMaxLevel());
                                break;
                            case "32k":
                                tag.setShort("lvl", (short) 32767);
                                break;
                            case "random":
                                tag.setShort("lvl", (short) random.nextInt(-32767, 32767));
                                break;
                            default:
                                int level = Integer.parseInt(args[0]);
                                tag.setShort("lvl", (short) level);
                                break;
                        }
                        ench.add(tag);
                    });
                    item.getTag().set("ench", ench);
                    sendMessage(sender, "&3Successfully applied all enchantments to&r&a %s&r", getItemName(item.getItem()));
                } else sendMessage(sender, "&cYou must be holding an item in your main hand");
            } else sendMessage(sender, "&c " + getUsage());
        } else sendMessage(sender, "&cYou must be a player");
    }

    @Override
    public List<String> getPossibleArgs() {
        return possibleArgs;
    }

    @Override
    public String getUsage() {
        return "everyench <maxlevel|32k|random|anynumber>";
    }
}
