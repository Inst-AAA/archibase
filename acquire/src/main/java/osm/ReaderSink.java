package osm;

import org.openstreetmap.osmosis.core.container.v0_6.*;
import org.openstreetmap.osmosis.core.domain.v0_6.Bound;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;

import java.util.Map;

public class ReaderSink implements Sink {

    @Override
    public void initialize(Map<String, Object> arg0) {

    }

    @Override
    public void complete() {

    }

    @Override
    public void close() {

    }

    @Override
    public void process(EntityContainer entityContainer) {

        if (entityContainer instanceof NodeContainer) {

            Node myNode = ((NodeContainer) entityContainer).getEntity();
            MergeOsmFile.nodes.add(myNode);

        } else if (entityContainer instanceof WayContainer) {

            // Get all geometry ways
            Way myWay = ((WayContainer) entityContainer).getEntity();
            MergeOsmFile.ways.add(myWay);

        } else if (entityContainer instanceof RelationContainer) {

            Relation myRelation = ((RelationContainer) entityContainer).getEntity();
            MergeOsmFile.relations.add(myRelation);

        } else if (entityContainer instanceof BoundContainer) {

            Bound myBound = ((BoundContainer) entityContainer).getEntity();
            MergeOsmFile.left = Math.min(MergeOsmFile.left, myBound.getLeft());
            MergeOsmFile.bottom = Math.min(MergeOsmFile.bottom, myBound.getBottom());
            MergeOsmFile.right = Math.max(MergeOsmFile.right, myBound.getRight());
            MergeOsmFile.top = Math.max(MergeOsmFile.top, myBound.getTop());
            MergeOsmFile.bound = myBound;

        } else {
            System.out.println("Unknown Entity!");
        }
    }

}
