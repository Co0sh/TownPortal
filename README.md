# TownPortal

TownPotal is a simple plugin which adds to the game teleportation scrolls.
You can use them to teleport to places specified in the configuration. There
are optional delays, custom names and lores and customizable messages.

## Installation

Just drop the plugin into the "plugins" directory and reload/restart your server.

## Configuration

Every entry is a separate scroll type. You can have unlimited amount of scrolls.
Each of them have those configuration options:

* `name` - display name of the scroll item
* `lore` - list of the lore lines of the item
* `loc` - location of the teleportation target, written like 
  'x;y;z;world;yaw;pitch' (for example 10.5;20;-30;world_nether;270;45)
* `id` - name of the material of the scroll item; should not be usable
  (like a block, tool or arrow)
* `data` - optional data value for the item; you don't have to specify it
* `teleport-msg` - message displayed when you use a scroll
* `cancel-msg` - message displayed when the teleportation is canceled
  (for example because the player moved)
* `done-msg` - message displayed after the delay has successfully passed
* `fail-msg` - message displayed when the player does not meet all conditions
* `delay` - amount of seconds the player has to wait until he's teleported;
  teleportation will be canceled if he moves or takes/deals damage; if you
  don't want any delay, just set it to 0 or remove this setting, cancel-msg and
  done-msg won't display then
* `conditions` - list of BetonQuest conditions to check before teleportation.
  If the conditions are not met, nothing will happen.
* `events` - list of BetonQuest events to fire after the player has teleported. 

## Commands

There are two commands:

* _/scroll <player> <scrollname>_ - gives the player a scroll, requires
  `townportal.give` permission
* _/townportalreload_ - reloads the configuration, requires
  `townportal.reload` permission

## Permissions

* townportal.use - allows using portal scrolls, default for players
* townportal.give - allows giving portal scrolls with a command
* townportal.reload - allows reloading the plugin with a command

## BetonQuest

There is an event which gives a scroll to the player (so you don't have to do
that with a command. The syntax for the event is simply "scroll <scrollname>",
for example "scroll beton".

Note that you can add "&2Quest Item" line to lore of the scroll, and it will
become BetonQuest's quest item, which means players will be able to store it in
their backpacks.