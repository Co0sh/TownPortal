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
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


/**
 * A command which gives portal scrolls.
 * 
 * @author Jakub Sapalski
 */
public class ScrollCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label,
            String[] args) {
        if (cmd.getName().equalsIgnoreCase("scroll")) {
            if (args.length < 2) {
                sender.sendMessage("§3Specify player and scroll name!");
                return true;
            }
            Player player = null;
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().equalsIgnoreCase(args[0])) {
                    player = p;
                    break;
                }
            }
            if (player == null) {
                sender.sendMessage("§3The player must be online!");
                return true;
            }
            String scroll = args[1];
            if (!TownPortal.exists(scroll)) {
                sender.sendMessage("§3This portal is not defined!");
                return true;
            }
            TownPortal.give(scroll, player);
            sender.sendMessage("§2Portal scroll given!");
            return true;
        }
        return false;
    }

}
