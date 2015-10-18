/**
 * Teleportation scrolls for Bukkit
 * Copyright (C) 2015 Jakub "Co0sh" Sapalski
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.betoncraft.townportal;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;


/**
 * Teleports the player after the delay
 * 
 * @author Jakub Sapalski
 */
public class Teleporter implements Listener {
    
    private int movesLeft = 3;
    private double x, y, z;
    private final Player player;
    private final Location loc;
    private final int delay;
    private final String cancel;
    private final String done;
    private final Teleporter instance;
    private String portalName;
    private BukkitRunnable runnable;

    public Teleporter(Player p, Location l, int d, String m, String c, String f,
            final PortalScroll portalScroll) {
        instance = this;
        player = p;
        loc    = l;
        delay  = d;
        cancel = c;
        done   = f;
        portalName = portalScroll.getName();
        player.sendMessage(m.replace('&', '§'));
        if (delay == 0) {
            player.teleport(loc);
            portalScroll.fireEvents(player);
            return;
        }
        ScrollUseListener.add(player);
        x = player.getLocation().getX();
        y = player.getLocation().getY();
        z = player.getLocation().getZ();
        // this runnable will teleport the player. it's canceled when player
        // does something forbidden, eg. moves
        runnable = new BukkitRunnable() {
            @Override
            public void run() {
                player.teleport(loc);
                player.sendMessage(done.replace('&', '§'));
                portalScroll.fireEvents(player);
                ScrollUseListener.remove(player);
                HandlerList.unregisterAll(instance);
            }
        };
        runnable.runTaskLater(TownPortal.getInstance(), delay * 20);
        Bukkit.getPluginManager().registerEvents(this, TownPortal.getInstance());
    }
    
    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        // this will cancel the runnable after 3 moves (it allows minor movement
        // caused by laggy connection)
        if (!player.equals(e.getPlayer())) return;
        if (e.getTo().getX() != x || e.getTo().getY() != y || e.getTo().getZ() != z) {
            movesLeft--;
            x = e.getTo().getX();
            y = e.getTo().getY();
            z = e.getTo().getZ();
        }
        if (movesLeft < 0) cancel();
    }
    
    @EventHandler
    public void onAttack(EntityDamageByEntityEvent e) {
        // it will cancel the runnable if the player attacks something or
        // is attacked by something
        if (e.isCancelled()) return;
        if (e.getDamager().equals(player)) cancel();
        if (e.getEntity().equals(player)) cancel();
    }
    
    /**
     * This cancels the countdown to teleportation.
     */
    private void cancel() {
        player.sendMessage(cancel.replace('&', '§'));
        TownPortal.give(portalName, player);
        ScrollUseListener.remove(player);
        runnable.cancel();
        HandlerList.unregisterAll(this);
    }

}
