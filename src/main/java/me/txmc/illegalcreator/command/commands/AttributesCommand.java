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
import java.util.concurrent.ThreadLocalRandom;

public class AttributesCommand extends BaseCommand {

    private final List<String> possibleArgs;
    private final List<String> attributeNames;

    private final List<String> slotNames;

    public AttributesCommand(IllegalCreator plugin) {
        super(plugin);
        possibleArgs = new ArrayList<>();
        attributeNames = new ArrayList<>();
        slotNames = Arrays.asList("mainhand", "offhand", "chest", "legs", "head", "feet");
        try {
            for (Field field : GenericAttributes.class.getDeclaredFields()) {
                if (field.getType() != IAttribute.class) continue;
                IAttribute attribute = (IAttribute) field.get(null);
                possibleArgs.add(attribute.getName());
                attributeNames.add(attribute.getName());
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        possibleArgs.addAll(Arrays.asList("all", "random"));
        possibleArgs.addAll(slotNames);
        possibleArgs.addAll(Arrays.asList("0", "1", "2"));
        possibleArgs.sort(String::compareToIgnoreCase);
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws Throwable {
        if (sender instanceof Player) {
            EntityPlayer player = fromSender(sender);
            ItemStack item = player.inventory.getItemInHand();
            if (!item.isEmpty()) {
                if (!item.hasTag()) item.setTag(new NBTTagCompound());
                double amount = -1;
                if (args.length >= 2) amount = Double.parseDouble(args[1]);
                String slot = null;
                if (args.length >= 3) slot = args[2];
                int operation;
                if (args.length != 4) operation = 0;
                else operation = Integer.parseInt(args[3]);
                switch (args[0]) {
                    case "all":
                        for (String arg : attributeNames) {
                            addAttribute(item, amount, arg, operation, slot);
                        }
                        break;
                    case "random":
                        int rand = ThreadLocalRandom.current().nextInt(1, attributeNames.size());
                        String randomAttribute = attributeNames.get(rand);
                        addAttribute(item,
                                ThreadLocalRandom.current().nextDouble(Integer.MIN_VALUE, Integer.MAX_VALUE), randomAttribute,
                                ThreadLocalRandom.current().nextInt(0, 3),
                                slotNames.get(ThreadLocalRandom.current().nextInt(0, slotNames.size())));
                        break;
                    default:
                        String attribute = args[0];
                        addAttribute(item, amount, attribute, operation, slot);
                        break;
                }
            } else sendMessage(sender, "&cYou must be holding an item in your main hand");
        } else sendMessage(sender, "&cYou must be a player");
    }

    @Override
    public List<String> getPossibleArgs() {
        return possibleArgs;
    }

    @Override
    public String getUsage() {
        return "attribute <name|all|random> <amount> <slot> <operation>";
    }
}
