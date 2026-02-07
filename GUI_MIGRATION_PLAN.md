# KAES GUI Migration Plan
## From AWT to Modern Java GUI Framework

**Document Version**: 1.0
**Created**: 2026-02-05
**Project**: KAES (Kinship Algebra Expert System) v3.05
**Authors**: Michael Fischer, Dwight Read

---

## Executive Summary

This document outlines a comprehensive plan to migrate the KAES application from deprecated AWT components to a modern Java GUI framework. The application contains **173 Java files** with **65 files** heavily dependent on AWT, including sophisticated 3D visualization, custom graph rendering, and complex mathematical displays.

### Migration Scope
- **25 custom frames, dialogs, panels, and canvas components**
- **Custom 3D rendering engine** (Matrix3D/Model3D system)
- **Complex graph visualization** with double-buffering
- **76 files using deprecated positioning methods**
- **Complete layout redesign** (currently uses null layouts with absolute positioning)

---

## 1. Framework Selection

### Option A: JavaFX (Recommended)
**Pros:**
- Modern, actively maintained by Oracle/OpenJFX community
- Excellent 2D/3D graphics support with hardware acceleration
- Built-in 3D scene graph (eliminates custom Matrix3D code)
- FXML for declarative UI design
- CSS styling support
- Superior printing API
- Strong canvas/rendering capabilities
- Better layout managers

**Cons:**
- Steeper learning curve than Swing
- Larger runtime footprint
- Requires complete rewrite (no AWT compatibility layer)
- May require Java 11+ for best support

**Recommended for**: Long-term modernization, especially given heavy 3D/graphics requirements

### Option B: Swing
**Pros:**
- Minimal learning curve (very similar to AWT)
- Backward compatibility with some AWT components
- Mature, stable API
- Can migrate incrementally (mix Swing/AWT during transition)
- Smaller runtime footprint
- Well-documented

**Cons:**
- Still legacy technology (no major development since Java 8)
- Weaker 3D support (would still need custom Matrix3D)
- Less modern appearance
- No GPU acceleration
- Event dispatch thread complexity

**Recommended for**: Quick migration, minimal disruption, if 3D visualization acceptable as-is

### Option C: Hybrid Approach
**Strategy**: Swing for UI, keep custom 3D engine
- Migrate dialogs/menus/panels to Swing
- Retain Matrix3D/Model3D as-is, wrap in JPanel
- Incremental migration path

---

## 2. Migration Strategy

### Recommended Approach: **Phased Migration to JavaFX**

#### Phase 1: Preparation & Infrastructure (4-6 weeks)
**Goal**: Set up build system, establish patterns, create proof-of-concept

**Tasks:**
1. **Build System Updates**
   - Upgrade build.xml to support JavaFX dependencies
   - Add JavaFX SDK to lib/ directory or use Maven/Gradle
   - Configure module system (Java 9+) if needed
   - Update Manifest for JavaFX application launcher

2. **Establish Migration Patterns**
   - Create JavaFX equivalents document (AWT → JavaFX mapping)
   - Design new layout strategy (use BorderPane, VBox, HBox, GridPane)
   - Define CSS stylesheet for consistent styling
   - Establish FXML usage guidelines

3. **Proof of Concept**
   - Migrate one simple dialog (AboutDialog.java → AboutDialogFX.java)
   - Migrate one simple panel (MessagePanel.java → MessagePanelFX.java)
   - Validate event handling patterns
   - Test printing functionality

4. **Dependency Analysis**
   - Create dependency graph of GUI components
   - Identify shared utilities that need updating
   - Plan migration order (leaf nodes first)

**Deliverables:**
- Updated build configuration
- Migration pattern guide document
- 2-3 migrated proof-of-concept components
- Dependency graph diagram

---

#### Phase 2: Core Infrastructure Migration (6-8 weeks)
**Goal**: Migrate foundation classes and window management

**Tasks:**
1. **Window Management**
   - Migrate GlobalWindowManager.java → GlobalWindowManagerFX.java
   - Convert to JavaFX Stage management
   - Implement window menu using JavaFX Menu/MenuItem
   - Preserve window state persistence

2. **Base Classes**
   - Migrate ProtoFrame.java → ProtoStageFX.java (base for all windows)
   - Update Printable implementation → JavaFX PrinterJob
   - Migrate PrintManager.java → PrintManagerFX.java

3. **Dialogs (Low Complexity)**
   - AboutDialog.java → AboutDialogFX.java
   - QuitDialog.java → QuitDialogFX.java
   - PreferencesDialog.java → PreferencesDialogFX.java
   - ElementDialog.java → ElementDialogFX.java
   - EquationDialog.java → EquationDialogFX.java

