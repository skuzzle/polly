Polly is a highly customizable irc bot with many build in features. For example
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
would like to thank the authors. Namely that are:

    * Paul Mutton for providing the PircBot API which polly uses to connect
      to IRC servers and for the JMegaHal implementation which is used by
      a polly plugin to generate random sentences when polly gets highlighted.
      URL: http://www.jibble.org
      
    * Hani Suleiman who is the author of JBot, another PircBot based IRC bot.
      I found the sources and decided to adapt his Classloader implementation 
      for pollys plugin system.
      URL: http://java.net/projects/jbot/
      
Please note that polly is open source but currently not subject to any
license. You may use and modify all sources that can be found here, as long
as you leave a reference to the originals in the code you use.

Additionally, it would be nice letting me know if you are using any parts of 
polly.