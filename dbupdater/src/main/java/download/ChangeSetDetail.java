package download;

import db.Utils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * @classname: archibase
 * @description:
 * @author: amomorning
 * @date: 2020/06/06
 */
public class ChangeSetDetail {

    public List<String> changeSetIDs;
    private SAXReader reader;
    private Utils db;
    private GeometryFactory gf;

    public ChangeSetDetail() {
        reader = new SAXReader();
        db = new Utils();
        gf = new GeometryFactory();

        getChangeSetID();


    }

    public static void main(String[] args) {
        ChangeSetDetail detail = new ChangeSetDetail();


        for (int i = 962; i < detail.changeSetIDs.size(); ++i) {
            String id = detail.changeSetIDs.get(i);
            System.out.println("i = " + i + ", id = " + id);
            detail.getChangeSetDetail(id);
        }

    }

    public void getChangeSetID() {
        changeSetIDs = new ArrayList<>();

        try {
            FileInputStream inputStream = new FileInputStream("./changesetID.txt");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                changeSetIDs.add(str);
            }

            //close
            inputStream.close();
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public boolean getChangeSetDetail(String id) {
        boolean flag = false;
        try {
            URL url = new URL("https://www.openstreetmap.org/api/0.6/changeset/" + id + "/download");

            Document doc = reader.read(url);
            Element root = doc.getRootElement();

            int cnt = 0;
            for (Iterator<Element> it = root.elementIterator(); it.hasNext(); ) {
                if(cnt > 20) break;
                Element status = it.next();
//                System.out.println(status.getName());
                Element element = status.elements().get(0);
//                System.out.println("----" + element.getName());
                if (element.getName().equals("node")) {

                    if (status.getName().equals("delete")) {
                        element = getHistoryNode(element.attribute("id").getValue());
                        if (element == null) continue;
                    }


                    double lat = Double.parseDouble(element.attribute("lat").getValue());
                    double lon = Double.parseDouble(element.attribute("lon").getValue());

                    if (distBbox(lat, lon) > 1) {
                        System.out.println("change_set far more out of bbox - node");
                        return false;
                    }
                    String[] v = new String[5];
                    if (checkBbox(lat, lon)) {
                        v[0] = element.attribute("id").getValue();

                        Point pt = gf.createPoint(new Coordinate(lon, lat));
                        v[1] = "ST_GeomFromText('" + pt.toText() + "', 4326)";
                        v[2] = "" + getStatusCode(status.getName());
                        String hstore = getTagsHStore(element);
                        if (hstore != null) v[3] = "'" + hstore + "'" + "::hstore";
                        v[4] = "'" + getTimestamp(element).toString() + "'";

                        db.insertFullData("change_set", 1, v);
                        flag = true;
                    } else {
                        System.out.println("chaege_set out of bbox - node");
                        cnt ++;
                    }


                } else if (element.getName().equals("way")) {

                    boolean ff = false;
                    if (status.getName().equals("delete")) {
                        System.out.println("deleted way = " + element.attribute("id").getValue());
                        element = getHistoryWay(element.attribute("id").getValue());
                        ff = true;
                        if (element == null) continue;
                    }

                    if (outOfRange(element.elements("nd").get(0))) {
                        System.out.println("change_set far more out of bbox - way");
                        return false;
                    }

                    LineString ls = getWayFromNodes(element.elements("nd"));
                    String[] v = new String[5];
                    if (ls != null) {
                        v[0] = element.attribute("id").getValue();
                        v[1] = "ST_GeomFromText('" + ls.toText() + "', 4326)";
                        v[2] = "" + getStatusCode(status.getName());

                        String hstore = getTagsHStore(element);
                        if (hstore != null) v[3] = "'" + hstore + "'" + "::hstore";
                        v[4] = "'" + getTimestamp(element).toString() + "'";

                        db.insertFullData("change_set", 1, v);
                        flag = true;
                    } else {
                        System.out.println("change_set out of bbox - way");
                        cnt ++;
                    }


//                    System.out.println(getTagsHStore(element));
                }

            }

        } catch (MalformedURLException | DocumentException | ParseException e) {
            e.printStackTrace();
        }


        return flag;
    }

    private boolean outOfRange(Element element) {
        String node_id = element.attribute("ref").getValue();
        Element node = getHistoryNode(node_id);

        double lat = Double.parseDouble(node.attribute("lat").getValue());
        double lon = Double.parseDouble(node.attribute("lon").getValue());

        return distBbox(lat, lon) > 1;

    }

    private Element getHistoryWay(String id) {
        System.err.println("Searching for history way #" + id);
        try {
            Document doc = reader.read(new URL("https://www.openstreetmap.org/api/0.6/way/" + id + "/history"));
            return doc.getRootElement().element("node");
        } catch (DocumentException | MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private LineString getWayFromNodes(List<Element> nd) {
        int num = nd.size();
        if (num < 2) return null;
        Coordinate[] coords = new Coordinate[num];

        boolean flag = false;
        for (int i = 0; i < num; ++i) {
            Element e = nd.get(i);
            String node_id = e.attribute("ref").getValue();
            Element node = getHistoryNode(node_id);

            if (node == null) return null;
            double lat = Double.parseDouble(node.attribute("lat").getValue());
            double lon = Double.parseDouble(node.attribute("lon").getValue());
            if (checkBbox(lat, lon)) {
                flag = true;
            }

            coords[i] = (new Coordinate(lon, lat));
        }

        return flag ? gf.createLineString(coords) : null;
    }

    private int getStatusCode(String name) {
        switch (name) {
            case "create":
                return 0;
            case "modify":
                return 1;
            case "delete":
                return 2;
            default:
                return 3;
        }
    }

    private Timestamp getTimestamp(Element element) throws ParseException {
        String timestamp = element.attribute("timestamp").getValue();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Date parsedDate = dateFormat.parse(timestamp);

        return new java.sql.Timestamp(parsedDate.getTime());
    }

    private boolean checkBbox(double lat, double lon) {
        double[] bbox = ChangeSetID.BBOX;
        return !(lat < bbox[1] || lat > bbox[3] || lon < bbox[0] || lon > bbox[2]);
    }

    private double distBbox(double lat, double lon) {
        double[] bbox = ChangeSetID.BBOX;
        double dLat = Math.abs((bbox[1] + bbox[3]) / 2.0 - lat);
        double dLon = Math.abs((bbox[0] + bbox[2]) / 2.0 - lon);

        return Math.max(dLat, dLon);

    }

    private Element getHistoryNode(String id) {
        System.err.println("Searching for history node #" + id);
        try {
            Document doc = reader.read(new URL("https://www.openstreetmap.org/api/0.6/node/" + id + "/history"));
            return doc.getRootElement().elements("node").get(0);
        } catch (DocumentException | MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }


    private String getTagsHStore(Element element) {
        List<Element> tags = element.elements("tag");
        if (tags.size() > 0) {
            String hstore = "";
            for (int i = 0; i < tags.size(); ++i) {
                if (i > 0) hstore += ",";
                String key = tags.get(i).attribute("k").getValue();
                String value = tags.get(i).attribute("v").getValue();
                value = db.checkString(value);
                hstore += key + "=>" + "\"" + value + "\"";
            }
            System.out.println(hstore);
            return hstore;
        }
        return null;
    }
}
