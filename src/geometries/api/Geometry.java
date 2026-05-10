package geometries.api;

import primitives.Color;
import primitives.Material;
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
     * The emission color of this geometry. Defaults to black (no emission).
     */
    private Color _emission = Color.BLACK;

    /**
     * The material properties of this geometry. Defaults to a new Material with
     * default values (no reflection, no refraction, etc.).
     */
    private Material _material = new Material();

    /**
     * Returns the emission color of this geometry.
     *
     * @return the emission color
     */
    public final Color getEmission() {
        return _emission;
    }

    /**
     * Sets the emission color of this geometry.
     *
     * @param emission the new emission color
     * @return this geometry (for method chaining)
     */
    public final Geometry setEmission(Color emission) {
        _emission = emission;
        return this;
    }

    /**
     * Returns the material of this geometry.
     *
     * @return the material
     */
    public final Material getMaterial() {
        return _material;
    }

    /**
     * Sets the material of this geometry.
     *
     * @param material the new material
     * @return this geometry (for method chaining)
     */
    public final Geometry setMaterial(Material material) {
        _material = material;
        return this;
    }

    /**
     * Returns the outward unit normal to this geometry at the given surface point.
     *
     * @param point a point on the surface of this geometry
     * @return the unit normal vector at that point
     */
    public abstract Vector getNormal(Point point);
}