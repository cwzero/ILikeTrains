package trains;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Station {
	private String name;
	private List<Stop> stops = new ArrayList<Stop>();

	public Station() {

	}

	public Station(String name) {
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

	private void sort() {
		Collections.sort(stops, new Comparator<Stop>() {
			@Override
			public int compare(Stop first, Stop second) {
				int result = first.getTime().compareTo(second.getTime());
				if (result == 0)
					result = first.getPlatform().compareTo(second.getPlatform());
				return result;
			}
		});
	}

	public List<Train> getTrains() {
		List<Train> trains = new ArrayList<Train>();

		for (Stop stop : stops) {
			trains.add(stop.getTrain());
		}

		return trains;
	}

	public String listDepartures() {
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(result);

		out.println(getName());

		for (Stop stop : stops) {
			if (stop.getTrain().getStopsAfter(stop).size() > 1) {
				out.println(stop.getTrain().getName() + ":");
				for (Stop schedule : stop.getTrain().getStopsAfter(stop)) {
					Stop next = null;
					try {
						next = schedule.getNextStop();
					} catch (IndexOutOfBoundsException ex) {
						break;
					}
					String time = Timetable.format.format(schedule.getTime());
					out.println("\t" + time + " " + next.getStationPlatform());
				}
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
