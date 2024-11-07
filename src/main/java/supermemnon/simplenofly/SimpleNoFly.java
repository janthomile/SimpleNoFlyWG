package supermemnon.simplenofly;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.BooleanFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class SimpleNoFly extends JavaPlugin {
    static final String bypassPermission = "simplenofly.bypass";
    static final String forceLandMessage = "You can't fly here!";
    static WorldGuard worldGuard;
    static final StateFlag flyFlag = new StateFlag("allow-flight", true);

    @Override
    public void onLoad() {
        this.worldGuard = WorldGuard.getInstance();
        try {
            FlagRegistry flagRegistry = this.worldGuard.getFlagRegistry();
            flagRegistry.register(flyFlag);

        }
        catch (Exception e) {
            this.getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new EventListener(), this);
    }

    public static boolean inFlyRegion(Location location) {
        ProtectedRegion globalRegion = worldGuard.getPlatform().getRegionContainer().get(BukkitAdapter.adapt(location.getWorld())).getRegion(ProtectedRegion.GLOBAL_REGION);
        boolean flag = globalRegion.getFlag(flyFlag) == StateFlag.State.ALLOW;
        int highest = 0;
        final ApplicableRegionSet regions = worldGuard.getPlatform().getRegionContainer().createQuery().getApplicableRegions(BukkitAdapter.adapt(Objects.requireNonNull(location)));
        for (ProtectedRegion region : regions) {
            if (region.getFlag(flyFlag) != null && region.getPriority() >= highest) {
                flag = region.getFlag(flyFlag) == StateFlag.State.ALLOW;
                highest = region.getPriority();
                break;
            }
        }
        return flag;
    }

    public class EventListener implements Listener {
        @EventHandler
        public void onPlayerMove(PlayerMoveEvent e) {
            if (
                    !e.getPlayer().isFlying() ||
//                            !((e.getFrom().getBlockX() != e.getTo().getBlockX()) ||
//                            (e.getFrom().getBlockY() != e.getTo().getBlockY()) ||
//                            (e.getFrom().getBlockZ() != e.getTo().getBlockZ())) ||
                    e.getPlayer().hasPermission(bypassPermission) ||
                    inFlyRegion(e.getTo())
                )
            {
                return;
            }
            e.getPlayer().setFlying(false);
            e.getPlayer().sendMessage(forceLandMessage);
        }
    }

}
