package geometries.api;

import java.util.List;

import primitives.Material;
import primitives.Point;
import primitives.Ray;

/**
 * Abstract base class for all objects that can be intersected by a ray.
 * <p>
 * Any geometry or composite of geometries that supports ray intersection must
 * extend this class and provide an implementation of
 * {@link #findIntersections(Ray)}.
 * </p>
 *
 * @author Amichai Mukades
 */
public abstract class Intersectable {

    /**
     * A hit record pairing a surface point with the geometry it belongs to.
     */
    public static final class Intersection {

        /**
         * The geometry that was intersected.
         */
        public final Geometry geometry;

        /**
         * The point at which the ray and the geometry meet.
         */
        public final Point point;
        /**
         * The material of the geometry
         */
        public final Material material;

        /**
         * Constructs an intersection record.
         *
         * @param geometry the intersected geometry
         * @param point    the intersection point on that geometry's surface
         */
        public Intersection(Geometry geometry, Point point) {
            this.geometry = geometry;
            this.point = point;
            this.material = geometry == null ? new Material() : geometry.getMaterial();
        }

        @Override
        public String toString() {
            return "Intersection{geometry=" + geometry + ", point=" + point + '}';
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            return obj instanceof Intersection other
                    && this.point.equals(other.point)
                    && this.geometry.equals(other.geometry);
        }
    }

    /**
     * Finds all intersection points between this object and the given ray.
     * <p>
     * Returns only intersections in the forward direction of the ray
     * (parameter {@code t > 0}).
     * Points on edges, vertices, or tangent points are not included.
     * </p>
     *
     * @param ray the ray to intersect with
     * @return a list of intersection points, or {@code null} if there are none
     */
    public final List<Point> findIntersections(Ray ray) {
        var intersections = calcIntersections(ray);
        return intersections == null ? null
                : intersections.stream()
                .map(intersection -> intersection.point)
                .toList();
    }

    /**
     * Computes all intersections between this object and the given ray, each
     * paired with the intersected geometry.
     *
     * @param ray the ray to intersect with
     * @return a list of {@link Intersection} records, or {@code null} if
     * there are none
     */
    public final List<Intersection> calcIntersections(Ray ray) {
        return calcIntersectionsHelper(ray);
    }

    /**
     * Template-method hook: subclasses implement the actual intersection logic here.
     *
     * @param ray the ray to intersect with
     * @return a list of {@link Intersection} records, or {@code null} if
     * there are none
     */
    protected abstract List<Intersection> calcIntersectionsHelper(Ray ray);
}