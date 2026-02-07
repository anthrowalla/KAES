# KAES HiDPI Scaling Implementation - Progress Summary

## Goal
Rewrite KAES (built for low-res screens ~2002-2011) to support display scaling at 1x, 2x, and 4x. All windows, components, text, and custom painting should scale via a central `ScaleManager` class controlled by a user preference.

## Architecture

### ScaleManager.java (COMPLETE)
Central static utility class providing two scaling methods:
- `ScaleManager.s(int pixels)` — scales pixel values by the current factor
- `ScaleManager.font(String name, int style, int size)` — creates a font with scaled size
- `ScaleManager.initFromPreferences()` — loads scale factor from `KAESPrefs.xml` before any windows are created
- Valid scale factors: 1, 2, 4

### Initialization Order (COMPLETE)
In `MainFrame.main()`, `ScaleManager.initFromPreferences()` is called first (line 355), before `InitPreferences.init()` and before any windows are constructed. This ensures the scale factor is set before any AWT components are created.

```java
public static void main(String args[]) {
    ScaleManager.initFromPreferences();
    InitPreferences.init(MainFrame.prefs);
    MainFrame k = new MainFrame();
    k.init();
    ...
}
```

### Preference Registration (COMPLETE)
`InitPreferences.java` registers `Display_Scale_Factor` with default value 1.

## Completed Work

### Files Scaled (34 files, ~294 `ScaleManager.s()` calls, ~53 `ScaleManager.font()` calls)

All major UI files have been updated to use `ScaleManager.s()` for pixel dimensions and `ScaleManager.font()` for font creation:

- **Core**: MainFrame, Kaes, ScaleManager
- **Dialogs**: AboutBox, AboutDialog, ElementDialog, EquationDialog, PreferencesDialog, QuitDialog
- **Panels**: ArrowChoices, ArrowPanel, CheckboxPanel, ElementPanel, EquationPanel, KinshipTermsPanel, KinshipTermsMapPanel, MessagePanel
- **Frames**: Frame1, Frame3D, HelpFrame, HelpMessageFrame, KintermFrame, KintermMapFrame, MessageFrame, TextWindow, InputWindow
- **Entry/Forms**: KintermEntry, KinTextEntry, KintermTextEntryForm
- **Tables/Graphics**: Table, SimpleTableMaker, ThreeD, MarchingAnts
- **Other**: KaesFrontFrame, MessageLine, ImagePanel

### What Was Scaled
- All `setSize()`, `setBounds()`, `reshape()` calls with hardcoded pixel values
- All `new Font(...)` constructors replaced with `ScaleManager.font()`
- Custom painting code (line widths, offsets, connection points)
- Canvas and panel dimensions
- Button and label dimensions
- Dialog and frame sizes

## Recent Session Work

### GitHub Repository Setup (COMPLETE)
Project published to GitHub at **https://github.com/anthrowalla/KAES** (public, under `anthrowalla` account).

- Created `.gitignore` excluding `bin/`, `dist/`, `.DS_Store`, `*.jsonl`, IDE files, `KAESPrefs.xml`, `*.xcodeproj/`
- Initialized git repo, committed all source files, pushed to GitHub

### KintermFrame Window Sizing Fix (COMPLETE)
**Problem**: When a kinterm map is loaded from MainFrame, the `KintermFrame` window opens too small at scale 2 or 4. The layout components (algebraControlPanel, ThreeD graph, text areas, scrollPane) are all positioned using `ScaleManager.s()`, but `setFromPreferences()` applies saved window dimensions from XML files as raw pixels. These saved values (e.g., `WindowWidth=990, WindowHeight=719`) were written at scale 1 and are far too small for scaled layouts.

**Root Cause**: `setFromPreferences()` applied saved window bounds directly without considering the current scale factor. The XML files did not store what scale factor was active when preferences were saved.

**Fix (in `src/KintermFrame.java`)**:

1. **`setFromPreferences()`** — Added minimum size enforcement based on scaled layout:
   ```java
   int minW = ScaleManager.s(976);
   int minH = ScaleManager.s(600);
   if (ww < minW) ww = minW;
   if (wh < minH) wh = minH;
   ```
   This ensures the window is always large enough for the algebraControlPanel, ThreeD graph, text areas, and scroll pane at the current scale factor.

2. **`updatePreferences()`** — Now saves the current scale factor alongside window dimensions for future use:
   ```java
   docPreferences.putPreference("ScaleFactor", ScaleManager.getScaleFactor());
   ```

**Build**: `ant compile` and `ant jar` both succeed. Changes not yet committed/pushed to GitHub.

## Remaining Work

### 1. Commit & Push Window Sizing Fix (NOT DONE)
The KintermFrame changes are built but not yet committed or pushed to GitHub.

### 2. PreferencesDialog Scale Factor UI (NOT DONE)
Currently the preferences dialog shows all prefs as generic text fields. Need to add a proper UI control (Choice dropdown or radio buttons) for the Display_Scale_Factor preference with valid values 1, 2, 4. The current generic text field approach works but is not user-friendly and has no validation.

### 3. PrintManager Font Scaling (NOT DONE)
`PrintManager.java` has 3 hardcoded `new Font("Helvetica", Font.PLAIN, 12)` calls at lines 313, 345, and 359. These are for print output. Scaling these is debatable — print output may intentionally use fixed sizes — but for consistency they could use `ScaleManager.font()`.

### 4. run-kaes.sh JVM Flags (NOT DONE)
The launch script has no HiDPI-related JVM flags. Could add `-Dsun.java2d.uiScale=1` to prevent the JVM from applying its own scaling on top of KAES's manual scaling. Current script:
```bash
java -Dapple.awt.application.name=KAES \
     -Dapple.laf.useScreenMenuBar=true \
     -jar dist/KAES_KAM305.jar
```

### 5. Build Verification (NOT DONE)
Compilation succeeds (`ant compile` — 9 warnings, all pre-existing deprecation warnings, no errors). JAR packaging and runtime testing at scale factors 2 and 4 have not been done.

## How to Change the Scale Factor
Currently, edit `KAESPrefs.xml` and set:
```xml
<entry key="Display_Scale_Factor">2</entry>
```
Valid values: `1` (default), `2`, or `4`. Restart KAES after changing.

Alternatively, open Preferences from the Edit menu — the Display Scale Factor will appear as a text field where you can type 1, 2, or 4.
