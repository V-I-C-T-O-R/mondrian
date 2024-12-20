## Welcome to Mondrian
Mondrian is an Online Analytical Processing (OLAP) server that enables business users to analyze large quantities of data in real-time.  Mondrian implements the Olap4J API.
### Building
#### Requirements
* JDK 11 or higher
* Maven 3.3 or higher
* Tomcat 9 

#### Quick Start
在根目录执行maven打包指令
```
mvn clean package
```
可在mondrian/target目录找到emondrian.war包，部署至tomcat的webapps目录即可
