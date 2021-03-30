package type;


import helper.ColorHelper;

import java.util.HashMap;
import java.util.Map;

public class OsmTypeDetail {
	public static final Map<String, Road> roadMap;
	public static final Map<String, int[]> roadColor;

	static {
		roadMap = new HashMap<>();
		for (Road road : Road.values()) {
			String[] str = road.getString();
			for (String s : str) {
				roadMap.put(s, road);
			}
		}

		int[][] c = ColorHelper.createGradientHue(Road.values().length, ColorHelper.RED, ColorHelper.BLUE);
		roadColor = new HashMap<>();
		for (int i = 0; i < Road.values().length; ++i) {
			roadColor.put(Road.values()[i].name(), c[i]);
		}
	}

	public enum Road {
		R1("motorway", "motorway_link"),
		R2("trunk", "primary", "secondary", "trunk_link", "primary_link", "secondary_link"),
		S1("tertiary", "tertiary_link"),
		S2("unclassified", "residential", "pedestrian", "living_street", "road"),
		S3("footway", "service", "cycleway", "contruction");

		private String[] string;

		Road(String... strs) {
			this.string = strs;
		}

		public String[] getString() {
			return string;
		}
	}
}
