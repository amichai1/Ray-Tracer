package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * Interface for external light sources that contribute to local illumination.
 * <p>
 * Implementations supply the direction-to-light vector and the attenuated
 * intensity at an arbitrary surface point.
 * </p>
 *
 * @author Amichai Mukades
 */
public interface LightSource {

    /**
     * Returns the unit vector from the given surface point toward this light.
     *
     * @param  p the surface point
     * @return   the normalized direction-to-light vector
     */
    Vector getL(Point p);

    /**
     * Returns the effective light intensity (after attenuation) at the given
     * surface point.
     *
     * @param  p the surface point
     * @return   the attenuated intensity color at {@code p}
     */
    Color getIntensity(Point p);

    /**
     * Returns the distance from the given surface point to this light source.
     *
     * @param  point the surface point
     * @return       the distance to the light source
     */
    double getDistance(Point point);
}
