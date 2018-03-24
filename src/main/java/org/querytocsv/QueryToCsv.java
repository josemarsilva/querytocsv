package org.querytocsv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;


public class QueryToCsv {

	// ////////////////////////////////////////////////////////////////////////
	// Error messages
	// ////////////////////////////////////////////////////////////////////////
	public static final String APPLICATION_NAME = new String("QueryToCsv");
	public static final String MSG_ABOUT_TITLE = new String(""+ APPLICATION_NAME + " [v0.2.00 2018-03-22] Tool query sql database and export csv file.");
	public static final String MSG_MSG_USAGE = new String( "Usage: "+ APPLICATION_NAME + "" );
	public static final String MSG_MSG_DETAILS = new String( "\n\n");
	public static final String MSG_ERROR_DATABASETYPE_IS_INVALID = new String ( "SQL database-type '%s' is invalid! Expected values: ['oracle', 'sqlserver', 'postgresql'] ");
	public static final String MSG_ERROR_ORACLE_DATABASEURL_IS_INVALID = new String ( "ORACLE database url '%s' is invalid or empty!");
	public static final String MSG_ERROR_POSTGRESQL_DATABASEURL_IS_INVALID = new String ( "POSTGRESQL database url '%s' is invalid or empty!");
	public static final String MSG_ERROR_SQLSERVER_DATABASEURL_IS_INVALID = new String ( "SQLSERVER database url '%s' is invalid or empty!");
	public static final String MSG_ERROR_INPUT_FILE_SELECTFROMORACLE_NOT_EXISTS = new String ( "SQL filename '%s' with clause of SELECT ... FROM Oracle does not exists!");
	public static final String MSG_ERROR_EXECUTION_ABORT = new String ( "Execution aborted!");
	public static final String DATABASE_TYPE_ORACLE = new String ("oracle");
	public static final String DATABASE_TYPE_POSTGRESQL = new String("postgresql");
	public static final String DATABASE_TYPE_SQLSERVER = new String("sqlserver");
	
