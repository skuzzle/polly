call build_parser

call ant -f polly.sdk/build.xml

copy polly.sdk\dist\de.skuzzle.polly.sdk.jar polly.core\lib\de.skuzzle.polly.sdk.jar
copy polly.sdk\dist\de.skuzzle.polly.sdk.jar polly\lib\de.skuzzle.polly.sdk.jar

call ant -f polly.core/build.xml
copy polly.core\dist\polly.core.jar polly\cfg\plugins\polly.core.jar

call ant -f polly/build.xml release
call ant -f polly.sdk/doc.xml

copy polly\cfg\META-INF\persistence.xml polly\src\META-INF\persistence.xml