4. **Preferences System**
   - Update Preferences.java for JavaFX window bounds
   - Test XML serialization compatibility
   - Migrate PreferencesDialog UI

**Deliverables:**
- Functioning window management system
- All dialog boxes migrated and tested
- Updated preferences storage
- Base classes for all other components

---

#### Phase 3: Main Application Windows (8-10 weeks)
**Goal**: Migrate primary frames and basic panels

**Tasks:**
1. **Startup Sequence**
   - Migrate MainFrame.java → MainApplication.java (JavaFX Application)
   - Create splash screen using JavaFX ProgressIndicator
   - Update application lifecycle

2. **Main Windows**
   - Migrate Kaes.java (algebra monitor) → KaesFX.java
   - Migrate KaesFrontFrame.java → KaesFrontFX.java
   - Update menu systems to JavaFX MenuBar

3. **Simple Panels**
   - MessagePanel.java → MessagePanelFX.java
   - ElementPanel.java → ElementPanelFX.java
   - CheckboxPanel.java → CheckboxPanelFX.java
   - ArrowPanel.java → ArrowPanelFX.java (use Canvas/SVGPath)

4. **Support Windows**
   - HelpFrame.java → HelpStageFX.java (use WebView for HTML help)
   - MessageFrame.java → MessageStageFX.java

**Deliverables:**
- Functioning application launcher
- All main windows operational
- Basic panels migrated
- Menu system fully functional

---

#### Phase 4: 2D Graph Visualization (10-12 weeks)
**Goal**: Migrate kinship graph rendering system

**Critical Components:**
- KinshipTermsPanel.java (core graph visualization)
- KintermMapCanvas.java
- KinshipTermsMapPanel.java
- KintermEntry.java (node rendering)
- LineObject.java (edge rendering)

**Tasks:**
1. **Canvas Migration**
   - Replace AWT Canvas with JavaFX Canvas
   - Convert Graphics2D calls to GraphicsContext API
   - Update double-buffering (JavaFX handles automatically)
   - Migrate color system (java.awt.Color → javafx.scene.paint.Color)

2. **Graph Rendering**
   - KinshipTermsPanel.java → KinshipGraphCanvas.java
   - Update node/edge drawing code
   - Migrate selection system (marching ants)
   - Convert mouse event handlers

3. **Graph Components**
   - KintermEntry.java → KintermNode.java (possibly use Circle/Group)
   - LineObject.java → KintermEdge.java (use Line/CubicCurve)
   - Consider using JavaFX scene graph instead of manual drawing

4. **Layout and Interaction**
   - Implement zoom/pan using JavaFX transformations
   - Migrate drag-and-drop for node repositioning
   - Update scrolling (ScrollPane → JavaFX ScrollPane)

**Migration Strategies:**

**Strategy A: Direct Canvas Port**
- Keep algorithmic approach, port drawing calls
- GraphicsContext replaces Graphics2D
- Fastest migration, familiar code structure

**Strategy B: Scene Graph Rewrite** (Recommended)
- Use JavaFX nodes (Circle, Line, Group) for graph elements
- Leverage built-in transformations, hit testing
- Better performance, cleaner code
- More work upfront, better long-term

**Deliverables:**
- Fully functioning 2D kinship graph display
- Mouse interaction (selection, dragging)
- Printing support
- Performance benchmarks

---

#### Phase 5: 3D Visualization System (12-16 weeks)
**Goal**: Migrate or replace custom 3D rendering engine

**Critical Components:**
- Matrix3D.java (transformation matrices)
- Model3D.java (3D model representation)
- ThreeD.java (rendering panel)
- Frame3D.java (3D window)
- Specialized models: SexMarkedModel3D.java, GenealogicalModel3D.java
- CalcCoordinates* (coordinate calculation system)

**Tasks:**

**Option A: Rewrite Using JavaFX 3D (Recommended)**

1. **3D Scene Setup**
   - Create JavaFX SubScene with PerspectiveCamera
   - Set up lighting (AmbientLight, PointLight)
   - Configure depth buffer

2. **Model Migration**
   - Matrix3D → Use JavaFX Rotate/Translate/Scale transformations
   - Model3D vertices → Point3D/TriangleMesh
   - Convert edge list to Cylinder/Box primitives or custom Shape3D
   - Node labels → Text with Billboard behavior

3. **Interaction**
   - Mouse rotation using Transform.rotate()
   - Trackball-style camera controls
   - Zoom using camera Z-position
   - Selection highlighting using material changes

