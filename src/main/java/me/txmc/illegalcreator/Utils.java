package me.txmc.illegalcreator;

import net.minecraft.server.v1_12_R1.EntityPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversable;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.List;

public class Utils {
    private static final String prefix = "&7[&r&6IllegalCreator&r&7]&r ";
    public static String formatList(List<String> toFormat) {
        StringBuilder builder = new StringBuilder();
        builder.append("&7(").append(toFormat.size()).append("):&r ");
        toFormat.forEach(s -> builder.append("&a").append(s).append("&r&7,&r "));
        return builder.substring(0, builder.length() - 4);
    }
    public static void sendMessage(Object conversable, String format, Object... args) {
        format = prefix + format;
        format = String.format(format, args);
        try {
            Method method = conversable.getClass().getMethod("sendMessage", String.class);
            method.setAccessible(true);
            method.invoke(conversable, ChatColor.translateAlternateColorCodes('&', format));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static EntityPlayer fromSender(CommandSender sender) {
        Player player = (Player) sender;
        return ((CraftPlayer)player).getHandle();
    }
}
