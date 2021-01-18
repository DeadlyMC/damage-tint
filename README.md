# Damage Tint
Adds a red tint when the player takes damage.
https://www.curseforge.com/minecraft/mc-mods/damage-tint
![Damage Tint](/media/damage-tint.png)
- The red vignette gradually darkens as the hp reduces.
- The health threshold value (Decides from which hp onward the tint starts rendering) is stored in `damage_tint.json` in the `config` folder of your root mc directory.  
- The health threshold is configurable using the `/tint` command.

## Compiling
- Clone this repo.
- Import this project in Intellij or Eclipse. The Gradle integration will handle the rest of the initial workspace setup. (This is optional if you don't want to change the source code) 
- Run `gradlew build` for creating a build (result in `build/libs/`)  
  
