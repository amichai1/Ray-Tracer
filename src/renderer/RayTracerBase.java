package renderer;

import primitives.Color;
import primitives.Ray;
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
}
