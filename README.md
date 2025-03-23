# Osu!-MP3
Play Songs From Your Osu! Directory!

![Release Version](https://img.shields.io/github/v/release/Supernova1114/Osu-MP3)
 
**Requires: Java 17+**
<br>
**Tested on Windows 10 and Ubuntu 22.0.4**

**How to use:**
1. Add Osu! beatmaps to a collection to be used as a playlist. (Osu! Stable: Close Osu! to apply collection changes).
2. Place Osu!-MP3 jar file in folder, as the program will create items within the same directory.
3. Start Osu!-MP3
4. For Osu! Stable: Go to (File > Set Osu! Stable Folder) and set this to the Osu! Stable installation directory.
Osu! Stable Mode will initialize and begin building a cache of songs (This may take a few minutes to process).
5. For Osu! Lazer: Go to (File > Set Osu! Lazer Folder) and set this to the Osu! Lazer "data directory" (Not installation directory).
6. Click a song name to begin playing a shuffled playlist of the selected collection.
7. You can switch between collections using the combo box near the top of the window.
8. You can switch between Osu! Lazer and Osu! Stable modes using the Mode Menu.


**Controls:**
* Up and down arrow keys for volume.
* Space key to play/pause.
* Keyboard and headphone media keys for audio playback (Play/Pause, SkipNext/SkipPrev) (Even while program is minimized).
* Click and drag mouse to move song panel around or use mouse wheel.

**Notes:**
* For headphones with a single media button, the controls are usually: double-click = next song, triple-click = prev song, single-click = play/pause

Uses libraries:
* https://github.com/tulskiy/jkeymaster for media button keystrokes.
* https://github.com/hendriks73/ffsampledsp and https://github.com/hendriks73/audioplayer4j for .OGG and .MP3 audio playback.
* https://github.com/mpatric/mp3agic for obtaining MP3 audio length.
* https://github.com/controlsfx/controlsfx for Searchable ComboBox
* https://github.com/openjdk/jfx for GUI components.
* https://github.com/realm/realm-kotlin for reading Osu! Lazer database.

![Application Image](repoimages/appv0.6.1.png)
