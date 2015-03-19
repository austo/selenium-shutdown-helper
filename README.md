####hub:####
```
java -cp selenium-server-standalone-2.45.0.jar:/Users/austin_moran/devProjects/java/selenium-shutdown-helper/build/libs/SeleniumShutdownHelper-1.0.jar org.openqa.grid.selenium.GridLauncher -role hub
```

####node:####
```
java -cp selenium-server-standalone-2.45.0.jar:/Users/austin_moran/devProjects/java/selenium-shutdown-helper/build/libs/SeleniumShutdownHelper-1.0.jar org.openqa.grid.selenium.GridLauncher -role node -hub http://localhost:4444/grid/register -servlets com.moraustin.NodeShutdownServlet -nodeConfig /Users/austin_moran/devProjects/java/selenium-shutdown-helper/config/config.json
```