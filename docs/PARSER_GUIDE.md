# Parser-Leitfaden

Die Mod erkennt derzeit zwei Donut-Formate für Zahlungsbenachrichtigungen: `<Spieler> hat dir $<Betrag> gezahlt.` und `You received $<Betrag> from <Spieler>`. Der Client verarbeitet sichtbare Servermeldungen; eine Zeile mit einem führenden Präfix wie `<PLAYER .Hiksi3686>` wird als Spielerchat behandelt und ausgeschlossen.

`/pay test` leitet den eingegebenen Befehl unverändert weiter und zeichnet danach höchstens 30 Sekunden sichtbare Servermeldungen lokal in einer `.log`-Datei auf. Beim ersten passenden Zahlungsformat endet die Aufzeichnung; die auslösende Zeile wird als `PAYMENT_MESSAGE_MATCH` gespeichert. Andere Servermeldungen bleiben `UNCLASSIFIED`.

Ein Muster-Treffer beschreibt nur den sichtbaren Text. Er bestätigt keine serverseitige Transaktion. Weitere Formulierungen, Betragskürzel und ein vollständiger Auktionsparser sind noch nicht implementiert. Unvollständige oder mehrdeutige Beobachtungen müssen `UNSUPPORTED` beziehungsweise `AMBIGUOUS` bleiben. Ein verschwundenes Angebot gilt nicht als bestätigter Verkauf.

Vor einem Auktionsadapter werden ein anonymisiertes Menübeispiel, sichtbare Feldpositionen, Menge, Preis und Währung benötigt. Teile aus Logdateien nur relevante Zeilen und anonymisiere darin enthaltene Spielernamen, wenn sie nicht benötigt werden.
