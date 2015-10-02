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

import java.util.List;
import java.util.ListIterator;

import org.bukkit.Bukkit;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;


/**
 * Listens to player dropping the scroll and blocks it.
 * 
 * @author Jakub Sapalski
 */
public class ScrollDropListener implements Listener {

    /**
     * Starts a listener for scroll dropping.
     */
    public ScrollDropListener() {
        Bukkit.getPluginManager().registerEvents(this, TownPortal.getInstance());
    }
    
    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        // prevents normal dropping
        ItemStack item = e.getItemDrop().getItemStack();
        if (TownPortal.getScroll(item) != null) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemMove(InventoryClickEvent e) {
        // prevents moving item to other inventories
        if (!(e.getWhoClicked() instanceof Player)) return;
        if (e.getView().getType().equals(InventoryType.CREATIVE)) {
            return;
        }
        if (TownPortal.getScroll(e.getCursor()) != null) {
            if (e.getAction().equals(InventoryAction.PLACE_ALL)
                || e.getAction().equals(InventoryAction.PLACE_ONE) || e.getAction().equals(
                    InventoryAction.PLACE_SOME)) {
                boolean isOutside = e.getRawSlot() < (e.getView().countSlots() - 36);
                if (isOutside) {
                    e.setCancelled(true);
                }
            }
        } else if (e.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
            // this prevents shift-clicking into other inventories
            if (TownPortal.getScroll(e.getCurrentItem()) != null) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onItemDrag(InventoryDragEvent e) {
        // this prevents dragging over other inventory
        if (TownPortal.getScroll(e.getOldCursor()) != null) {
            for (Integer slot : e.getRawSlots()) {
                if (slot < (e.getView().countSlots() - 36)) {
                    e.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(PlayerDeathEvent e) {
        // prevents dropping on death
        List<ItemStack> drops = e.getDrops();
        ListIterator<ItemStack> litr = drops.listIterator();
        while (litr.hasNext()) {
            ItemStack stack = litr.next();
            if (TownPortal.getScroll(stack) != null) {
                litr.remove();
            }
        }
    }

    @EventHandler
    public void onItemFrameClick(PlayerInteractEntityEvent e) {
        // prevents putting scrolls into frames
        if (e.getRightClicked() instanceof ItemFrame
            && (TownPortal.getScroll(e.getPlayer().getItemInHand()) != null)) {
            e.setCancelled(true);
        }
    }
    
}
