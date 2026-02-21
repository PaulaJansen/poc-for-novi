# A Lot Of Art

GitHub repository: https://github.com/PaulaJansen/poc-for-novi

API Documentatie:

## Inhoudsopgave
* Inleiding
* Benodigdheden
* Projectstructuur
* Project starten
* Testing

## Inleiding
De A Lot Of Artwork (ALOA) web-API is gebouwd met gebruik van het Spring Framework. De frontend applicatie is gebouwd met React. ALOA is een gemakkelijke etalage voor kunstenaars om hun werken de wereld in te sturen en een winkel voor bezoekers die geïnteresseerd zijn in toegankelijke kunst. Zij kunnen hier zoeken naar werk dat het aanspreekt en dit uiteindelijk ook kopen en/of huren. 

## Benodigdheden
### Voor de backend
...........................................................................................
* PostgreSQL
  * PostgreSQL is een databasemanagementsysteem om databases te creeëren en beheren.
  * Minimale versie: 14.20
* Apache Maven
  * Apache Maven is een tool die primair gebruikt wordt voor beheer van dependencies, compilatie en testing in Java-projecten.  
  * Minimale versie: 3.9.11
* Java
  * Java is de programmeertaal waarin de API is geschreven. Het is pas mogelijk om Maven te gebruiken na installatie van Java.
  * Minimale JDK versie: 21.0.10
* pgAdmin 4 (niet essentieel)
  * pgADdmin is een beheerplatform voor PostgreSQL-database dat een GUI biedt voor een database. In pgAdmin kun je data uit de database uitlezen en queries uitvoeren. PostgreSQL draait ook zonder pgAdmin en is te gebruiken via de command line. pgAdmin biedt echter een sterk verbeterde en visuele ervaring voor databasebeheer.   
  * Minimale versie: 9.6
* Postman (niet essentieel)
  * Postman is een tool om API's te testen door middel van het versturen van HTTP-requests naar de API, zonder dat er een werkende frontend nodig is. In dit project kan de frontend ook gebruikt worden om endpoints te triggeren, in plaats van Postman.
  * Minimale versie 11.80.0

### Voor de frontend
...........................................................................................
* Node.js
  * Node.js is een runtime environment waarin JavaScript-applicaties kunnen draaien. 
  * Minimale versie: 22.15.0
* npm
  * npm (node package manager) is beheersoftware voor packages voor Node.js en biedt commando's onder andere om de frontendapplicatie te draaien. 
  * Minimale versie: 10.9.2
* Vite
  * Vite is een bundler en ontwikkelserver voor frontendontwikkeling. 
  * Minimale versie: 7.1.7
* React
  * React is een JavaScript-bibliotheek voor het bouwen van dynamische gebruiksinterfaces on webapplicaties. 
  * Minimale versie: 19.1.16

## Projectstructuur
Deze applicatie bestaat uit een frontend en een backend applicatie. De frontend is geschreven in JavaScript en gebouwd met de JavaScript library React. De backend  is geschreven in Java en gebouwd met het Spring Framework.

## Project starten
1. Clone bovenstaande GitHub-repository naar je eigen systeem.
2. Installeer PostgreSQL als je dit nog niet eerder hebt gedaan:
   * https://www.postgresql.org/download/
   * Selecteer je OS en volg de instructies.
3. Installeer Apache Maven als je dit nog niet eerder hebt gedaan:
   * https://maven.apache.org/download.cgi
   * Download de gewenste installatiepackage (minimaal versie 3.9.11)
   * Volg eventueel de installatie-instructies op https://maven.apache.org/install.html.
4. Installeer Java als je dit nog niet eerder hebt gedaan:
   * https://www.oracle.com/nl/java/technologies/downloads/#java21
   * Selecteer je OS en download de gewenste installatiepackage.
     Volg eventueel de installatie-instructies op https://docs.oracle.com/en/java/javase/21/install/overview-jdk-installation.html
5. Installeer indien gewenst pgAdmin 4:
   * https://www.pgadmin.org/download/
   * Selecteer de gewenste installatiemethode en volg de instructies.
6. Installeer indien gewenst Postman:
   * https://www.postman.com/downloads/
     Als alternatief voor het downloaden en installeren kan Postman ook in de browser gebruikt worden. Hiervoor klik je op de downloadpagina op “Try the Web Version”.
7. Installeer Node.js en npm als je dit nog niet eerder hebt gedaan:
   * https://nodejs.org/en/download
   * Voer de gewenste versie (minimaal 22.15.0) en je OS in. Kies “using nvm with npm”.
   * Voer de commando’s uit. 
8. Maak een nieuwe database:
   * Optie 1: Open een terminal (Powershell of Command Prompt in Windows, Terminal in MacOs/Linux) en typ de volgende commando's in:
     * `psql -U postgres`
     * (voer je wachtwoord in)
     * `CREATE DATABASE aloa;`
     * `\l` (om te controleren of de database goed is aangemaakt)
   * Optie 2: Open pgAdmin en open de query tool. Typ in `CREATE DATABASE aloa;` en voer het script uit. 
