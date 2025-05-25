# 🧄 OnionGarlicRun

OnionGarlicRun is an Android game in which the player controls an eggplant that must dodge falling bombs (garlic, purple onion, white onion) and collect coins to earn points. The game features multiple modes and high-score tracking with location data.

---

## 🎮 Game Modes

- **Slow Mode**: Default speed for beginners.
- **Fast Mode**: Faster drop and spawn rate for more challenge.
- **Sensor Mode**: Control the eggplant using device tilt (accelerometer).

---

## 🧠 Features

- 🧅 Random bomb types (garlic, onion variants)
- 💰 Coins that increase your score
- ❤️ Heart system (3 lives)
- 📈 High-score tracking (name + location)
- 🗺️ Google Maps integration to show player location of top scores
- 🔊 Sound effects
- 🧑 Accessibility via `contentDescription`

---

## 🧪 How to Run

1. Clone the project or download the ZIP.
2. Open in **Android Studio Hedgehog** or later.
3. Make sure you have:
   - Google Maps API key in `AndroidManifest.xml`
   - Permissions for **Internet** and **Location**
4. Build and run on a real device or emulator with sensors.

---

## 📁 Project Structure

| File / Folder                | Purpose                                  |
|-----------------------------|------------------------------------------|
| `MainActivity.kt`           | Main game logic and event loop           |
| `GameLogic.kt`              | Handles movement, collision, score, lives|
| `StartApp.kt`               | Mode selection screen                    |
| `FirstScreen.kt`            | Entry screen and location handling       |
| `RecordsActivityV2.kt`      | High-score display with Google Map       |
| `utils/Smanager.kt`         | Toasts & vibrations                      |
| `utils/Sounds.kt`           | Sound playback manager                   |
| `utils/ScoreManager.kt`     | High-score saving/loading (SharedPreferences) |
| `utils/TiltDetector.kt`     | Handles accelerometer input              |
| `res/layout`                | All UI XML files                         |
| `res/drawable`              | Game assets (images)                     |
| `res/values/strings.xml`    | All app strings for localization         |

---

## 📝 Notes

- The eggplant is controlled using buttons or tilt.
- Only 10 top scores are saved, each with name and location.
- `tag` values are used on grid cells to avoid collisions (e.g. "bomb", "coin").
- The game uses `CountDownTimer` for drop animations.

---

## 🛡️ Permissions

- `INTERNET` – To use Google Maps
- `ACCESS_FINE_LOCATION` – For player geolocation
- `VIBRATE` – For haptic feedback on collision

---

## 🔑 Google Maps API

Make sure you add your own API key here:
```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="YOUR_KEY_HERE" />
