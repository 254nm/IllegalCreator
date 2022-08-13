package me.txmc.illegalcreator;

import net.minecraft.server.v1_12_R1.*;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Utils {
    private static final String prefix = "&7[&r&6IllegalCreator&r&7]&r ";

    /**
     * Formats a list into a string
     *
     * @param toFormat The list to format
     * @return A String of the list contents ex: (3): PLS, JOIN B2T2
     */
    public static String formatList(List<String> toFormat) {
        StringBuilder builder = new StringBuilder();
        builder.append("&7(").append(toFormat.size()).append("):&r ");
        toFormat.forEach(s -> builder.append("&a").append(s).append("&r&7,&r "));
        return builder.substring(0, builder.length() - 4);
    }

    /**
     * Gets the method sendMessage(String) in the provided object and invokes it with the provided message
     *
     * @param sender The Object to attempt to send a message to.
     * @param format The format for {@link String#format(String, Object...)}
     * @param args   The arguments for {@link String#format(String, Object...)}
     */
    public static void sendMessage(CommandSender sender, String format, @Nullable Object... args) {
        format = prefix + format;
        format = String.format(format, args);
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', format));
    }

    /**
     * Attempts to cast the provided CommandSender to an NMS {@link EntityPlayer}
     *
     * @param sender the CommandSender
     * @return EntityPlayer The CommandSender as an NMS {@link EntityPlayer}
     */
    public static EntityPlayer fromSender(CommandSender sender) {
        Player player = (Player) sender;
        return ((CraftPlayer) player).getHandle();
    }

    public static String getItemName(Item item) {
        return item.getName().toLowerCase().replace("tile.", "").replace("item.", "");
    }

    /**
     * Will get the inventory contents of a ShulkerBox
     *
     * @param itemStack The shulker box
     * @return Will return a Map<Byte, ItemStack> (Slot, Item) if the shulker has contents otherwise an empty map
     * @throws IllegalArgumentException if {@param itemStack} is not a {@link ItemShulkerBox}
     */
    public static Map<Byte, ItemStack> getShulkerContents(ItemStack itemStack) {
        if (!(itemStack.getItem() instanceof ItemShulkerBox))
            throw new IllegalArgumentException("The item must be a shulker box");
        if (!itemStack.hasTag()) return new HashMap<>();
        Map<Byte, ItemStack> buf = new HashMap<>();
        NBTTagCompound tag = itemStack.getTag();
        NBTTagList items = tag.getCompound("BlockEntityTag").getList("Items", 10);
        items.list.stream().map(b -> (NBTTagCompound) b).forEach(c -> {
            byte slot = c.getByte("Slot");
            buf.put(slot, new ItemStack(c));
        });
        return buf;
    }

    /**
     * Will set the inventory contents of a ShulkerBox
     *
     * @param itemStack The shulker box
     * @param newItems  The new contents of the ShulkerBox
     * @throws IllegalArgumentException if {@param itemStack} is not a {@link ItemShulkerBox}
     */
    public static void setShulkerContents(ItemStack itemStack, Map<Byte, ItemStack> newItems) {
        if (!(itemStack.getItem() instanceof ItemShulkerBox))
            throw new IllegalArgumentException("The item must be a shulker box");
        if (!itemStack.hasTag()) itemStack.setTag(new NBTTagCompound());
        NBTTagCompound tag = itemStack.getTag();
        if (!tag.hasKey("BlockEntityTag")) {
            NBTTagCompound bet = new NBTTagCompound();
            bet.set("Items", new NBTTagList());
            tag.set("BlockEntityTag", bet);
        }
        NBTTagList items = new NBTTagList();
        newItems.forEach((slot, stack) -> {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setByte("Slot", slot);
            stack.save(compound);
            items.add(compound);
        });
        tag.getCompound("BlockEntityTag").set("Items", items);
    }

    public static String translate(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    /**
     * Will name or rename an item with the name provided
     *
     * @param item The item to name / rename
     * @param name The name
     * @return The item being renamed
     */
    public static ItemStack setItemName(ItemStack item, String name) {
        if (!item.hasTag()) item.setTag(new NBTTagCompound());
        if (!item.getTag().hasKey("display")) item.getTag().set("display", new NBTTagCompound());
        item.getTag().getCompound("display").set("Name", new NBTTagString(translate(name)));
        return item;
    }

    /**
     * Will drop an {@link ItemStack} as an {@link EntityItem} at the provided location
     *
     * @param world     The world to drop the item in
     * @param pos       The position to drop the item at
     * @param itemStack The item to drop
     */
    public static void dropItem(World world, BlockPosition pos, ItemStack itemStack) {
        EntityItem entity = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), itemStack);
        entity.pickupDelay = 10;
        world.addEntity(entity, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    /**
     * Will put the provided item in first available slot in the player's inventory or drop the item on the ground using {@link Utils#dropItem(World, BlockPosition, ItemStack)}
     *
     * @param player    The player to return the item to
     * @param itemStack The item to return
     */
    public static void giveItemBack(EntityPlayer player, ItemStack itemStack) {
        PlayerInventory inventory = player.inventory;
        int firstEmpty = inventory.getFirstEmptySlotIndex();
        if (firstEmpty == -1) {
            BlockPosition pos = new BlockPosition(player.locX, player.locY, player.locZ);
            dropItem(player.world, pos, itemStack);
        } else inventory.setItem(firstEmpty, itemStack);
    }

    public static void addAttribute(ItemStack item, double amount, String attributeName, int operation, @Nullable String slot) {
        if (!item.hasTag()) item.setTag(new NBTTagCompound());
        if (!item.getTag().hasKey("AttributeModifiers")) item.getTag().set("AttributeModifiers", new NBTTagList());
        if (operation < 0 || operation > 3) operation = 0;
        NBTTagCompound compound = item.getTag();
        NBTTagList modifiers = compound.getList("AttributeModifiers", 10);
        NBTTagCompound attributeTag = new NBTTagCompound();
        attributeTag.set("UUIDLeast", new NBTTagInt(894654));
        attributeTag.set("UUIDMost", new NBTTagInt(2872));
        attributeTag.set("Amount", new NBTTagDouble(amount));
        if (slot != null) attributeTag.set("Slot", new NBTTagString(slot));
        attributeTag.set("AttributeName", new NBTTagString(attributeName));
        attributeTag.set("Name", new NBTTagString(attributeName));
        attributeTag.setInt("Operation", operation);
        modifiers.add(attributeTag);
        compound.set("AttributeModifers", modifiers);
        item.setTag(compound);
    }
}
