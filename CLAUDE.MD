# Project Context for Claude

## Project Overview
KAES (Kinship Algebra Expert System) - A specialized application for analyzing kinship terminology systems and constructing kinship algebras. This is an anthropological research tool that models kinship relationships using algebraic structures.

## Key Information
- **Project Type**: Desktop Java Application (AWT-based GUI)
- **Main Technologies**:
  - Java (legacy codebase, circa 2002-2011)
  - Apache Ant build system
  - AWT for GUI components
  - XML for data persistence
- **Version**: 3.05 (as of build.xml)
- **Purpose**: Anthropological research tool for analyzing kinship terminology and constructing algebraic models of kinship systems
- **Authors**: Michael Fischer & Dwight Read
- **Organization**: HRAF (Human Relations Area Files)

## Core Functionality
- **Kinship Term Mapping**: Visual mapping and editing of kinship terms
- **Algebra Construction**: Building kinship algebras from generators
- **Graph Visualization**: 2D and 3D graph rendering of kinship structures
- **Cayley Tables**: Mathematical representation of kinship algebra operations
- **Rule Processing**: Multiple rule types (Crow Skewing, Spouse Sibling, Cousin, etc.)
- **XML Import/Export**: Saving and loading kinship analysis data

## Preferences
- **Code Style**:
  - Early 2000s Java conventions
  - Minimal commenting (code from 2002-2011 era)
  - Uses AWT rather than Swing
  - Some legacy patterns and naming conventions
- **Testing Requirements**: None apparent (no test framework present)
- **Documentation Style**: Inline code comments, historical change notes in file headers

## Important Notes
- This is a **legacy application** - code dates from 2002-2011
- Uses deprecated Java AWT (not Swing) for GUI components
- Built for older Java versions - may need compatibility considerations
- Active research tool for anthropological kinship analysis
- Contains complex mathematical/algebraic operations for kinship modeling
- Has both 2D and 3D visualization capabilities

## File Structure
```
├── src/              # Java source files (~175 files)
│   ├── MainFrame.java          # Main application entry point
│   ├── KinshipAlgebra.java     # Core algebra engine
│   ├── KintermFrame.java       # Kinship term editor
│   ├── Algebra.java            # Base algebra operations
│   ├── CayleyTable.java        # Cayley table generation
│   └── [Various Rule classes]  # Rule processors
├── bin/              # Compiled classes (273+ files)
├── lib/              # External JARs (aelfred, icebrowserbean, org_csac_io)
├── dist/             # Distribution JARs
├── resources/        # Application resources (images, etc.)
├── Notes/            # Development notes and tasks
├── Terminologies/    # Kinship terminology data
├── DR additions/     # Dwight Read's additions
├── build.xml         # Ant build configuration
├── Manifest          # JAR manifest (Main-Class: MainFrame)
└── KAESPrefs.xml     # Application preferences
```

## Common Tasks
- **Build**: `ant compile` or `ant jar`
- **Run**: `ant run` (builds and executes JAR)
- **Clean**: `ant clean` (removes bin/ and dist/)
- **Main Entry Point**: MainFrame.java (creates splash screen, menus, main window)

## Domain Terminology
- **Kin Terms**: Terms like "mother", "uncle", "cousin" in various cultural systems
- **Kin Types**: Genealogical relationships (e.g., MBD = Mother's Brother's Daughter)
- **Generators**: Base elements used to construct kinship algebras (UP, SIDE orientations)
- **Cayley Table**: Multiplication table for algebraic group operations
- **Reciprocals**: Inverse kinship relationships

---
*This anthropological research tool models kinship systems using mathematical algebra*
