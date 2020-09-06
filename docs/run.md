## How can I run a local instance of Belisarius?

#### Minimum requirements

- Maven 3.6
- Java 8
- SQLite for reading the database - instructions to install for [Windows](https://www.sqlitetutorial.net/download-install-sqlite), [Linux](https://linoxide.com/linux-how-to/install-use-sqlite-linux) and [MacOS](https://flaviocopes.com/sqlite-how-to-install)

#### Compile and run

Clone the repository and build it with Maven:

    git clone https://github.com/SOBotics/Belisarius
    cd Belisarius
    mvn clean install

Edit the `properties/login.properties` file with the information you like.  
Run `sqlite3 database/belisarius.db` and edit the room and Higgs information.  
Schema for `Room`:

- `RoomId`, integer: the room id the bot should run.
- `Site`, text: the chat server (`stackoverflow` or `stackexchange`)
- `OutputMessage`, integer: `1` (true) if the bot should send chat messages, `0` if it shouldn't.

Schema for `Higgs`:

- `BotId`, integer: Higgs dashboard id.
- `SecretKey`, text: the secret of the Higgs dashboard. You can get/set it from https://higgs.sobotics.org/admin/bot/<bot_id>
- `Url`, text: Higgs API URL, set it to `https://api.higgs.sobotics.org`.

Now run the bot with the following command:

    java -cp target/Belisarius-1.0-SNAPSHOT.jar:./lib/* bugs.stackoverflow.belisarius.Application

-----

If you want to changes the log location, then edit `src/main/resources/log4j.xml` and change the path in line 16.
However, you should build the project again with `mvn install`, so that the changes are applied.