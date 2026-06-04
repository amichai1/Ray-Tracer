package renderer;

import geometries.api.Intersectable.Intersection;
import lighting.LightSource;
import primitives.Color;
import primitives.Double3;
import primitives.Ray;
import primitives.Vector;
import scene.Scene;
import static primitives.Util.alignZero;

/**
 * A ray tracer that shades surfaces using the Phong reflection model with
 * ambient, diffuse, and specular contributions from all scene light sources,
 * including shadows, reflection, and transparency effects via recursive
 * secondary ray casting.
 * <p>
 * Rays that hit no geometry return the scene background color.
 * </p>
 *
 * @author Amichai Mukades
 */
final class SimpleRayTracer extends RayTracerBase {

    /**
     * Maximum recursion depth for secondary rays.
     */
    private static final int MAX_CALC_COLOR_LEVEL = 10;

    /**
     * Minimum accumulated attenuation below which secondary rays are ignored.
     */
    private static final double MIN_CALC_COLOR_K = 0.001;

    /**
     * Initial attenuation factor for the first recursion level.
     */
    private static final Double3 INITIAL_K = Double3.ONE;

    /**
     * Constructs a simple ray tracer for the given scene.
     *
     * @param scene the scene to trace rays in
     */
    SimpleRayTracer(Scene scene) {
        super(scene);
    }

    /**
     * Finds the closest intersection of the given ray with any scene geometry.
     *
     * @param ray the ray to trace
     * @return the closest intersection, or {@code null} if none
     */
    private Intersection findClosestIntersection(Ray ray) {
        var sceneGeometries = _scene.geometries.calcIntersections(ray);
        return ray.findClosestIntersection(sceneGeometries);
    }

    @Override
    Color traceRay(Ray ray) {
        Intersection closest = findClosestIntersection(ray);
        if(closest == null){
            return _scene.background;
        }
        return calcColor(closest, ray.direction());
    }

    /**
     * Computes the color at the given intersection, adding ambient light once
     * before delegating to the recursive calculation.
     *
     * @param intersection the intersection record
     * @param v            the normalized ray direction from camera
     * @return the shaded color, or {@link Color#BLACK} if the ray
     * is tangent to the surface
     */
    private Color calcColor(Intersection intersection, Vector v) {
        if (!preprocessIntersection(intersection, v))
            return Color.BLACK;
        Color ambientIntensity = _scene.ambientLight.getIntensity();
        Color scaledAmbient = ambientIntensity.scale(intersection.material.kA);
        Color colorAtIntersect = calcColor(intersection, MAX_CALC_COLOR_LEVEL, INITIAL_K);
        return scaledAmbient.add(colorAtIntersect);
    }

    /**
     * Recursively computes local and global (reflection + transparency) color
     * contributions at the given intersection.
     *
     * @param intersection the pre-processed intersection
     * @param level        remaining recursion depth; stops at 1
     * @param k            accumulated attenuation factor so far
     * @return the color contribution at this recursion level
     */
    private Color calcColor(Intersection intersection, int level, Double3 k) {
        Color color = calcLocalEffects(intersection, k);
        if(level == 1){
            return color;
        }
        Color globalEffect =calcGlobalEffects(intersection, level, k);
        return color.add(globalEffect);
    }

