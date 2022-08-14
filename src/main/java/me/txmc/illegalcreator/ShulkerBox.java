package me.txmc.illegalcreator;

import lombok.Getter;
import net.minecraft.server.v1_12_R1.ItemShulkerBox;
import net.minecraft.server.v1_12_R1.ItemStack;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShulkerBox {
    @Getter
    private final ItemStack itemStack;
    @Getter
    private final List<ShulkerBox> internalBoxes;
    @Getter
    private Map<Byte, ItemStack> inventoryContent;

    public ShulkerBox(ItemStack itemStack, Map<Byte, ItemStack> inventoryContent, List<ShulkerBox> internalBoxes) {
        if (!(itemStack.getItem() instanceof ItemShulkerBox))
            throw new IllegalArgumentException("The item must be a shulker");
        this.inventoryContent = inventoryContent;
        this.internalBoxes = internalBoxes;
        this.itemStack = itemStack;
    }

    public static ShulkerBox construct(ItemStack itemStack) {
        if (!(itemStack.getItem() instanceof ItemShulkerBox))
            throw new IllegalArgumentException("The item must be a shulker box");
        if (!itemStack.hasTag()) return new ShulkerBox(itemStack, new HashMap<>(), new ArrayList<>());
        Map<Byte, ItemStack> buf = new HashMap<>();
        NBTTagCompound tag = itemStack.getTag();
        NBTTagList items = tag.getCompound("BlockEntityTag").getList("Items", 10);
        List<ShulkerBox> internalBoxes = new ArrayList<>();
        items.list.stream().map(b -> (NBTTagCompound) b).forEach(c -> {
            byte slot = c.getByte("Slot");
            ItemStack item = new ItemStack(c);
            if (item.getItem() instanceof ItemShulkerBox) {
                ShulkerBox internalBox = construct(item);
                internalBoxes.add(internalBox);
                internalBoxes.addAll(internalBox.internalBoxes);
            }
            buf.put(slot, item);
        });
        return new ShulkerBox(itemStack, buf, internalBoxes);
    }

    public ItemStack getItem(int slot) {
        return inventoryContent.getOrDefault((byte) slot, ItemStack.a);
    }

    public void setItem(int slot, ItemStack itemStack) {
        inventoryContent.put((byte) slot, itemStack);
        update();
    }
    public int size() {
        return 9*3;
    }

    public void replaceInventoryContent(Map<Byte, ItemStack> newItems) {
        this.inventoryContent = newItems;
        update();
    }

    public void clear() {
        inventoryContent.clear();
        update();
    }

    public void update() {
        if (!itemStack.hasTag()) itemStack.setTag(new NBTTagCompound());
        NBTTagCompound tag = itemStack.getTag();
        if (!tag.hasKey("BlockEntityTag")) {
            NBTTagCompound bet = new NBTTagCompound();
            bet.set("Items", new NBTTagList());
            tag.set("BlockEntityTag", bet);
        }
        NBTTagList items = new NBTTagList();
        inventoryContent.forEach((slot, stack) -> {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setByte("Slot", slot);
            stack.save(compound);
            items.add(compound);
        });
        tag.getCompound("BlockEntityTag").set("Items", items);
    }
}
