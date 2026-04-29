# Netzwerk
### Kurzbeschreibung der übernommenen Teile:
- Netzwerkkommunikation in `NetworkService`: Registrierung, Kartenübertragung, Statusabfrage und Bewegungen.
- Konzepte und Endpoints aus dem bereitgestellten Netzwerkprotokoll übernommen.
- Umsetzung mit Spring `WebClient`, XML und bereitgestellten Network-Library-Klassen.
- Hierbei wurde die Erklärung zum Konzept aber kein Code oder Pseudocode übernommen.

### Quellen der übernommenen Teile:
- `Netzwerkprotokoll.pdf`
- `Software Engineering I XML, XML Schema, Netzwerk.pdf`
- Bereitgestellte Network Library der Lehrveranstaltung
  https://homepage.univie.ac.at/kristof.boehmer/SE1/Network_Library/Documentation/
- Spring Framework Documentation: WebClient  
  https://docs.spring.io/spring-framework/reference/web/webflux-webclient.html

---

# Kartengenerierung
### Kurzbeschreibung der übernommenen Teile:
- Kartengenerierung in `HalfMapGenerator`.
- Für die Erreichbarkeitsprüfung wurde das Konzept einer Breitensuche verwendet.
- Hierbei wurde die Erklärung zum Konzept des Algorithmus aber kein Code oder Pseudocode übernommen.


### Quellen der übernommenen Teile:
- `SpielIdee.pdf`
- `Netzwerkprotokoll.pdf`
- Wikipedia: Breadth-first search  
  https://en.wikipedia.org/wiki/Breadth-first_search

---

# Wegfindung
### Kurzbeschreibung der übernommenen Teile:
- Wegfindung in `PathFinder`.
- Karte als Graph modelliert: Felder als Knoten, Nachbarfelder als Kanten.
- Für die günstigste Route wurde das Konzept des Dijkstra-Algorithmus verwendet.
- Hierbei wurde die Erklärung zum Konzept des Algorithmus aber kein Code oder Pseudocode übernommen.

### Quellen der übernommenen Teile:
- `SpielIdee.pdf`
- Wikipedia: Dijkstra's algorithm  
  https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm
- Oracle Java Documentation: PriorityQueue  
  https://docs.oracle.com/javase/8/docs/api/java/util/PriorityQueue.html

---

# Datenstrukturen
### Kurzbeschreibung der übernommenen Teile:
- Eigene Datenstrukturen: `Position`, `MapNode`, `GameHalfMap`, `TerrainType`, `Direction`.
- `HalfMapConverter` wandelt eigene Domain-Klassen in Network-Library-DTOs um.
- Übernommen wurde nur das Architekturkonzept der Trennung zwischen Domain-Modell und Netzwerk-DTOs.

### Quellen der übernommenen Teile:
- `Software Engineering I Architektur Tipps und Tricks.pdf`
- `Netzwerkprotokoll.pdf`

---


# Sonstige Quellen (welche nicht zu den vorherigen Punkten eingeordnet werden können)
### Kurzbeschreibung der übernommenen Teile:
- Vorgaben zu Projektstruktur, GitLab-Abgabe, Gradle, JAR-Erstellung wurden aus den Kursunterlagen übernommen.

### Quellen der übernommenen Teile:
- `Allgemeine Informationen zur Übung.pdf`
- `README on GitLab.pdf`
- `Software Engineering I Source Code Management.pdf`
- `Software Engineering I XML, XML Schema, Netzwerk.pdf`
- `git-cheatsheet.pdf`
