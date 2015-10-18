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

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Represents a portal scroll.
 * 
 * @author Jakub Sapalski
 */
public class PortalScroll {
    
    private String name;
    private ItemStack item;
    private Location loc;
    private int delay;
    private String message;
    private String cancel;
    private String done;
    private String fail;
    private List<String> conditions;
    private List<String> events;

    /**
     * Loads a portal scroll with given name from configuration.
     * 
     * @param name
     *              name of the portal
     * @throws InstantiationException
     */
    @SuppressWarnings("deprecation")
    public PortalScroll(String name) throws InstantiationException {
        // portal scrolls are created when the plugin is enabled, all data is
        // extracted from configuration and errors are printed in the console
        this.name = name;
        String id = TownPortal.getInstance().getConfig().getString(name + ".id");
        if (id == null) {
            throw new InstantiationException("Syntax error, missing 'id' variable");
        }
        Material m = Material.matchMaterial(id);
        if (m == null) {
            throw new InstantiationException("Item type does not exist");
        }
        int data = TownPortal.getInstance().getConfig().getInt(name + ".data");
        item = new ItemStack(m);
        MaterialData d = item.getData();
        d.setData((byte) data);
        item.setData(d);
        String displayName = TownPortal.getInstance().getConfig().getString(name
                + ".name").replace('&', '§');
        if (displayName == null) {
            throw new InstantiationException("Syntax error, missing 'name' variable");
        }
        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(m);
        meta.setDisplayName(displayName);
        List<String> lore = TownPortal.getInstance().getConfig().getStringList(name
                + ".lore");
        if (lore == null) {
            throw new InstantiationException("Syntax error, missing 'lore' variable");
        }
        for (int i = 0; i < lore.size(); i++) {
            lore.set(i, lore.get(i).replace('&', '§'));
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        String rawLoc = TownPortal.getInstance().getConfig().getString(name + ".loc");
        if (rawLoc == null) {
            throw new InstantiationException("Syntax error, missing 'loc' variable");
        }
        String[] parts = rawLoc.split(";");
        if (parts.length != 4 && parts.length != 6) {
            throw new InstantiationException("Wrong location syntax");
        }
        Double x, y, z;
        try {
            x = Double.parseDouble(parts[0]);
            y = Double.parseDouble(parts[1]);
            z = Double.parseDouble(parts[2]);
        } catch (NumberFormatException e) {
            throw new InstantiationException("Wrong location syntax");
        }
        World world = Bukkit.getWorld(parts[3]);
        if (world == null) {
            throw new InstantiationException("World does not exist");
        }
        if (parts.length == 6) {
            Float yaw, pitch;
            yaw = Float.parseFloat(parts[4]);
            pitch = Float.parseFloat(parts[5]);
            loc = new Location(world, x, y, z, yaw, pitch);
        } else {
            loc = new Location(world, x, y, z);
        }
        delay = TownPortal.getInstance().getConfig().getInt(name + ".delay");
        message = TownPortal.getInstance().getConfig().getString(name
                + ".teleport-msg");
        if (message == null) message = "§2Teleporting in " + delay
                + " seconds, do not move.";
        cancel = TownPortal.getInstance().getConfig().getString(name
                + ".cancel-msg");
        if (cancel == null) cancel = "§cTeleportation canceled.";
        done = TownPortal.getInstance().getConfig().getString(name
                + ".done-msg");
        if (done == null) done = "§eTeleported!";
        fail = TownPortal.getInstance().getConfig().getString(name
                + ".fail-msg");
        if (fail == null) fail = "§cYou cannot use this scroll now.";
        conditions = TownPortal.getInstance().getConfig().getStringList(
                name + ".conditions");
        events = TownPortal.getInstance().getConfig().getStringList(
                name + ".events");
    }

    /**
     * @return the name of the portal
     */
    public String getName() {
        return name;
    }
    
    public String getFailMessage() {
        return fail;
    }
    
    /**
     * Teleports a player to this portal, with delay.
     * 
     * @param player
     *              player to teleport
     */
    public void teleport(Player player) {
        new Teleporter(player, loc, delay, message, cancel, done, this);
    }

    /**
     * @return the ItemStack representing this scroll.
     */
    public ItemStack getItem() {
        return item.clone();
    }

    /**
     * @param item
     *          ItemStack to check
     * @return true if it's this portal scroll, false otherwise
     */
    public boolean check(ItemStack item) {
        return this.item.isSimilar(item);
    }
    
    /**
     * Checks BetonQuest conditions for this scroll. Returns true if BetonQuest
     * is not enabled.
     * 
     * @param player
     *            ID of the player
     * @return true if met or BetonQuest not enabled, false otherwise.
     */
    public boolean checkConditions(Player player) {
        if (!Bukkit.getPluginManager().isPluginEnabled("BetonQuest")) return true;
        for (String conditionID : conditions) {
            if (!BetonQuest.condition(PlayerConverter.getID(player), conditionID))
                return false;
        }
        return true;
    }
    
    /**
     * Fires BetonQuest events for this scroll. If BetonQuest is not enabled it
     * does nothing.
     * 
     * @param player
     *            ID of the player
     */
    public void fireEvents(Player player) {
        if (!Bukkit.getPluginManager().isPluginEnabled("BetonQuest")) return;
        for (String eventID : events) {
            BetonQuest.event(PlayerConverter.getID(player), eventID);
        }
        return;
    }

}