9. Pas de applicatie configuratie aan voor jouw database:
   * Open `[application.properties]()`:
     * Controleer dat deze regel erin staat: `spring.datasource.url=jdbc:postgresql://localhost:5432/aloa`
     * Controleer dat de naam van je database overeenkomt met de naam aan het einde van deze regel. Heb je je database een andere naam gegeven? Pas deze regel dan aan.
   * Zorg dat jouw PostgreSQL-gebruikersnaam overeenkomt met de gebruikersnaam in dit bestand:
     * Zoek deze regel: `spring.datasource.username= [jouw-gebruikersnaam]`
     * Vervang `[jouw-gebruikersnaam]` voor jouw postgres gebruikersnaam.
   * Zorg dat jouw PostgreSQL-wachtwoord overeenkomt met het in dit project gebruikte wachtwoord:
     * Voeg je wachtwoord als omgevingsvariabele toe aan via de terminal:
       * Ga naar de [backend root map](C:\Users\paula\FullstackPocNovi\backend\Poc-backend) van het project met het command `cd <adres-waarnaar-je-het-project-hebt-gecloned>\FullstackPocNovi\backend\Poc-backend`. Gebruik het command `setx POSTGRESQL_PASSWORD "jouw-wachtwoord"` (Windows) of `export POSTGRESQL_PASSWORD="jouw-wachtwoord"` (MacOS/Linux) om je wachtwoord toe te voegen. 
     * Voeg de omgevingsvariabelen toe in je IDE:
        * Voeg de waarde `POSTGRESQL_PASSWORD=[jouw wachtwoord]` toe.
     * Wil je geen omgevingsvariabelen gebruiken? Voer dan achter de lijn `spring.datasource.password=` jouw wachtwoord in.
10. Draai de backend applicatie:
    * Open een nieuwe terminal en navigeer naar de [backend root map](C:\Users\paula\FullstackPocNovi\backend\Poc-backend) van het project met het command `cd <adres-waarnaar-je-het-project-hebt-gecloned>\FullstackPocNovi\backend\Poc-backend`.
    * Voeg nog een omgevingsvariabele voor de JWT-key toe:
      * Gebruik het command `setx JWT_SECRET  WhenthepawnhitstheconflictshethinkslikeakingWhatheknowsthr0wsthebl0ws` (Windows) of `export JWT_SECRET=WhenthepawnhitstheconflictshethinkslikeakingWhatheknowsthr0wsthebl0ws` (MacOS/Linux). Je mag ook een andere waarde van tenminste 32 bytes invoeren. 
    * Sluit deze terminal en open een nieuwe. Navigeer weer naar de root map.
    *  Voer het commando `mvn spring-boot:run` uit.
    * Als hij klaar is loggen en de laatste regel zegt  Started PocBackendApplication in..., dan is de applicatie succesvol gestart.
11. Draai de frontend applicatie:
    * Open een nieuwe terminal. Navigeer naar de [frontend root map](C:\Users\paula\FullstackPocNovi\frontend) met het command `cd <adres-waarnaar-je-het-project-hebt-gecloned>\FullstackPocNovi\frontend`.
    * Voer het commando `npm install` uit.
    * Voer het commando `npm run dev` uit. 
    * Als de regel  `➜  Local:   http://localhost:5173/` verschijnt, dan is de applicatie succesvol gestart. Voer het adres in in de browser of klik hem aan vanuit de terminal om de applicatie te openen in de browser. 

## Testing
De tests in the API kunnen gedraaid worden met Maven. Ga hiervoor in de terminal naar de [backend root map](C:\Users\paula\FullstackPocNovi\backend\Poc-backend) en gebruik het commando `mvn test`.

### Testgebruikers
...........................................................................................

Bij het runnen van de API wordt ook standaarddata in de database geladen. Deze data is te vinden in [data.sql](C:\Users\paula\FullstackPocNovi\backend\Poc-backend\src\main\resources\data.sql). Hierin staat onder andere een aantal gebruikers om mee te testen. Hieronder een overzicht van hun inloggegevens en rollen:
* Henkie Jansen
  * Gebruikersnaam: henkie123
  * Wachtwoord: password123
  * Rollen: ARTIST
* Anne de Vries
  * Gebruikersnaam: anne123
  * Wachtwoord: password456
  * Rollen: ARTIST
* Claudia Bos
  * Gebruikersnaam: claudia123
  * Wachtwoord: password789
  * Rollen: ARTIST
* Paula Jansen
  * Gebruikersnaam: PaulaMarijke
  * Wachtwoord: password1234
  * Rollen: ARTIST
* Pietje
  * Gebruikersnaam: pietjelovesart
  * Wachtwoord: wachtwoord123
  * Rollen: VISITOR
* Susan
  * Gebruikersnaam: artlover
  * Wachtwoord: wachtwoord456
  * Rollen: VISITOR
* Jaap
  * Gebruikersnaam: jaapiejaap
  * Wachtwoord: wachtwoord789
  * Rollen: VISITOR



