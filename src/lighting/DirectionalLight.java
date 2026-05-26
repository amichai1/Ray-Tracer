package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * An infinitely distant directional light source.
 * <p>
 * The light travels in a fixed direction with no distance attenuation;
 * intensity is constant everywhere.
 * </p>
 *
 * @author Amichai Mukades
 */
public final class DirectionalLight extends Light implements LightSource {

    /**
     * The normalized direction the light travels (toward the scene).
     */
    private final Vector _direction;

    /**
     * Constructs a directional light with the given intensity and direction.
     *
     * @param  intensity the emission color (I₀)
     * @param  direction the light direction vector (normalized internally)
     */
    public DirectionalLight(Color intensity, Vector direction) {
        super(intensity);
        _direction = direction.normalize();
    }

    @Override
    public Vector getL(Point p) {
        return _direction;
    }

    @Override
    public Color getIntensity(Point p) {
        return getIntensity();
    }
}
