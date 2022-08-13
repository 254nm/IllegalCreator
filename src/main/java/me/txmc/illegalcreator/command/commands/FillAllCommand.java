package me.txmc.illegalcreator.command.commands;

import lombok.Getter;
import me.txmc.illegalcreator.ICInventory;
import me.txmc.illegalcreator.IllegalCreator;
import me.txmc.illegalcreator.Utils;
import me.txmc.illegalcreator.command.BaseCommand;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FillAllCommand extends BaseCommand implements Listener {
    public FillAllCommand(IllegalCreator plugin) {
        super(plugin);
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws Throwable {
        if (sender instanceof Player) {
            EntityPlayer player = fromSender(sender);
            ItemStack mainHand = player.inventory.getItemInHand();
            player.openContainer(new FillGUI(player.getBukkitEntity(), mainHand.cloneItemStack()));
           if (!mainHand.isEmpty()) mainHand.setCount(-1);
        } else sendMessage(sender, "&cYou must be a player");
    }

    @Override
    public List<String> getPossibleArgs() {
        return Collections.emptyList();
    }

    @Override
    public String getUsage() {
        return null;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        CraftInventory clickedInventory = (CraftInventory) event.getClickedInventory();
        if (clickedInventory == null) return;
        if (clickedInventory.getInventory() instanceof FillGUI) {
            FillGUI inventory = (FillGUI) clickedInventory.getInventory();
            int slot = event.getSlot();
            if (slot == 3 || slot == 5) return;
            if (slot == 4 && event.getClick().isLeftClick()) {
                ItemStack left = inventory.getItem(3), right = inventory.getItem(5);
                if (left.isEmpty() || right.isEmpty()) {
                    Utils.sendMessage(event.getWhoClicked(), "&cBoth sides must be populated!");
                    event.setCancelled(true);
                    return;
                }
                if (right.getItem() instanceof ItemShulkerBox) {
                    Map<Byte, ItemStack> map = new HashMap<>();
                    for (int i = 0; i < 27; i++) map.put((byte) i, left);
                    Utils.setShulkerContents(right, map);
                    Utils.sendMessage(event.getWhoClicked(), "&3Successfully filled the shulker on the right with &r&a%s", Utils.getItemName(left.getItem()));
                } else Utils.sendMessage(event.getWhoClicked(), "&cThe item on the right must be a shulker");
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        CraftInventory eventInventory = (CraftInventory) event.getInventory();
        if (eventInventory.getInventory() instanceof FillGUI) {
            FillGUI inv = (FillGUI) eventInventory.getInventory();
            EntityPlayer player = ((CraftPlayer) event.getPlayer()).getHandle();
            ItemStack left = inv.getItem(3), right = inv.getItem(5);
            if (!left.isEmpty()) Utils.giveItemBack(player, left);
            if (!right.isEmpty()) Utils.giveItemBack(player, right);
        }
    }


    protected static class FillGUI extends ICInventory {
        @Getter
        private final ItemStack handItem;

        public FillGUI(InventoryHolder owner, ItemStack handItem) {
            super(owner, 9, "Fill GUI");
            this.handItem = handItem;
            setupGUI();
        }

        private void setupGUI() {
            setItem(0, new ItemStack(Item.getById(160), 1, 15));
            setItem(1, new ItemStack(Item.getById(160), 1, 15));
            setItem(2, new ItemStack(Item.getById(160), 1, 15));
            setItem(8, new ItemStack(Item.getById(160), 1, 15));
            setItem(7, new ItemStack(Item.getById(160), 1, 15));
            setItem(6, new ItemStack(Item.getById(160), 1, 15));
            setItem(3, handItem.cloneItemStack());
            setItem(4, setItemName(new ItemStack(Item.getById(160), 1, 5), "&aDo the thing"));
        }
    }
}
