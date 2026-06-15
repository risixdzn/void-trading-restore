# Void Trading Restore

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.9-blue)](https://minecraft.net)
[![Fabric](https://img.shields.io/badge/Mod_Loader-Fabric-dbd0b4)](https://fabricmc.net)
[![Fabric Loader](https://img.shields.io/badge/Fabric_Loader-0.17.2%2B-orange)](https://fabricmc.net)

Brings back void trading for Minecraft 1.21.9+.

## What changed in 1.21.5

In 1.21.5, Mojang tightened `MerchantEntity.canInteract()` to require
the player be within 8 blocks of the villager to open the trade screen.
This killed void trading entirely. You cannot even open the GUI from a
distance anymore, let alone exploit infinite trades.

## How this mod fixes it

This mod overrides that proximity check so you can open the trade screen
from any range, as long as the villager is alive.

When you close the screen, all offers reset to their original stat,
but only if the villager is outside every player's simulation distance.
If anyone is close enough, trades stay used up and prices stay high.

The result: normal trading works as vanilla. Void trading requires the
old setup effort (portal transport, remote location) to get the reward.

## Requirements

- Minecraft 1.21.9
- Fabric Loader 0.17.2 or later
