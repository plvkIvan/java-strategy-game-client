# Netzwerk

### Kurzbeschreibung der übernommenen Teile:

* Die Netzwerkumsetzung basiert auf den SE1-Unterlagen, dem Netzwerkprotokoll und der Network Library.

### Quellen der übernommenen Teile:

* `Netzwerkprotokoll.pdf`
* `Software Engineering I XML, XML Schema, Netzwerk.pdf`
* SE1 Network Library Documentation: https://homepage.univie.ac.at/kristof.boehmer/SE1/Network_Library/Documentation/

# Benutzeroberfläche & MVC

### Kurzbeschreibung der übernommenen Teile:

* Das MVC Pattern wurde als Konzept für die CLI-Benutzeroberfläche verwendet.
* Im Projekt betrifft dies `GameUiModel`, `GameUiController`, `CliGameView`, `MapRenderer` und `LegendRenderer`.
* Zusätzlich wurde das Observer Pattern für die Aktualisierung der View genutzt.
* Für die CLI-Darstellung wurden UTF-8-Emojis verwendet.
* Es wurde kein externer Code direkt übernommen; die Konzepte wurden an das Projekt angepasst.

### Quellen der übernommenen Teile:

* https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93controller
* https://en.wikipedia.org/wiki/Observer_pattern
* https://de.wikipedia.org/wiki/UTF-8

# Kartengenerierung

### Kurzbeschreibung der übernommenen Teile:

* Das Open-Closed Principle wurde als Konzept für die Kartenvalidierung verwendet.
* Im Projekt betrifft dies `MapValidationRule`, `MapValidator` und die einzelnen Validation-Rule-Klassen.
* Das Notification Pattern wurde für das Sammeln und Anzeigen von Validierungsfehlern genutzt.

### Quellen der übernommenen Teile:

* https://en.wikipedia.org/wiki/Open%E2%80%93closed_principle
* https://java-design-patterns.com/patterns/notification/

# Wegfindung

### Kurzbeschreibung der übernommenen Teile:

* Das Strategy Pattern wurde als Konzept für die Suchlogik verwendet.
* Im Projekt betrifft dies `TargetSearchStrategy`, `TreasureTargetSearchStrategy`, `CastleTargetSearchStrategy`, `TreasureSearch`, `CastleSearch` und `TargetSearchService`.
* Übernommen wurde nur die Grundidee austauschbarer Strategien über ein gemeinsames Interface.
* Der konkrete Code wurde selbst an Schatzsuche, Burgsuche und Spielregeln angepasst.

### Quellen der übernommenen Teile:

* https://en.wikipedia.org/wiki/Strategy_pattern
* https://java-design-patterns.com/patterns/strategy/

# Datenstrukturen

### Kurzbeschreibung der übernommenen Teile:

* Die verwendeten Datenstrukturen wurden auf Basis von Java-Grundlagen und SE1-Anforderungen eingesetzt.

### Quellen der übernommenen Teile:

* Keine externen Quellen.

# Business Logik (Logik/Algorithmen, die nicht zu den vorherigen Themen passen)

### Kurzbeschreibung der übernommenen Teile:

* Die Business-Logik basiert auf der Spielidee, der SE1-Angabe und der eigenen Projektstruktur.

### Quellen der übernommenen Teile:

* Keine externen Quellen.

# Qualitätsfaktoren - Logging

### Kurzbeschreibung der übernommenen Teile:

* Für Logging wurde SLF4J verwendet.
* Im Projekt betrifft dies `Logger`, `LoggerFactory`, verschiedene Log-Level und die Konfiguration in `logback.xml`.
* Übernommen wurde das allgemeine Logging-Konzept, nicht konkreter Code.

### Quellen der übernommenen Teile:

* https://www.slf4j.org/manual.html
* https://www.slf4j.org/docs.html

# Qualitätsfaktoren - Fehlerbehandlung

### Kurzbeschreibung der übernommenen Teile:

* Die eigenen checked und unchecked Exceptions wurden selbst für den Client erstellt.

### Quellen der übernommenen Teile:

* Keine externen Quellen.

# Qualitätsfaktoren - Testing

### Kurzbeschreibung der übernommenen Teile:

* Für Unit Tests wurde JUnit 5 verwendet.
* Für Mocking wurde Mockito verwendet.
* Übernommen wurden die allgemeinen Konzepte von Assertions, `when(...)` und `verify(...)`.
* Der konkrete Testcode wurde selbst für die Projektklassen geschrieben.

### Quellen der übernommenen Teile:

* https://docs.junit.org/5.10.5/user-guide/
* https://site.mockito.org/
* https://javadoc.io/doc/org.mockito/mockito-core/latest/index.html
