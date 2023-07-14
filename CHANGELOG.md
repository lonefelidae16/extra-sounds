# Changelog

## [Unreleased]
### ⌛ Coming

* <small>_Exciting new features may be coming here._</small>

### 💭 Bugs / Issues

* <small>_Caught bugs and known issues are written._</small>

## [2.3.1+1.20.1-build.1] - 2023-07-14
### ✨ Added

* Compatible with 1.20 - 1.20.1.
* Entity Death sound.

### 🔧 Fixed

* Calculation of sound packs loading time.

## [2.3.1+1.20-build.1] - 2023-06-08
### ✨ Added

* Port to 1.20.
* Supports Pottery _Sherds_ sound (name has changed).

### 🔧 Fixed

* Typing sound turns on by default.
* Keyboard’s Cut action sound on edit screen.
  - Book and Quill
  - Signboard

### 👷 Technical

* Dependency updates:
  - Gradle 8.1.1
  - fabric-loom 1.2
  - fabric-loader 0.14.21
* Updates the API specification.
  - No longer required to specify the modId in `mapping.SoundGenerator`.

## [2.3.1+1.19.4-build.4] - 2023-05-21
### ✨ Added

* Tooltip feature in settings screen.

### 👷 Technical

* Uses fabric-loader 0.14.19.
* Produces helper method for creating `SoundEvent` instance.
* Refactors Log output methods.

## [2.3.1+1.19.4-build.3] - 2023-03-27
### ✨ Added

* Makes Drop Sound optional.
* More Chat and Command input sounds.
  - Suggestion select sound.
  - Suggestion accept sound.
  - Scroll sound for Chat log screen.

### 👷 Technical

* Refactors mixins.
  - Changes the class name based on its mixin target class.
  - Adds prefix `extrasounds$` to method names.

## [2.3.1+1.19.4-build.2] - 2023-03-24
### ✨ Added

* Port to 1.19.4.
* Includes [`SoundCategories`](https://github.com/lonefelidae16/sound-categories) project using Git-Submodule feature.
* Supports some item sounds:
  - Pottery Shards
  - Smithing Templates
  - Brush

### 🔧 Fixed

* Keyboard’s Cursor sound when moving rows.
* Changes working directory of the cache.
* Item delete sound when moving a stack from HotBar to creative slot
  on CreativeInventory screen.

### 👷 Technical

* Dependency updates:
  - Java 17
  - Gradle 8.0.1

## [2.3.1+1.19.3-build.4] - 2023-03-14
### ✨ Added

* Port to 1.19.3.
* More Keyboard sounds during editing.
  - Book and Quill
  - Signboard
* More sounds.
  - Swap-with-Offhand action (default <kbd>F</kbd> key).
  - Swap-with-HotBar action (default <kbd>1</kbd>-<kbd>9</kbd> key).
  - Drop sound from HotBar (default <kbd>Q</kbd> key).
  - Pick item sound (default Mouse Wheel Click).
* Changes Stick sound.
* Supports some item sounds:
  - Disc Fragment
  - Bundle

### 🔧 Fixed

* Prevents doubled-sounds.
* Prevents inventory sound of other players.
* Allows to join any players who don’t have ExtraSounds to a Singleplayer world
  opened on the LAN.
* Pitch calculation of Drop sound.

### 👷 Technical

* Dependency updates:
  - Gradle 7.6
  - fabric-loom 1.1
* Refactors `mapping.SoundPackLoader` to reduce memory usage.
* Refactors handling the cache.
* Supports JVM argument `extrasounds.searchundef` to find the sound that is not defined
  in `sounds.json` or the class that uses `mapping.SoundGenerator`.
* Validates the version string in the first line of the cache.

### ❓ Known Issues

* The sound is played regardless of whether the action succeeds or fails.
  - e.g.) When both Chest and PlayerInventory are full and trying to move the stack with
    <kbd>Shift</kbd> + Click. The stack will not be moved, but the sound will be played.
  - e.g.2) Trying to place an item in a slot that doesn’t accept it, such as
    placing in the Result slot on the Crafting, Furnace, Brewing screen, and so on.

## [2.3.2]

### Added

* 1.19.1+ support

### Fixed

* Crashes when playing sounds in edge cases (ConcurrentModificationException)

## [2.3.1] - 2022-06-12

### Fixed

* a very large oopsie, should no longer crash immediately

## [2.3.0] - 2022-06-12

### Added

- New sounds:
    - Bow pulling
    - Repeater clicking
- Support for different sounds for types of actions
- Sound caching (very slightly faster startup, will mostly help in heavily modded scenarios)
- Optifine is now compatible!

### Changed

- New colorful icon :)
- Various sound and volume changes (not final, will make an automated solution eventually)
- Russian translation (thank you, Felix14-v2!)

### Fixed

- Inventory open/close sounds playing when resizing window
- Transfer sounds being played when no items are transferred
- Inventory Profiles (& perhaps other inventory mod) compatibility
- Master mod volume being ignored

## [2.2.1] - 2022-03-04

### Added

- Item based sound on hotbar scroll

### Fixed

- 1.18.2 crash on startup (updated ARRP)

## [2.2.0] - 2021-12-28

### Added

- More granular volume settings

### Removed

- Typing sounds resource pack (previously used to toggle sounds, moved to volume settings)

## [2.1.0] - 2021-12-15

### Added

- New translations (Russian and Korean)

### Changed

- Reworked sound options menu (now is a very slightly modified version of the vanilla one)

## [2.0.2] - 2021-12-10

### Fixed

- Chat message sound when changing chat opacity
- Potion effects being played continuously

## [2.0.1] - 2021-11-09

### Fixed

- Effect removal sounds playing without any effect present
- Modded block sounds not being autogenerated

## [2.0.0] - 2021-11-05

### Added

- Resource pack support (thanks to ARRP)
- Better modded item support
- Auto generation API
- Effect sounds (buffs/debuffs)
- Volume control via Minecraft sound menu

### Changed

- Default sounds, including...
    - Item delete & item clone (sounds like items sizzling in lava)
    - Chat mention (different chime)
    - Other minor changes

### Removed

- Config file & screen

## [1.4.1] - 2021-06-10

### Changed

- Ported to 1.17

## [1.4.0] - 2021-05-23

### Added

- Creative block pick sound

### Fixed

- Reworked crash fix, should now work when the game is not ticking (e.g. with Inventory Pause)
- Item sound if transferring to full inventory
- Delete sound in creative if nothing is deleted

### Changed

- Tweaked food & other item sounds

## [1.3.1] - 2021-05-21

### Fixed

- Modded items not having sounds
- Crashing if item does not have a sound assigned

### Changed

- Moved more sounds from hardcoded to class based detection

## [1.3.0] - 2021-05-20

### Added

- Item stack drag sound
- Drop sound pitch based on amount (versus max stack)

### Fixed

- Item transfer sound in creative inventory
- Multiple sounds being played at the same time
- Crash when playing sounds during a sound tick

### Changed

- Moved sound logic to item creation instead of click event

## [1.2.0] - 2021-05-17

### Added

- Hotbar sound on key press
- Item delete sound (in creative inventory)

### Fixed

- Drop sound when disconnecting
- Drop sound playing when dropping nothing
- Crashing, once and for all (hopefully)

## [1.1.1] - 2021-05-10

### Changed

- Removed cloth config from jar
- Set environment to client only

## [1.1.0] - 2021-05-09

### Added

- Block-based inventory sounds
- Creative inventory click sounds

### Fixed

- Fix crashes on certain clicks
- Include cloth config in jar

## [1.0.0] - 2021-05-05

Initial release
