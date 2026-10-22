# Axiom Tinker Filter (Minecraft 26.3)

Clientseitige Fabric-Erweiterung für Axiom. Im ESC-Menü erscheinen links unten zwei Schalter:

- **Tinker Wolle: AUS/AN**
- **Tinker Concrete: AUS/AN**

Beim ersten Start stehen beide auf **AUS**. Axioms Tinker ignoriert dann die 16 Vanilla-Wollblöcke und die 16 Vanilla-Concrete-Blöcke. Die Einstellungen stehen in `config/axiom-tinker-filter.properties`.

Dieses Pack wurde vom Autor mit Codex per Vibe Coding erstellt.

## Installation

`axiom-tinker-filter-1.0.0-mc26.3-cucuking.jar` zusammen mit [Axiom 6.1.3 für Minecraft 26.3](https://modrinth.com/mod/axiom/version/CrfjL7cY) in den Fabric-`mods`-Ordner legen. Die Erweiterung ersetzt Axiom nicht.

## Test und technische Hinweise

Getestet mit Minecraft 26.3 und [Axiom 6.1.3 von Modrinth](https://modrinth.com/mod/axiom/version/CrfjL7cY).

Der Tinker-Hook wurde anhand der lokal vorhandenen älteren Axiom-JAR erstellt.

Der lokale Build verwendet den vorhandenen Java-21-Compiler und eine nur zum Kompilieren erzeugte Kopie des Minecraft-26.3-Archivs mit angepasstem Classfile-Header. Die ausgegebene Mod-JAR enthält keine Minecraft- oder Axiom-Dateien.
