# mybatis-generator-core-fix

mybatis generator 生成数据库备注为注释 修整部分代码的样式

详细信息请参考链接：```https://blog.csdn.net/liudongdong0909/article/details/52427967```

1. master 主要是原生样式的代码生成，不包含任何的第三方。

2. branches-swagger 主要是整合了 swagger来生成带有swagger 注释的model， 用于在线文档。

3. branches-swagger-tkmapper 主要整合了 swagger 和 tk 通用mapper ，以及hibernate-validator的数据校验

4. 相比于源码修改的内容有：  
    4.1 pom.xml文件  
    4.2 不修改默认的类DefaultCommentGenerator，而是新建一个自己的类 DG2CommentGenerator。按照自己的需求重写一些方法，主要有生成mapper接口的备注信息，生成model对象的注释。  
    4.3 修改生成model对象类的注释  
        4.3.1 在FullyQualifiedTable中添加remark字段，用于保存数据库表的备注信息。  
        4.3.2 修改DatabaseIntrospector的calculateIntrospectedTables方法，添加一段获取数据库备注的代码。  
        4.3.3 修改DG2CommentGenerator中的 addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable) 方法。  
        4.3.4 具体样式可以按自己的爱好设置。  
    4.4 修改model对象中字段的注释  
        4.4.1 在IntrospectedColumn中添加remarks属性,用于保存数据库对应表中字段的备注信息，需要setter/getter。  
        4.4.2 修改DG2CommentGenerator中的 addFieldComment(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) 方法。  
    4.5 修改model对象中setter/getter的注释  
        4.5.1 修改DG2CommentGenerator中的 addGetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) 方法。  
        4.5.2 修改DG2CommentGenerator中的 addSetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) 方法。  
    4.6 修改生成mapper中的注释    
        4.6.1 修改DG2CommentGenerator中的 addGeneralMethodComment 方法。  
    4.7 删除mapper.xml中的注释  
        4.7.1 修改DG2CommentGenerator中的 addComment 方法。  
    4.8 添加toString方法  
        4.8.1 JavaBeansUtil中添加 getToStringMethod 方法，提供调用。  
        4.8.2 修改BaseRecordGenerator中的 getCompilationUnits 方法。  
        4.8.3 修改SimpleModelGenerator中的 getCompilationUnits 方法。  
    4.9 修改数字长度小于等于10位数的整数为Integer  
        4.9.1 修改 JavaTypeResolverDefaultImpl中的 calculateJavaType(IntrospectedColumn introspectedColumn) 方法。  

5. maven工程的打包命令：```mvn clean install -Dmaven.test.skip=true```

6. 如何在工程中引用  
6.1 需要修改generatorConfig.xml。  
将
```
<commentGenerator>
    <!-- 是否去除自动生成的注释 true：是 ： false:否 -->
    <property name="suppressAllComments" value="true" />
</commentGenerator>
```
替换为
```
<commentGenerator type="org.mybatis.generator.internal.DG2CommentGenerator">
    <property name="javaFileEncoding" value="UTF-8"/>
    <!-- 是否去除自动生成的注释 true：是 ： false:否 -->
    <property name="suppressAllComments" value="false" />
    <property name="suppressDate" value="true" />
</commentGenerator>
```
6.2 需要修改pom.xml  
将插件中的依赖
```
<dependencies>
    <dependency>
        <groupId>org.mybatis.generator</groupId>
        <artifactId>mybatis-generator-core</artifactId>
        <version>1.3.2</version>
    </dependency>
</dependencies>
```
修改为
```
<dependencies>
    <dependency>
        <groupId>org.mybatis.generator</groupId>
        <artifactId>mybatis-generator-core</artifactId>
        <!--使用自己的版本-->
        <version>1.3.2-fix</version>
    </dependency>
</dependencies>
```
至此，大功告成。

---
# mybatis-generator的使用参照：

Intellij IDEA 2016学习系列之（二）mybatis-generator自动生成:http://blog.csdn.net/liudongdong0909/article/details/51534735


