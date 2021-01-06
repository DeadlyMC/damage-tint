# Damage Tint
Adds a red tint when the player takes damage.
![Damage Tint](/damage-tint.png)

## About
- The red vignette gradually darkens as the hp reduces.
- The health threshold value (Decides from which hp onward the tint starts rendering) is stored 
  in `damage_tint.json` in the `config` folder of your root mc directory.  
- The health threshold is configurable using the `/tint` command.
  Typing `/tint` simply displays the the currently set health value.
  Typing `/tint reset` sets it to 20 which is the default value.
  Typing `/tint anynumberbetween0and20here` customizes the health threshold.
  
## Compiling
- Clone this repo.
- Run `gradlew genSources idea` for IntelliJ and `gradlew genSources eclipse` for Eclipse and 
  import the Gradle project. (This is optional if you don't want to change the source code) 
- Run `gradlew build` for creating a build (result in `build/libs/`)  
  