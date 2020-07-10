cd C:\Users\imodm\eclipse-workspace\Def
call mvn clean compile assembly:single
@copy C:\Users\imodm\eclipse-workspace\Def\target\Default.jar C:\Users\imodm\Documents\Servers\TestServer\plugins /y 
@copy C:\Users\imodm\eclipse-workspace\Def\target\Default.jar C:\Users\imodm\Documents\Servers\DummyServer\plugins /y 
echo Successargsy built and copied
timeout 5