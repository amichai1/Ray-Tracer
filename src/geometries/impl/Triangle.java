package geometries.impl;

import java.util.List;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import static primitives.Util.alignZero;
import static primitives.Util.isZero;

/**
 * Represents a triangle in 3D space.
 * <p>
 * A triangle is a special case of a convex {@link Polygon} with exactly three
 * vertices. It inherits all polygon behavior including normal computation.
 * </p>
 * <p>
 * This class is immutable.
 * </p>
 *
 * @author Amichai Mukades
 */
public final class Triangle extends Polygon {

    /**
     * Constructs a triangle from three vertices.
     *
     * @param p1 first vertex
     * @param p2 second vertex
     * @param p3 third vertex
     */
    public Triangle(Point p1, Point p2, Point p3) {
        super(p1, p2, p3);
    }

    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray) {
        return calcIntersectionsHelper(ray, Double.POSITIVE_INFINITY);
    }

    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray, double maxDistance) {
        List<Intersection> planeResult = _plane.calcIntersections(ray, maxDistance);
        if (planeResult == null) {
            return null;
        }

        Point p = planeResult.getFirst().point;
        Vector n = _plane.getNormal(p);
        Point v1 = _vertices.get(0);
        Point v2 = _vertices.get(1);
        Point v3 = _vertices.get(2);

        try {
            double d1 = alignZero(v2.subtract(v1).crossProduct(p.subtract(v1)).dotProduct(n));
            double d2 = alignZero(v3.subtract(v2).crossProduct(p.subtract(v2)).dotProduct(n));
            double d3 = alignZero(v1.subtract(v3).crossProduct(p.subtract(v3)).dotProduct(n));

            if (isZero(d1) || isZero(d2) || isZero(d3)) {
                return null; // on edge or vertex
            }
            if ((d1 > 0 && d2 > 0 && d3 > 0) || (d1 < 0 && d2 < 0 && d3 < 0)) {
                return List.of(new Intersection(this, p));
            }
            return null;
        } catch (IllegalArgumentException e) {
            return null; // intersection point coincides with a vertex
        }
    }
}
