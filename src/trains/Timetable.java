package trains;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Timetable {
	public static SimpleDateFormat format = new SimpleDateFormat("HH:mm");
	private List<Station> stations = new ArrayList<Station>();
	private List<Train> trains = new ArrayList<Train>();
	private List<Stop> stops = new ArrayList<Stop>();
	
	public Timetable() {
		
	}
	
	public Timetable(Train[] trains, Station[] stations, Stop[] stops) {
		this.trains.addAll(Arrays.asList(trains));
		this.stations.addAll(Arrays.asList(stations));
		this.stops.addAll(Arrays.asList(stops));
		sort();
	}
	
	public List<Station> getStations() {
		return stations;
	}
	
	public void addStation(Station station) {
		stations.add(station);
		sortStations();
	}
	
	public void addTrain(Train train) {
		trains.add(train);
		sortTrains();
	}
	
	public void addStop(Stop stop) {
		stops.add(stop);
		sortStops();
	}

	public void setStations(List<Station> stations) {
		this.stations = stations;
		sortStations();
	}

	public List<Train> getTrains() {
		return trains;
	}

	public void setTrains(List<Train> trains) {
		this.trains = trains;
		sortTrains();
	}

	public List<Stop> getStops() {
		return stops;
	}

	public void setStops(List<Stop> stops) {
		this.stops = stops;
		sortStops();
	}
	
	public void sort() {
		sortTrains();
		sortStations();
		sortStops();
	}
	
	public void sortStops() {
		Collections.sort(stops, new Comparator<Stop>() {
			@Override
			public int compare(Stop o1, Stop o2) {
				return o1.getTime().compareTo(o2.getTime());
			}
		});
		
	}
	
	public void sortStations() {
		Collections.sort(stations, new Comparator<Station>() {
			@Override
			public int compare(Station o1, Station o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
	}
	
	public void sortTrains() {
		Collections.sort(trains, new Comparator<Train>() {
			@Override
			public int compare(Train o1, Train o2) {
				int result = o1.getFirstStop().getTime().compareTo(o2.getFirstStop().getTime());
				if (result == 0) {
					result = o1.getSource().getName().compareTo(o2.getSource().getName());
				}
				return result;
			}
		});
	}
	
	public Station getStation(String stationName) {
		for (Station station: stations) {
			if (station.getName().equals(stationName))
				return station;
		}
		return null;
	}
	
	public String listConnections(String stationName) {
		return getStation(stationName).toString();
	}
	
	public String listSchedule() {
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(result);

		for (Train train: trains) {
			out.println(train.listStops());
		}
		
		out.close();
		
		try {
			return result.toString("UTF8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String listDepartures() {
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(result);
		
		for (Station station: stations) {
			out.println(station.listDepartures());
		}
		
		out.close();
		
		try {
			return result.toString("UTF8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
