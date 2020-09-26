## Filtering

In order to find if a post has been wrongly edited, titles, bodies and edit summaries are run through some filters.

### Titles are run through the following filters:

  - `BlacklistedWords`; the title matches a blacklisted regex.

### The question/answer body is run through the following filters:
 
 - `TextRemoved`; 80% or more of the body must have been removed with a [Jaro Winkler][3] score of less than 0.6
 - `BlacklistedWords`; the post matches a blacklisted regex.
 - `CodeRemoved`; the code has been removed with the latest edit (for questions only).
 - `FewUniqueCharacters`; the body is either 30+ characters long and has less than 7 unique characters or 100+ characters long and has less than 15 unique characters.
 - `RepeatedWords`; the body has been replaced with 5 or less unique words as of the latest edit
 - `VeryLongWord`; there a word bigger than 50 characters in the body.

### Edit summaries are run through the following filters:

 - `BlacklistedWords`; the edit summary matches a blacklisted regex.
 - `OffensiveWord`; the edit summary matches an offensive regex.

**Note**: In order to reduce false positives in `VeryLongWord`, `TextRemoved` and `BlacklistedWords` reasons, some HTML tags are stripped (`a`, `code`, `img`, `pre`, `blockquote`).

### Where's the list of blacklisted and offensive words?

The bot fetches the blacklisted and offensive regexes from the database. You can find the blacklisted words CSV [here](https://github.com/SOBotics/Belisarius/blob/e5e7be6425209a2bb217275c901d0790d76a1c2f/ini/BlacklistedWords.csv) and the one with the offensive words [here](https://github.com/SOBotics/Belisarius/blob/e5e7be6425209a2bb217275c901d0790d76a1c2f/ini/OffensiveWords.csv).