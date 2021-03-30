package display;

import Guo_Cam.Vec_Guo;
import utils.GeoCity;
import helper.GeoMath;
import helper.Tools;
import processing.core.PApplet;
import utils.GeoContainer;


public class Show extends PApplet {
    public static final int LEN_OF_CAMERA = 5000;
    Tools tools;

    GeoMath geoMath;
    GeoCity city;


    public void settings() {
        size(1000, 1000, P3D);
    }

    public void setup() {
        tools = new Tools(this, LEN_OF_CAMERA);
//        System.out.println(GeoContainer.MAP_LAT_LNG[0] + " " + GeoContainer.MAP_LAT_LNG[1]);
        geoMath = new GeoMath(GeoContainer.MAP_LAT_LNG);
        city = new GeoCity(geoMath);


        tools.cam.top();
        tools.cam.getCamera().setPosition(new Vec_Guo(-200, -220, LEN_OF_CAMERA));
        tools.cam.getCamera().setLookAt(new Vec_Guo(-200, -220, 0));

        background(255);
        city.setTags( "building");

        String filename = "./fig/wien-fine.jpg";
        city.save(this, 5000, 5000, filename);
    }

    public void draw() {
        background(255);
        city.drawFilter(tools);

    }


}
