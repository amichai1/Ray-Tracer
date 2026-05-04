package renderer;

import primitives.Color;
import primitives.Point;
import primitives.Ray;
import scene.Scene;

/**
 * A basic ray tracer that shades intersected surfaces with ambient light only.
 * <p>
 * Rays that hit no geometry return the scene background color.
 * Rays that hit geometry return the scene's ambient light intensity.
 * </p>
 *
 * @author Amichai Mukades
 */
final class SimpleRayTracer extends RayTracerBase {

   /**
    * Constructs a simple ray tracer for the given scene.
    *
    * @param  scene the scene to trace rays in
    */
   SimpleRayTracer(Scene scene) {
      super(scene);
   }

   /**
    * Computes the color at the given intersection point.
    * <p>
    * In this implementation, returns the scene's ambient light intensity.
    * </p>
    *
    * @param  intersection the intersection point on a geometry surface
    * @return              the color at that point
    */
   private Color calcColor(Point intersection) {
      return _scene.ambientLight.getIntensity();
   }

   @Override
   Color traceRay(Ray ray) {
      var intersections = _scene.geometries.findIntersections(ray);
      return intersections == null ? _scene.background : calcColor(intersections.getFirst());
   }
}