	// ////////////////////////////////////////////////////////////////////////
	// JDBC Oracle Driver 
	// ////////////////////////////////////////////////////////////////////////
	private final static String ORACLE_JDBC_DRIVER = new String("oracle.jdbc.driver.OracleDriver");
	private final static String POSTGRESQL_JDBC_DRIVER = new String("org.postgresql.Driver");
	private final static String SQLSERVER_JDBC_DRIVER = new String("com.microsoft.sqlserver.jdbc.SQLServerDriver");

	
	// ////////////////////////////////////////////////////////////////////////
	// Class Properties 
	// ////////////////////////////////////////////////////////////////////////
	String querySqlSelectFilename = new String("");
	String outputQueryResultsetFilename = new String("");
	String databaseType = new String("");
	String oracleDatabaseUrl = new String("");
	String postgresqlDatabaseUrl = new String("");

	
	public static void main(String[] args) throws JSAPException, ClassNotFoundException, SQLException, IOException {

		//Trata os argumentos de entrada com a Classe JSAP		 
		JSAP jsap = new JSAP();
		jsap.registerParameter(new Switch("help",'h',"help","Print help message"));
		
		// Flag utilizada -f
		FlaggedOption	opt1 = new FlaggedOption("query-sql-select-filename")
		.setStringParser(JSAP.STRING_PARSER)
		.setRequired(true)
		.setShortFlag('f')
		.setLongFlag(JSAP.NO_LONGFLAG);
		opt1.setHelp("SQL filename with clause of SELECT ... FROM Oracle. Ex: C:\\TEMP\\select-from-oracle-filename.sql.");
		jsap.registerParameter(opt1);
		
		// Flag utilizada -r
		FlaggedOption	opt2 = new FlaggedOption("output-query-resultset-filename")
		.setStringParser(JSAP.STRING_PARSER)
		.setRequired(false)
		.setShortFlag('r')
		.setLongFlag(JSAP.NO_LONGFLAG);
		opt2.setHelp("Output query result filenameEx: C:\\TEMP\\result-query.txt. When not specified Then output to console.");
		jsap.registerParameter(opt2);
		
		// Flag utilizada -t
		FlaggedOption	opt3 = new FlaggedOption("database-type")
		.setStringParser(JSAP.STRING_PARSER)
		.setRequired(true)
		.setShortFlag('t')
		.setLongFlag(JSAP.NO_LONGFLAG);
		opt3.setHelp("Database type values list: [ '" + DATABASE_TYPE_ORACLE + "', '" + DATABASE_TYPE_SQLSERVER + "', " + DATABASE_TYPE_POSTGRESQL + "'] ");
		jsap.registerParameter(opt3);
		
		// Flag utilizada -o
		FlaggedOption	opt4 = new FlaggedOption("oracle-database-url")
		.setStringParser(JSAP.STRING_PARSER)
		.setRequired(false)
		.setShortFlag('o')
		.setLongFlag(JSAP.NO_LONGFLAG);
		opt4.setHelp("Oracle JDBC database Url. Example: jdbc:oracle:thin:@localhost:1521;databaseName=orcl;user=owner_user;password=secret.");
		jsap.registerParameter(opt4);
		
		// Flag utilizada -p
		FlaggedOption	opt5 = new FlaggedOption("postgresql-database-url")
		.setStringParser(JSAP.STRING_PARSER)
		.setRequired(false)
		.setShortFlag('p')
		.setLongFlag(JSAP.NO_LONGFLAG);
		opt5.setHelp("PostgreSQL JDBC database Url. Example: jdbc:postgresql://localhost:5432/dbname?user=username&password=secret&ssl=true.");
		jsap.registerParameter(opt5);

		
		// Flag utilizada -s
		FlaggedOption	opt6 = new FlaggedOption("sqlserver-database-url")
		.setStringParser(JSAP.STRING_PARSER)
		.setRequired(false)
		.setShortFlag('s')
		.setLongFlag(JSAP.NO_LONGFLAG);
		opt1.setHelp("Sqlserver JDBC database Url. Example: jdbc:sqlserver://localhost:1433;user=sa;password=secret123;databaseName=Northwind");
		jsap.registerParameter(opt6);

		
		// Config command line arguments
		JSAPResult configJsapArgs = jsap.parse(args);
		
		// Config is Help?
		boolean isPrintHelp = configJsapArgs.getBoolean("help");
		if (isPrintHelp) {
			System.err.println(MSG_ABOUT_TITLE);
			System.err.println(jsap.getHelp());
			System.err.println(MSG_MSG_USAGE + jsap.getUsage() + MSG_MSG_DETAILS);
			System.err.println(MSG_ERROR_EXECUTION_ABORT);
			System.exit(1);
		}
		
		// Config is *not* Success?
		if (!configJsapArgs.success()) {
			System.err.println();
			System.err.println(MSG_MSG_USAGE + jsap.getUsage()+ MSG_MSG_DETAILS);
			System.err.println();
			// print out specific error messages describing the problems with the command line
			for (@SuppressWarnings("rawtypes")
			java.util.Iterator errs = configJsapArgs.getErrorMessageIterator(); errs.hasNext();) {
				System.err.println("Error: " + errs.next());
			}
			System.err.println(MSG_ERROR_EXECUTION_ABORT);
			System.exit(1);
		}
		
		// Get command line arguments
		String querySqlSelectFilename = configJsapArgs.getString("query-sql-select-filename");
		String outputQueryResultsetFilename = configJsapArgs.getString("output-query-resultset-filename");
		String databaseType = configJsapArgs.getString("database-type");
		String oracleDatabaseUrl = configJsapArgs.getString("oracle-database-url");
		String postgresqlDatabaseUrl = configJsapArgs.getString("postgresql-database-url");
		String sqlserverDatabaseUrl = configJsapArgs.getString("sqlserver-database-url");
    	System.out.println("querySqlSelectFilename = "+ querySqlSelectFilename);
    	System.out.println("outputQueryResultsetFilename = "+ outputQueryResultsetFilename);
    	System.out.println("databaseType = "+ databaseType);
    	System.out.println("oracleDatabaseUrl = "+ oracleDatabaseUrl);
    	System.out.println("postgresqlDatabaseUrl = "+ postgresqlDatabaseUrl);
    	System.out.println("sqlserverDatabaseUrl = "+ postgresqlDatabaseUrl);
    	
		// Check list before continue ...
    	checkFile (querySqlSelectFilename );
    	checkDatabaseType(databaseType, oracleDatabaseUrl, postgresqlDatabaseUrl, sqlserverDatabaseUrl);
		
		
		// Connect database Oracle
    	System.out.println("Conectando "+ databaseType + " ...");
    	Connection connJDBC = doConnectDatabase( getDatabaseUrl(databaseType, oracleDatabaseUrl, postgresqlDatabaseUrl, sqlserverDatabaseUrl), 
    			getJDBCDriver(databaseType) );

		// Load all file into database
		doIterateInsertAsSelect(connJDBC, querySqlSelectFilename, outputQueryResultsetFilename);

		// Disconnect database Oracle
		doDisconnect( connJDBC );
		
	}
	
