Polly is an open source IRC bot available under GNU General Public License 
(GPLv3). See the GPLv3.txt in this directory for detailed licensing information.

Polly is a highly customizable irc bot with many built in features. For example
polly has a parser which supports several types (like numbers, dates, booleans,
etc.), operators and functions. Additionally every user may assign own variables
or functions.

After the parser evaluated a user input, polly looks for a matching command and
passes the parsed arguments to execute it. One command can has multiple 
signatures with different types.

Your plugins are not restricted to use the default parser. You can get the raw
irc messages that are sent to polly using pollys event system by adding
for example a MessageListener.

Polly also has a build in user management and database backend which can be 
accessed by all plugins. And there also exists an auto login feature which
currently only works on irc servers that support the `NICKSERV` command.

Please visit the Wiki page to see how to build and how to setup polly.

Polly currently uses several third partie sources and libraries for which I
would like to thank the authors. 

    * Polly uses a fork of the PircBot-PPF 
      (http://sourceforge.net/projects/pircbot-ppf), which itself is a fork of 
      the famous PircBot by jibble.org.

    * Hani Suleiman who is the author of JBot, another PircBot based IRC bot.
      I found the sources and decided to adapt his Classloader implementation 
      for pollys plugin system.
      URL: http://java.net/projects/jbot/

It would be nice letting me now if you are using any sources of polly by sending
an e-mail to polly@skuzzle.de