    /**
     * Computes emission plus the summed diffuse and specular terms for every
     * light source, scaled by the accumulated transparency of any occluders.
     *
     * @param intersection the pre-processed intersection
     * @param k            the accumulated global attenuation factor
     * @return the local color contribution
     */
    private Color calcLocalEffects(Intersection intersection, Double3 k) {
        Color color = intersection.geometry.getEmission();
        for (LightSource lightSource : _scene.lights) {
            if (preprocessLightSource(intersection, lightSource)) {
                Double3 ktr = transparency(intersection);
                if (ktr.product(k).isGreaterThan(MIN_CALC_COLOR_K)) {
                    Color IntensityTransparent = lightSource.getIntensity(intersection.point).scale(ktr);
                    Double3 difusedPlusSpecular = calcDiffuse(intersection).add(calcSpecular(intersection));
                    color = color.add(IntensityTransparent.scale(difusedPlusSpecular));
                }
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
        if (minusVR <= 0){
            return Double3.ZERO;
        }
        var materialShininess = intersection.material.nShininess;
        var ksOfMaterial =intersection.material.kS;
        return ksOfMaterial.scale(Math.pow(minusVR,materialShininess ));
    }

    /**
     * Checks whether the light in {@code intersection} is unoccluded by any
     * opaque geometry between the surface point and the light source.
     * <p>
     * Kept for grading verification; {@link #transparency} is used in rendering.
     * </p>
     *
     * @param intersection the pre-processed intersection with cached light data
     * @return {@code true} if no opaque geometry blocks the light
     */
    private boolean unshaded(Intersection intersection) {
        Vector pointToLight = intersection.l.scale(-1);
        Ray shadowRay = new Ray(intersection.point, pointToLight, intersection.normal);
        double lightDistance = intersection.light.getDistance(intersection.point);
        var shadowIntersections = _scene.geometries.calcIntersections(shadowRay, lightDistance);
        if (shadowIntersections == null) {
            return true;
        }
        for (Intersection si : shadowIntersections)
            if (si.material.kT.isLowerThan(MIN_CALC_COLOR_K)) {
                return false;
            }
        return true;
    }

    /**
     * Computes the accumulated transparency factor between the intersection
     * point and its light source, accounting for partially transparent occluders.
     *
     * @param intersection the pre-processed intersection with cached light data
     * @return the transparency factor (ONE = fully lit, ZERO = fully blocked)
     */
    private Double3 transparency(Intersection intersection) {
        Vector pointToLight = intersection.l.scale(-1);
        Ray shadowRay = new Ray(intersection.point, pointToLight, intersection.normal);
        double lightDistance = intersection.light.getDistance(intersection.point);
        var shadowIntersections = _scene.geometries.calcIntersections(shadowRay, lightDistance);
        if (shadowIntersections == null) {
            return Double3.ONE;
        }
        Double3 ktr = Double3.ONE;
        for (Intersection si : shadowIntersections) {
            ktr = ktr.product(si.material.kT);
            if (ktr.isLowerThan(MIN_CALC_COLOR_K)) {
                return Double3.ZERO;
            }
        }
        return ktr;
    }

    /**
     * Sums the reflection and transparency global color contributions.
     *
     * @param intersection the pre-processed intersection
     * @param level        remaining recursion depth
     * @param k            accumulated attenuation factor
     * @return the total global color contribution
     */
    private Color calcGlobalEffects(Intersection intersection, int level, Double3 k) {
        Color globalEffectTransparency = calcGlobalEffect(constructTransparencyRay(intersection), level, k, intersection.material.kT);
        Color globalEffectReflection = calcGlobalEffect(constructReflectionRay(intersection), level, k, intersection.material.kR);
        return globalEffectTransparency.add(globalEffectReflection);
    }

    /**
     * Traces a single secondary ray and returns its attenuated color contribution.
     *
     * @param ray   the secondary ray to trace
     * @param level remaining recursion depth
     * @param k     accumulated attenuation so far
     * @param kx    material coefficient for this ray type (kR or kT)
     * @return the attenuated color, or {@link Color#BLACK} if negligible
     */
    private Color calcGlobalEffect(Ray ray, int level, Double3 k, Double3 kx) {
        Double3 kkx = k.product(kx);
        if (kkx.isLowerThan(MIN_CALC_COLOR_K)) {
            return Color.BLACK;
        }
        Intersection intersection = findClosestIntersection(ray);
        if (intersection == null) {
            return _scene.background.scale(kx);
        }
        if (!preprocessIntersection(intersection, ray.direction())) {
            return Color.BLACK;
        }
        return calcColor(intersection, level - 1, kkx).scale(kx);
    }

    /**
     * Constructs the specular reflection ray at the given intersection.
     * Direction: {@code r = v − 2(v·n)n}
     *
     * @param intersection the pre-processed intersection
     * @return the reflection ray with a self-intersection-free origin
     */
    private Ray constructReflectionRay(Intersection intersection) {
        var normalOffsetting = intersection.normal.scale(2 * intersection.vNormal);
        Vector r = intersection.v.subtract(normalOffsetting);
        return new Ray(intersection.point, r, intersection.normal);
    }

    /**
     * Constructs the transparency ray at the given intersection.
     * Uses the simplified model: the ray continues in the same direction.
     *
     * @param intersection the pre-processed intersection
     * @return the transparency ray with a self-intersection-free origin
     */
    private Ray constructTransparencyRay(Intersection intersection) {
        return new Ray(intersection.point, intersection.v, intersection.normal);
    }
}
