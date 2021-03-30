package download;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @classname: archibase
 * @description:
 * @author: amomorning
 * @date: 2020/06/06
 */
public class ChangeSetID {
    public static final double[] BBOX = {11.0202, 43.831, 11.1475, 43.9253};
    Set<Long> changeSetID;

    public static void main(String[] args) {
        ChangeSetID changeset = new ChangeSetID();
        changeset.getBboxChangesetID(BBOX, "2019-12-05");
        changeset.saveChangesetID();
    }

    public void getBboxChangesetID(double[] bbox, String lastDate) {
        changeSetID = new HashSet<>();

        String url = "https://www.openstreetmap.org/api/0.6/changesets?bbox=" + bbox[0] + "," + bbox[1] + "," + bbox[2] + "," + bbox[3] + "&time=" + lastDate;

        String last = getChangesetIDBefore(url, "");
        while (last != null) {
            last = getChangesetIDBefore(url, "," + last);
        }

        System.out.println("Total = " + changeSetID.size());

    }

    private void saveChangesetID() {
        try {
            OutputStream f = new FileOutputStream("./changesetID.txt");
            OutputStreamWriter writer = new OutputStreamWriter(f, "UTF-8");
            for (Long id : changeSetID) {
                writer.append(String.valueOf(id)).append("\n");
            }
            writer.close();
            f.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    private String getChangesetIDBefore(String str, String time) {
        try {
            URL url = new URL(str + time);
            System.out.println("Open URL " + url.toString());

            int cnt = 0;
            String ret = null;

            Document doc = parse(url);
            Element root = doc.getRootElement();
            for (Iterator<Element> it = root.elementIterator(); it.hasNext(); ) {
                Element element = it.next();

                System.out.println(element.attribute(1).getValue() + " " + element.attribute(0).getValue());

                ret = element.attribute(1).getValue();
                changeSetID.add(Long.parseLong(element.attribute(0).getValue()));
                cnt++;
            }

            System.out.println(cnt);
            if (cnt < 100) return null;
            return ret;
        } catch (MalformedURLException | DocumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Document parse(URL url) throws DocumentException {
        SAXReader reader = new SAXReader();
        return reader.read(url);
    }

}
