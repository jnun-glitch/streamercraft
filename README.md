# StreamerCraft

Client-side Fabric-Mod für Minecraft Java Edition 1.21.11. Die Mod baut auf sichtbaren Client-Informationen, manuellen Eingaben und ausdrücklich eingerichteten Quellen auf. Sie führt keine Käufe oder Verkäufe aus.

## Stand

Das Projekt enthält aktuell Phase 1 der Spezifikation: Fabric-Lifecycle, einen über die Steuerung belegbaren Dashboard-Keybind, ein klar als Grundgerüst markiertes Dashboard, lokal gespeicherte Designeinstellungen und eine optionale Mod Menu-Konfigurationsseite.

Es werden noch keine Auktionsangebote oder bestätigten Serverzahlungen erkannt, und es gibt noch keine Marktpreise oder OBS-Verbindungen. Das Dashboard zeigt deshalb keine Beispieldaten.

### Zahlungsnachricht testweise aufzeichnen

Gib im Spiel `/pay test` ein. Die Mod sendet den Befehl unverändert an den Server und zeichnet danach höchstens 30 Sekunden lang sichtbare Servermeldungen auf. Spielerchat wird ignoriert. Erkennt sie in einer Servermeldung eines der unten genannten Donut-Muster, markiert sie die Zeile als `PAYMENT_MESSAGE_MATCH` und beendet die Aufnahme. Die Datei liegt lokal unter `config/streamercraft/pay-tests/` im Minecraft-Konfigurationsordner. Verwende den Befehl nur, wenn `/pay test` auf deinem Server keine unerwünschte Aktion ausführt.

Die aktuellen Muster kommen aus den Beispielen `LuisZanz hat dir $1M gezahlt.` und `You received $1 from cranxcey`. Andere Formulierungen werden nicht als Zahlung markiert. Ein Treffer bedeutet nur, dass der Text passt; die Mod kann damit nicht serverseitig bestätigen, dass Geld wirklich übertragen wurde. Spielernamen können im sichtbaren Nachrichtentext enthalten sein. Die Datei wird nicht hochgeladen.

## Entwicklungsvoraussetzungen

- JDK 21
- Gradle 9.8.2
- Minecraft 1.21.11 mit den festgehaltenen Fabric-Versionen aus `gradle.properties`

Die Build-Konfiguration verwendet `net.fabricmc.fabric-loom-remap` und Mojang-Mappings.

## Build

Im Projektverzeichnis mit JDK 21 und Gradle 9.8.2:

```powershell
gradle assemble
```

Die Mod-JAR wird unter `build/libs/streamercraft-0.1.0.jar` abgelegt. `assemble` wurde mit JDK 21 und Gradle 9.8.2 erfolgreich ausgeführt. Dabei wurden Java- und Client-Code kompiliert und das Mod-JAR remappt. Minecraft wurde dabei nicht gestartet.

## Erste Nutzung

1. Mod-JAR, Fabric Loader und Fabric API für Minecraft 1.21.11 installieren.
2. Im Steuerungsmenü die Taste für „StreamerCraft-Dashboard öffnen“ prüfen oder neu belegen (Standard: O).
3. Optional Mod Menu installieren, um dieselbe Einstellungsseite von dort zu öffnen.

Die lokale Einstellungsdatei liegt unter `config/streamercraft/settings.properties` im Minecraft-Konfigurationsordner.

## Projektentscheidungen und offene Punkte

- Technische Festlegungen stehen in [DECISIONS.md](DECISIONS.md).
- Für den ersten server-spezifischen Parser erforderliche Angaben stehen in [OPEN_QUESTIONS.md](OPEN_QUESTIONS.md).
- Schichten und Abhängigkeiten beschreibt [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md).
