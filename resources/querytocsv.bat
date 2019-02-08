java -jar ..\dist\querytocsv.jar -f .\query-oracle.sql    -r .\query-oracle.csv    -t oracle    -o jdbc:oracle:thin:oracle/tiger@10.64.113.16:1521/BDTHBOB1
java -jar ..\dist\querytocsv.jar -f .\query-sqlserver.sql -r .\query-sqlserver.csv -t sqlserver -s jdbc:sqlserver://localhost:1433;user=sa;password=secret123;databaseName=Northwind