4. **Rendering Features**
   - Dashed lines → Custom MeshView with texture/shader
   - Arrows → Cone primitives or custom geometry
   - Color coding → PhongMaterial with different colors
   - Layer visibility → Node.setVisible()

5. **Coordinate Calculation**
   - Keep CalcCoordinates* logic mostly intact
   - Output Point3D instead of custom format
   - Potentially simplify with JavaFX transformations

**Option B: Port Custom Engine to JavaFX Canvas**
- Keep Matrix3D/Model3D algorithms
- Replace Graphics calls with GraphicsContext
- Faster migration, less modern
- Miss out on hardware acceleration

**Deliverables:**
- Functioning 3D kinship algebra visualization
- Interactive rotation/zoom/pan
- All generator types displayed correctly
- Layer visibility controls
- Printing support for 3D views

---

#### Phase 6: Advanced Components (8-10 weeks)
**Goal**: Migrate remaining specialized components

**Tasks:**
1. **Kinterm Editor**
   - KintermFrame.java → KintermStageFX.java
   - Integrate migrated 2D/3D panels
   - Update toolbars and controls
   - Migrate ScrollPane usage

2. **Table Systems**
   - Table.java → Use JavaFX TableView
   - SimpleTablePanel.java → TableView with custom cell factories
   - SimpleTableMaker.java → Update for JavaFX
   - Cayley table display with custom formatting

3. **Equation System**
   - EquationPanel.java → EquationPanelFX.java
   - Consider MathJax/LaTeX rendering if needed
   - Maintain equation editing capabilities

4. **Custom Controls**
   - ImageButton.java → JavaFX Button with ImageView
   - MessageLine.java → Label with styling
   - MarchingAnts → Use Timeline animation

5. **File Dialogs**
   - Replace FileDialog with JavaFX FileChooser
   - Add file type filters
   - Remember last directory

**Deliverables:**
- Complete kinterm editor functionality
- Table displays working
- All custom controls migrated
- File operations functional

---

#### Phase 7: Testing & Polish (4-6 weeks)
**Goal**: Comprehensive testing and UI refinement

**Tasks:**
1. **Functional Testing**
   - Test all algebra operations
   - Verify kinship term mapping
   - Validate 3D visualization accuracy
   - Test print functionality across all views
   - Verify XML save/load compatibility

2. **Visual Polish**
   - Create consistent CSS stylesheet
   - Improve layout responsiveness
   - Add modern icons
   - Smooth animations/transitions
   - Improve color scheme

3. **Performance Optimization**
   - Profile rendering performance
   - Optimize 3D scene complexity
   - Reduce unnecessary redraws
   - Memory leak testing

4. **Documentation**
   - Update user documentation
   - Create developer guide for new architecture
   - Document JavaFX patterns used
   - Migration lessons learned

5. **Regression Testing**
   - Test with existing .kaes data files
   - Verify backwards compatibility
   - Test on Windows/Mac/Linux
   - Different Java versions (11, 17, 21)

**Deliverables:**
- Fully tested application
- Performance benchmarks
- Updated documentation
- Release candidate build

---

## 3. Technical Mapping Guide

### AWT → JavaFX Component Mapping

| AWT Component | JavaFX Equivalent | Notes |
|---------------|-------------------|-------|
| Frame | Stage | Top-level window |
| Dialog | Stage with modality | Use initModality(Modality.APPLICATION_MODAL) |
| Panel | Pane/VBox/HBox | Use layout panes instead of null layout |
| Canvas | Canvas | Similar API, GraphicsContext vs Graphics |
| Button | Button | Very similar |
| Label | Label | Very similar |
| TextField | TextField | Very similar |
| TextArea | TextArea | Enhanced with rich text support |
| Checkbox | CheckBox | CamelCase naming |
| Choice | ComboBox | More powerful |
| Menu/MenuBar | Menu/MenuBar | Very similar |
| MenuItem | MenuItem | Very similar |
| ScrollPane | ScrollPane | Different API but similar concept |
| FileDialog | FileChooser | More modern, better filters |

### Layout Migration

| AWT Pattern | JavaFX Solution |
|-------------|-----------------|
| setLayout(null) + setBounds() | Use BorderPane, VBox, HBox, GridPane |
| CardLayout | StackPane with visibility toggles or TabPane |
| GridLayout | GridPane |
| BorderLayout | BorderPane |
| Absolute positioning | Anchor/margin constraints in layout panes |

### Event Handling

