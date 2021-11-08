package helper;


public class GeoMath {

    private static final double EQUATORIAL_RADIUS = 6378137.0;
    private static final double POLAR_RADIUS = 6356752.3;
    public static final double EPS = Double.longBitsToDouble(971L << 52);

    private double MAPRATIO = 0.;
    private double EARTH_RADIUS = (EQUATORIAL_RADIUS + POLAR_RADIUS) / 2.;
    private double[] CENTER = {0., 0.};

    public GeoMath(double[] bl, double[] tr) {
        double lat = (bl[0] + tr[0]) / 2;
        double lng = (bl[1] + tr[1]) / 2;
        setCenter(lat, lng);
        setRatio(bl, tr);
    }


    /**
     * @param lat
     * @param lng
     * @Function:GeoMath
     * @Description:TODO
     */
    public GeoMath(double lat, double lng) {
        setCenter(lat, lng);
    }

    /**
     * @param latLng
     * @Function:GeoMath
     * @Description:TODO
     */
    public GeoMath(double[] latLng) {
        setCenter(latLng[0], latLng[1]);
    }


    public double getLat() {
        return CENTER[0];
    }

    public double getLng() {
        return CENTER[1];
    }

    public double getRatio() {
        return MAPRATIO;
    }

    /**
     * @param lat
     * @Function: calcEarthRadius
     * @Description: calculate Geocentric radius at geodetic latitude
     * https://en.wikipedia.org/wiki/Earth_radius
     * @return: void
     */
    public void calcEarthRadius(double lat) {
        lat = Math.toRadians(lat);

        double a = EQUATORIAL_RADIUS;
        double b = POLAR_RADIUS;

        double ta = a * Math.cos(lat);
        double tb = b * Math.sin(lat);

        EARTH_RADIUS = Math.sqrt((ta * a * ta * a + tb * b * tb * b) / (ta * ta + tb * tb));
    }

    public double calcRatio(double[] bl, double[] tr) {
        double d = haversineDistance(bl[0], bl[1], bl[0], tr[1]);

//		System.out.println(d);

        double y = Math.toRadians(tr[0] - bl[0]);

//		System.out.println(y * EARTH_RADIUS);


        double yp = Math.log(Math.tan(Math.PI / 4 + Math.toRadians(tr[0]) / 2))
                - Math.log(Math.tan(Math.PI / 4 + Math.toRadians(bl[0]) / 2));

//		System.out.println(y + " " + yp);
        return y / yp;
    }

    public void setRatio(double[] bl, double[] tr) {
        MAPRATIO = calcRatio(bl, tr);
    }

    public void setRatio(double ratio) {
        MAPRATIO = ratio;
    }

    /**
     * Caculate the haversine distance between two points
     *
     * @param lat1
     * @param lon1
     * @param lat2
     * @param lon2
     * @return distance between the two point in meters
     */
    public double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        calcEarthRadius((lat1 + lat2) / 2);

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }

    /**
     * @param lat
     * @param lng
     * @Function: setCenter
     * @Description: TODO
     * @return: void
     */
    public void setCenter(double lat, double lng) {
        CENTER[0] = lat;
        CENTER[1] = lng;
        calcEarthRadius(lat);
    }

    public double[] latLngToXY(double[] latLng) {
        return latLngToXY(latLng[0], latLng[1]);
    }

    /**
     * @param lat
     * @param lng
     * @return
     * @Function: latLngToXY
     * @Description: TODO
     * @return: double[]
     */
    public double[] latLngToXY(double lat, double lng) {
        double d = haversineDistance(lat, lng, CENTER[0], CENTER[1]);
        double y = Math.toRadians(lat - CENTER[0]);
        double xp = Math.toRadians(lng - CENTER[1]);

        if (MAPRATIO == 0) {
            calcRatio(new double[]{lat, lng}, CENTER);
        }

        double x = MAPRATIO * xp;

        return new double[]{EARTH_RADIUS * x, EARTH_RADIUS * y};
    }


    /**
     * @param x
     * @param y
     * @return
     * @Function: xyToLatLng
     * @Description: TODO
     * @return: double[]
     */
    public double[] xyToLatLng(double x, double y) {
        double lat = Math.toDegrees(y / EARTH_RADIUS + EPS) + CENTER[0];
        x /= MAPRATIO;
        double lng = Math.toDegrees(x / EARTH_RADIUS + EPS) + CENTER[1];

        return new double[]{lat, lng};
    }

    @Override
    public String toString() {
        return "GeoMath{ ratio = " + getRatio() + ", " +
                "lat = " + getLat() + ", " +
                "lng = " + getLng() + ", " +
                "}";
    }
}
