package element;

import wblut.geom.WB_Point;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Poi {
	Date date;
	WB_Point position;
	Map<String, String> tags;

	public Poi(WB_Point pts) {
		position = pts.copy();
		tags = new HashMap<>();
	}

	public void addTag(String key, String value) {
		tags.put(key, value);
	}

	public WB_Point getPosition() {
		return position;
	}

	public void setPosition(WB_Point position) {
		this.position = position;
	}

	public Map<String, String> getTags() {
		return tags;
	}

	public void setTags(Map<String, String> tags) {
		this.tags = tags;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date d) {
		date = d;
	}

	public void printTag() {
		for (String key : tags.keySet()) {
			System.out.println("POI key = " + key + ", value = " + tags.get(key));
		}
		System.out.println("---------------------------------------------");
	}


}
