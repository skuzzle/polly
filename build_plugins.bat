copy polly.sdk\dist\de.skuzzle.polly.sdk.jar polly.core\lib\de.skuzzle.polly.sdk.jar
copy polly.sdk\dist\de.skuzzle.polly.sdk.jar polly.reminds\lib\de.skuzzle.polly.sdk.jar
copy polly.sdk\dist\de.skuzzle.polly.sdk.jar polly.tv\lib\de.skuzzle.polly.sdk.jar
copy polly.sdk\dist\de.skuzzle.polly.sdk.jar polly.logging\lib\de.skuzzle.polly.sdk.jar

call ant -f polly.core/build.xml

call ant -f polly.reminds/build.xml

call ant -f polly.tv/build.xml

call ant -f polly.logging/build.xml

copy polly.core\dist\polly.core.jar polly\cfg\plugins\polly.core.jar
copy polly.core\dist\polly.core.properties polly\cfg\plugins\polly.core.properties

copy polly.reminds\dist\polly.reminds.jar polly\cfg\plugins\polly.reminds.jar
copy polly.reminds\dist\polly.reminds.properties polly\cfg\plugins\polly.reminds.properties

copy polly.tv\dist\polly.tv.jar polly\cfg\plugins\polly.tv.jar
copy polly.tv\dist\polly.tv.properties polly\cfg\plugins\polly.tv.properties

copy polly.logging\dist\polly.logging.jar polly\cfg\plugins\polly.logging.jar
copy polly.logging\dist\polly.logging.properties polly\cfg\plugins\polly.logging.properties