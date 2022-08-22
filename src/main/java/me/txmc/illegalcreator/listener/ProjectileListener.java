package me.txmc.illegalcreator.listener;

import me.txmc.illegalcreator.IllegalCreator;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.util.Vector;

public class ProjectileListener extends Trajectories implements Listener {

    @EventHandler
    public void onLaunch(ProjectileLaunchEvent event) {
        Projectile projectile = event.getEntity();
        MovingObjectPosition landing = traceEntity(projectile);
        if (landing == null || !projectile.getWorld().getChunkAt(landing.a().getX(), landing.a().getZ()).isLoaded()) {
            projectile.remove();
            broadcast("&cRemoved " + projectile.getClass().getSimpleName() + " from entering unloaded chunks");
            return;
        }
        BlockPosition position = landing.a();
        broadcast("&e" + projectile.getClass().getSimpleName() + " is EXPECTED to land at " + position);

        Bukkit.getScheduler().runTaskLater(IllegalCreator.getPlugin(IllegalCreator.class), () -> {
            if (!projectile.isDead() && !projectile.isOnGround()) {
                broadcast("&bremoved a thrown projectile that existed longer than 300 ticks");
                projectile.remove();
            }
        }, (20L * 15L));
    }

    public void broadcast(String msg) {
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', msg));
    }

    @EventHandler
    public void onHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        Location location = new Location(event.getEntity().getWorld(), event.getHitBlock().getX(), event.getHitBlock().getY(), event.getHitBlock().getZ());
        BlockPosition actualLandingPos = new BlockPosition(location.getX(), location.getY(), location.getZ());
        broadcast("&a" + projectile.getClass().getSimpleName() + " has ACTUALLY landed at " + actualLandingPos);
    }

    public MovingObjectPosition traceEntity(Entity entity) {
        World world = ((CraftWorld) entity.getWorld()).getHandle();
        net.minecraft.server.v1_12_R1.Entity nmsEntity = ((CraftEntity) entity).getHandle();
        Location loc = entity.getLocation();
        MovingObjectPosition landing = null;
        double posX = loc.getX();
        double posY = loc.getY();
        double posZ = loc.getZ();
        BlockPosition originalPosition = new BlockPosition(posX, posY, posZ);
        float gravityM = getGravityModifier(entity);
        float motionM = getMotionModifier(entity);

        Vector velocity = new Vector(nmsEntity.motX, nmsEntity.motY, nmsEntity.motZ);
        if (nmsEntity instanceof EntityFireball) {
            EntityFireball fireball = (EntityFireball) nmsEntity;
            velocity = new Vector(fireball.dirX, fireball.dirY, fireball.dirZ);
        }
        System.out.println(velocity);
        System.out.println("X: " + posX + " Y: " + posY + " Z: " + posZ);
        double motionX = velocity.getX();
        double motionY = velocity.getY();
        double motionZ = velocity.getZ();

        boolean hasLanded = false;
        while (!hasLanded && posY > 0.0D) {
            double fPosX = posX + motionX;
            double fPosY = posY + motionY;
            double fPosZ = posZ + motionZ;

            Vec3D start = new Vec3D(posX, posY, posZ);
            Vec3D future = new Vec3D(fPosX, fPosY, fPosZ);

            landing = world.rayTrace(start, future, false, true, false);
            hasLanded = landing != null && landing.a() != null;

            posX = fPosX;
            posY = fPosY;
            posZ = fPosZ;
            motionX *= motionM;
            motionY *= motionM;
            motionZ *= motionM;
            motionY -= gravityM;

            double distSquared = originalPosition.distanceSquared(posX, posY, posZ);
            System.out.println(distSquared);
            if (originalPosition.distanceSquared(posX, posY, posZ) > 48000) break;
            entity.getWorld().spawnParticle(Particle.BARRIER, posX, posY, posZ, 1);
        }
        return landing;
    }

    public float getMotionModifier(Entity entity) {
        switch (entity.getType()) {
            case FIREBALL:
            case SMALL_FIREBALL:
                return 1.08f;
            case WITHER_SKULL:
                return 1.15f;
            default:
                return 0.99f;
        }
    }

    public float getGravityModifier(Entity entity) {
        switch (entity.getType()) {
            case SNOWBALL:
            case ENDER_PEARL:
            case EGG:
                return 0.03f;
            case ARROW:
            case SPECTRAL_ARROW:
            case TIPPED_ARROW:
            case LINGERING_POTION:
            case SPLASH_POTION:
                return 0.05f;
            case THROWN_EXP_BOTTLE:
                return 0.07f;
            default:
                return 0.0f;
        }
    }
}


