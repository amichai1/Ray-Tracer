package geometries.api;

import java.util.List;

import lighting.LightSource;
import primitives.Material;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import static primitives.Util.alignZero;

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
         * Cached surface normal at the intersection point.
         */
        public Vector normal;

        /**
         * Cached ray direction vector (from camera).
         */
        public Vector v;

        /**
         * Cached dot product of the ray direction and the surface normal.
         */
        public double vNormal;

        /**
         * The light source being evaluated in the current shading step.
         */
        public LightSource light;

        /**
         * Cached direction from the intersection point to the current light source.
         */
        public Vector l;

        /**
         * Cached dot product of the light direction and the surface normal.
         */
        public double lNormal;

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
            if (this == obj) {
                return true;
            }
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
        if (intersections == null) {
            return null;
        }
        return intersections.stream().map(intersection -> intersection.point).toList();
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
        return calcIntersectionsHelper(ray, Double.POSITIVE_INFINITY);
    }

    /**
     * Computes all intersections with the given ray up to {@code maxDistance}.
     *
     * @param ray         the ray to intersect with
     * @param maxDistance the maximum allowed distance from the ray origin
     * @return a list of {@link Intersection} records within range,
     * or {@code null} if there are none
     */
    public final List<Intersection> calcIntersections(Ray ray, double maxDistance) {
        return calcIntersectionsHelper(ray, maxDistance);
    }

    /**
     * Template-method hook: subclasses implement the actual intersection logic here.
     *
     * @param ray the ray to intersect with
     * @return a list of {@link Intersection} records, or {@code null} if
     * there are none
     */
    protected abstract List<Intersection> calcIntersectionsHelper(Ray ray);

    /**
     * Template-method hook with max-distance support.
     * <p>
     * Default implementation calls {@link #calcIntersectionsHelper(Ray)} and
     * filters out intersections beyond {@code maxDistance}. Subclasses may
     * override for a more efficient early-exit computation.
     * </p>
     *
     * @param ray         the ray to intersect with
     * @param maxDistance the maximum allowed distance from the ray origin
     * @return filtered intersections, or {@code null} if none
     */
    protected List<Intersection> calcIntersectionsHelper(Ray ray, double maxDistance) {
        var all = calcIntersectionsHelper(ray);
        if (all == null) return null;
        var stream = all.stream();
        var filtered = stream
                .filter(i -> alignZero(i.point.distance(ray.origin()) - maxDistance) <= 0)
                .toList();
        if (filtered.isEmpty()){
            return null;
        }
        return filtered;
    }
}