Intellij IDEA 2016学习系列之（三）修改mybatis-generator源码生成中文:http://blog.csdn.net/liudongdong0909/article/details/52427967

## 在自己工程中的resources 文件下新建一个：generatorConfig.xml


```
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE generatorConfiguration
  PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
  "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd">

<generatorConfiguration>

	<!-- 本地数据库连接jar -->
	<classPathEntry location="D:/mysql-connector-java-5.1.20-bin.jar" />

	<context id="testTables" targetRuntime="MyBatis3">

		<commentGenerator type="org.mybatis.generator.internal.DG2CommentGenerator">
			<property name="javaFileEncoding" value="UTF-8"/>
			<!-- 是否去除自动生成的注释 true：是 ： false:否 -->
			<!--建议一定要保留suppressAllComments属性(使用默认值false)，
            一定要取消(设为true)时间戳suppressDate，避免重复提交SVN。-->
			<property name="suppressAllComments" value="false" />
			<property name="suppressDate" value="true" />
		</commentGenerator>

		<!--数据库连接的信息：驱动类、连接地址、用户名、密码 -->
		<jdbcConnection driverClass="com.mysql.jdbc.Driver"
			connectionURL="jdbc:mysql://localhost:3306/ecps" userId="root" password="root">
		</jdbcConnection>

		<!-- 默认false，把JDBC DECIMAL 和 NUMERIC 类型解析为 Integer，为 true时把JDBC DECIMAL 
			和 NUMERIC 类型解析为java.math.BigDecimal -->
		<javaTypeResolver>
			<property name="forceBigDecimals" value="false" />
		</javaTypeResolver>

		<!-- targetProject:生成PO类的位置 -->
		<javaModelGenerator targetPackage="com.ecps.pojo"
			targetProject="../ecps-manager-pojo/src/main/java">
			<!-- enableSubPackages:是否让schema作为包的后缀 -->
			<property name="enableSubPackages" value="false" />
			<!-- 从数据库返回的值被清理前后的空格 -->
			<property name="trimStrings" value="true" />
		</javaModelGenerator>

		<!-- targetProject:mapper映射文件生成的位置 -->
		<sqlMapGenerator targetPackage="com.ecps.mapper"
			targetProject="../ecps-manager-mapper/src/main/java">
			<!-- enableSubPackages:是否让schema作为包的后缀 -->
			<property name="enableSubPackages" value="false" />
		</sqlMapGenerator>

		<!-- targetPackage：mapper接口生成的位置 -->
		<javaClientGenerator type="XMLMAPPER" targetPackage="com.ecps.mapper"
			 targetProject="../ecps-manager-mapper/src/main/java">
			<!-- enableSubPackages:是否让schema作为包的后缀 -->
			<property name="enableSubPackages" value="false" />
		</javaClientGenerator>

		<!-- 指定数据库表 -->
		<table schema="" tableName="tb_user"></table>

	</context>
</generatorConfiguration>

```
## IDEA 中在自己工程的pom.xml中添加：
```
 <build>
        <finalName>${project.artifactId}</finalName>
            <plugins>
                <plugin>
                    <groupId>org.mybatis.generator</groupId>
                    <artifactId>mybatis-generator-maven-plugin</artifactId>
                    <version>1.3.2</version>
                    <configuration>
                        <!--配置文件的位置-->
                        <configurationFile>src/main/resources/generatorConfig.xml</configurationFile>
                        <verbose>true</verbose>
                        <overwrite>true</overwrite>
                    </configuration>
                    <executions>
                        <execution>
                            <id>Generate MyBatis Artifacts</id>
                            <goals>
                                <goal>generate</goal>
                            </goals>
                        </execution>
                    </executions>
                    <dependencies>
                        <dependency>
                            <groupId>org.mybatis.generator</groupId>
                            <artifactId>mybatis-generator-core</artifactId>
                            <!-- 版本是本工程checkout 之后 clean install 之后的版本号-->
                            <version>1.3.2-fix</version>
                        </dependency>
                    </dependencies>
                </plugin>
            </plugins>
    </build>
```
## Eclipse 中在install本工程后，直接时候插件生成即可，不需要配置pom.xml
