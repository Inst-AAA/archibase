package loader;

import org.locationtech.jts.geom.*;
import org.openstreetmap.osmosis.core.container.v0_6.*;
import org.openstreetmap.osmosis.core.domain.v0_6.*;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;
import utils.GeoContainer;
import utils.GeoMultiLines;
import utils.GeoPoint;
import utils.GeoPolyLine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class PbfReader implements Sink {

	@Override
	public void initialize(Map<String, Object> arg0) {
	}

	@Override
	public void complete() {
		System.out.println("Nodes = " + GeoContainer.nodeCount);
		System.out.println("Ways = " + GeoContainer.wayCount);
		System.out.println("Relations = " + GeoContainer.relationCount);
	}

	@Override
	public void close() {

	}

	@Override
	public void process(EntityContainer entityContainer) {
		GeometryFactory gf = new GeometryFactory();

		if (entityContainer instanceof NodeContainer) {

			Node node = ((NodeContainer) entityContainer).getEntity();

			if (!GeoContainer.nodeId.containsKey(node.getId())) {
				// 添加点到点集
				Point pt = gf.createPoint(new Coordinate(node.getLongitude(), node.getLatitude()));

				GeoPoint gpt = new GeoPoint(pt);
				gpt.setTimestamp(node.getTimestamp());
				gpt.setOsm_id(node.getId());

				for (Tag tag : node.getTags()) {
					String key = tag.getKey();
					String value = tag.getValue();
					gpt.addTag(key, value);
				}

				GeoContainer.nodeId.put(node.getId(), GeoContainer.nodeCount++);
				GeoContainer.nodes.add(gpt);

			}

		} else if (entityContainer instanceof WayContainer) {

			Way way = ((WayContainer) entityContainer).getEntity();
			if (!GeoContainer.wayId.containsKey(way.getId())) {
				List<WayNode> nodeList = way.getWayNodes();

				int num = nodeList.size();
				if (num >= 2) {
					Coordinate[] pts = new Coordinate[num];
					for (int i = 0; i < num; ++i) {
						Integer id = GeoContainer.nodeId.get(nodeList.get(i).getNodeId());
						pts[i] = GeoContainer.nodes.get(id).getCoord();
					}
					LineString ls = gf.createLineString(pts);
					GeoPolyLine gpl = new GeoPolyLine(ls);
					gpl.setTimestamp(way.getTimestamp());
					gpl.setOsm_id(way.getId());

					for (Tag tag : way.getTags()) {
						String key = tag.getKey();
						String value = tag.getValue();
						gpl.addTag(key, value);
					}
					GeoContainer.wayId.put(way.getId(), GeoContainer.wayCount++);
					GeoContainer.ways.add(gpl);
				}

			}

		} else if (entityContainer instanceof RelationContainer) {
			Relation re = ((RelationContainer) entityContainer).getEntity();
			if (!GeoContainer.relationId.containsKey(re.getId())) {
				boolean flag = false;
				for (Tag tag : re.getTags()) {
					if (tag.getValue().equals("multipolygon")) {
						flag = true;
					}
				}
				if (flag) {
					List<RelationMember> members = re.getMembers();
					List<LineString> ls = new ArrayList<>();
					for (RelationMember member : members) {
						if (member.getMemberType().equals(EntityType.Way)) {
							Integer id = GeoContainer.wayId.get(member.getMemberId());
							if (id != null) {
								ls.add((LineString) GeoContainer.ways.get(id).getGeometry());
							}
						}

					}
					MultiLineString mls = gf.createMultiLineString(ls.toArray(new LineString[0]));
					mls = joinMultiLine(mls);

					GeoMultiLines gml = new GeoMultiLines(mls);
					gml.setTimestamp(re.getTimestamp());
					gml.setOsm_id(re.getId());

					for (Tag tag : re.getTags()) {
						String key = tag.getKey();
						String value = tag.getValue();
						gml.addTag(key, value);
					}

					GeoContainer.relationId.put(re.getId(), GeoContainer.relationCount++);
					GeoContainer.relations.add(gml);
				}
			}

		} else if (entityContainer instanceof BoundContainer) {
			Bound myBound = ((BoundContainer) entityContainer).getEntity();

			double y = (myBound.getBottom() + myBound.getTop()) / 2.0;
			double x = (myBound.getLeft() + myBound.getRight()) / 2.0;

			System.out.println(y + ", " + x);

			GeoContainer.MAP_LAT_LNG = new double[]{y, x};
			GeoContainer.SW_LAT_LNG = new double[]{myBound.getBottom(), myBound.getLeft()};
			GeoContainer.NE_LAT_LNG = new double[]{myBound.getTop(), myBound.getRight()};

			System.out.println(GeoContainer.SW_LAT_LNG[0] + ", " + GeoContainer.SW_LAT_LNG[1]);
			System.out.println(GeoContainer.NE_LAT_LNG[0] + ", " + GeoContainer.NE_LAT_LNG[1]);
		} else {
			System.out.println("Unknown Entity!");
		}
	}

	public List<LineString> getReverse(MultiLineString mls) {
		int num = mls.getNumGeometries();
		GeometryFactory gf = new GeometryFactory();

		List<Coordinate> pts = new ArrayList<>(Arrays.asList(mls.getGeometryN(num - 1).getCoordinates()));

		List<LineString> lss = new ArrayList<>();

		boolean joined = false;
		for (int i = num - 2; i >= 0; --i) {

			Coordinate cur = pts.get(pts.size() - 1);
			Coordinate[] cos = mls.getGeometryN(i).getCoordinates();

			if (cur.distance(cos[0]) < 1e-6) {
//				System.out.println("i = " + i + "find reversed !! ");

				pts.addAll(Arrays.asList(cos).subList(1, cos.length));
				joined = true;
			} else {
				LineString ls = gf.createLineString(pts.toArray(new Coordinate[0]));
				if (joined) lss.add(ls);
				pts = new ArrayList<>(Arrays.asList(cos));
				joined = false;
			}

		}
		if (pts.size() > 0) {
			LineString ls = gf.createLineString(pts.toArray(new Coordinate[0]));
			if (joined) lss.add(ls);
		}

		return lss;
	}

	public MultiLineString joinMultiLine(MultiLineString mls) {
		if (mls.getNumGeometries() < 1) return mls;

		GeometryFactory gf = new GeometryFactory();

		List<Coordinate> pts = new ArrayList<>(Arrays.asList(mls.getGeometryN(0).getCoordinates()));

		List<LineString> lss = new ArrayList<>();

		List<LineString> rev_lss = getReverse(mls);
		for (LineString ls : rev_lss) {
			if (ls.isClosed()) lss.add(ls);
		}
		if (lss.size() > 0) {
			return gf.createMultiLineString(lss.toArray(new LineString[0]));
		}

		for (int i = 1; i < mls.getNumGeometries(); ++i) {
			Coordinate cur = pts.get(pts.size() - 1);
			Coordinate[] cos = mls.getGeometryN(i).getCoordinates();

			if (cur.distance(cos[0]) < 1e-6) {

				pts.addAll(Arrays.asList(cos).subList(1, cos.length));

			} else if (cur.distance(cos[cos.length - 1]) < 1e-6) {
				for (int j = cos.length - 2; j >= 0; --j) {
					pts.add(cos[j]);
				}
//
//            } else if(isIntersect(rev_lss, pts)) {
//                System.out.println("after pts = " + pts.size());
			} else {
				LineString ls = gf.createLineString(pts.toArray(new Coordinate[0]));
				lss.add(ls);
				pts = new ArrayList<>(Arrays.asList(cos));
			}

		}
		if (pts.size() > 0) {
			LineString ls = gf.createLineString(pts.toArray(new Coordinate[0]));
			lss.add(ls);
		}


		return gf.createMultiLineString(lss.toArray(new LineString[0]));

	}

}
