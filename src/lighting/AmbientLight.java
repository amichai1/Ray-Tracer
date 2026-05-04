package lighting;

import primitives.Color;

/**
 * Represents ambient light in a scene.
 * <p>
 * Ambient light is a non-directional, background illumination applied uniformly
 * to all surfaces regardless of their orientation or position in the scene.
 * The effective intensity is stored as a {@link Color} value.
 * </p>
 *
 * @author Amichai Mukades
 */
public final class AmbientLight {

   /**
    * Pre-built ambient light with zero intensity (black — no ambient contribution).
    */
   public static final AmbientLight NONE = new AmbientLight(Color.BLACK);

   /**
    * The effective RGB intensity of this ambient light source.
    */
   private final Color _intensity;

   /**
    * Constructs an ambient light from the given color intensity.
    *
    * @param  color the RGB intensity of the ambient light
    */
   public AmbientLight(Color color) {
      _intensity = color;
   }

   /**
    * Returns the effective RGB intensity of this ambient light.
    *
    * @return the intensity color
    */
   public Color getIntensity() {
      return _intensity;
   }
}
