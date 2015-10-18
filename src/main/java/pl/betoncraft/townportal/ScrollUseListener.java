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

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;


/**
 * Listens to player use the scroll, removes it and teleports the player.
 * 
 * @author Jakub Sapalski
 */
public class ScrollUseListener implements Listener {

    private static ArrayList<Player> players = new ArrayList<>();
    
    /**
     * Starts a listener for scroll using.
     */
    public ScrollUseListener() {
        Bukkit.getPluginManager().registerEvents(this, TownPortal.getInstance());
    }
    
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        // checks if the interaction was right click with scroll, checks
        // permission and removes one scroll
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction()
                != Action.RIGHT_CLICK_BLOCK) return;
        if (isBlocked(e.getPlayer())) return;
        ItemStack item = e.getItem();
        if (item == null) return;
        PortalScroll scroll = TownPortal.getScroll(item);
        if (scroll == null) return;
        if (!e.getPlayer().hasPermission("townportal.use")) {
            e.getPlayer().sendMessage("§3No permission.");
            return;
        }
        if (!scroll.checkConditions(e.getPlayer())) {
            e.getPlayer().sendMessage(scroll.getFailMessage().replace('&', '§'));
            return;
        }
        if (item.getAmount() == 1) {
            e.getPlayer().setItemInHand(null);
        } else {
            item.setAmount(item.getAmount() - 1);
        }
        scroll.teleport(e.getPlayer());
    }
    
    // these 3 methods are blocking the use of a scroll when there is
    // a pending teleportation
    
    public static void add(Player player) {
        players.add(player);
    }
    
    public static void remove(Player player) {
        players.remove(player);
    }
    
    public static boolean isBlocked(Player player) {
        return players.contains(player);
    }
    
}
