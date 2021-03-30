package gmaps;

import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;
import db.Info;
import db.Tables;
import db.Utils;
import helper.GeoMath;
import type.GmapsTypeDetail;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Download file and enter into database
 * Usage:
 * run with args pathname latlng_bottomleft latlng_upright
 * OSMDownload "./data/request_result.log" 48.110 16.152 48.324 16.625
 */
public class GmapsRequest {

	public static void main(String[] args) {

		for (PlaceType type : PlaceType.values()) {
			if (GmapsTypeDetail.map.get(type.toUrlValue()) == null) {
				System.err.println(type.toUrlValue() + " null");
			} else
				System.out.println(type.toUrlValue() + " " + GmapsTypeDetail.map.get(type.toUrlValue()));

		}

		GmapsRequest gr = new GmapsRequest();

		String filename = args[0];

		double[] bl = new double[]{Double.parseDouble(args[1]), Double.parseDouble(args[2])};
		double[] tr = new double[] {Double.parseDouble(args[3]), Double.parseDouble(args[4])};

		try {
			gr.gridSearch(bl, tr, 70, filename);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * collect data by grid, results are recorded into file ./data/number_of_result.log
	 * @param bl bottom left [lat, lng] coordinates
	 * @param tr top right [lat, lng] coordinates
	 * @param radius request radius
	 * @throws IOException file writer exception
	 */
	public void gridSearch(double[] bl, double[] tr, int radius, String filename) throws IOException {
		GeoMath geoMath = new GeoMath(bl, tr);
		double[] min = geoMath.latLngToXY(bl);
		double[] max = geoMath.latLngToXY(tr);

		GeoApiContext context = new GeoApiContext.Builder().apiKey(Info.API_KEY).build();
		Utils db = new Utils();

		File file = new File(filename);
		FileWriter out = new FileWriter(file);

		double step = Math.floor(radius * Math.sqrt(2.0));

		int total =  ((int)((max[0]-min[0])/step) *(int)((max[1]-min[1])/step));
		System.out.println("Total Request: " + total);
		for (double x = min[0] + step / 2; x < max[0]; x += step) {
			for (double y = min[1] + step / 2; y < max[1]; y += step) {
				double[] latlng = geoMath.xyToLatLng(x, y);
				LatLng position = new LatLng(latlng[0], latlng[1]);
				int tot = searchNearBy(db, context, position, radius);
				System.out.println("( " + x + " " + y + " )");
				System.out.println("-------------\n" + tot + " at position " + position + "\n--------------");
				out.write("( " + x + " " + y + " )");
				out.write("-------------\n" + tot + " at position " + position + "\n--------------");
			}
			out.write("\r\n");
		}
		out.close();
	}

	/**
	 *
	 * @param db
	 * @param context
	 * @param position
	 * @param radius
	 * @return
	 */
	public int searchNearBy(Utils db, GeoApiContext context, LatLng position, int radius) {
		System.out.println("https://maps.googleapis.com/maps/api/place/nearbysearch/json?" + "location=" + position.lat
				+ "," + position.lng + "&radius=" + radius + "&key=" + Info.API_KEY);
		int total = 0;
		try {
			PlacesSearchResponse response = PlacesApi.nearbySearchQuery(context, position)
					.language("en").radius(radius).await();

			for (int i = 0; i < response.results.length; ++i) {
				resultToDb(db, response.results[i]);
			}
			total = response.results.length;

			total += searchNextPage(db, context, response.nextPageToken);
		} catch (ApiException | InterruptedException | IOException e) {
			e.printStackTrace();
		}

		return total;
	}

	public int searchNextPage(Utils db, GeoApiContext context, String nextPageToken) {
		int total = 0;
		try {
			while (nextPageToken != null) {
				TimeUnit.SECONDS.sleep(10);
				System.out.println("https://maps.googleapis.com/maps/api/place/nearbysearch/json?" + "pagetoken="
						+ nextPageToken + "&key=" + Info.API_KEY);
				PlacesSearchResponse response = PlacesApi.nearbySearchNextPage(context, nextPageToken).await();

				for (int i = 0; i < response.results.length; ++i) {
					resultToDb(db, response.results[i]);
				}

				total += response.results.length;
				nextPageToken = response.nextPageToken;
			}
		} catch (InterruptedException | ApiException | IOException e) {
			e.printStackTrace();
		}
		return total;
	}


	/**
	 * store request result to database
	 * @param db dbUtils
	 * @param result place search results
	 */
	private void resultToDb(Utils db, PlacesSearchResult result) {
		LatLng position = result.geometry.location;

		String name = result.name;

		if (name.contains("'"))
			name = name.replace("'", "''");

		if (name.length() > 255)
			name = name.substring(0, 255);

		String[] values = new String[Tables.functions.length];

		values[0] = "'" +result.placeId+ "'";
		values[1] = ""+position.lat;
		values[2] = ""+position.lng;
		values[3] = ""+result.rating;
		values[4] = ""+result.userRatingsTotal;
		values[5] = "'" +name+ "'";

		GmapsTypeDetail.Types type = null;
		String typeDetail = null;
		for (int i = 0; i < result.types.length; ++i) {
			typeDetail = result.types[i];
			type = GmapsTypeDetail.map.get(typeDetail);
			if (type != null)
				break;
		}

		values[6] = "'" +type+ "'";
		values[7] = "'" +typeDetail+ "'";

		db.insertFullData("functions", 1,values);
	}

}
