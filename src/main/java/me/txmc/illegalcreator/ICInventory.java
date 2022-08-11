package me.txmc.illegalcreator;

import net.minecraft.server.v1_12_R1.*;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryHolder;

import java.util.ArrayList;
import java.util.List;

public class ICInventory implements IInventory {
    private final NonNullList<ItemStack> items;
    private final List<HumanEntity> viewers;
    private final String title;
    private final InventoryHolder owner;
    private int maxStack;

    public ICInventory(InventoryHolder owner, int size, String title) {
        this.maxStack = 64;
        Validate.notNull(title, "Title cannot be null");
        this.items = NonNullList.a(size, ItemStack.a);
        this.title = title;
        this.viewers = new ArrayList<>();
        this.owner = owner;
    }

    @Override
    public int getSize() {
        return items.size();
    }

    @Override
    public ItemStack getItem(int i) {
        return items.get(i);
    }

    @Override
    public ItemStack splitStack(int i, int j) {
        ItemStack stack = getItem(i);
        if (stack == ItemStack.a) {
            return stack;
        } else {
            ItemStack result;
            if (stack.getCount() <= j) {
                setItem(i, ItemStack.a);
                result = stack;
            } else {
                result = CraftItemStack.copyNMSStack(stack, j);
                stack.subtract(j);
            }

            update();
            return result;
        }
    }

    @Override
    public ItemStack splitWithoutUpdate(int i) {
        ItemStack stack = getItem(i);
        if (stack == ItemStack.a) {
            return stack;
        } else {
            ItemStack result;
            if (stack.getCount() <= 1) {
                setItem(i, null);
                result = stack;
            } else {
                result = CraftItemStack.copyNMSStack(stack, 1);
                stack.subtract(1);
            }

            return result;
        }
    }


    @Override
    public void setItem(int i, ItemStack itemstack) {
        items.set(i, itemstack);
        if (itemstack != ItemStack.a && getMaxStackSize() > 0 && itemstack.getCount() > getMaxStackSize()) {
            itemstack.setCount(getMaxStackSize());
        }

    }

    @Override
    public int getMaxStackSize() {
        return maxStack;
    }

    @Override
    public void setMaxStackSize(int size) {
        maxStack = size;
    }

    @Override
    public void update() {
    }

    @Override
    public boolean a(EntityHuman entityhuman) {
        return true;
    }

    @Override
    public List<ItemStack> getContents() {
        return items;
    }

    @Override
    public void onOpen(CraftHumanEntity who) {
        viewers.add(who);
    }

    @Override
    public void onClose(CraftHumanEntity who) {
        viewers.remove(who);
    }

    @Override
    public List<HumanEntity> getViewers() {
        return viewers;
    }

    @Override
    public InventoryHolder getOwner() {
        return owner;
    }

    @Override
    public boolean b(int i, ItemStack itemstack) {
        return true;
    }

    @Override
    public void startOpen(EntityHuman entityHuman) {
    }

    @Override
    public void closeContainer(EntityHuman entityHuman) {
    }

    @Override
    public int getProperty(int i) {
        return 0;
    }

    @Override
    public void setProperty(int i, int j) {
    }

    @Override
    public int h() {
        return 0;
    }

    @Override
    public void clear() {
        items.clear();
    }

    @Override
    public String getName() {
        return title;
    }

    @Override
    public boolean hasCustomName() {
        return title != null;
    }

    @Override
    public IChatBaseComponent getScoreboardDisplayName() {
        return new ChatComponentText(title);
    }

    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public boolean x_() {
        for (ItemStack itemstack : items) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }
        return true;
    }
}

