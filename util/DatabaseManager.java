package main.util;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.App;

public class DatabaseManager {
	private Connection conn;
	
	private String user;
	private String password;
	
	private String database;
	private String table;
	
	public DatabaseManager() throws ClassNotFoundException {
		Class.forName("org.postgresql.Driver");
	}
	
	public void setUser(String user, String password) {
		this.user = user;
		this.password = password;
	}
	
	/* ----- Content functions ----- */
	
	/** Select all persons 
	 * @throws DatabaseException */
	public List<Person> selectAll() throws DatabaseException {
		if (this.table == null) throw new DatabaseException("No table selected");
		List<Person> result = null;
		result = handleResultSet(
				executeCallableStatementWithResult("{ ? = call select_all(?) }", this.table)
				);		
		return result;
	}
	
	public List<Person> selectBySurname(String surname) throws DatabaseException {
		if (this.table == null) throw new DatabaseException("No table selected");
		List<Person> result = null;
		result = handleResultSet(
				executeCallableStatementWithResult("{ ? = call select_person(?, ?) }", this.table, surname)
				);		
		return result;
	}
	
	public void insert(Person person) throws DatabaseException {
		handlePerson("{ call insert_person(?, ?, ?, ?, ?, ?, ?, ?)}", person, -1);
	}
	
	public void update(Person person, int oldId) throws DatabaseException {
		handlePerson("{ call update_person(?, ?, ?, ?, ?, ?, ?, ?, ?)}", person, oldId);
	}
	
	public void deleteBySurname(String surname) throws DatabaseException {
		executeCallableStatement("{ call delete_person(?, ?) }", table, surname);
	}
	
	/* ----- Table functions ----- */
	
	public void createTable(String tableName) throws DatabaseException {
		executeCallableStatement("{ call create_table(?) }", tableName);
	}
	
	public void deleteTable(String tableName) throws DatabaseException {
		executeCallableStatement("{ call delete_table(?) }", tableName);
	}
	
	public void truncateTable(String tableName) throws DatabaseException {
		executeCallableStatement("{ call truncate_table(?) }", tableName);
	}
	
	public void openTable(String tableName) throws DatabaseException {
		this.table = tableName;
//		return selectAll();
	}
	
	/* ----- Database functions ----- */
	
	public void createDatabase(String dbName) throws DatabaseException {
		createDB(dbName);
		openDatabase(dbName);
		configueDatabase(dbName);
	}
	private void createDB(String dbName) throws DatabaseException {
		executDatabaseStatement("CREATE DATABASE \"%s\" OWNER admin_user TEMPLATE template_db", dbName);
	}
	private void configueDatabase(String dbName) throws DatabaseException {
		executeCallableStatement("{ call configue_db(?) }", dbName);
	}
	
	public void deleteDatabase(String dbName) throws DatabaseException {
		if(dbName.equals(this.database)) {
			connect();
		}
		deleteDB(dbName);
	}
	private void deleteDB(String dbName) throws DatabaseException {
		openDatabase("default_db");
		executDatabaseStatement("DROP DATABASE IF EXISTS \"%s\"", dbName);
	}
		
	public void openDatabase(String dbName) throws DatabaseException {
		closeConnetion();
		connect(dbName);
	}
	
	/* ----- Roles functions ----- */
	
	public void createUser(String userName, String userPassword, boolean isAdmin) throws DatabaseException {
		String query = null;
		if(isAdmin) {
			query = "{ call create_admin(?, ?) }";
		} else {
			query = "{ call create_user(?, ?) }";
		}
		executeCallableStatement(query, userName, userPassword);
	}
	
	public void deleteUser(String userName) throws DatabaseException {
		executeCallableStatement("{ call delete_role(?) }", userName);
	}
	
	/* ----- Manage connection ----- */
	
	/** 
	 * First connection to default_db
	 * @throws DatabaseException
	 */
	public void connect() throws DatabaseException {
		connect("default_db");
	}
	private void connect(String database) throws DatabaseException {
		this.database = database;
		try {
			if(conn != null) conn.close();
			conn = DriverManager.getConnection(Config.DB_URL + this.database, user, password);
		} catch (SQLException e) {
			if(App.DEBUG) e.printStackTrace();
			throw new DatabaseException("Connection error");
		} 
	}

	public void closeConnetion() throws DatabaseException {
		try {
			if(conn != null) conn.close();
		} catch (SQLException e) {
			if(App.DEBUG) e.printStackTrace();
			throw new DatabaseException("Cannot close current connetion");
		}
	}
	/* ----- Meta info -----*/
	public List<String> getDbList() throws DatabaseException {
		return getMeta("get_database_list()");
	}
	
	public List<String> getTablesList() throws DatabaseException {
		return getMeta("get_tables_list()");
	}
	
