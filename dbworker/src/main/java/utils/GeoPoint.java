package utils;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GeoPoint extends GeoGeom {
	Long osm_id;
	Date date;
	Point point;

	Map<String, String> tags;

	public GeoPoint(Point pts) {
		point = (Point) pts.copy();
		tags = new HashMap<>();
	}

	public void addTag(String key, String value) {
		tags.put(key, value);
	}

	public Geometry getGeometry() {
		return point;
	}

	public void setPoint(Point point) {
		this.point = point;
	}

	public Map<String, String> getTags() {
		return tags;
	}

	public void setTags(Map<String, String> tags) {
		this.tags = tags;
	}

	public Date getTimestamp() {
		return date;
	}

	public void setTimestamp(Date d) {
		date = d;
	}

	public Coordinate getCoord() {
		return new Coordinate(point.getX(), point.getY());
	}

	public long getOsm_id() {
		return osm_id;
	}

	public void setOsm_id(long id) {
		osm_id = id;
	}

	public void printTag() {
		for (String key : tags.keySet()) {
			System.out.println("Node # " + osm_id + " key = " + key + ", value = " + tags.get(key));
		}
		System.out.println("---------------------------------------------");
	}

	public boolean isGreen() {
		return false;
	}

	public boolean isBrown() {
		return false;
	}
}
