# Running KAES on macOS

## Quick Start

### Build the Application
```bash
ant clean
ant jar
```

### Run the Application
```bash
./run-kaes.sh
```

Or use Ant:
```bash
ant run
```

## Requirements

- Java 17 or later (OpenJDK recommended)
- Apache Ant (for building)
- macOS (tested on Darwin 23.2.0)

## What Was Fixed

This legacy application (circa 2000) has been updated to run on modern macOS with Java 17. Key fixes include:

1. **Character encoding** - Fixed non-UTF-8 source files
2. **Deprecated Thread methods** - Replaced suspend/resume with wait/notify
3. **Java module system** - Fixed access to encapsulated APIs
4. **ICE Browser compatibility** - Gracefully handles SecurityManager incompatibility
5. **macOS integration** - Added proper AWT configuration for macOS

## Known Limitations

- **HTML Help Browser**: The embedded help browser is not compatible with Java 17+. Help windows display plain text instead of HTML. Original help files are available in `KaesHelp/` directory.

## Documentation

See `MODERNIZATION_NOTES.md` for detailed technical information about all changes made.

## Support

For issues or questions about KAES functionality, please refer to the original documentation in the `Notes/` directory.