	/*
	 * Recebe uma URL de conexão do JDBC e seu respectivo Driver.
	 * Abre a conexão e devolve ela.
	 */
	private static Connection doConnectDatabase(String databaseUrl, String jdbcDriver) throws ClassNotFoundException, SQLException {
		
		// Class.forName
		Class.forName(jdbcDriver);
		//
		// DriverManager.getConnection ...
		//		
		Connection conn = DriverManager.getConnection(databaseUrl);
		conn.setAutoCommit(false);
		
		
		//
		// Return
		//
		System.out.println ("Conexao criada com a URL "+databaseUrl+ "."); 
		return conn;
		
	}

	
	
	/*
	 * Recebe uma conexão de banco de dados e a encerra.
	 */
	private static void doDisconnect(Connection conn) throws SQLException {

		//
		// Force last commit
		//
		conn.commit();
		
		//
		// DriverManager.getConnection ...
		//
		conn.close();
		
		
	}
	
	
	
	/*
	 * Verificar se o caminho informado se refere a um arquivo. 
	 * Caso negativo, encerra a execução devolvendo um erro para o batch.
	 * 
	 */
	private static void checkFile (String absolutePath) {
		File file = new File (absolutePath);
		if (!file.isFile () ) {
			System.err.println(MSG_ERROR_INPUT_FILE_SELECTFROMORACLE_NOT_EXISTS.replace("%s", absolutePath));
			System.exit(1);			
		}
	}

	
	
	/*
	 * Verificar se database type and url's are valid
	 * 
	 */
	private static void checkDatabaseType (String databaseType, String oracleDatabaseUrl, String postgresqlDatabaseUrl, String sqlserverDatabaseUrl) {
		if (!databaseType.equals(DATABASE_TYPE_ORACLE) 
				&& !databaseType.equals(DATABASE_TYPE_POSTGRESQL)
				&& !databaseType.equals(DATABASE_TYPE_SQLSERVER) ) {
			System.err.println(MSG_ERROR_DATABASETYPE_IS_INVALID.replace("%s", databaseType));
			System.exit(1);			
		} else if (databaseType.equals(DATABASE_TYPE_ORACLE) && oracleDatabaseUrl==null) {
			System.err.println(MSG_ERROR_ORACLE_DATABASEURL_IS_INVALID.replace("%s", ""));
			System.exit(1);			
		} else if (databaseType.equals(DATABASE_TYPE_POSTGRESQL) && postgresqlDatabaseUrl==null) {
			System.err.println(MSG_ERROR_POSTGRESQL_DATABASEURL_IS_INVALID.replace("%s", ""));
			System.exit(1);			
		} else if (databaseType.equals(DATABASE_TYPE_SQLSERVER) && sqlserverDatabaseUrl==null) {
			System.err.println(MSG_ERROR_SQLSERVER_DATABASEURL_IS_INVALID.replace("%s", ""));
			System.exit(1);			
		}
	}
	

