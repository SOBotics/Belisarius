## How can I run a local instance of Belisarius?

#### Minimum requirements

- Maven 3.6
- Java 11
- SQLite for reading the database - instructions to install for [Windows](https://www.sqlitetutorial.net/download-install-sqlite), [Linux](https://linoxide.com/linux-how-to/install-use-sqlite-linux) and [MacOS](https://flaviocopes.com/sqlite-how-to-install)

#### Compile and run

- Clone the repository and build it with Maven:

      git clone https://github.com/SOBotics/Belisarius
      cd Belisarius

- Install the swagger-java-client jar, which is used to communicate with Higgs, and build:

      mvn -B install:install-file -Dfile="./lib/swagger-java-client-1.0.0.jar" \
                                  -DgroupId="io.swagger" \
                                  -DartifactId="swagger-java-client" \
                                  -Dversion="1.0.0" \
                                  -Dpackaging="jar" \
                                  -DgeneratePom="true"
      mvn clean install

- Edit the `properties/login.properties` file with the information you like.  
- Run the bot with the following command:

      java -cp target/belisarius-1.7.1.jar:./lib/* bugs.stackoverflow.belisarius.Application

-----

If you want to changes the log location, then edit `src/main/resources/log4j.xml` and change the path in line 16.
However, you should build the project again with `mvn install`, so that the changes are applied.
