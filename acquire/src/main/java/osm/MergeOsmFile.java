package osm;

import crosby.binary.osmosis.OsmosisReader;
import crosby.binary.osmosis.OsmosisSerializer;
import org.openstreetmap.osmosis.core.domain.v0_6.Bound;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.osmbinary.file.BlockOutputStream;
import org.openstreetmap.osmosis.xml.common.CompressionMethod;
import org.openstreetmap.osmosis.xml.v0_6.XmlReader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Merge osm or pbf file
 */
public class MergeOsmFile {

    public static List<Node> nodes;
    public static List<Way> ways;
    public static List<Relation> relations;
    public static Bound bound;
    public static double right, left, top, bottom;


    /**
     *
     * @param isPbf   true uses pbf reader method, false uses xml reader method.
     * @param outfile   merged output file name
     * @param files  a list of raw osm files
     */
    public static void merge(boolean isPbf, String outfile, List<File> files) throws FileNotFoundException {
        nodes = new ArrayList<>();
        ways = new ArrayList<>();
        relations = new ArrayList<>();

        top = -90;
        bottom = 90;
        left = 180;
        right = -180;

        for (File file : files) {
            if (!isPbf) {
                XmlReader reader = new XmlReader(file, true, CompressionMethod.None);
                reader.setSink(new ReaderSink());
                reader.run();
            } else {
                OsmosisReader reader = new OsmosisReader(new FileInputStream(file));
                reader.setSink(new ReaderSink());
                reader.run();
            }
        }

        Bound nb = bound;
        bound = new Bound(right, left, top, bottom, nb.getOrigin());
        bound.setChangesetId(nb.getChangesetId());
        bound.setVersion(nb.getVersion());
        bound.setId(nb.getId());
        bound.setTimestamp(nb.getTimestamp());
        bound.setUser(nb.getUser());

        OutputStream outputStream = new FileOutputStream(outfile);
        PbfWriter writer = new PbfWriter();
        writer.setSink(new OsmosisSerializer(new BlockOutputStream(outputStream)));
        writer.write();
        writer.complete();
    }
}
