package geometries.impl;

import java.util.List;

import geometries.api.AABB;
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
    protected AABB calcBoundingBox() {
        double cx = _center.getX(), cy = _center.getY(), cz = _center.getZ();
        return new AABB(cx - _radius, cy - _radius, cz - _radius,
                        cx + _radius, cy + _radius, cz + _radius);
    }

    @Override
    public Vector getNormal(Point point) {
        return point.subtract(_center).normalize();
    }

    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray) {
        return calcIntersectionsHelper(ray, Double.POSITIVE_INFINITY);
    }

    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray, double maxDistance) {
        double tm;
        double uLenSq;
        try {
            Vector u = _center.subtract(ray.origin());
            tm = ray.direction().dotProduct(u);
            uLenSq = u.lengthSquared();
        } catch (IllegalArgumentException e) {
            // ray origin is at the sphere center: only forward intersection at t = radius
            if (alignZero(_radius - maxDistance) > 0) {
                return null;
            }
            var intersected = new Intersection(this, ray.getPoint(_radius));
            return List.of(intersected);
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
        if (t1 > 0 && alignZero(t1 - maxDistance) <= 0) {
            if (alignZero(t2 - maxDistance) <= 0) {
                var intersected1 = new Intersection(this, ray.getPoint(t1));
                var intersected2 = new Intersection(this, ray.getPoint(t2));
                return List.of(intersected1, intersected2);
            }
            var intersected = new Intersection(this, ray.getPoint(t1));
            return List.of(intersected);
        }
        if (alignZero(t2 - maxDistance) <= 0) {
           var intersected = new Intersection(this, ray.getPoint(t2));
           return List.of(intersected);
        }
        return null;
    }
}
