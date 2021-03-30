package element;

import org.locationtech.jts.geom.LineString;

public class Block {
    Long id;
    LineString ply;

    public Long getID() {
        return id;
    }

    public void setID(Long id) {
        this.id = id;
    }

    public LineString getPly() {
        return ply;
    }

    public void setPly(LineString ply) {
        this.ply = ply;
    }

    @Override
    public String toString() {
        return "Block{" +
                "id=" + id +
                ", ply=" + ply.toText() +
                '}';
    }
}
