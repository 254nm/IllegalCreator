package me.txmc.illegalcreator.listener;

import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;

public class ProjectileListener extends Trajectories implements Listener {

    @EventHandler
    public void onLaunch(ProjectileLaunchEvent event) {
        Projectile projectile = event.getEntity();
        EntityProjectile entity = (EntityProjectile) ((CraftEntity) projectile).getHandle();
        EntityPlayer player = (EntityPlayer) entity.getShooter();
        if (player == null) return;
        BlockPosition position = getExpectedLandingPosition(player, entity);
        System.out.println(entity.getClass().getSimpleName() + " is EXPECTED to land at " + position);
        if (position == null || !entity.world.getChunkAt(position.getX(), position.getZ()).isLoaded()) {
            entity.die();
            System.out.println("Removed a snowball from entering unloaded chunks");
        }
    }

    @EventHandler
    public void onHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        EntityProjectile entity = (EntityProjectile) ((CraftEntity) projectile).getHandle();
        EntityPlayer player = (EntityPlayer) entity.getShooter();
        if (player == null) return;
        Location location = new Location(event.getEntity().getWorld(), entity.locX, entity.locY, entity.locZ);
        BlockPosition actualLandingPos = new BlockPosition(location.getX(), location.getY(), location.getZ());
        System.out.println(entity.getClass().getSimpleName() + " has ACTUALLY landed at " + actualLandingPos);
    }

    public BlockPosition getExpectedLandingPosition(EntityPlayer player, EntityProjectile entity) {
        ItemStack item = (!player.getItemInMainHand().isEmpty() && isThrowable(player.getItemInMainHand())) ? player.getItemInMainHand() :
                (!player.getItemInOffHand().isEmpty() && isThrowable(player.getItemInOffHand())) ? player.getItemInOffHand() : null;
        if (item == null) return null;
        double posX = entity.locX;
        double posY = entity.locY;
        double posZ = entity.locZ;
        double motionX = entity.motX;
        double motionY = entity.motY;
        double motionZ = entity.motZ;

        boolean hasLanded = false;
        BlockPosition landingPos = null;
        while (!hasLanded && posY > 0.0) {
            Vec3D present = new Vec3D(posX, posY, posZ);
            Vec3D future = new Vec3D(posX + motionX, posY + motionY, posZ + motionZ);
            MovingObjectPosition landing = player.world.rayTrace(present, future, false, true, false);
            if (landing != null) {
                landingPos = landing.a();
                hasLanded = true;
            }
            posX += motionX;
            posY += motionY;
            posZ += motionZ;
            float motionAdjustment = 0.99f;
            motionX *= motionAdjustment;
            motionY *= motionAdjustment;
            motionZ *= motionAdjustment;
            motionY -= getGravity(item);
        }
        return landingPos;
    }
}
