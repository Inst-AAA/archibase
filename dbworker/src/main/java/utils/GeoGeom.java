package utils;

import org.locationtech.jts.geom.Geometry;

import java.util.Date;
import java.util.Map;

/**
 * @classname: archibase
 * @description:
 * @author: amomorning
 * @date: 2020/05/24
 */
public abstract class GeoGeom {
    public abstract Date getTimestamp();

    public abstract void setTimestamp(Date timestamp);

    public abstract long getOsm_id();

    public abstract void setOsm_id(long id);

    public abstract Map<String, String> getTags();

    public abstract void setTags(Map<String, String> tags);

    public abstract void printTag();

    public abstract void addTag(String key, String value);

    public abstract Geometry getGeometry();

}
