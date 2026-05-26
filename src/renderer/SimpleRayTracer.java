package renderer;

import static primitives.Util.alignZero;

import geometries.api.Intersectable.Intersection;
import lighting.LightSource;
import primitives.Color;
import primitives.Double3;
import primitives.Ray;
import primitives.Vector;
import scene.Scene;

/**
 * A ray tracer that shades surfaces using the Phong reflection model with
 * ambient, diffuse, and specular contributions from all scene light sources.
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
     * Computes the Phong-shaded color at the given intersection point.
     *
     * @param intersection the intersection record (geometry + surface point)
     * @param v            the normalized ray direction (from camera)
     * @return the color at that intersection, or {@link Color#BLACK} if the
     *         ray is tangent to the surface
     */
    private Color calcColor(Intersection intersection, Vector v) {
        return !preprocessIntersection(intersection, v) ? Color.BLACK
                : _scene.ambientLight.getIntensity().scale(intersection.material.kA)
                        .add(calcLocalEffects(intersection));
    }

    /**
     * Computes the local shading contribution: emission plus the summed
     * diffuse and specular terms for every light source in the scene.
     *
     * @param intersection the pre-processed intersection record
     * @return the local color contribution
     */
    private Color calcLocalEffects(Intersection intersection) {
        Color color = intersection.geometry.getEmission();
        for (LightSource lightSource : _scene.lights) {
            if (preprocessLightSource(intersection, lightSource)) {
                color = color.add(
                        lightSource.getIntensity(intersection.point)
                                .scale(calcDiffuse(intersection)
                                        .add(calcSpecular(intersection))));
            }
        }
        return color;
    }

    /**
     * Computes the diffuse term: {@code kD · |l·n|}.
     *
     * @param intersection the pre-processed intersection record
     * @return the diffuse coefficient scaled by |l·n|
     */
    private Double3 calcDiffuse(Intersection intersection) {
        return intersection.material.kD.scale(Math.abs(intersection.lNormal));
    }

    /**
     * Computes the specular term: {@code kS · max(0, −v·r)^nSh},
     * where {@code r = l − 2(l·n)n} is the reflection of {@code l}.
     *
     * @param intersection the pre-processed intersection record
     * @return the specular coefficient scaled by the highlight factor
     */
    private Double3 calcSpecular(Intersection intersection) {
        Vector r = intersection.l.subtract(
                intersection.normal.scale(2 * intersection.lNormal));
        double minusVR = alignZero(-intersection.v.dotProduct(r));
        return minusVR <= 0 ? Double3.ZERO
                : intersection.material.kS.scale(
                        Math.pow(minusVR, intersection.material.nShininess));
    }

    @Override
    Color traceRay(Ray ray) {
        var intersections = _scene.geometries.calcIntersections(ray);
        var closestIntersection = ray.findClosestIntersection(intersections);
        return closestIntersection == null ? _scene.background
                : calcColor(closestIntersection, ray.direction());
    }
}
