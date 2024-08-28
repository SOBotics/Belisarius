## Filtering

In order to find if a post has been wrongly edited, titles, bodies and edit summaries are run through some filters.

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

### Where's the list of blacklisted and offensive words?

The bot fetches the blacklisted and offensive regexes from the database. You can find the [blacklisted words CSV here](https://github.com/SOBotics/Belisarius/blob/master/ini/BlacklistedWords.csv) and the [offensive words CSV here](https://github.com/SOBotics/Belisarius/blob/master/ini/OffensiveWords.csv).