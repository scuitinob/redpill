# RedPill 0.2.3

RedPill is a portable Java 21 desktop utility by Corvin Develop. While active, it reproduces the original BluePill mouse/Shift sequence and can detect real keyboard/mouse activity to ask whether the automation should stop.

## Stack

- Java 21 LTS
- Gradle 9.7.1 (Groovy DSL)
- Swing / AWT
- JNativeHook 2.2.2 for global keyboard/mouse activity detection

## Run during development

Windows:

```powershell
.\gradlew.bat run
```

Build:

```powershell
.\gradlew.bat clean build
```

Executable JAR:

```powershell
java -jar .\build\libs\redpill-0.2.3.jar
```

Portable image for the current OS:

```powershell
.\gradlew.bat portableImage
```

The result is written to `build/portable/`. `jpackage` must be run separately on Windows, macOS and Linux for each native package.

## Activity sequence

Each ACTION reproduces the original Python BluePill sequence:

1. Move mouse to `(0, 0)`, `(0, 4)`, ... `(0, 196)`.
2. Wait 100 ms after each movement, matching PyAutoGUI's default pause.
3. Move to `(1, 1)` and wait 100 ms.
4. Press and release `Shift` and wait 100 ms.
5. Wait for the selected interval before the next ACTION.

A complete automatic ACTION therefore takes about 5.2 seconds plus the selected interval before the next one begins.

## Human activity

While RedPill is active, a global keyboard press or mouse movement pauses the current/future automatic activity and displays a 10-second confirmation dialog.

- **SÍ, DETENER**: stops RedPill and resets ACTIONS to `0`.
- **NO, CONTINUAR**: resumes RedPill and waits one full interval before the next action.
- **No response for 10 seconds**: same behavior as Continue.

RedPill marks the coordinates and Shift press it generates itself so those Robot events are not treated as human input.

## Branding

The supplied RedPill artwork is used as the in-app title. A dedicated red-pill image is used for window, dialog and tray icons. The official Corvin Develop logo is embedded in the footer.


## Movement log

The GUI includes a scrolling Movement Log. Each action writes a start timestamp, completion timestamp, and separator, mirroring BluePill's terminal output. The first action is scheduled immediately when START is pressed; the selected interval is applied between completed actions.
