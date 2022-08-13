package me.txmc.illegalcreator.command.commands;

import me.txmc.illegalcreator.IllegalCreator;
import me.txmc.illegalcreator.command.BaseCommand;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AttributesCommand extends BaseCommand {

    private final List<String> possibleArgs;

    public AttributesCommand(IllegalCreator plugin) {
        super(plugin);
        possibleArgs = new ArrayList<>();
        try {
            for (Field field : GenericAttributes.class.getDeclaredFields()) {
                if (field.getType() != IAttribute.class) continue;
                IAttribute attribute = (IAttribute) field.get(null);
                possibleArgs.add(attribute.getName());
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        possibleArgs.addAll(Arrays.asList("all", "random"));
        possibleArgs.sort(String::compareToIgnoreCase);
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
        return possibleArgs;
    }

    @Override
    public String getUsage() {
        return "attribute <name|all|random> <level> <slot> <operation>";
    }
}
