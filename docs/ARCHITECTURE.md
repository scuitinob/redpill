# RedPill architecture — 0.2.2

## Core

`MouseActivityService` owns the scheduler, action state and AWT `Robot`. It reproduces the original BluePill sequence with PyAutoGUI-equivalent 100 ms pauses.

`HumanActivityMonitor` owns JNativeHook's global listeners. It is armed only while RedPill is ACTIVE. Before each Robot mouse movement or Shift press, the service tells the monitor what synthetic event to expect. Matching events are ignored; other global mouse/key activity is treated as human input.

## UI

`RedPillWindow` owns the Swing window and binds START/STOP to `MouseActivityService`.

`HumanActivityDialog` pauses automation and gives the user 10 seconds to stop or continue.

`RedPillButton` uses a basic Swing UI so Windows/macOS/Linux look-and-feel implementations cannot replace RedPill's button colors.

`CorvinSignaturePanel` and `ResourceImages` load branding from `src/main/resources/images`.

`TrayController` uses the RedPill artwork for the system-tray icon where supported.
