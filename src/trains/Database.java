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

	private static String createTrain = "INSERT INTO TRAIN (TRAIN_NAME) VALUES (?)";
	private static String readTrains = "SELECT TRAIN_NAME FROM TRAIN";
	private static String updateTrain = "UPDATE TRAIN SET TRAIN_NAME = ? WHERE TRAIN_NAME = ?";
	private static String deleteTrain = "DELETE FROM TRAIN WHERE TRAIN_NAME = ?";

	public static void openConnection() throws ClassNotFoundException, SQLException {
		Class.forName("org.hsqldb.jdbc.JDBCDriver");

		connection = DriverManager.getConnection("jdbc:hsqldb:mem:trains", "sa", "sa");
		connection.setAutoCommit(false);
	}

	public static void createTables() throws SQLException {
		createTrainTable();
		createStationTable();
		createStopTable();
		createBridgeTable();
	}

	public static void executeStatement(String sql) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(sql);

		if (statement.execute()) {
			connection.commit();
		} else {
			throw statement.getWarnings();
		}
	}

	public static void executeStatement(String[] sql) throws SQLException {
		StringBuilder builder = new StringBuilder();
		for (String s : sql) {
			builder.append(s + "\n");
		}
		String sqlBuf = builder.toString();

		PreparedStatement statement = connection.prepareStatement(sqlBuf);
		if (statement.execute()) {
			connection.commit();
		} else {
			throw statement.getWarnings();
		}
	}

	public static void createBridgeTable() throws SQLException {
		executeStatement(bridgeTable);
	}

	public static void createTrainTable() throws SQLException {
		executeStatement(trainTable);
	}

	public static void createStationTable() throws SQLException {
		executeStatement(stationTable);
	}

	public static void createStopTable() throws SQLException {
		executeStatement(stopTable);
	}

	public static void createProcedure() throws SQLException {
		createTables();
		executeStatement(proc);
	}

	public static void createTrain(Train train) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(createTrain);

		statement.setString(1, train.getName());

		if (statement.executeUpdate() > 0) {
			connection.commit();
		} else {
			throw statement.getWarnings();
		}
	}

	public static Train[] readTrains() throws SQLException {
		List<Train> result = new ArrayList<Train>();

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
		return result.toArray(new Train[result.size()]);
	}

	public static void updateTrain(Train oldTrain, Train newTrain) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(updateTrain);

		statement.setString(1, oldTrain.getName());
		statement.setString(2, newTrain.getName());

		if (statement.executeUpdate() > 0) {
			connection.commit();
		} else {
			throw statement.getWarnings();
		}
	}

	public static void deleteTrain(Train train) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(deleteTrain);

		statement.setString(1, train.getName());

		if (statement.executeUpdate() > 0) {
			connection.commit();
		} else {
			throw statement.getWarnings();
		}
	}

	public static void closeConnection() throws SQLException {
		connection.close();
	}
}
