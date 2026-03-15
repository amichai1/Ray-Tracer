package geometries.impl;

import primitives.Ray;

/**
 * Represents a finite cylinder (tube with two flat caps) in 3D space.
 * <p>
 * A cylinder is a {@link Tube} with a finite height.
 * </p>
 * <p>
 * This class is immutable.
 * </p>
 *
 * @author Mihael Dabbah
 */
public final class Cylinder extends Tube {

    /**
     * The height of the cylinder along its axis.
     *according to the logic of the updated instructions "height" should have a ' _ '
     * /
    private final double _height;
    /**
     * Constructs a cylinder with the given radius, axis and height.
     *
     * @param radius the radius of the cylinder
     * @param axis   the central axis ray
     * @param height the height of the cylinder
     */
    public Cylinder(double radius, Ray axis, double height) {
        super(radius, axis);
        _height = height;
    }
}
