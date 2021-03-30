package osm;

import org.openstreetmap.osmosis.core.container.v0_6.BoundContainer;
import org.openstreetmap.osmosis.core.container.v0_6.NodeContainer;
import org.openstreetmap.osmosis.core.container.v0_6.RelationContainer;
import org.openstreetmap.osmosis.core.container.v0_6.WayContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;
import org.openstreetmap.osmosis.core.task.v0_6.Source;

import java.io.FileNotFoundException;

/**
 * @ClassName: osm.PbfWriter
 * @Description: implement
 * @author: amomorning
 * @date: Dec 9, 2019 8:08:45 PM
 */
public class PbfWriter implements Source {

    private Sink sink;

    @Override
    public void setSink(Sink sink) {
        this.sink = sink;
    }

    public void complete() {
        sink.complete();
    }

    public void write() {
        sink.process(new BoundContainer(MergeOsmFile.bound));
        for (Node node : MergeOsmFile.nodes) {
            sink.process(new NodeContainer(node));
        }
        for (Way way : MergeOsmFile.ways) {
            sink.process(new WayContainer(way));
        }
        for (Relation relation : MergeOsmFile.relations) {
            sink.process(new RelationContainer(relation));
        }
    }

}
