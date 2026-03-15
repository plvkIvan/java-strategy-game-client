# Software Engineering I - Teilaufgabe 1 (Anforderungsanalyse und Planungsphase)

## Abgabedokument - Teilaufgabe 1 (Anforderungsanalyse und Planungsphase)

- Nachname, Vorname: Polovko Ivan
- Matrikelnummer: 12232715

## Aufgabe 1: Anforderungsanalyse

### Quellen dokumentieren - Aufgabe 1: Anforderungsanalyse

- **Kurzbeschreibung der übernommenen Teile**:  
  Die Anforderungen wurden auf Basis der bereitgestellten **Spielidee** des Übungsprojekts erstellt. Aus diesem Dokument wurden zentrale Inhalte analysiert und daraus mehrere funktionale und nichtfunktionale Anforderungen sowie eine Designbedingung abgeleitet.  
  Übernommen wurden die grundlegenden **Ideen und Spielmechaniken** (z. B. Bewegung über die Karte, Finden des Schatzes, Zeitlimit der Aktionen, Client-Server Architektur).

- **Quellen der übernommenen Teile**:
  - Dokument **„Spielidee“** aus den Unterlagen der Lehrveranstaltung
  - Kursmaterialien / Repository der Lehrveranstaltung Software Engineering
  - Vorlage und Aufgabenbeschreibung im GitLab-Repository

### Typ der Anforderung: funktional

- **Anforderung**: Karte erkunden und eigenen Schatz finden – Der Client muss seine Spielfigur über die Spielkarte bewegen, um schrittweise neue Kartenfelder aufzudecken und dadurch den eigenen Schatz zu finden.
- **Bezugsquelle**: Spielidee, **Zitat**
  > „Um den Schatz zu finden, bewegen beide KIs ihre Spielfigur über die Karte und decken dabei mit ihrer Spielfigur schrittweise Kartenfelder auf.”

  **Anforderung 2**

- **Anforderung**: Kartenhälfte erzeugen und an Server senden – Der Client muss eine eigene Kartenhälfte erzeugen und diese an den Server übertragen, damit die vollständige Spielkarte erstellt werden kann.
- **Bezugsquelle**: Spielidee, **Zitat**
  > „Nach Start des Clients registrieren sich die KIs für das Spiel am Server und erstellen/tauschen danach mit dem Server Kartenhälften aus.“

**Anforderung 3**

- **Anforderung**: Nach Schatzfund gegnerische Burg suchen – Nachdem der Client den eigenen Schatz gefunden hat, muss er die gegnerische Burg auf der Karte suchen und dorthin navigieren.
- **Bezugsquelle**: Spielidee, **Zitat**
  > „Sobald eine KI "ihren" Schatz gefunden bzw. aufgedeckt hat, muss sie ihre Spielfigur zu diesem bewegen, um ihn aufnehmen zu können. Danach, sobald der Schatz aufgenommen wurde, muss die KI mit ihrer Spielfigur die gegnerische Burg finden.“

### Typ der Anforderung: nicht funktional

**Anforderung 4**

- **Anforderung**: Spielaktionen innerhalb von Zeitlimit durchführen – Der Client muss jede Spielaktion innerhalb von maximal 5 Sekunden durchführen, damit das Spiel korrekt fortgesetzt werden kann.
- **Bezugsquelle**: Spielidee, **Zitat**
  > „Um die Spiele für die Zuschauer spannend zu gestalten, wurde festgelegt, dass ein Spiel insgesamt nicht länger als 320 Spielaktionen (und damit 320 Runden) dauern darf. Für jede dieser rundenbasierten Spielaktion hat die KI maximal 5 Sekunden Bedenkzeit. Insgesamt dauert ein Spiel maximal 10 Minuten.“

**Anforderung 5**

- **Anforderung**: Autonome Spielentscheidungen treffen – Der Client muss das Spiel autonom ausführen und Entscheidungen ohne menschliche Eingriffe treffen.
- **Bezugsquelle**: Spielidee, **Zitat**
  > „Die KI entscheidet selbst, ohne menschliches Zutun, wie hierzu am Besten vorzugehen ist.“

**Anforderung 6**

- **Anforderung**: Spielzustände korrekt anzeigen – Der Client muss vom Server gemeldete Gegnerpositionen auch bei ungewöhnlichen Positionsänderungen im Client-UI anzeigen.
- **Bezugsquelle**: Spielidee, **Zitat**
  > „Zufällige Position des Gegners: Bis zum Abschluss der ersten 8 eigenen Clientaktionen hat die vom Server gemeldete Position des Gegners zufällig zu sein. Dies kann zu unerwarteten Positionen und vermeintlichen Sprüngen zwischen Feldern für den Gegner führen. Zeigen Sie diese trotzdem einfach im Client-UI an.“

### Typ der Anforderung: Designbedingung

**Anforderung 7**

- **Anforderung**: Client-Server Architektur verwenden – Das System muss eine Client-Server Architektur verwenden, bei der Clients über einen Server miteinander interagieren.
- **Bezugsquelle**: Spielidee, **Zitat**
  > „Die Grobarchitektur ist damit als klassische Client/Server Architektur vorgegeben.“


## Aufgabe 2: Anforderungsdokumentation

- **Name**: Karte erkunden und eigenen Schatz finden
- **Beschreibung und Priorität**: Der Client muss seine Spielfigur über die Spielkarte bewegen, um schrittweise neue Kartenfelder aufzudecken und dadurch den eigenen Schatz zu finden. Die Anforderung ist zentral für den Spielverlauf, da ohne das Erkunden der Karte der Schatz nicht gefunden werden kann und der Client das Spielziel nicht weiterverfolgen kann. 
- **Priorität: Hoch**