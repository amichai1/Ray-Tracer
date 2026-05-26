package renderer;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import lighting.DirectionalLight;
import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * Unit tests for {@link DirectionalLight}.
 *
 * @author Amichai Mukades
 */
class DirectionalLightTests {

    /** Default constructor to satisfy JavaDoc generator */
    DirectionalLightTests() { /* to satisfy JavaDoc generator */ }

    /** The raw intensity used across tests */
    private static final Color  INTENSITY  = new Color(400, 300, 200);
    /** The direction supplied to the light (unnormalized) */
    private static final Vector DIRECTION  = new Vector(1, 1, 1);
    /** The expected normalized direction */
    private static final Vector NORMALIZED = DIRECTION.normalize();
    /** An arbitrary surface point – directional light should not depend on it */
    private static final Point  ANY_POINT  = new Point(5, -3, 10);

    /** EP01 – getL returns the same normalized direction regardless of point */
    @Test
    void testGetL() {
        DirectionalLight light = new DirectionalLight(INTENSITY, DIRECTION);
        assertEquals(NORMALIZED, light.getL(ANY_POINT),
                "getL should return the normalized light direction");
        assertEquals(NORMALIZED, light.getL(Point.ZERO),
                "getL must be constant for all surface points");
    }

    /** EP01 – getIntensity(p) returns I₀ unchanged for any point */
    @Test
    void testGetIntensity() {
        DirectionalLight light = new DirectionalLight(INTENSITY, DIRECTION);
        assertEquals(INTENSITY, light.getIntensity(ANY_POINT),
                "getIntensity should return I₀ unchanged (no attenuation)");
        assertEquals(INTENSITY, light.getIntensity(Point.ZERO),
                "getIntensity must be constant for all surface points");
    }
}
