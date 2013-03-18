#Polly - Java 1.7 IRC Framework

Polly is an open source IRC bot available under GNU General Public License 
(GPLv3). See the GPLv3.txt in this directory for detailed licensing information.

##Abstract
Polly is a highly customizable irc bot with many built in features. It is 
authored and maintained by Simon Taddiken. Its main purpose was to serve some
irc functionality related to the german browser game "Revorix" (www.revorix.de).
Over the years of development, polly soon grew to be a pretty cool Java IRC
framework.

Pollys main clue is the parser which relies on techniques that are used in
compilers too. That allows polly to deal with different types like numbers,
strings, dates, etc. Input matching the polly command syntax is parsed, 
context-checked, evaluated and then passed to the actual command implementation 
which now can access the input provided by the user.

Commands that can be executed this way, can be provided by plugins which will
be loaded when polly starts. Creating plugins is very easy, as they can use
a pretty huge SDK framework which grants access to many further polly features.

##Key features

* context-free syntax which provides many (many!) syntactical features to
  use with any command. To mention just a few: the parser supports 
  list-types, operator overloads, custom variables and functions based on
  different namespaces, basic lambda calculus and many more.
* Extensible command system to provide irc commands using dynamically loaded
  plugins.
* Each irc command can have multiple signatures based on the types supported
  by the parser.
* Simple JPA based persistence layer.
* Swing-style event listeners that let plugins react on irc- and other 
  events independent from the above mentioned irc commands.
* Built-in user management which uses the before mentioned persistence 
  layer.
* Dynamic role-based user rights management which controlls access to 
  irc commands and other polly features.
* Web-interface which can be easily extended by plugins.
* A few default plugins that provide, for example, irc administration for
  polly, logging of irc messages and timed or event based remind messages.
    
There are a lot of other features of polly that can be used by plugins. Please
refer to the SDK documentation (http://www.polly.skuzzle.de/sdk/doc/) for latest
information (*)

## Getting started
Your first steps using polly are very simple, as the repository comes with 
everything you could ever need to build polly. One key feature is, that the 
build script even generates eclipse projects for you, so that you are ready to
start after a few seconds.

Polly's build system is based on Apache Ant which comes shipped with checking 
out the polly repository. There is no need to install additional software. 

Steps to get working with polly:
* check out the repository
* goto `projects` directory
* run `build.bat`
* run `projects.bat`
* in Eclipse, select `File -> Import -> Existing Projects into Workspace`,
  click `browse`, goto `projects` directory, hit `enter`. Now, select all 
  listed polly projects to import.

All eclipse projects now reference all the dependent polly projects in the 
same workspace, so that code changes in one sub project are automatically 
reflected to all other projects. However, this does not apply to polly plugins. 
If you modify a plugin, you would still have to run the `build` script in the 
plugin's project directory, because plugins are loaded dynamically from their 
jar files when launching polly.

You may now want to modify some settings in `de.skuzzle.polly.core/cfg`. When 
done, you can launch polly by running the main class 
`de.skuzzle.polly.core.PollyBootstrapper`.
  
## Third party sources
Polly currently uses several third party sources and libraries for which I
would like to thank the authors. 

* Polly uses a fork of the PircBot-PPF 
  (http://sourceforge.net/projects/pircbot-ppf), which itself is a fork of 
  the famous PircBot by jibble.org.

* Hani Suleiman who is the author of JBot, another PircBot based IRC bot.
  I found the sources and decided to adapt his Classloader implementation 
  for pollys plugin system.
  URL: http://java.net/projects/jbot/

* There is no need to say how awesome the open source projects of the Apache
  foundation are. Polly uses Apache Velocity to create HTML pages from templates 
  and log4j for debug logging.
  URL: http://www.apache.org/
  
* By default, polly uses the lightweight SQL database HSQLDB 
  (http://hsqldb.org/) in conjunction with the eclipse link JPA 
  implementation (http://www.eclipse.org/eclipselink/).
      
It would be nice letting me now if you are using any sources of polly by sending
an e-mail to polly@skuzzle.de


(*) As polly is under constant development and is more like a private fun 
project, there currently exists no release that is considered "stable". 
Nevertheless, polly is used every day by a rather small group of people on the 
euIRC making irc life a bit more comfortable for everyone :).

Visit polly in #tal-der-ahnungslosen on irc.euirc.net or follow @PollyProject 
on Twitter!