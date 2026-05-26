package lighting;

import primitives.Color;

/**
 * Abstract base class for all light sources.
 * <p>
 * Holds the raw emission intensity {@code _intensity} (I₀).
 * Subclasses apply attenuation and direction logic on top.
 * </p>
 *
 * @author Amichai Mukades
 */
abstract class Light {

    /**
     * The raw emission intensity of this light source.
     */
    protected final Color _intensity;

    /**
     * Constructs a light with the given intensity.
     *
     * @param  intensity the raw RGB emission color (I₀)
     */
    protected Light(Color intensity) {
        _intensity = intensity;
    }

    /**
     * Returns the raw emission intensity of this light (I₀, no attenuation).
     *
     * @return the intensity color
     */
    public Color getIntensity() {
        return _intensity;
    }
}
