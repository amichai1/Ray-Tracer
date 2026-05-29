package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

import static primitives.Util.alignZero;

/**
 * A directed point light source (spotlight) with a cone beam.
 * <p>
 * The intensity at a surface point is the PointLight intensity multiplied by
 * {@code max(0, dir·l)}, where {@code l} is the direction to the surface point.
 * Points outside the forward half-space receive no illumination.
 * </p>
 *
 * @author Amichai Mukades
 */
public final class SpotLight extends PointLight {

    /**
     * The normalized direction the spotlight points toward.
     */
    private final Vector _direction;

    /**
     * Constructs a spotlight at the given position pointing in the given direction.
     *
     * @param  intensity  the emission color (I₀)
     * @param  position   the position of the spotlight
     * @param  direction  the beam direction (normalized internally)
     */
    public SpotLight(Color intensity, Point position, Vector direction) {
        super(intensity, position);
        _direction = direction.normalize();
    }

    @Override
    public SpotLight setKc(double kC) {
        return (SpotLight) super.setKc(kC);
    }

    @Override
    public SpotLight setKl(double kL) {
        return (SpotLight) super.setKl(kL);
    }

    @Override
    public SpotLight setKq(double kQ) {
        return (SpotLight) super.setKq(kQ);
    }

    @Override
    public Vector getL(Point p) {
        return super.getL(p);
    }

    @Override
    public Color getIntensity(Point p) {
        double beamFactor = alignZero(_direction.dotProduct(getL(p)));
        if( beamFactor <= 0 ){
            return Color.BLACK;
        }
        return  super.getIntensity(p).scale(beamFactor);
    }
}
