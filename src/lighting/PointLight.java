package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * An omnidirectional point light source with quadratic distance attenuation.
 * <p>
 * The intensity at distance {@code d} is:
 * {@code I₀ / (kC + kL·d + kQ·d²)}.
 * Default coefficients: {@code kC=1, kL=0, kQ=0} (no attenuation).
 * </p>
 *
 * @author Amichai Mukades
 */
public class PointLight extends Light implements LightSource {

    /**
     * The position of this light source in 3D space.
     */
    protected final Point _position;

    /**
     * Constant attenuation coefficient.
     */
    private double _kC = 1;

    /**
     * Linear attenuation coefficient.
     */
    private double _kL = 0;

    /**
     * Quadratic attenuation coefficient.
     */
    private double _kQ = 0;

    /**
     * Constructs a point light with the given intensity at the given position.
     *
     * @param  intensity the emission color (I₀)
     * @param  position  the position of the light source
     */
    public PointLight(Color intensity, Point position) {
        super(intensity);
        _position = position;
    }

    /**
     * Sets the constant attenuation coefficient.
     *
     * @param  kC the constant attenuation factor (≥ 1 to avoid division by zero)
     * @return    this light (for chaining)
     */
    public PointLight setKc(double kC) {
        _kC = kC;
        return this;
    }

    /**
     * Sets the linear attenuation coefficient.
     *
     * @param  kL the linear attenuation factor
     * @return    this light (for chaining)
     */
    public PointLight setKl(double kL) {
        _kL = kL;
        return this;
    }

    /**
     * Sets the quadratic attenuation coefficient.
     *
     * @param  kQ the quadratic attenuation factor
     * @return    this light (for chaining)
     */
    public PointLight setKq(double kQ) {
        _kQ = kQ;
        return this;
    }

    @Override
    public Vector getL(Point p) {
        return p.subtract(_position).normalize();
    }

    @Override
    public Color getIntensity(Point p) {
        double d = _position.distance(p);
        return _intensity.scale(1d / (_kC + _kL * d + _kQ * d * d));
    }

    @Override
    public double getDistance(Point point) {
        return _position.distance(point);
    }
}
