package renderer;

import geometries.api.Intersectable.Intersection;
import primitives.Color;
import primitives.Ray;
import scene.Scene;

/**
 * A basic ray tracer that shades intersected surfaces with ambient light and
 * each geometry's own emission color.
 * <p>
 * Rays that hit no geometry return the scene background color.
 * </p>
 *
 * @author Amichai Mukades
 */
final class SimpleRayTracer extends RayTracerBase {

    /**
     * Constructs a simple ray tracer for the given scene.
     *
     * @param scene the scene to trace rays in
     */
    SimpleRayTracer(Scene scene) {
        super(scene);
    }

    /**
     * Computes the color at the given intersection.
     * <p>
     * Returns the sum of the scene's ambient light intensity and the
     * geometry's own emission color.
     * </p>
     *
     * @param gp the intersection record (geometry + surface point)
     * @return the color at that intersection
     */
    private Color calcColor(Intersection gp) {
        return _scene.ambientLight.getIntensity().scale(gp.material.kA)
                .add(gp.geometry.getEmission());
    }

    @Override
    Color traceRay(Ray ray) {
        var intersections = _scene.geometries.calcIntersections(ray);
        var closestIntersection = ray.findClosestIntersection(intersections);
        return closestIntersection == null ? _scene.background : calcColor(closestIntersection);
    }
}