	/*
	 */
	private static String getJDBCDriver(String databaseType) {
		if (databaseType.equals(DATABASE_TYPE_ORACLE)) {
			return ORACLE_JDBC_DRIVER;
		} else if (databaseType.equals(DATABASE_TYPE_POSTGRESQL)) {
			return POSTGRESQL_JDBC_DRIVER;
		} else if (databaseType.equals(DATABASE_TYPE_SQLSERVER)) {
			return SQLSERVER_JDBC_DRIVER;
		} else {
			System.err.println(MSG_ERROR_DATABASETYPE_IS_INVALID.replace("%s", databaseType));
			System.exit(1);
			return "";			
		}
	}

	
	/*
	 */
	private static String getDatabaseUrl(String databaseType, String oracleDatabaseUrl, String postgresqlDatabaseUrl, String sqlserverDatabaseUrl) {
		if (databaseType.equals(DATABASE_TYPE_ORACLE)) {
			return oracleDatabaseUrl;
		} else if (databaseType.equals(DATABASE_TYPE_POSTGRESQL)) {
			return postgresqlDatabaseUrl;
		} else if (databaseType.equals(DATABASE_TYPE_SQLSERVER)) {
			return sqlserverDatabaseUrl;
		} else {
			System.err.println(MSG_ERROR_DATABASETYPE_IS_INVALID.replace("%s", databaseType));
			System.exit(1);
			return "";			
		}
	}

	
	/*
	 * Método responsável por carregar dados da tabela Oracle.
	 * 
	 */
	private static void doIterateInsertAsSelect(Connection conn, 
			String querySqlSelectFilename,
			String outputQueryResultsetFilename	) throws IOException {
		
		// Executa o select sobre a base Oracle
		ResultSet result = doSelectFromOracle (conn, querySqlSelectFilename);
		
		// Output query result set to BufferWriter in a FileWriter ...
		PrintWriter outputFile = null;
		if (outputQueryResultsetFilename!=null) {
			if (!outputQueryResultsetFilename.equals("")) {
				outputFile = new PrintWriter(outputQueryResultsetFilename);
			}
		}
		
		//Escreve o arquivo de saida
		doWriteOutputTo (outputFile, result) ;
		
	}
	
	
	
	/*
	 * O método faz a escrita de todas as linhas obtidas na consulta ao Oracle.
	 * 
	 */
	private static void doWriteOutputTo (PrintWriter outputFile, ResultSet selectResult) throws IOException {
		
		try {
			
			// Get ResultSetMetaData ...
			ResultSetMetaData metaData = selectResult.getMetaData ();
			
			// Create BufferWrite
			BufferedWriter bufferedWriter = null;
			
			long rowWritten = 0;
			//Faz a iteração dentro do result set obtido do oracle
			while (selectResult.next()) {
				
				metaData = selectResult.getMetaData ();
				
				// First row "CSV Headding" ?
				if (rowWritten==0) {
					// Write CSV Heading: col#1;col#2;..;col#n
					String csvHeading = new String("");
					for (int i = 1; i <= metaData.getColumnCount (); i++) {
						if (!csvHeading.equals("")) {
							csvHeading = csvHeading + ";" + metaData.getColumnLabel(i).replaceAll(";", "_");
						} else {
							csvHeading = metaData.getColumnLabel(i).replaceAll(";", "_");
						}
					}
					if (outputFile!=null) {
						outputFile.write(csvHeading + "\n");
					}
				}
				
				// Fetch each row
				String csvRow = new String("");
				for (int i = 1; i <= metaData.getColumnCount (); i++) {
					
					int type = getGenericType (metaData.getColumnType (i));
					
					// Write CSV Row: col#1;col#2;..;col#n
					if (!csvRow.equals("")) {
						csvRow = csvRow + ";";
					}
					
					
					if (type == Types.NUMERIC) {
						csvRow = csvRow + selectResult.getInt(i);
					}
					if (type == Types.DATE) {
						csvRow = csvRow + selectResult.getDate(i);
					}
					if (type == Types.VARCHAR) {
						csvRow = csvRow + selectResult.getString (i);
					}
					if (type == Types.DOUBLE) {
						csvRow = csvRow + selectResult.getDouble (i);
					}
										
				}
				
				// Write csvRow ...
				if (outputFile!=null) {
					outputFile.write(csvRow + "\n");
				}
				
				rowWritten++;
				if ( 
						(rowWritten < 10)
						|| (rowWritten >= 10 && rowWritten < 100 && rowWritten % 10 == 0)
						|| (rowWritten >= 100 && rowWritten < 1000 && rowWritten % 100 == 0)
						|| (rowWritten >= 1000 && rowWritten < 10000 && rowWritten % 1000 == 0)
						|| (rowWritten >= 10000 && rowWritten < 100000 && rowWritten % 5000 == 0)
						|| (rowWritten >= 100000 && rowWritten % 10000 == 0)
					) {
					System.out.print ("\rFetching row (" + rowWritten + ") ..." );
				}
			}
			System.out.println("\n" + rowWritten + " row(s) fetched successfully!");

		}
		catch (SQLException e) {
			e.printStackTrace ();
			System.out.println (MSG_ERROR_EXECUTION_ABORT);
			System.exit(1);
		} finally {
			if (outputFile!=null) {
				outputFile.flush();
				outputFile.close();
			}
		}
	}
	
	
	
