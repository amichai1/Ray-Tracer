package geometries.impl;

import java.util.ArrayList;
import java.util.List;

import geometries.api.Intersectable;
import primitives.Point;
import primitives.Ray;

/**
 * Composite geometry that aggregates a collection of {@link Intersectable} objects.
 * <p>
 * Implements the <em>Composite</em> design pattern: a {@code Geometries} object
 * can itself be treated as an {@code Intersectable}, delegating the
 * {@code findIntersections} query to each element and collecting all results.
 * </p>
 * <p>
 * This class is not {@code final} to allow future extension.
 * </p>
 *
 * @author Amichai Mukades
 */
public class Geometries extends Intersectable {

    /**
     * The collection of intersectable objects in this composite.
     */
    private final List<Intersectable> _geometries = new ArrayList<>();

    /**
     * Constructs an empty {@code Geometries} scene.
     */
    public Geometries() { }

    /**
     * Constructs a {@code Geometries} scene and adds the given objects to it.
     *
     * @param  geometries one or more intersectable objects to add
     */
    public Geometries(Intersectable... geometries) {
        add(geometries);
    }

    /**
     * Adds one or more intersectable objects to this composite.
     *
     * @param  geometries one or more intersectable objects to add
     */
    public void add(Intersectable... geometries) {
        _geometries.addAll(List.of(geometries));
    }

    @Override
    public List<Point> findIntersections(Ray ray) {
        List<Point> result = null;
        for (Intersectable g : _geometries) {
            List<Point> pts = g.findIntersections(ray);
            if (pts != null) {
                if (result == null)
                    result = new ArrayList<>(pts);
                else
                    result.addAll(pts);
            }
        }
        return result;
    }
}
