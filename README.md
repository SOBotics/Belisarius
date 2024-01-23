# Belisarius - Detecting vandalism on Stack Overflow

![Test](https://github.com/SOBotics/Belisarius/workflows/Test/badge.svg)

## Background

This bot has been developed in an attempt to help capture possible vandalism. This includes:

 - Removing all code
 - Replacing all content with nonsense
 - Replacing all content with repeated words
 - Adding solutions to their questions instead of posting an answer
 - Removing large amounts of text from their post
 - Using certain keywords or offensive language within the edit summary
 
## Why do we need the bot?

The point of the bot is to help identify bad edits and/or potential vandalism made to posts in real time so that the changes can be quickly rolled back.

## Implementation

The bot queries the [Stack Exchange API][1] once every minute to get a list of the latest posts. There is logic to check that the post has been edited and that it has been edited by the author.

The `post_id` from each post is then taken and the [Stack Exchange API][2] is again queried for the list of revisions. To limit calls we utilise the functionality of pushing multiple ids into the API and then logic is in place to ensure we are using the latest revision.

Edits can be made up of a title change, body change of a question, tag changes or changes made to the body of an answer. Currently tags are not checked. Instead the title, question body and answer body depending on what has been edited are run through filters, as is the edit summary.

## Filtering

### Titles are run through the following filters:

  - `BlacklistedWords`; certain words are appended to titles. The bot reads a file which holds a list of keywords to watch out for within titles

### The question/answer body is run through the following filters:
 
 - `TextRemoved`; 80% or more of the body must have been removed and then it must have a [Jaro Winkler][3] score of less than 0.6
 - `BlacklistedWords`; certain words are appended to posts. The bot reads a separate file for questions and answers. Both hold a list of keywords to watch for
 - `CodeRemoved`; the bot watches for all code being removed
 - `FewUniqueCharacters`; the body must either be 30 plus characters long and have less than 7 unique characters or be 100 plus characters long and have less than 16 unique characters
 - `RepeatedWords`; this is when an edit is made were all the body is replaced with repeated words. The bot will output if 5 or less unique words are found
 - `VeryLongWord`; the bot checks the post for a word longer than 50 characters long. Code is removed before the check is done

### Edit summaries are run through the following filters:

 - `BlacklistedWords`; certain words are used within the edit summaries. The bot holds a separate file for question edit summaries and answer edit summaries. Both hold a list of keywords to watch for
 - `OffensiveWord`; the bot checks for offensive language used within the edit summary. This is done via a separate regex file
 
## Accounts 

The project is running under the user [Belisarius][4] in the [SOBotics][5] room. A more detailed presentation is at http://belisarius.sobotics.org/ including a [list of commands][6].

## Feedback:

Currently feedback is taken by replying to the chat message with either `tp` (True Positive) or `fp` (False Positive).

A sample image of a report is: 
  
  [![Sample Image][7]][7]

## How to run a local instance of Belisarius

#### Minimum requirements

- Maven 3.6
- Java 11
- SQLite for reading the database - instructions to install for [Windows](https://www.sqlitetutorial.net/download-install-sqlite), [Linux](https://linoxide.com/linux-how-to/install-use-sqlite-linux) and [MacOS](https://flaviocopes.com/sqlite-how-to-install)

#### Run

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

The source code is available on [GitHub][8] and suggestions are welcome.

 [1]: https://api.stackexchange.com/docs/posts
 [2]: https://api.stackexchange.com/docs/revisions-by-ids
 [3]: https://en.wikipedia.org/wiki/Jaro%E2%80%93Winkler_distance
 [4]: https://stackoverflow.com/users/13903854/belisarius
 [5]: http://chat.stackoverflow.com/rooms/111347/sobotics
 [6]: http://belisarius.sobotics.org/commands
 [7]: https://user-images.githubusercontent.com/38133098/94342659-2af8d680-001b-11eb-9842-e6d0f5f4a70b.png
 [8]: https://github.com/SOBotics/Belisarius
