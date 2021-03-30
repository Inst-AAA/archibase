package osm;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * Recursive OSM file downloader from osm website(https://www.openstreetmap.org)
 */
public class OSMRequest {

    private static String PATHNAME = null;

    public static boolean getBoundary(double minLng, double minLat, double maxLng, double maxLat) {
        URL url;
        HttpURLConnection connection;
        String bbox = minLng + "," + minLat + "," + maxLng + ","
                + maxLat;
        try {
            url = new URL("https://www.openstreetmap.org/api/0.6/map?bbox=" + bbox);
            System.out.println(url.toString());
//            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 4780));

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
            connection.setRequestProperty("Content-type", "application/json");
            connection.connect();
            boolean flag = true;
            if (connection.getResponseCode() == 400) {
                System.err.println("WARNING: Map boundary box split to 4 parts.");
                double dlat = (maxLat - minLat) / 2.;
                double dlng = (maxLng - minLng) / 2.;

                for (int i = 0; i < 2; ++i) {
                    for (int j = 0; j < 2; ++j) {
                        double mlat = minLat + i * dlat;
                        double mlng = minLng + j * dlng;
                        flag &= getBoundary(mlng, mlat, mlng + dlng, mlat + dlat);
                    }
                }
            } else if (connection.getResponseCode() == 200) {
                System.out.println("downloading....");
                BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
                String filename = PATHNAME + "/" + bbox + ".osm";
                FileOutputStream fileOutputStream = new FileOutputStream(filename);
                byte[] dataBuffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                }
                System.out.println(filename + " download finished");
                fileOutputStream.close();
                in.close();
            } else if (connection.getResponseCode() == 509) {


                TimeUnit.SECONDS.sleep(60);
                System.err.println("Retry after 60 seconds");
                getBoundary(minLng, minLat, maxLng, maxLat);

            } else if (connection.getResponseCode() == 429) {

                TimeUnit.MINUTES.sleep(20);
                System.err.println("Retry after 20 minutes");
                getBoundary(minLng, minLat, maxLng, maxLat);
            } else {
                System.err.println("ERROR " + connection.getResponseCode() + ": Connection error.");
            }
            System.out.println("waiting....");
            TimeUnit.SECONDS.sleep(1);
            return flag;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }


    public static void setFilepath(String path) {
        PATHNAME = path;
    }
}