| AWT Pattern | JavaFX Solution |
|-------------|-----------------|
| ActionListener | EventHandler<ActionEvent> or lambda |
| MouseListener | setOnMouseClicked/Pressed/Released |
| WindowListener | setOnCloseRequest, setOnShown, etc. |
| KeyListener | setOnKeyPressed/Released/Typed |
| ItemListener | ChangeListener for properties |

### Graphics/Rendering

| AWT API | JavaFX API |
|---------|------------|
| Graphics.drawLine() | GraphicsContext.strokeLine() |
| Graphics.fillRect() | GraphicsContext.fillRect() |
| Graphics.setColor() | GraphicsContext.setStroke/setFill() |
| Image | javafx.scene.image.Image |
| BufferedImage | WritableImage |
| Graphics2D transforms | GraphicsContext transforms or Node transforms |

### 3D Rendering

| Custom AWT 3D | JavaFX 3D |
|---------------|-----------|
| Matrix3D | Rotate, Translate, Scale transforms |
| Model3D vertices | Point3D, TriangleMesh |
| Manual projection | PerspectiveCamera |
| Z-buffer sorting | Depth buffer (automatic) |
| Custom lighting | AmbientLight, PointLight |

---

## 4. Risk Assessment

### High Risk Items

1. **3D Visualization Complexity**
   - **Risk**: Custom 3D engine is tightly coupled to AWT Graphics
   - **Mitigation**: Prototype JavaFX 3D early, validate feasibility
   - **Fallback**: Keep custom engine, wrap in JavaFX Canvas

2. **Performance Regression**
   - **Risk**: JavaFX rendering slower than optimized AWT code
   - **Mitigation**: Benchmark early, optimize scene graph
   - **Fallback**: Use Canvas for complex visualizations

3. **Backwards Compatibility**
   - **Risk**: .kaes XML files may have serialized AWT components
   - **Mitigation**: Test thoroughly, add migration code if needed
   - **Fallback**: Maintain compatibility layer

4. **Layout Complexity**
   - **Risk**: Absolute positioning replacement is time-consuming
   - **Mitigation**: Create reusable layout templates
   - **Fallback**: Use JavaFX with null layout initially (technical debt)

### Medium Risk Items

1. **Printing System**
   - **Risk**: JavaFX printing API differs significantly
   - **Mitigation**: Test printing early and often

2. **Platform Dependencies**
   - **Risk**: JavaFX runtime availability varies by platform
   - **Mitigation**: Use OpenJFX, create platform installers

3. **Learning Curve**
   - **Risk**: Development slower due to unfamiliarity
   - **Mitigation**: Training, prototyping, code reviews

---

## 5. Resource Requirements

### Development Time Estimate
- **Phase 1**: 4-6 weeks (1 developer)
- **Phase 2**: 6-8 weeks (1 developer)
- **Phase 3**: 8-10 weeks (1 developer)
- **Phase 4**: 10-12 weeks (1-2 developers)
- **Phase 5**: 12-16 weeks (1-2 developers)
- **Phase 6**: 8-10 weeks (1 developer)
- **Phase 7**: 4-6 weeks (1-2 developers)

**Total**: 52-68 weeks (1 year to 1.3 years) with 1 full-time developer

With 2 developers in parallel (Phases 4-6):
**Total**: 40-52 weeks (~10-12 months)

### Skills Required
- Java 11+ expertise
- JavaFX experience (Scene Builder helpful)
- 3D graphics knowledge (for Phase 5)
- Domain knowledge of kinship algebra (for testing)
- UI/UX design (for Phase 7 polish)

---

## 6. Alternative: Incremental Swing Migration

If JavaFX is deemed too large an investment, a Swing migration offers a middle path:

### Swing Migration Advantages
- Can mix AWT and Swing during transition
- Smaller changes to existing code
- Less training required
- Faster initial migration

### Swing Migration Timeline
- **Phase 1-3**: 12-16 weeks (infrastructure, windows, dialogs)
- **Phase 4**: 6-8 weeks (2D graphs using JPanel)
- **Phase 5**: 4-6 weeks (keep custom 3D, wrap in JPanel)
- **Phase 6**: 6-8 weeks (advanced components)
- **Phase 7**: 3-4 weeks (testing)

**Total**: 31-42 weeks (~8-10 months)

### Swing Migration Tradeoffs
- Still uses legacy technology
- No GPU acceleration
- Less modern appearance
- 3D system remains custom
- May need future migration to JavaFX anyway

---

## 7. Quick Start: First Migration Steps

### Immediate Actions (Week 1)

