package me.txmc.illegalcreator.command.commands;

import me.txmc.illegalcreator.IllegalCreator;
import me.txmc.illegalcreator.command.BaseCommand;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.ItemStack;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AttributesCommand extends BaseCommand {

    public AttributesCommand(IllegalCreator plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws Throwable {
        if (sender instanceof Player) {
            EntityPlayer player = fromSender(sender);
            ItemStack item = player.inventory.getItemInHand();
            if (!item.isEmpty()) {
                if (!item.hasTag()) item.setTag(new NBTTagCompound());

            }
        }
    }

    @Override
    public List<String> getPossibleArgs() {
        return Collections.emptyList();
    }

    @Override
    public String getUsage() {
        return "attribute <name|all|random> <level> <slot> <operation>";
    }
}
