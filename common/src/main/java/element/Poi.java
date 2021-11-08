package element;

import org.locationtech.jts.geom.Point;

public class Poi {
	String id;
	double lat;
	double lng;
	double rating;
	int userRatingsTotal;
	String name;
	String type;
	String typeDetail;
	Point point;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	public int getUserRatingsTotal() {
		return userRatingsTotal;
	}

	public void setUserRatingsTotal(int userRatingsTotal) {
		this.userRatingsTotal = userRatingsTotal;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTypeDetail() {
		return typeDetail;
	}

	public void setTypeDetail(String typeDetail) {
		this.typeDetail = typeDetail;
	}

	public Point getPoint() {
		return point;
	}

	public void setPoint(Point point) {
		this.point = point;
	}

	@Override
	public String toString() {
		return "Poi{" +
				"id='" + id + '\'' +
				", lat=" + lat +
				", lng=" + lng +
				", rating=" + rating +
				", user_ratings_total=" + userRatingsTotal +
				", name='" + name + '\'' +
				", type='" + type + '\'' +
				", type_detail='" + typeDetail + '\'' +
				'}';
	}
}
