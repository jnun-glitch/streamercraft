# Technische Entscheidungen

## 2026-11-12 – Phase 1

- **Ziel:** Minecraft Java 1.21.11 und Fabric; ausschließlich Client-Umgebung.
- **Erster Zielserver:** Donut SMP, abgeleitet aus den vom Nutzer gelieferten Zahlungsbeispielen.
- **Mappings:** Mojang-Mappings für den ersten Code-Stand. Fabric führt diese Namen in seiner versionsbezogenen 1.21.11-Dokumentation und stellt dafür einen Mapping-Migrationspfad bereit.
- **Loom:** `net.fabricmc.fabric-loom-remap`, da 1.21.11 noch obfuskiert ist. Gepinnte Loom-Version: `1.17.21`.
- **Build- und Laufzeitbasis:** Java 21 Toolchain; Gradle Wrapper-Ziel `9.2.1`.
- **Fabric-Komponenten:** Loader `0.20.1`, Fabric API `0.141.6+1.21.11`.
- **Optionale Mod Menu-Integration:** `17.0.1`, nur `modCompileOnly`; Mod Menu ist keine Laufzeitpflicht.
- **Konfiguration:** einfache Java-Properties-Datei in `config/streamercraft/`. Für die einzige Phase-1-Option wird keine zusätzliche Config-Bibliothek eingebunden.
- **Dashboard-Design:** kosmisches Navy/Violett als Standard und ein dunkles Mitternachts-Theme als zweite lokale Option; beide sind reversibel und austauschbar.
- **Keine Platzhalterdaten:** Serverprofil, Marktbeobachtungen und Streamevents bleiben leer, bis reale Adapter und Datenquellen umgesetzt sind.
- **Zahlungsdiagnose:** Nur der exakt eingegebene Befehl `/pay test` startet ein höchstens 30-Sekunden-Fenster. Die Mod verändert oder blockiert den Befehl nicht. Sie zeichnet nur sichtbare Servermeldungen lokal unter `config/streamercraft/pay-tests/` auf und ignoriert Spielerchat. Die angegebenen deutschen und englischen Donut-Formulierungen werden nur in Servermeldungen als `PAYMENT_MESSAGE_MATCH` markiert; der Texttreffer ist keine serverseitige Transaktionsbestätigung und beendet das Testfenster.
- **Nachrichtenquelle:** Erkennung läuft ausschließlich über Fabric's serverseitiges `GAME`-Nachrichtenereignis. Sichtbare Texte mit dem Präfix `<PLAYER` werden zusätzlich ausgeschlossen.
- **Begrenzung der Aufnahme:** höchstens 256 wartende Meldungen; Datei-I/O läuft in einem separaten Daemon-Thread. Überläufe werden in der Logdatei gezählt. Das Diagnosefenster speichert keine Server-IP und sendet nichts nach außen.

## Verifizierte Primärquellen

- [Fabric 1.21.11 Release-Hinweise](https://www.fabricmc.net/2025/12/05/12111.html)
- [Fabric Loom Plugin-IDs und Versionen](https://docs.fabricmc.net/develop/loom/)
- [Fabric-Dokumentation: Key Mappings für 1.21.11](https://github.com/FabricMC/fabric-docs/blob/main/versions/1.21.11/develop/key-mappings.md)
- [Fabric-Beispielcode: Key Mapping für 1.21.11](https://github.com/FabricMC/fabric-docs/blob/main/reference/1.21.11/src/client/java/com/example/docs/keymapping/ExampleModKeyMappingsClient.java)
- [Fabric-Beispielcode: Screen für 1.21.11](https://github.com/FabricMC/fabric-docs/blob/main/reference/1.21.11/src/client/java/com/example/docs/rendering/DrawContextExampleScreen.java)
- [Fabric API 1.21.11: ClientReceiveMessageEvents](https://maven.fabricmc.net/docs/fabric-api-0.141.6%2B1.21.11/net/fabricmc/fabric/api/client/message/v1/ClientReceiveMessageEvents.html)
- [Fabric API 1.21.11: ClientSendMessageEvents](https://maven.fabricmc.net/docs/fabric-api-0.141.6%2B1.21.11/net/fabricmc/fabric/api/client/message/v1/ClientSendMessageEvents.html)
- [Fabric API 1.21.11: ClientLifecycleEvents](https://maven.fabricmc.net/docs/fabric-api-0.141.6%2B1.21.11/net/fabricmc/fabric/api/client/event/lifecycle/v1/ClientLifecycleEvents.html)
- [Fabric Maven: Fabric API für 1.21.11](https://maven.fabricmc.net/net/fabricmc/fabric-api/fabric-api/)
- [Fabric Loom Remap Plugin Marker](https://maven.fabricmc.net/net/fabricmc/fabric-loom-remap/net.fabricmc.fabric-loom-remap.gradle.plugin/)
- [Mod Menu Entwicklerdokumentation](https://github.com/TerraformersMC/ModMenu)

## Noch nicht festgelegt

- Erste unterstützte Server und deren GUI-/Chatformat.
- Welche sichtbare Nachricht für Zahlungen oder bestätigte Verkäufe steht.
- Namensspeicherung, Aufbewahrung und Exportregeln für Spielerdaten.
- Lizenz des Gesamtprojekts und mögliche Asset-Lizenzen.
