# Welcome to Mondrian
Mondrian is an Online Analytical Processing (OLAP) server that enables business users to analyze large quantities of data in real-time.

## Sub modules
  * **mondrian** - the core mondrian java library
  * **workbench** - A desktop GUI for generating Mondrian schemas

### Building
#### Requirements
* JDK 11 or higher
* Maven 3.3 or higher
* Tomcat 9

#### Quick Start
1. 由于本项目采用JDK 11以上，先设置Java临时环境变量：
set "JAVA_HOME=D:\Software\jdk-11.0.24"
set "PATH=%JAVA_HOME%\bin;%PATH%"
2. 在根目录执行maven打包指令
```
mvn clean package
```
3. 可在mondrian/target目录找到emondrian.war包，部署至tomcat的webapps目录即可
