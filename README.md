# TRM Game & Interactive Media Showcase

A curated collection of interactive video games and 3D digital assets developed by students of Teknologi Rekayasa Multimedia (TRM). This repository serves as the official portfolio and exhibition catalog for campus academic events, exhibitions, and orientation showcases.

---

## Catalog Overview

| Project Name | Platform / Type | Technology Stack | Status | Distribution |
|---|---|---|---|---|
| PlatformMazeGame | Desktop (Windows / Linux / macOS) | Java, Swing, Maven | Stable | Source Code & Standalone Executable (.jar) |
| Souls2D | Desktop (Windows / Linux / macOS) | Java, Swing | Stable | Source Code & Standalone Executable (.jar) |
| Nerrow | Desktop (Windows x64) | Unity Engine, C# | Stable | Standalone Package (.zip) & Binary (.exe) |
| Clumsy Thief | Mobile (Android) | Android SDK | Stable | Android Application Package (.apk) |
| Rumah Environment | 3D Asset / Environment | Blender | Complete | Production Source File (.blend) |

---

## Standalone Executables and Downloads

All compiled packages, runnable archives, mobile installers, and production 3D assets are hosted under the official GitHub Release:

- Official Release: [v1.0.0 - Exhibition Release](https://github.com/muadzhdz/trm-game-showcase/releases/tag/v1.0.0)

### Asset Summary

1. PlatformMazeGame.jar (Java Runnable Archive)
   - Prerequisite: Java Runtime Environment (JRE) or Java Development Kit (JDK) 17 or higher.
   - Execution: Double-click the file, or execute via terminal:
     ```bash
     java -jar PlatformMazeGame.jar
     ```

2. Souls2D.jar (Java Runnable Archive)
   - Prerequisite: Java Runtime Environment (JRE) or Java Development Kit (JDK) 17 or higher.
   - Execution: Double-click the file, or execute via terminal:
     ```bash
     java -jar Souls2D.jar
     ```

3. NerrowBuild-Windows-x64.zip (Unity Desktop Standalone)
   - Prerequisite: 64-bit Windows 10 or 11 with DirectX 11/12 support.
   - Execution: Extract the zip archive and run `nerroww.exe`.

4. nerroww.exe (Unity Standalone Binary)
   - Direct binary executable for the Nerrow game client.

5. Clumsy-Thief.apk (Android Package)
   - Prerequisite: Android 8.0 (Oreo) or higher.
   - Execution: Sideload onto an Android device or launch within an Android emulator.

6. Rumah.blend (Blender 3D Project)
   - Prerequisite: Blender 3.6 LTS or Blender 4.x.
   - Details: High-fidelity residential architectural 3D environment with materials and scene hierarchy.

---

## Game Profiles and Gameplay Instructions

### 1. PlatformMazeGame

A cooperative 2D grid-based labyrinth puzzle game focused on timing, movement synchronization, and diamond collection.

- Gameplay Mechanics:
  - Players navigate through tile-based maze stages.
  - Collect scattered diamonds across the maze.
  - Reach the designated exit door to progress to subsequent levels.
- Controls:
  - Horizontal Axis (Left / Right): Keys `A` and `D`
  - Vertical Axis (Up / Down): Keys `UP ARROW` and `DOWN ARROW`
- Source Location:
  - Directory: `PlatformMazeGame/`
  - Build Tool: Apache Maven (`pom.xml`)

### 2. Souls2D

A top-down atmospheric horror adventure game featuring procedural room generation, entity pathfinding, and interactive altar mechanics.

- Gameplay Mechanics:
  - Evade hostile shadow patrol entities lurking in the chambers.
  - Discover and retrieve lost Soul fragments distributed across interconnected rooms.
  - Deposit retrieved souls onto ancient altars to unlock the primary gateway.
  - Escape through the grand Soul Door upon completing the rituals.
- Controls:
  - Movement: `W`, `A`, `S`, `D` or `Arrow Keys`
  - Action / Interact: Key `E` (Harvest soul / Activate altar)
  - Navigation / Pause: `ESC` / `ENTER`
- Source Location:
  - Directory: `Souls2D/`
  - Build Structure: Standard Java Source and Resource tree

### 3. Nerrow

A 3D exploration and horror adventure developed with the Unity Engine.

- Gameplay Mechanics:
  - First-person traversal in darkened interior settings.
  - Physics-based object interactions and suspense mechanics.
- Controls:
  - Movement: `W`, `A`, `S`, `D`
  - Camera: Mouse look
  - Interaction: Left Mouse Click / Key `E`

### 4. Clumsy Thief

A mobile casual game centered around reflex-based heist navigation and obstacle avoidance.

- Target Architecture: ARM64-v8a / armeabi-v7a (Android)
- Interaction: Touch controls

### 5. Rumah 3D Model

A comprehensive 3D architectural model suitable for game engine importing, virtual reality staging, and rendering passes.

- Software Compatibility: Blender, Unreal Engine, Unity (via FBX/glTF export)
- Contents: Geometry, lighting setups, materials, and spatial layout.

---

## Building from Source

### Building PlatformMazeGame (Maven)

Ensure Java 17+ and Apache Maven are installed.

```bash
cd PlatformMazeGame
mvn clean package
java -jar target/PlatformMazeGame-1.0-SNAPSHOT.jar
```

### Building Souls2D (JDK javac)

Ensure JDK 17+ is installed.

```bash
cd Souls2D
mkdir -p bin
javac -d bin $(find src -name "*.java")
jar cfe Souls2D.jar main.GameMain -C bin .
java -jar Souls2D.jar
```

---

## Exhibition and Booth Deployment Guide

For event staff and demonstration operators:

1. Java Environment: Verify that OpenJDK or Oracle JRE is installed on the demonstration workstation:
   ```bash
   java -version
   ```
2. One-Click Execution: Place `PlatformMazeGame.jar` and `Souls2D.jar` on the workstation desktop for immediate execution.
3. Mobile Kiosk: Install `Clumsy-Thief.apk` on a dedicated Android display tablet with screen lock configured for demo mode.
4. Windows Station: Extract `NerrowBuild-Windows-x64.zip` on the primary gaming terminal.

---

## License and Attribution

Developed as part of the student portfolio program at Teknologi Rekayasa Multimedia (TRM). All rights reserved by respective student authors and contributors. Educational and exhibition distribution permitted under academic guidelines.
