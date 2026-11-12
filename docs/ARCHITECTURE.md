# Architektur

Der initiale Code hält Minecraft-Integration, lokale Einstellungen und Darstellung getrennt.

- `src/main`: Mod-Lifecycle und Minecraft-unabhängige Einstellungen.
- `src/client`: Keybind, Dashboard und optionale Mod Menu-Anbindung.
- `domain`: künftig neutrale Datenmodelle und Regeln ohne GUI-Abhängigkeit.
- `capture`: künftig nur clientseitig sichtbare Informationen in neutrale Beobachtungen umwandeln.
- `auction` / `serverprofile`: künftig serverbezogene Parser und Profile.
- `economy`: künftig Preisnormalisierung und Statistiken.
- `stream`: künftig manuelle und erkannte Events sowie Regeln.
- `overlay`: künftig nur bei Nutzeraktivierung gestarteter lokaler OBS-Gateway.
- `data`: künftig lokaler Speicher, Migrationen, Export und Löschung.

Beim Ausbau bleiben Tick- und Rendercallbacks frei von blockierendem I/O. Unbekannte Serverformate erzeugen keine Preis- oder Verkaufswerte.
