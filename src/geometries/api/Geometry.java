package geometries.api;

import primitives.Point;
import primitives.Vector;

/**
 * Abstract base class for all geometric shapes in the scene.
 * <p>
 * Every concrete geometry must implement {@link #getNormal(Point)}, which
 * returns the outward unit normal at a given surface point.
 * </p>
 * <p>
 * This class also extends {@link Intersectable}, so every geometry supports
 * ray intersection queries.
 * </p>
 *
 * @author Amichai Mukades
 */
public abstract class Geometry extends Intersectable {
    /**
     * Returns the outward unit normal to this geometry at the given surface point.
     *
     * @param point a point on the surface of this geometry
     * @return the unit normal vector at that point
     */
    public abstract Vector getNormal(Point point);
}