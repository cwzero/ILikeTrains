package trains;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Train {
	private String name;
	private List<Stop> stops = new ArrayList<Stop>();
	
	public Train() {
		
	}
	
	public Train(String name) {
		setName(name);
	}
	
	public void addStop(Stop stop) {
		stops.add(stop);
		sort();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Stop> getStops() {
		return stops;
	}

	public void setStops(List<Stop> stops) {
		this.stops = stops;
		sort();
	}

	public void addStop(Station station, String platform, Time time) {
		stops.add(new Stop(this, station, platform, time));
		sort();
	}

	public Stop getFirstStop() {
		return stops.get(0);
	}

	public Stop getLastStop() {
		return stops.get(stops.size());
	}

	public Station getSource() {
		return getFirstStop().getStation();
	}

	public Station getDestination() {
		return getLastStop().getStation();
	}

	public Stop getStop(Station current) {
		return getStop(current.getName());
	}

	public Station getNext(Station current) {
		return getNextStop(getStop(current)).getStation();
	}

	public Stop getNextStop(Stop current) {
		return stops.get(stops.indexOf(current) + 1);
	}

	public Stop getPreviousStop(Stop current) {
		return stops.get(stops.indexOf(current) - 1);
	}

	public Station getPrevious(Station current) {
		return getPreviousStop(getStop(current)).getStation();
	}

	public Stop getStop(String stationName) {
		for (Stop stop : stops) {
			if (stop.getStation().getName().equals(stationName)) {
				return stop;
			}
		}
		return null;
	}

	public List<Stop> getStopsAfter(String stationName) {
		return getStopsAfter(getStop(stationName));
	}

	public List<Stop> getStopsAfter(Stop current) {
		List<Stop> result = new ArrayList<Stop>();

		for (int stop = stops.indexOf(current); stop < stops.size(); stop++) {
			result.add(stops.get(stop));
		}

		return result;
	}

	public List<Stop> getStopsAfter(Station current) {
		return getStopsAfter(getStop(current));
	}

	private void sort() {
		Collections.sort(stops, new Comparator<Stop>() {
			@Override
			public int compare(Stop o1, Stop o2) {
				return o1.getTime().compareTo(o2.getTime());
			}
		});
	}
	
	public String listStops() {
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(result);
		
		out.println(getName() + ":");
		
		for (Stop stop: stops) {
			Stop next = null;
			try {
				next = stop.getNextStop();
			} catch (IndexOutOfBoundsException ex) {
				break;
			}
			String time = Timetable.format.format(stop.getTime());
			if (next.getPlatform() == null || next.getPlatform().equals("")) {
				out.println("\t" + time + " " + next.getStation().getName());
			} else {
				out.println("\t" + time + " " + next.getStation().getName() + "(" + next.getPlatform() + ")");
			}
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
