# Belisarius

## Background

This bot has been developed in an attempt to help capture possible vandalism. This includes:

 - Removing all code
 - Replacing all content with nonsense/repeated words
 - Adding solutions to their questions instead of posting an answer
 - Removing large amounts of text from their post
 - Using certain keywords or offensive language within the edit summary
 
## Why do we need the bot?

The point of the bot is to help identify bad edits and/or potential vandalism made to posts in real time so that the changes can be quickly rolled back.

## Implementation

The bot queries the [Stack Exchange API][1] once every minute to get a list of the latest posts. There is logic to check that the post has been edited and that it has been edited by the author.

The `post_id` from each post is then taken and the [Stack Exchange API][2] is again queried for the list of revisions. To limit calls we utilise the functionality of pushing multiple ids into the API and then logic is in place to ensure we are using the latest revision.

Edits can be made up of a title change, body change of a question, tag changes or changes made to the body of an answer. Currently tags are not checked. Instead the title, question body and answer body depending on what has been edited are run through filters, as is the edit summary.
 
## Accounts 

The project is running under the user [Belisarius][4] in the [SOBotics][5] room. A sample image of a report is: 
  
  [![sample report][6]][6]
  
The source code is available on [GitHub][7] and suggestions are welcome.

 [1]: https://api.stackexchange.com/docs/posts
 [2]: https://api.stackexchange.com/docs/revisions-by-ids
 [3]: https://en.wikipedia.org/wiki/Jaro%E2%80%93Winkler_distance
 [4]: https://stackoverflow.com/users/13903854/belisarius
 [5]: http://chat.stackoverflow.com/rooms/111347/sobotics
 [6]: https://user-images.githubusercontent.com/38133098/94342659-2af8d680-001b-11eb-9842-e6d0f5f4a70b.png
 [7]: https://github.com/SOBotics/Belisarius

Quick Links

- [List of commands](/commands).
- [Run the bot under another account](/run).
- [Providing feedback](/feedback).
- [Higgs](/hippo).
- [Filtering](/filters).
- [Auto comments](/comments).
