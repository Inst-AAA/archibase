package analysis;

import processing.core.PApplet;
import utils.GeoContainer;

/**
 * @classname: archibase
 * @description:
 * @author: amomorning
 * @date: 2020/07/13
 */
public class Generate {
    public static void main(String[] args) {
        GeoContainer.CITYNAME = "london";
        GeoContainer.OSM_FILENAME = "./data/"+GeoContainer.CITYNAME+".pbf";
        GeoContainer.init();
        PApplet.main("analysis.Show");
    }
}
