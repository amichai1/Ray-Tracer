package geometries.impl;

import primitives.Point;

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
}
