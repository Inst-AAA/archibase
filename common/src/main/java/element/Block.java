package element;

import org.locationtech.jts.geom.LineString;

public class Block {
    public Long id;
    public LineString ply;
    public String city;

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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "Block{" +
                "id=" + id +
                ", ply=" + ply.toText() +
                '}';
    }
}
