package trains;

import java.sql.Time;
import java.text.ParseException;

public class Stop {
	private Train train;
	private Station station;
	private String platform;
	private Time time;
	
	public Stop() {
		
	}
	
	public Stop(Train train, Station station, String time) {
		try {
			this.time = new Time(Timetable.format.parse(time).getTime());
		} catch (ParseException ex) {
			ex.printStackTrace();
		}
		this.train = train;
		this.station = station;
		train.addStop(this);
		station.addStop(this);
	}
	
	public Stop(Train train, Station station, Time time) {
		this.train = train;
		this.station = station;
		this.time = time;
	}
	
	
	public Stop(Train train, Station station, String platform, String time) {
		this(train, station, time);
		this.platform = platform;
	}
	
	public Stop(Train train, Station station, String platform, Time time) {
		this(train, station, time);
		this.platform = platform;
	}

	public Train getTrain() {
		return train;
	}

	public void setTrain(Train train) {
		this.train = train;
	}

	public Station getStation() {
		return station;
	}

	public void setStation(Station station) {
		this.station = station;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public Time getTime() {
		return time;
	}

	public void setTime(Time time) {
		this.time = time;
	}
	
	public boolean isLastStop() {
		try {
			getNextStop();
			return true;
		} catch (IndexOutOfBoundsException ex) {
			return false;
		}
	}
	
	public Stop getNextStop() {
		return train.getNextStop(this);
	}
	
	public String getStationPlatform() {
		if (getPlatform() == null || getPlatform().trim().equals("")) {
			return station.getName();
		} else {
			return station.getName() + "(" + platform + ")";
		}
	}
}
