# KAES - Kinship Algebra Expert System

A specialized application for analyzing kinship terminology systems and constructing kinship algebras. This is an anthropological research tool that models kinship relationships using algebraic structures.

## Version

3.05

## Authors

- Michael Fischer
- Dwight Read

## Organization

[HRAF (Human Relations Area Files)](https://hraf.yale.edu/)

## Overview

KAES is a desktop Java application designed for anthropological research. It provides tools for:

- **Kinship Term Mapping**: Visual mapping and editing of kinship terms
- **Algebra Construction**: Building kinship algebras from generators
- **Graph Visualization**: 2D and 3D graph rendering of kinship structures
- **Cayley Tables**: Mathematical representation of kinship algebra operations
- **Rule Processing**: Multiple rule types (Crow Skewing, Spouse Sibling, Cousin, etc.)
- **XML Import/Export**: Saving and loading kinship analysis data

## Requirements

- **Java**: Java 17 or later (OpenJDK recommended)
- **Build Tool**: Apache Ant
- **Operating System**: macOS, Linux, Windows (tested primarily on macOS)

## Building

```bash
# Clean previous builds
ant clean

# Compile source code
ant compile

# Build JAR file
ant jar
```

The built JAR file will be located at `dist/KAES_KAM305.jar`.

## Running

### Using Ant

```bash
ant run
```

### Direct Java Execution

```bash
java -Dapple.awt.application.name=KAES \
     -Dapple.laf.useScreenMenuBar=true \
     -jar dist/KAES_KAM305.jar
```

### macOS Script

A convenience shell script is provided:

```bash
./run-kaes.sh
```

## Features

### Kinship Analysis
- Map and edit kinship terms visually
- Define kin types and relationships
- Calculate reciprocals and validate terminology
- Support for multiple cultural systems

### Algebra Construction
- Build algebras from generators (UP, SIDE orientations)
- Create and manipulate algebraic symbols
- Generate equations and test for consistency
- Handle sex-marked and sex-unmarked algebras

### Visualization
- 2D graph rendering with interactive controls
- 3D graph visualization with rotation and zoom
- Color-coded kinship terms
- Export capabilities

### Rule Processing
- Crow Skewing rules
- Spouse Sibling rules
- Cousin rules
- Sex rules
- Product rewriting rules
- And more...

## Project Structure

```
├── src/              # Java source files (~175 files)
├── bin/              # Compiled classes
├── lib/              # External JAR dependencies
├── dist/             # Distribution JARs
├── resources/        # Application resources (images, etc.)
├── Notes/            # Development notes and tasks
├── Terminologies/    # Kinship terminology data
├── DR additions/     # Dwight Read's additions
├── build.xml         # Ant build configuration
├── Manifest          # JAR manifest
└── KAESPrefs.xml     # Application preferences
```

## Documentation

- `README-MACOS.md` - macOS-specific setup and known issues
- `MODERNIZATION_NOTES.md` - Technical details about modernization work
- `progress.md` - Development progress tracking
- `GUI_MIGRATION_PLAN.md` - Planned GUI modernization efforts

## Display Scaling

KAES supports display scaling for HiDPI/Retina displays. The scale factor can be set via preferences:

- **1x**: Standard resolution (default)
- **2x**: 2x scaling for HiDPI displays
- **4x**: 4x scaling for 4K/UHD displays

Edit `KAESPrefs.xml`:

```xml
<entry key="Display_Scale_Factor">2</entry>
```

## Known Limitations

- **HTML Help Browser**: The embedded help browser is not compatible with Java 17+. Help windows display plain text instead of HTML. Original help files are available in the `KaesHelp/` directory.

## License

Please refer to the authors and organization for licensing information.

## Support

For issues or questions about KAES functionality, please refer to the documentation in the `Notes/` directory.

## Contributing

This is a legacy application (circa 2002-2011) that has been updated for modern Java versions. Contributions are welcome, especially for:

- GUI modernization from AWT to Swing/JavaFX
- Enhanced compatibility with newer Java versions
- Improved help system
- Additional kinship terminology modules

## Acknowledgments

KAES was developed as part of anthropological research at HRAF (Human Relations Area Files) to model kinship systems using mathematical algebra.
