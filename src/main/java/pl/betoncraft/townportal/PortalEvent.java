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

import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.utils.PlayerConverter;


/**
 * BetonQuest event which gives the player a portal scroll.
 * 
 * @author Jakub Sapalski
 */
public class PortalEvent extends QuestEvent {
    
    private String portalName;

    public PortalEvent(String packName, String instructions)
            throws InstructionParseException {
        super(packName, instructions);
        String[] parts = instructions.split(" ");
        if (parts.length < 2) {
            throw new InstructionParseException("Portal name not defined");
        }
        portalName = parts[1];
        if (!TownPortal.exists(portalName)) {
            throw new InstructionParseException("Portal '" + portalName + "' not defined");
        }
    }

    @Override
    public void run(String playerID) {
        TownPortal.give(portalName, PlayerConverter.getPlayer(playerID));
    }

}
