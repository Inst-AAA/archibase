package type;

import java.util.HashMap;
import java.util.Map;

public class GmapsTypeDetail {
	public static final Map<String, Types> map;

	static {
		map = new HashMap<>();
		map.put("bakery", Types.food);
		map.put("restaurant", Types.food);
		map.put("cafe", Types.food);
		map.put("bar", Types.food);
		map.put("meal_delivery", Types.food);
		map.put("meal_takeaway", Types.food);
		map.put("food", Types.food);

		map.put("lodging", Types.hotel);

		map.put("book_store", Types.retail);
		map.put("convenience_store", Types.retail);
		map.put("shoe_store", Types.retail);
		map.put("hardware_store", Types.retail);
		map.put("florist", Types.retail);
		map.put("clothing_store", Types.retail);
		map.put("bicycle_store", Types.retail);
		map.put("grocery_or_supermarket", Types.retail);
		map.put("shopping_mall", Types.retail);
		map.put("department_store", Types.retail);
		map.put("supermarket", Types.retail);
		map.put("car_dealer", Types.retail);
		map.put("furniture_store", Types.retail);
		map.put("electronics_store", Types.retail);
		map.put("home_goods_store", Types.retail);
		map.put("pet_store", Types.retail);
		map.put("liquor_store", Types.retail);
		map.put("jewelry_store", Types.retail);
		map.put("store", Types.retail);

		map.put("night_club", Types.entertain);
		map.put("movie_theater", Types.entertain);
		map.put("casino", Types.entertain);

		map.put("spa", Types.beauty);
		map.put("hair_care", Types.beauty);
		map.put("beauty_salon", Types.beauty);

		map.put("locksmith", Types.service);
		map.put("accounting", Types.service);
		map.put("movie_rental", Types.service);
		map.put("painter", Types.service);
		map.put("travel_agency", Types.service);
		map.put("electrician", Types.service);
		map.put("real_estate_agency", Types.service);
		map.put("bank", Types.service);
		map.put("car_wash", Types.service);
		map.put("gas_station", Types.service);
		map.put("roofing_contractor", Types.service);
		map.put("car_repair", Types.service);
		map.put("atm", Types.service);
		map.put("car_rental", Types.service);
		map.put("storage", Types.service);
		map.put("lawyer", Types.service);
		map.put("insurance_agency", Types.service);
		map.put("laundry", Types.service);
		map.put("plumber", Types.service);
		map.put("moving_company", Types.service);
		map.put("rv_park", Types.service);

		map.put("gym", Types.sport);
		map.put("bowling_alley", Types.sport);
		map.put("stadium", Types.sport);

		map.put("zoo", Types.civic);
		map.put("aquarium", Types.civic);
		map.put("art_gallery", Types.civic);
		map.put("tourist_attraction", Types.civic);
		map.put("natural_feature", Types.civic);
		map.put("museum", Types.civic);
		map.put("park", Types.civic);
		map.put("amusement_park", Types.civic);
		map.put("library", Types.civic);

		map.put("church", Types.religious);
		map.put("synagogue", Types.religious);
		map.put("cemetery", Types.religious);
		map.put("place_of_worship", Types.religious);
		map.put("funeral_home", Types.religious);
		map.put("hindu_temple", Types.religious);
		map.put("mosque", Types.religious);

		map.put("school", Types.education);
		map.put("secondary_school", Types.education);
		map.put("university", Types.education);
		map.put("primary_school", Types.education);

		map.put("doctor", Types.health);
		map.put("pharmacy", Types.health);
		map.put("dentist", Types.health);
		map.put("drugstore", Types.health);
		map.put("health", Types.health);
		map.put("hospital", Types.health);
		map.put("physiotherapist", Types.health);
		map.put("veterinary_care", Types.health);

		map.put("police", Types.government);
		map.put("post_office", Types.government);
		map.put("embassy", Types.government);
		map.put("courthouse", Types.government);
		map.put("local_government_office", Types.government);
		map.put("fire_station", Types.government);
		map.put("city_hall", Types.government);

		map.put("airport", Types.transport);
		map.put("bus_station", Types.transport);
		map.put("parking", Types.transport);
		map.put("subway_station", Types.transport);
		map.put("taxi_stand", Types.transport);
		map.put("transit_station", Types.transport);
		map.put("light_rail_station", Types.transport);
		map.put("train_station", Types.transport);
	}

	public enum Types {
		food,
		hotel,
		retail,
		entertain,
		beauty,
		service,
		sport,
		civic,
		religious,
		education,
		health,
		government,
		transport
	}

}
