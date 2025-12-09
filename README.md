# HubClient

![Java](https://img.shields.io/badge/Java-8-orange) ![Forge](https://img.shields.io/badge/Forge-1.8.9-gray) ![Status](https://img.shields.io/badge/Status-Active-brightgreen)

**HubClient** is a highly customizable, feature-rich utility mod designed for Minecraft 1.8.9 (Forge). It focuses on Quality of Life (QOL), Rendering enhancements, and "Legit" combat utilities. It features a fully custom-coded ClickGUI, a high-definition font rendering engine, and extensive customization options including Chroma support.

Was Supposed to be a PvP/ Hypixel Skyblock "QOL" Cheat but if i decide to update later down the line currently there are no SkyBlock features hence why lots of mentions of "Skyblock QOL" or "Features are mentioned in Packages and Code.

## 🌟 Key Features

### 🖥️ Render & Visuals
* **Advanced ESP (Extra Sensory Perception):**
    * **Modes:** Toggle between **3D Box** (Wireframe) and **2D Box** (Billboard style).
    * **Targets:** Selectively target **Players**, **Animals**, or **Mobs**.
    * **Health Bars:** Dynamic Green-to-Red health indicators that render through walls.
    * **Skeleton ESP:** Real-time bone structure rendering for players.
    * **Tracers:** Stable lines drawn from the crosshair to targets.
    * **Exemplified Nametags:** Scaled nametags with background plates for high visibility.
    * **Chroma Support:** Full RGB cycle support for all ESP elements.
* **Image ESP:** Render custom `.png` images above entities in the world. Includes opacity control.
* **Visual Spin:** Client-side "Spinbot" animation (Third-person only). Does not affect server-side rotation.
* **FullBright:** Standard gamma toggle.

<img width="1917" height="1007" alt="Screenshot 2025-12-09 015328" src="https://github.com/user-attachments/assets/fa2f3824-3c73-4252-b281-a0f692d6505c" />
<img width="1919" height="1013" alt="Screenshot 2025-12-09 015355" src="https://github.com/user-attachments/assets/e8c3cbd5-4974-4370-ab8a-67f675636626" />

### ⚔️ Combat
* **Legit AimAssist:**
    * Smooth tracking based on sensitivity.
    * **Visual FOV:** Draws a circle on-screen showing the targeting zone.
    * **Smart Filtering:** Options for "Click Only" and "Weapon Only".
    * **Vertical Aiming:** Optional pitch assistance.
* **AutoClicker:** Configurable CPS (Clicks Per Second).

<img width="1919" height="1044" alt="Screenshot 2025-12-09 015215" src="https://github.com/user-attachments/assets/bed9f588-730e-4373-95df-51d893d795c0" />

### 🎨 Customization & GUI
* **Custom Font Engine:** * Integrated high-definition font renderer.
    * Supports 10+ fonts (Arial, Verdana, Roboto, Comic Sans, etc.).
    * Includes anti-aliasing and spacing fixes for a clean, modern look.
* **Theming:** * Global **Theme Color** slider.
    * **Chroma Mode:** Rainbow wave effect across the entire GUI and HUD.
* **Watermark:** * Customizable text input directly in the GUI.
    * "Side Glitch" effect (`§k`) for a hacker-style aesthetic.
    * Displays FPS and Coordinates.

<img width="1139" height="662" alt="Screenshot 2025-12-09 015242" src="https://github.com/user-attachments/assets/17feff78-2a4a-4e05-88ef-aae26bbe9314" />

### 🛠️ Utilities
* **AutoMiner:** Automatically holds the attack key for AFK mining tasks.
* **Sprint:** Simple toggle sprint.

---

## 📥 Installation

1.  **Download:** Get the latest `.jar` file from the [Releases](https://github.com/yourusername/HubClient/releases) tab.
2.  **Install Forge:** Ensure you have Minecraft Forge **1.8.9** installed.
3.  **Drop in Mod Folder:** Move the `HubClient-1.0.jar` into your `.minecraft/mods` folder.
4.  **Launch:** Start Minecraft and enjoy!

---

## 🎮 Usage

* **Open GUI:** Press **`Right Shift`** to open the ClickGUI.
* **Navigation:**
    * **Left Click:** Toggle modules on/off.
    * **Right Click:** Expand module settings (Sliders, Checkboxes, Modes).
    * **Click & Drag:** Move the windows around the screen.
* **Customizing Images:**
    * Place `.png` images in `.minecraft/config/hubclient/images/`.
    * Type the filename in the ImageESP settings.

---

## 🏗️ Building from Source

If you want to modify the code or contribute:

1.  Clone the repository:
    ```bash
    git clone [https://github.com/yourusername/HubClient.git](https://github.com/yourusername/HubClient.git)
    ```
2.  Open the project in **IntelliJ IDEA** or **Eclipse**.
3.  Run the gradle setup command:
    ```bash
    ./gradlew setupDecompWorkspace
    ```
4.  Build the mod:
    ```bash
    ./gradlew build
    ```
    The output file will be in `build/libs/`.

---

## 📝 Configuration

Configuration is handled automatically via `hubclient.cfg`.
* **Visuals:** Font choice, Chroma toggle, Theme Hue.
* **Modules:** Every setting (Range, FOV, Speed, etc.) is saved instantly upon change.

---

## ⚠️ Disclaimer

This mod is for educational purposes. The developers are not responsible for any bans or punishments received while using this client. Always check the server rules before using specific modules (especially AimAssist and Macros).

---

**Created by Realoliver123 (Me)**
