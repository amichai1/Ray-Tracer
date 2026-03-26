package geometries.impl;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

import java.util.List;

import geometries.api.Geometry;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Represents an infinite plane in 3D space.
 * <p>
 * A plane is defined by a point on the plane and an outward unit normal vector.
 * </p>
 * <p>
 * This class is immutable.
 * </p>
 *
 * @author Amichai Mukades
 */
public final class Plane extends Geometry {

    /**
     * A representative point on the plane.
     */
    private final Point _point;

    /**
     * The outward unit normal to the plane.
     */
    private final Vector _normal;

    /**
     * Constructs a plane from three non-collinear points.
     * <p>
     * The first point is stored as the representative point on the plane.
     * The normal is computed as the normalized cross product of the two edge
     * vectors {@code (p2 - p1)} and {@code (p3 - p1)}.
     * </p>
     *
     * @param p1 first point (stored as the representative point)
     * @param p2 second point
     * @param p3 third point
     */
    public Plane(Point p1, Point p2, Point p3) {
        _point = p1;
        _normal = p2.subtract(p1).crossProduct(p3.subtract(p1)).normalize();
    }

    /**
     * Constructs a plane from a point on the plane and a normal vector.
     * <p>
     * The normal is normalized before storage.
     * </p>
     *
     * @param point  a point on the plane
     * @param normal the normal direction (need not be a unit vector)
     */
    public Plane(Point point, Vector normal) {
        _point = point;
        _normal = normal.normalize();
    }

    @Override
    public Vector getNormal(Point point) {
        return _normal;
    }

    @Override
    public List<Point> findIntersections(Ray ray) {
        double nv = _normal.dotProduct(ray.direction());
        if (isZero(nv)) return null;

        Vector qMinusP0;
        try {
            qMinusP0 = _point.subtract(ray.origin());
        } catch (IllegalArgumentException e) {
            return null; // ray origin coincides with reference point Q → t = 0
        }

        double t = alignZero(_normal.dotProduct(qMinusP0) / nv);
        return t <= 0 ? null : List.of(ray.getPoint(t));
    }
}
