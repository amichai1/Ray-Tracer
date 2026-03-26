package geometries.api;

import java.util.List;

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
     * Finds all intersection points between this object and the given ray.
     * <p>
     * Returns only intersections in the forward direction of the ray
     * (parameter {@code t > 0}).
     * Points on edges, vertices, or tangent points are not included.
     * </p>
     *
     * @param  ray the ray to intersect with
     * @return     a list of intersection points, or {@code null} if there are none
     */
    public abstract List<Point> findIntersections(Ray ray);
}
