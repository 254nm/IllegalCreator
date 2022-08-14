package me.txmc.illegalcreator.command.commands;

import me.txmc.illegalcreator.IllegalCreator;
import me.txmc.illegalcreator.Utils;
import me.txmc.illegalcreator.command.BaseCommand;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class StatisticsCommand extends BaseCommand {
    public StatisticsCommand(IllegalCreator plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws Throwable {
        if (sender instanceof Player) {
            EntityPlayer player = fromSender(sender);
            ItemStack itemStack = player.inventory.getItemInHand();
            if (!itemStack.isEmpty()) {
                String lore = genLore(itemStack);
                setLore(itemStack, Utils.translate(lore).split("\n"));
            } else sendMessage(sender, "&cYou must be holding an item");
        } else sendMessage(sender, "&cYou must be a player");
    }

    private String genLore(ItemStack itemStack) {
        StringBuilder lore = new StringBuilder();
        if (itemStack.getItem() instanceof ItemShulkerBox) {
            int itemCount = enumerateItems(itemStack);
            lore.append("&r&3&lItem Count:&r&a ").append(itemCount).append(" (").append(round(itemCount / 64, 2)).append(") Stacks&r\n");
        }
        lore.append("&r&3&lSize in bytes:&r&a ").append((!itemStack.hasTag()) ? 48 : itemStack.getTag().toString().getBytes().length + 48).append("/2097152\n");
        lore.append("&r&3&lItemID:&r&a ").append(Item.getId(itemStack.getItem())).append("\n");
        lore.append("&r&3&lData/Durability:&r&a ").append(itemStack.getData()).append("\n");
        if (itemStack.hasTag()) {
            NBTTagCompound tag = itemStack.getTag();
            lore.append("&r&3&lTags:&r&a \n");
            for (String name : itemStack.getTag().map.keySet()) {
                lore.append("&r&7   - &r&a").append(tag.get(name).getClass().getSimpleName()).append(": ").append(name).append("&r\n");
            }
        }
        lore.append("&r&3&lDate Created:&r&a ").append(new Date(System.currentTimeMillis()));
        return lore.toString();
    }

    private int enumerateItems(ItemStack itemStack) {
        int count = 0;
        if (itemStack.getItem() instanceof ItemShulkerBox) {
            ItemStack[] items = getShulkerContents(itemStack).values().toArray(new ItemStack[0]);
            for (ItemStack internal : items) {
                if (internal.getItem() instanceof ItemShulkerBox) {
                    double internalCount = enumerateItems(internal);
                    count += internalCount;
                } else count += internal.getCount();
            }
        }
        return count;
    }

    @Override
    public List<String> getPossibleArgs() {
        return Collections.emptyList();
    }

    @Override
    public String getUsage() {
        return null;
    }
}
