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
 * OSMDownload "./data/wien" 48.110 16.152 48.324 16.625
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
