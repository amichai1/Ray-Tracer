package geometries.impl;

import java.util.List;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import static primitives.Util.alignZero;

/**
 * Represents a sphere in 3D space.
 * <p>
 * A sphere is defined by a center point and a radius.
 * </p>
 * <p>
 * This class is immutable.
 * </p>
 *
 * @author Amichai Mukades
 */
public final class Sphere extends RadialGeometry {

    /**
     * The center point of the sphere.
     */
    private final Point _center;

    /**
     * Constructs a sphere from a center point and a radius.
     *
     * @param center the center of the sphere
     * @param radius the radius of the sphere
     */
    public Sphere(Point center, double radius) {
        super(radius);
        _center = center;
    }

    @Override
    public Vector getNormal(Point point) {
        return point.subtract(_center).normalize();
    }

    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray) {
        double tm;
        double uLenSq;
        try {
            Vector u = _center.subtract(ray.origin());
            tm = ray.direction().dotProduct(u);
            uLenSq = u.lengthSquared();
        } catch (IllegalArgumentException e) {
            // ray origin is at the sphere center: t1 = -r (excluded), t2 = r
            return List.of(new Intersection(this, ray.getPoint(_radius)));
        }

        double d2 = uLenSq - tm * tm;
        if (alignZero(d2 - _radiusSquared) >= 0) {
            return null; // miss or tangent
        }

        double th = Math.sqrt(_radiusSquared - d2);
        double t1 = alignZero(tm - th);
        double t2 = alignZero(tm + th);

        if (t2 <= 0) {
            return null;
        }
        if(t1 <= 0){
            return List.of(new Intersection(this, ray.getPoint(t2)));
        }

        return List.of(new Intersection(this, ray.getPoint(t1)),
                new Intersection(this, ray.getPoint(t2)));
    }
}
