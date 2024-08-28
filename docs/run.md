## How can I run a local instance of Belisarius?

#### Minimum requirements

- Maven 3.6
- Java 11
- SQLite for reading the database - instructions to install for [Windows](https://www.sqlitetutorial.net/download-install-sqlite), [Linux](https://linoxide.com/linux-how-to/install-use-sqlite-linux) and [MacOS](https://flaviocopes.com/sqlite-how-to-install)

#### Compile and run

- Clone the repository:

      git clone https://github.com/SOBotics/Belisarius
      cd Belisarius

- Install dependencies:

      mvn clean install

- Run

      cp properties/login.example.properties properties/login.properties

  and fill `properties/login.properties`.

- Start the bot by running:

      java -cp target/belisarius-1.8.0.jar:./lib/* bugs.stackoverflow.belisarius.Application

-----

If you want to change the location of the log file, edit `src/main/resources/log4j.xml`. The project must be rebuilt (`mvn install`), for the changes to be applied.