	/*
	 * Recebe um arquivo contendo a query que representa o select que será realizado 
	 * no banco de dados Oracle.
	 * Após carregar a query, o método executa a consulta e retorna um ResultSet com as 
	 * informações obtidas. 
	 */
	private static ResultSet doSelectFromOracle (Connection connOracle, String selectFromOracleSqlFilename) {
		
		ResultSet result = null;
		String query = getQuery (selectFromOracleSqlFilename);
		try {
			Statement selectStm = connOracle.createStatement ();			
			System.out.println ("Executando SELECT FROM Oracle ...");
			result = selectStm.executeQuery (query); 
		}
		catch (SQLException e ) {
			e.printStackTrace ();
			System.out.println (MSG_ERROR_EXECUTION_ABORT);
			System.exit(1);
		}
		
		return result;
	} 
	
	
	
	/*
	 * Método para fazer a conversão de um datatype de uma coluna obtida no STAR 
	 * para o datatype usado no PostgreSQL correspondente.
	 *  
	 */
	private static int getGenericType (int type) {
				 
		switch (type) {
		case  Types.BIGINT:
			return Types.NUMERIC;
		case  Types.BINARY:
			return Types.NUMERIC;
		case  Types.BIT:
			return Types.NUMERIC;
		case  Types.INTEGER:
			return Types.NUMERIC;
		case  Types.ROWID:
			return Types.NUMERIC;
		case  Types.SMALLINT:
			return Types.NUMERIC;
		case  Types.TINYINT:
			return Types.NUMERIC;
		case  Types.DECIMAL:
			return Types.DOUBLE;
		case  Types.DOUBLE:
			return Types.DOUBLE;
		case  Types.FLOAT:
			return Types.DOUBLE;
		case  Types.NUMERIC:
			return Types.DOUBLE;
		case  Types.DATE:
			return Types.DATE;	
		case  Types.DATALINK:
			return Types.DATE;	
		case  Types.TIMESTAMP:
			return Types.DATE;
		case  Types.TIME:
			System.out.println (MSG_ERROR_EXECUTION_ABORT);
			System.exit(1);
		case  Types.CHAR:
			return Types.VARCHAR;	
		case Types.LONGNVARCHAR:
			return Types.VARCHAR;	
		case Types.LONGVARCHAR:
			return Types.VARCHAR;	
		case Types.NCHAR:
			return Types.VARCHAR;	
		case Types.NULL:
			return Types.VARCHAR;
		case Types.NVARCHAR:
			return Types.VARCHAR;
		case Types.OTHER:
			return Types.VARCHAR;
		case Types.VARBINARY:
			return Types.VARCHAR;
		case Types.VARCHAR:
			return Types.VARCHAR;
		}		
		
		System.out.println ("Datatype no reconhecido.");
		System.exit (1);
		return 0;
	}
	
	
	
	/*
	 * Recebe o caminho de um arquivo que contém uma query SQL, carrega em uma String
	 * e retorna a query que deve ser executada.
	 *  
	 */
	private static String getQuery (String absolutePath) {
		File file = new File (absolutePath);
		FileReader fileReader;
		BufferedReader buffReader;
		
		String query=null;

		if (file.isFile () ) {
			try {
				fileReader = new FileReader (file);
				buffReader = new BufferedReader (fileReader);
				String line=null;
				
				//Le linhas do arquivo para formar a querys
				while ( (line=buffReader.readLine()) != null  ) {
					if (query == null) query = line;
					else query = query + " " + line;					
				}
				query = query + " ";
				buffReader.close ();
				return query;
			}
			catch (IOException e) {
				e.printStackTrace ();
				System.out.println (MSG_ERROR_EXECUTION_ABORT);
				System.exit(1);
			}
		}
		return null;
	}

}
