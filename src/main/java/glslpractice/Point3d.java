package glslpractice;

public class Point3d {
    final double x, y, z;
    public Point3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public Point3d () {
        this(0d, 0d, 0d);
    }
    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}
