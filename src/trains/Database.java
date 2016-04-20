package trains;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Database {
	private static Connection connection;

	private static String bridgeTable = "CREATE TABLE IF NOT EXISTS TRAIN_STOP (TRAIN_ID INT, STOP_ID INT, PRIMARY KEY (TRAIN_ID, STOP_ID), FOREIGN KEY (TRAIN_ID) REFERENCES TRAIN(TRAIN_ID), FOREIGN KEY (STOP_ID) REFERENCES STOP(STOP_ID))";
	private static String trainTable = "CREATE TABLE IF NOT EXISTS TRAIN (TRAIN_ID INT IDENTITY, TRAIN_NAME VARCHAR(255) NOT NULL)";
	private static String stopTable = "CREATE TABLE IF NOT EXISTS STOP (STOP_ID INT IDENTITY, STATION_ID INT NOT NULL, STOP_TIME TIME NOT NULL, STOP_PLATFORM VARCHAR(255) NOT NULL, FOREIGN KEY (STATION_ID) REFERENCES STATION(STATION_ID))";
	private static String stationTable = "CREATE TABLE IF NOT EXISTS STATION (STATION_ID INT IDENTITY, STATION_NAME VARCHAR(255) NOT NULL)";

	private static String[] proc = { "CREATE PROCEDURE is_platform_free(IN t TIME, IN platform VARCHAR(255))",
			"READS SQL DATA DYNAMIC RESULT SETS 1", "BEGIN ATOMIC",
			"DECLARE free CURSOR FOR (SELECT COUNT(*) FROM STOP WHERE STOP_PLATFORM = platform AND STOP_TIME = t);",
			"OPEN free;", "END" };
	
	private static String insertTrain = "INSERT INTO TRAIN (TRAIN_NAME) VALUES (?)";
	
	private static String readTrains = "SELECT TRAIN_NAME FROM TRAIN";

	public static void openConnection() {
		try {
			Class.forName("org.hsqldb.jdbc.JDBCDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		try {
			connection = DriverManager.getConnection("jdbc:hsqldb:mem:trains", "sa", "sa");
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void createTables() throws Exception {
		createTrainTable();
		createStationTable();
		createStopTable();
		createBridgeTable();
	}

	public static void executeStatement(String sql) throws Exception {
		try {
			PreparedStatement statement = connection.prepareStatement(sql);

			if (statement.execute()) {
				connection.commit();
			} else {
				throw statement.getWarnings();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void executeStatement(String[] sql) throws Exception {
		StringBuilder builder = new StringBuilder();
		for (String s : sql) {
			builder.append(s + "\n");
		}
		String sqlBuf = builder.toString();

		try {
			PreparedStatement statement = connection.prepareStatement(sqlBuf);
			if (statement.execute()) {
				connection.commit();
			} else {
				throw statement.getWarnings();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void createBridgeTable() throws Exception {
		executeStatement(bridgeTable);
	}

	public static void createTrainTable() throws Exception {
		executeStatement(trainTable);
	}

	public static void createStationTable() throws Exception {
		executeStatement(stationTable);
	}

	public static void createStopTable() throws Exception {
		executeStatement(stopTable);
	}

	public static void createProcedure() throws Exception {
		createTables();
		executeStatement(proc);
	}
	
	public static void insertTrain(Train train) throws Exception {
		try {
			PreparedStatement statement = connection.prepareStatement(insertTrain);
			
			statement.setString(1, train.getName());

			if (statement.executeUpdate() > 0) {
				connection.commit();
			} else {
				throw statement.getWarnings();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static Train[] readTrains() throws Exception {
		List<Train> result = new ArrayList<Train>();

		try {
			Statement statement = connection.createStatement();

			if (statement.execute(readTrains)) {
				connection.commit();
				
				ResultSet rs = statement.getResultSet();
				
				if (rs.next()) {
					while (!rs.isAfterLast()) {
						result.add(new Train(rs.getString(1)));
						rs.next();
					}
				}
			} else {
				throw statement.getWarnings();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result.toArray(new Train[result.size()]);
	}

	public static void closeConnection() {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