1. **Set up JavaFX Development Environment**
   ```bash
   # Download OpenJFX SDK
   # Update build.xml with JavaFX dependencies
   # Test "Hello World" JavaFX application
   ```

2. **Create Migration Branch**
   ```bash
   git checkout -b feature/javafx-migration
   ```

3. **Migrate AboutDialog as Proof-of-Concept**
   - Create src/fx/AboutDialogFX.java
   - Implement using Stage, Scene, VBox
   - Add OK button with event handler
   - Test launching from existing AWT code

4. **Document Patterns**
   - Record migration decisions
   - Create code templates
   - Establish naming conventions (FX suffix for new classes)

### Example: AboutDialog Migration

**Before (AWT):**
```java
public class AboutDialog extends Dialog {
    public AboutDialog(Frame parent) {
        super(parent, "About KAES", true);
        setLayout(null);
        setSize(300, 200);
        Label label = new Label("KAES v3.05");
        label.setBounds(100, 50, 100, 20);
        add(label);
        // ... more components
    }
}
```

**After (JavaFX):**
```java
public class AboutDialogFX {
    public void show() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("About KAES");

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER);

        Label title = new Label("KAES v3.05");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label authors = new Label("Michael Fischer & Dwight Read");

        Button okButton = new Button("OK");
        okButton.setOnAction(e -> dialog.close());

        content.getChildren().addAll(title, authors, okButton);

        Scene scene = new Scene(content, 300, 200);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
}
```

---

## 8. Decision Points

### Critical Decisions Needed

1. **Framework Choice**
   - [ ] JavaFX (modern, long-term)
   - [ ] Swing (faster migration)
   - [ ] Hybrid (Swing + custom 3D)

2. **3D Strategy**
   - [ ] Rewrite with JavaFX 3D
   - [ ] Port custom engine to JavaFX Canvas
   - [ ] Keep AWT 3D, wrap in Swing/JavaFX

3. **Layout Approach**
   - [ ] Full redesign with proper layout managers
   - [ ] Quick port maintaining absolute positioning
   - [ ] Hybrid (new layouts for new code, preserve old)

4. **Migration Style**
   - [ ] Big bang (complete rewrite before release)
   - [ ] Incremental (release hybrid versions)
   - [ ] Parallel (maintain both codebases)

5. **Version Targeting**
   - [ ] Java 11 LTS (oldest supported)
   - [ ] Java 17 LTS (recommended)
   - [ ] Java 21 LTS (newest, best JavaFX support)

---

## 9. Success Criteria

### Functional Requirements
- [ ] All existing features work identically
- [ ] .kaes XML files load/save correctly
- [ ] 3D visualization matches current implementation
- [ ] Printing produces identical output
- [ ] Preferences persist correctly
- [ ] All keyboard shortcuts work

### Non-Functional Requirements
- [ ] No performance regression (target: equal or better)
- [ ] Runs on Windows, macOS, Linux
- [ ] Modern, professional appearance
- [ ] Responsive layouts (handles window resize)
- [ ] Accessible (keyboard navigation, screen reader support)

### Technical Requirements
- [ ] No deprecated API usage
- [ ] Clean separation of concerns (MVC pattern)
- [ ] Maintainable, well-documented code
- [ ] Automated tests for core functionality
- [ ] Successful builds on CI/CD

---

## 10. Conclusion

The migration from AWT to a modern GUI framework is a substantial undertaking due to:
- Heavy use of custom 3D rendering
- Extensive absolute positioning throughout
- Complex graph visualization requirements
- 173 source files with 65 GUI-dependent files

### Recommended Path Forward

**Short Term (6 months):**
1. Select JavaFX as target framework
2. Complete Phases 1-3 (infrastructure, dialogs, main windows)
3. Create proof-of-concept 2D graph rendering
4. Validate 3D approach with prototype

**Medium Term (12 months):**
1. Complete Phases 4-5 (2D and 3D visualization)
2. Beta testing with real kinship data
3. Performance optimization

**Long Term (18 months):**
1. Complete Phase 6-7 (advanced components, polish)
2. Full regression testing
3. Production release
4. Retire AWT codebase

### Next Steps

1. **Obtain stakeholder approval** for framework selection
2. **Allocate resources** (developer time, budget)
3. **Set up development environment** with JavaFX SDK
4. **Begin Phase 1** with proof-of-concept migrations
5. **Schedule regular reviews** to assess progress and adjust plan

---

**Document Control:**
- **Version**: 1.0
- **Last Updated**: 2026-02-05
- **Next Review**: After Phase 1 completion
- **Owner**: Michael Fischer