	public List<String> getUsersList() throws DatabaseException {
		return getMeta("get_users_list()");
	}	
	
	private List<String> getMeta(String function) throws DatabaseException {
		List<String> meta = new ArrayList<>();
		try(PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + function);
				ResultSet rs = ps.executeQuery()){
			while(rs.next()) {
				meta.add(rs.getString(1));
			}
		} catch (SQLException e) {
			if(App.DEBUG) e.printStackTrace();
			throw new DatabaseException(e.getMessage());
		}
		return meta;
	}
	
	/* ----- helper functions ----- */
	
	/** 
	 * Execute CallableStatemen with a return value 
	 * @throws DatabaseException
	 * @return ResultSet(refcursor) 
	 */
	private ResultSet executeCallableStatementWithResult(String query, String... args) throws DatabaseException {
		ResultSet rs = null;
		try (CallableStatement cs = conn.prepareCall(query)){
			cs.registerOutParameter(1, Types.OTHER);
			int idx = 2;
			for(String param : args) {
				cs.setString(idx++, param);
			}
			conn.setAutoCommit(false);
			cs.execute();
			rs = (ResultSet) cs.getObject(1);
		} catch (SQLException e) {
			if(App.DEBUG) e.printStackTrace();
			throw new DatabaseException("Procidure Exception");
		} 
		return rs;
	}
	
	
	/** 
	 * Execute CallableStatemen without a return value 
	 * @throws DatabaseException 
	 */
	private void executeCallableStatement(String query, String... args) throws DatabaseException{
		try (CallableStatement cs = conn.prepareCall(query)){
			int idx = 1;
			for(String param : args) {
				cs.setString(idx++, param);
			}
			cs.execute();
		} catch (SQLException e) {
			if(App.DEBUG) e.printStackTrace();
			throw new DatabaseException("Procidure Exception");
		} 
	}
	
	/** 
	 * Execute PreparedStatemen without a return value 
	 * @throws DatabaseException 
	 */
	@SuppressWarnings("unused")
	private void executePreparedStatement(String query, String... args) throws DatabaseException {
		try (PreparedStatement ps = conn.prepareCall(query)){
			int idx = 1;
			for(String param : args) {
				ps.setObject(idx++, param);
			}
			ps.executeUpdate();
		} catch (SQLException e) {
			if(App.DEBUG) e.printStackTrace();
			throw new DatabaseException("Query Exception");
		} 
	}
	
	private void handlePerson(String query, Person person, int oldId) throws DatabaseException {
		try (CallableStatement cs = conn.prepareCall(query)){
			cs.setString(1, table);
			cs.setInt(2, person.getNumber());
			cs.setString(3, person.getSurname());
			cs.setString(4, person.getName());
			cs.setString(5, person.getCity());
			cs.setString(6, person.setStringDate());
			cs.setString(7, person.getRank());
			cs.setBoolean(8, person.getAdmission());
			if(oldId > 0) cs.setInt(9, oldId);
			cs.execute();
		} catch (SQLException e) {
			if(App.DEBUG) e.printStackTrace();
			throw new DatabaseException("Procidure Exception");
		} 
	}
	
	private List<Person> handleResultSet(ResultSet rs) throws DatabaseException {
		List<Person> result = new ArrayList<>();
		try{
			while(rs.next()) {
				Person p = new PersonBuilder(rs.getInt(1), rs.getString(2))
						.setName(rs.getString(3))
						.setCity(rs.getString(4))
						.setDate(rs.getDate(5).toString())
						.setRank(rs.getString(6))
						.setAddmision(rs.getBoolean(7))
						.build();
				result.add(p);
			}
		} catch (SQLException e) {
			if(App.DEBUG) e.printStackTrace(); 
		} finally{
			try {
				if(rs != null) rs.close();
				conn.commit();
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				if(App.DEBUG) e.printStackTrace();
			}
		}
		
		return result;
	}
	
	/* ----- Create/Drop Database ----- */
	
	/** 
	 * Manipulate database
	 * @throws DatabaseException 
	 */
	private void executDatabaseStatement(String query, String dbName) throws DatabaseException {
		if(hasInjection(dbName)) throw new DatabaseException("SQL INJECTION DETECTED!");
		String q = String.format(query, dbName);
		try (Statement s = conn.createStatement()){
			s.executeUpdate(q);
		} catch (SQLException e) {
			if(App.DEBUG) e.printStackTrace();
			throw new DatabaseException("Query Exception");
		} 
	}
	
	private boolean hasInjection(String target) {
		Pattern p = Pattern.compile("'|;|=|(--)");
		Matcher m = p.matcher(target);
		return m.find();
	}

	public void reset() {
		this.database = "default_db";
		this.table = null;
		this.user = null;
		this.password = null;
		
	}
}
