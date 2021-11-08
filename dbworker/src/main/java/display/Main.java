package display;

import processing.core.PApplet;
import utils.GeoContainer;

/**
 * @classname: archibase
 * @description:
 * @author: amomorning
 * @date: 2020/05/24
 */
public class Main {

    public static void main(String[] args) {
        GeoContainer.CITYNAME = "athens";
        GeoContainer.OSM_FILENAME = "./data/"+GeoContainer.CITYNAME+".pbf";
        GeoContainer.BLOCK_FILENAME = "./data/"+GeoContainer.CITYNAME+"-block.dxf";
        GeoContainer.init();
        PApplet.main("display.Show");
    }
}
