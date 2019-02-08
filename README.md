# querytocsv

## 1. Introdução ##

Este repositório contém o código fonte do componente **querytocsv**. Este componente distribuído como um arquivo (.jar) pode ser executado em linha de comando (tanto Windows quanto Linux) para realizar uma consulta a uma base de dados SQL (Oracle, SQLServer, etc) e retornar o resultado em um arquivo separado por ";" (pontos e vírgulas).

### 2. Documentação ###

### 2.1. Diagrama de Caso de Uso (Use Case Diagram) ###

#### Diagrama de Caso de Uso

![UseCaseDiagram](doc/UseCaseDiagram%20-%20Context%20-QueryToCsv.png) 


### 2.5. Requisitos ###

* n/a


## 3. Projeto ##

### 3.1. Pré-requisitos ###

* Linguagem de programação: Java
* IDE: Eclipse (recomendado Oxigen 2)
* JDK: 1.8
* Oracle JDBC 7 Driver, SQLServer JDBC Driver, PostgreSQL JDBC Driver
* JSAP Martian Software - command line parser

### 3.2. Guia para Desenvolvimento ###

* Obtenha o código fonte através de um "git clone". Utilize a branch "master" se a branch "develop" não estiver disponível.
* Faça suas alterações, commit e push na branch "develop".


### 3.3. Guia para Configuração ###

* Pre-requisitos: bibliotecas instaladas localmente `${pom.basedir}` no Maven

```cmd
mvn install:install-file -Dfile=ojdbc7-12.1.0.2.0.jar -DgroupId=com.oracle -DartifactId=ojdbc7 -Dversion=12.1.0.2.0 -Dpackaging=jar
mvn install:install-file -Dfile=JSAP-2.0a.jar -DgroupId=martiansoftware -DartifactId=JSAP -Dversion=2.0.a -Dpackaging=jar
```


### 3.4. Guia para Teste ###

* n/a


### 3.5. Guia para Implantação ###

* Obtenha o último pacote (.war) estável gerado disponível na sub-pasta './dist'.



### 3.6. Guia para Demonstração ###

* n/a


### 3.7. Guia para Execução ###

* Exemplo do uso do **querytocsv** com o banco de dados **Oracle**. 
    * Suponha um arquivo 'query-exemplo-oracle.sql' no diretório corrente da aplicação com uma query qualquer no banco de dados **Oracle**.

```bat
java -jar querytocsv.jar -f query-exemplo-oracle.sql -r query-exemplo-oracle.csv -t oracle -o jdbc:oracle:thin:USER/SECRET123@127.0.0.1:1521:XE

```


* O componente **querytocsv** funciona com argumentos de linha de comando (tanto Windows quanto Linux). Com o argumento '-h' mostra o help da a aplicação.

```bat
C:\My Git\workspace-github\querytocsv\dist>java -jar querytocsv.jar -h
QueryToCsv - v.2019.02.07 - Tool query sql database and export csv file.
  [-h|--help]
        Print help message

  -f <query-sql-select-filename>
        SQL filename with clause of SELECT ... FROM Oracle. Ex:
        C:\TEMP\select-from-oracle-filename.sql.

  [-r <output-query-resultset-filename>]
        Output query result filenameEx: C:\TEMP\result-query.txt. When not
        specified Then output to console.

  -t <database-type>
        Database type values list: [ 'oracle', 'sqlserver', postgresql'] 

  [-o <oracle-database-url>]
        Oracle JDBC database Url. Example:
        jdbc:oracle:thin:username/password@localhost:1521:sid

  [-p <postgresql-database-url>]
        PostgreSQL JDBC database Url. Example:
        jdbc:postgresql://localhost:5432/dbname?user=username&password=secret&ssl=true

  [-s <sqlserver-database-url>]
        Sqlserver JDBC database Url. Example:
        jdbc:sqlserver://localhost:1433;user=sa;password=secret123;databaseName=Northwind

Usage: QueryToCsv[-h|--help] -f <query-sql-select-filename> [-r <output-query-resultset-filename>] -t <database-type> [-o <oracle-database-url>] [-p <postgresql-database-url>] [-s <sqlserver-database-url>]

Execution aborted!
```



## Referências ##

* n/a
