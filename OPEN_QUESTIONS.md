# Offene Produktfragen

Die folgenden Antworten werden erst benötigt, bevor server-spezifische Erkennung oder produktive Streamalerts implementiert werden. Das neutrale Grundgerüst verwendet keine dieser Annahmen.

## Vor dem ersten Auktionsparser

1. Wie sieht Donuts Auktionsmenü aus, und wo erscheinen Item, Menge, Preis und Währung? Ein anonymisierter Screenshot oder Beispiel reicht.
2. Gibt es eine eindeutige sichtbare Bestätigung, dass ein Listing verkauft wurde?
3. Welche Gebühren oder Steuern gelten auf diesem Server?

## Vor automatischen Stream-Events

1. Bitte bestätige nach einem Test, ob beide Donut-Formulierungen im Log als `PAYMENT_MESSAGE_MATCH` markiert werden.
2. Falls Donut weitere Formulierungen oder Betragskürzel nutzt, teile jeweils nur eine anonymisierte Beispielzeile. Die Mod zeichnet Spielerchat nicht auf.
3. Sollen Namen im HUD/OBS angezeigt, maskiert oder weggelassen werden?

## Vor dem OBS-Gateway

1. Reicht eine lokale OBS Browser Source oder wird zusätzlich Szenensteuerung benötigt?
2. Soll der lokale Dienst standardmäßig aus bleiben und erst nach Aktivierung starten? Die Spezifikation spricht für „aus“.
3. Welche Zielauflösung und Standardposition soll das Alert-Overlay haben?

## Daten und Veröffentlichung

1. Wie lange sollen Beobachtungen aufbewahrt und sollen Exporte standardmäßig anonymisiert werden?
2. Welche Lizenz soll das Gesamtprojekt verwenden?
3. Für optionale Sounds oder Bilder: Liegen freigegebene Dateien und zugehörige Lizenznachweise vor?
