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

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import pl.betoncraft.betonquest.BetonQuest;

/**
 * TownPortal plugin.
 * 
 * @author Jakub Sapalski
 */
public class TownPortal extends JavaPlugin {
    
    private static TownPortal instance;
    private HashMap<String, PortalScroll> scrolls = new HashMap<>();
    
    @Override
    public void onEnable() {
        instance = this;
        Bukkit.getPluginCommand("scroll").setExecutor(new ScrollCommand());
        Bukkit.getPluginCommand("townportalreload").setExecutor(new ReloadCommand());
        new ScrollDropListener();
        new ScrollUseListener();
        // hook into BetonQuest
        try {
            BetonQuest.getInstance().registerEvents("scroll", PortalEvent.class);
        } catch (NoClassDefFoundError e) {}
        saveDefaultConfig();
        load();
    }
    
    /**
     * Loads all data from the configuration.
     */
    private void load() {
        // for each scroll create a corresponding object
        for (String key : getConfig().getKeys(false)) {
            try {
                PortalScroll scroll = new PortalScroll(key);
                scrolls.put(key, scroll);
            } catch (InstantiationException e) {
                getLogger().warning("Could not parse portal '" + key + "': "
                        + e.getMessage());
            }
        }
        getLogger().info("Loaded " + scrolls.size() + " portals.");
    }
    
    /**
     * @return the instance of the plugin
     */
    public static TownPortal getInstance() {
        return instance;
    }
    
    /**
     * Checks if the portal with given name is defined in the configuration.
     * 
     * @param portalName
     *                  name of the portal
     * @return true if it exists and does not have any errors, false otherwise
     */
    public static boolean exists(String portalName) {
        return instance.scrolls.containsKey(portalName);
    }
    
    /**
     * Gives the speficied scroll to the player.
     * 
     * @param portalName
     *                  name of the portal
     * @param player
     *                  the player who will receive the scroll
     */
    public static void give(String portalName, Player player) {
        ItemStack item = instance.scrolls.get(portalName).getItem();
        player.getInventory().addItem(item);
    }
    
    /**
     * Retrieves a scroll from an ItemStack.
     * 
     * @param item
     *            the ItemStack to match      
     * @return the matching scroll or null if it does not match
     */
    public static PortalScroll getScroll(ItemStack item) {
        // checks if the item is a scroll, will return null if it's not
        for (PortalScroll scroll : instance.scrolls.values()) {
            if (scroll.check(item)) {
                return scroll;
            }
        }
        return null;
    }

    /**
     * Reloads the configuration.
     */
    public static void reload() {
        instance.reloadConfig();
        instance.load();
    }
}
