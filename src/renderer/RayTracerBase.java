package renderer;

import static primitives.Util.alignZero;

import geometries.api.Intersectable.Intersection;
import lighting.LightSource;
import primitives.Color;
import primitives.Ray;
import primitives.Vector;
import scene.Scene;

/**
 * Abstract base class for all ray-tracing strategies.
 * <p>
 * Each concrete subclass implements a specific ray-tracing algorithm
 * by overriding {@link #traceRay(Ray)}.
 * </p>
 *
 * @author Amichai Mukades
 */
abstract class RayTracerBase {

   /**
    * The scene being rendered.
    */
   protected final Scene _scene;

   /**
    * Constructs a ray tracer for the given scene.
    *
    * @param  scene the scene to trace rays in
    */
   RayTracerBase(Scene scene) {
      _scene = scene;
   }

   /**
    * Traces a single ray through the scene and returns the resulting color.
    *
    * @param  ray the ray to trace
    * @return     the color seen along the ray
    */
   abstract Color traceRay(Ray ray);

   /**
    * Caches the view direction, surface normal, and their dot product on the
    * intersection record.
    *
    * @param  intersection the intersection to populate
    * @param  v            the normalized view direction vector (from camera)
    * @return              {@code true} if the ray is not tangent to the surface
    *                      ({@code v·n ≠ 0}); {@code false} otherwise
    */
   protected boolean preprocessIntersection(Intersection intersection, Vector v) {
      intersection.v = v;
      intersection.normal = intersection.geometry.getNormal(intersection.point);
      intersection.vNormal = alignZero(intersection.v.dotProduct(intersection.normal));
      return intersection.vNormal != 0;
   }

   /**
    * Caches the light source, direction-to-light, and their dot product with the
    * normal on the intersection record.
    *
    * @param  intersection the intersection to populate (must already have {@code normal})
    * @param  light        the light source to evaluate
    * @return              {@code true} if the light and eye are on the same side of
    *                      the surface ({@code l·n} and {@code v·n} have the same sign)
    */
   protected boolean preprocessLightSource(Intersection intersection, LightSource light) {
      intersection.light = light;
      intersection.l = light.getL(intersection.point);
      intersection.lNormal = alignZero(intersection.l.dotProduct(intersection.normal));
      return intersection.lNormal * intersection.vNormal > 0;
   }
}
