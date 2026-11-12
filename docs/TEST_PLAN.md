# Prüfstatus und manueller Prüfplan

## Lokale Prüfungen ohne Minecraft

- `gradle --no-daemon assemble` wurde am 12.11.2026 mit JDK 21 und Gradle 9.8.2 erfolgreich ausgeführt. Java- und Client-Quellen wurden kompiliert, die Ressourcen verarbeitet und das Mod-JAR remappt.
- 22 virtuelle Java-Prüfungen des Zahlungs-Parsercodes bestanden. Abgedeckt waren die deutschen und englischen Donut-Beispiele, Beträge mit Leerzeichen und Dezimalstellen, führende Leerzeichen, das Präfix `<PLAYER .Hiksi3686>`, gewöhnlicher Servertext und ungültige Zahlungsformate.
- Der Minecraft-Client wurde nicht gestartet. Die folgenden Laufzeitprüfungen stehen noch aus.

## Manuelle Prüfungen in Minecraft

- Client mit Minecraft 1.21.11 und Fabric API starten.
- Start ohne Mod Menu prüfen.
- Dashboard per Keybind öffnen und schließen.
- Theme wechseln, Client neu starten und gespeicherte Einstellung prüfen.
- Optional Mod Menu installieren und Konfigurationsknopf prüfen.
- `/pay test` eingeben und prüfen, ob der Befehl unverändert an den Server geht.
- Prüfen, ob sichtbare Servermeldungen lokal unter `config/streamercraft/pay-tests/` gespeichert werden.
- Die Donut-Beispiele `LuisZanz hat dir $1M gezahlt.` und `You received $1 from cranxcey` sollen als `PAYMENT_MESSAGE_MATCH` erscheinen und die auslösende Zeile im Log hinterlassen.
- Spielerchat mit einem Präfix wie `<PLAYER .Hiksi3686>` darf nicht aufgezeichnet oder als Zahlung markiert werden.
- Der Texttreffer darf nicht als serverseitige Transaktionsbestätigung dargestellt werden.
