package osm;

import element.LatLng;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Download osm file with timestamp and user description
 * Usage:
 * run with args pathname latlng_bottomleft latlng_upright
 * deltaLat < 0.15, deltaLng < 0.25
 *
 * OSMDownload "./data/wien" 48.110 16.152 48.324 16.625
 * OSMDownload "./data/zurich2" 47.3088 8.4631 47.4464 8.6692
 * OSMDownload "./data/paris" 48.8008 2.2172 48.9196 2.4390
 *  48.84535 2.411275 48.860200000000006 2.439
 *  48.860200000000006 2.2172 48.9196 2.3281
 *  48.912175000000005 2.2587875000000004 48.9196 2.2726500000000005
 * OSMDownload "./data/athens" 37.9033 23.6515 38.0288 23.7881
 * OSMDownload "./data/london" 51.4686 -0.1734 51.5492 0.0130
 */
public class OSMDownload {

    public static void main(String[] args) throws FileNotFoundException {
        System.out.println(Arrays.toString(args));

        String pathname = args[0];

        LatLng bl = new LatLng(Double.parseDouble(args[1]), Double.parseDouble(args[2]));
        LatLng tr = new LatLng(Double.parseDouble(args[3]), Double.parseDouble(args[4]));

        File path = new File(pathname);
        if (!path.exists() && path.mkdirs()) {
            System.out.println(path.getName() + " is created!");
        }
        OSMRequest.setFilepath(pathname);
        if (OSMRequest.getBoundary(bl.longitude, bl.latitude, tr.longitude, tr.latitude)) {

            File[] files = path.listFiles();
            List<File> toPbf = new ArrayList<>();
            assert files != null;
            for (File file : files) {
                if (file.toString().contains(".osm")) {
                    toPbf.add(file);
                }
            }
            MergeOsmFile.merge(false, pathname + ".pbf", toPbf);

        } else {
            System.err.println("ERROR");
        }

    }

}
