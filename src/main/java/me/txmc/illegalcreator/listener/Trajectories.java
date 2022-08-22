package me.txmc.illegalcreator.listener;

import net.minecraft.server.v1_12_R1.*;

import java.util.ArrayList;
import java.util.List;

public class Trajectories {

    public static boolean isThrowable(ItemStack itemStack) {
        Item item = itemStack.getItem();
        return item instanceof ItemEnderPearl
                || item instanceof ItemExpBottle
                || item instanceof ItemSnowball
                || item instanceof ItemEgg
                || item instanceof ItemSplashPotion
                || item instanceof ItemLingeringPotion;
    }

    public static float getDistance(ItemStack itemStack) {
        return itemStack.getItem() instanceof ItemBow ? 1.0f : 0.4f;
    }

    public static float getThrowVelocity(ItemStack itemStack) {
        Item item = itemStack.getItem();
        if (item instanceof ItemSplashPotion || item instanceof ItemLingeringPotion) {
            return 0.5f;
        }
        if (item instanceof ItemExpBottle) {
            return 0.59f;
        }
        return 1.5f;
    }

    public static int getThrowPitch(ItemStack itemStack) {
        Item item = itemStack.getItem();
        if (item instanceof ItemSplashPotion || item instanceof ItemLingeringPotion || item instanceof ItemExpBottle) {
            return 20;
        }
        return 0;
    }

    public static float getGravity(ItemStack itemStack) {
        Item item = itemStack.getItem();
        if (item instanceof ItemBow || item instanceof ItemSplashPotion || item instanceof ItemLingeringPotion || item instanceof ItemExpBottle) {
            return 0.05f;
        }
        return 0.03f;
    }

    public static List<Entity> getEntitiesWithinAABB(AxisAlignedBB bb, EntityPlayer player) {
        final ArrayList<Entity> list = new ArrayList<>();
        final int chunkMinX = MathHelper.floor((bb.a - 2.0) / 16.0);
        final int chunkMaxX = MathHelper.floor((bb.d + 2.0) / 16.0);
        final int chunkMinZ = MathHelper.floor((bb.c - 2.0) / 16.0);
        final int chunkMaxZ = MathHelper.floor((bb.f + 2.0) / 16.0);
        for (int x = chunkMinX; x <= chunkMaxX; ++x) {
            for (int z = chunkMinZ; z <= chunkMaxZ; ++z) {
                if (player.world.getChunkProvider().getLoadedChunkAt(x, z) != null) {
                    player.world.getChunkAt(x, z).a(player, bb, list, IEntitySelector.e);
                }
            }
        }
        return list;
    }
}
