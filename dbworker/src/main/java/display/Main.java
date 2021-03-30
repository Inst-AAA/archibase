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
        GeoContainer.OSM_FILENAME = "./data/wien.pbf";
        GeoContainer.BLOCK_FILENAME = "./data/wien-block.dxf";
        GeoContainer.init();
        PApplet.main("display.Show");
    }
}
