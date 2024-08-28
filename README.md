# Belisarius - Detecting vandalism on Stack Overflow

![Test](https://github.com/SOBotics/Belisarius/workflows/Test/badge.svg) &nbsp;
[![License: MIT](https://img.shields.io/badge/License-MIT-brightgreen.svg)](https://opensource.org/licenses/MIT)

## Background

This bot has been developed in an attempt to help capture possible vandalism by identifying edits that:

 - remove all code
 - replace content with nonsense or repeated words
 - include solutions to questions
 - remove large amounts of text from the post
 - use certain keywords or offensive language within the edit summary
 
## Why do we need the bot?

The point of the bot is to help identify bad edits and/or potential vandalism made to posts in real time so that the changes can be quickly rolled back.

## Implementation

The bot queries the [Stack Exchange API][1] every minute to fetch a list of the most recently edited posts. There is logic to check that the post has been edited and that it has been edited by the author.

The `post_id` from each post is then extracted and the [Stack Exchange API][2] is again queried for a list of revisions. To reduce API calls multiple ids are sent at once, and then logic is in place to ensure we are using the latest revision.

Edits can be made up of a title change, body change of a question, tag changes or changes made to the body of an answer. Currently tags are not checked. Instead the title, question body and answer body depending on what has been edited are run through filters, as is the edit summary.

## Filtering

### Titles are run through the following filters:

  - `BlacklistedWords`; certain words are appended to titles. The bot reads a file which holds a list of keywords to watch out for within titles

### The question/answer body is run through the following filters:
 
 - `TextRemoved`; the bot checks if 80% or more of the body has been removed and whether the [Jaro Winkler][3] score of the diff is less than 0.6.
 - `BlacklistedWords`; certain words are appended to posts. The bot reads a separate file for questions and answers. Both hold a list of keywords to watch for
 - `CodeRemoved`; the bot checks if the latest edit removed all code from the post.
 - `FewUniqueCharacters`; the bot checks if the post contains few unique characters &mdash; this rule is similar to [SmokeDetector's "Few unique characters" one](https://metasmoke.erwaysoftware.com/reason/23).
 - `RepeatedWords`; the bot checks whether there are 5 or less unique words in the post.
 - `VeryLongWord`; the bot checks the post for a word longer than 50 characters long. Code blocks are stripped before the check is performed.

### Edit summaries are run through the following filters:

 - `BlacklistedWords`; certain words are used within the edit summaries. The bot holds a separate file for question edit summaries and answer edit summaries. Both hold a list of keywords to watch for.
 - `OffensiveWord`; the bot checks for offensive language used within the edit summary. This is done via a separate regex file.
 
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

 [1]: https://api.stackexchange.com/docs/posts
 [2]: https://api.stackexchange.com/docs/revisions-by-ids
 [3]: https://en.wikipedia.org/wiki/Jaro%E2%80%93Winkler_distance
 [4]: https://stackoverflow.com/users/13903854/belisarius
 [5]: http://chat.stackoverflow.com/rooms/111347/sobotics
 [6]: http://belisarius.sobotics.org/commands
 [7]: https://user-images.githubusercontent.com/38133098/94342659-2af8d680-001b-11eb-9842-e6d0f5f4a70b.png
