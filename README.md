# Requirements
- Java 17

# Build
Currently, there are two options for building this app, for Windows and Linux operating systems.

### Windows build
First use `maven package` to create necessary jar files.  
Then, using `jpackage` you're able to generate an installer file that is easy to use by regular users.
Input command below to your terminal or console, it's one line! Note, be in `target` directory
```bash
# with explanation
jpackage --name ExcelImporter 
--input . 
--main-jar ExcelImporter-{version}.jar 
--main-class com.prodactivv.excelimporter.App 
--win-console       # this is required otherwise app won't start
--win-dir-chooser   # optional, gives user an option to choose installation directory
--win-shortcut      # optional, creates shortcut on desktop
--win-menu          # optional, creates windows menu entry
--win-menu-group "Bpower2" # optional, creates windows menu group with given name
--icon icon.ico     # optional, adds an custom icon
```

```bash
# copy paste
jpackage --name ExcelImporter --app-version x.x.x --input . --main-jar ExcelImporter-{version}.jar --main-class com.prodactivv.excelimporter.App --win-console --win-dir-chooser --win-shortcut --win-menu --win-menu-group "Bpower2" --icon icon.ico
```

### Linux build
Just use `maven package`. ExcelImporter-{version}.jar generated in `target` dir is the right file to run on Linux platform (from terminal obviously).