package lighting;

import primitives.Color;

/**
 * Represents ambient light in a scene.
 * <p>
 * Ambient light is a non-directional, background illumination applied uniformly
 * to all surfaces regardless of their orientation or position in the scene.
 * The effective intensity is inherited from {@link Light}.
 * </p>
 *
 * @author Amichai Mukades
 */
public final class AmbientLight extends Light {

   /**
    * Pre-built ambient light with zero intensity (black — no ambient contribution).
    */
   public static final AmbientLight NONE = new AmbientLight(Color.BLACK);

   /**
    * Constructs an ambient light from the given color intensity.
    *
    * @param  color the RGB intensity of the ambient light
    */
   public AmbientLight(Color color) {
      super(color);
   }
}
