package trains;

import java.sql.SQLException;

public class Main {
	public static void main(String[] args) {
		try {
			Database.openConnection();
			Database.createProcedure();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Train[] trains = new Train[14];
		Station[] stations = new Station[5];
		Stop[] stops = new Stop[70];

		trains[0] = new Train("N 329");
		trains[1] = new Train("S 330");
		trains[2] = new Train("N 331");
		trains[3] = new Train("S 332");
		trains[4] = new Train("N 333");
		trains[5] = new Train("S 334");
		trains[6] = new Train("N 335");
		trains[7] = new Train("S 336");
		trains[8] = new Train("N 337");
		trains[9] = new Train("S 338");
		trains[10] = new Train("N 339");
		trains[11] = new Train("S 340");
		trains[12] = new Train("N 341");
		trains[13] = new Train("S 342");

		try {
			for (Train t : trains) {
				Database.createTrain(t);
			}

			for (Train tr : Database.readTrains()) {
				System.out.println(tr.getName());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		stations[0] = new Station("Chicago IL - Union Station");
		stations[1] = new Station("Glennview IL");
		stations[2] = new Station("Sturtevant WI");
		stations[3] = new Station("Milwaukee WI - Airport Rail Station");
		stations[4] = new Station("Milwaukee WI - DT");

		String[][] times = { { "06:10", "06:32", "07:10", "07:24", "07:39" },
				{ "06:15", "06:26", "06:43", "07:25", "07:57" }, { "08:25", "08:47", "09:25", "09:39", "09:54" },
				{ "08:05", "08:15", "08:28", "09:06", "09:34" }, { "10:20", "10:42", "11:20", "11:34", "11:49" },
				{ "11:00", "11:10", "11:23", "12:01", "12:29" }, { "13:05", "13:27", "14:05", "14:19", "14:34" },
				{ "13:00", "13:10", "13:23", "14:01", "14:29" }, { "15:15", "15:37", "16:15", "16:29", "16:44" },
				{ "15:00", "15:10", "15:23", "16:01", "16:29" }, { "17:08", "17:32", "18:14", "18:28", "18:45" },
				{ "17:45", "17:55", "18:08", "18:46", "19:14" }, { "20:05", "20:27", "21:05", "21:19", "21:34" },
				{ "19:35", "19:45", "19:58", "20:36", "21:04" } };

		int stop = 0;
		for (int train = 0; train < trains.length; train++) {
			for (int station = 0; station < stations.length; station++) {
				if (train % 2 == 0) {
					stops[stop] = new Stop(trains[train], stations[station], times[train][station]);
				} else {
					stops[stop] = new Stop(trains[train], stations[station], times[train][4 - station]);
				}
				stop++;
			}
		}

		Timetable timetable = new Timetable(trains, stations, stops);
		
		Train oldTrain = new Train("N 343");
		Train newTrain = new Train("S 344");
		try {
			Database.createTrain(oldTrain);
			Database.updateTrain(oldTrain, newTrain);
			Database.deleteTrain(newTrain);
			Database.closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
