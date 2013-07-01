package glslpractice;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.media.opengl.glu.GLU;

/**
 * マウス、キーボード入力によるカメラアングルの変更をさばく
 */
public class Camera extends MouseAdapter implements KeyListener {
    private Point prev = new Point();
    private Point3d eye = new Point3d();
    private Point3d up = new Point3d(0d, 1d, 0d);
    private Point3d center = new Point3d();
    private double pitch, yaw;

    public Camera(double ex, double ey, double ez, double cx,
            double cy, double cz) {
        setEye(ex, ey, ez);
        setCenter(cx, cy, cz);
    }

    public Camera() {
        this(0d, 0d, 0d, 0d, 0d, 0d);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        prev.setLocation(e.getPoint());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    private final double dAngle = Math.PI / 360d / 5d;

    @Override
    public void mouseDragged(MouseEvent e) {
        Point p = e.getPoint();
        int dx = p.x - prev.x;
        int dy = p.y - prev.y;
        pitch += -dy * dAngle;
        if (pitch > Math.PI / 2d) {
            pitch = Math.PI / 2d;
        }
        if (pitch < -Math.PI / 2d) {
            pitch = -Math.PI / 2d;
        }
        yaw += dx * dAngle;
        if (yaw < 0) {
            yaw += Math.PI * 2;
        }
        if (yaw > Math.PI * 2) {
            yaw -= Math.PI * 2;
        }
        updateCenter();
        prev.setLocation(p);
    }

    public Point3d getEye() {
        return eye;
    }

    public Point3d getCenter() {
        return center;
    }

    public Point3d getUp() {
        return up;
    }

    public void look(GLU glu) {
        glu.gluLookAt(eye.x, eye.y, eye.z, center.x, center.y,
                center.z, up.x, up.y, up.z);
    }

    public void setEye(double x, double y, double z) {
        eye = new Point3d(x, y, z);
        calcPitchAndYaw();
    }

    public void setCenter(double x, double y, double z) {
        center = new Point3d(x, y, z);
        calcPitchAndYaw();
    }

    private void calcPitchAndYaw() {
        double dx = center.x - eye.x;
        double dy = center.y - eye.y;
        double dz = center.z - eye.z;
        pitch = Math.atan(dy / Math.sqrt(dx * dx + dz * dz));
        yaw = Math.atan(dz / dx);
    }

    private void updateCenter() {
        center = new Point3d(eye.x + Math.cos(yaw), eye.y
                + Math.tan(pitch), eye.z + Math.sin(yaw));
    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
        switch (e.getKeyChar()) {
        case 'a':
            shift(-1d, 0d);
            break;
        case 'd':
            shift(1d, 0d);
            break;
        case 's':
            shift(0d, -1d);
            break;
        case 'w':
            shift(0d, 1d);
            break;
        default:
            System.out.println("key? imputed: " + e.getKeyChar());
        }
    }

    public void shift(double d, double w) {
        // front or back
        double dx = center.x - eye.x;
        double dy = center.y - eye.y;
        double dz = center.z - eye.z;
        double r = Math.sqrt(dx * dx + dy * dy + dz * dz);
        double dex = w * dx / r;
        double dey = w * dy / r;
        double dez = w * dz / r;
        // right or left
        double r2 = Math.sqrt(dx * dx + dz * dz);
        double cx = -dz / r2;
        double cz = dx / r2;
        dex += d * cx;
        dez += d * cz;
        eye = new Point3d(eye.x + dex, eye.y + dey, eye.z + dez);
        center = new Point3d(center.x + dex, center.y + dey, center.z
                + dez);
    }
}
