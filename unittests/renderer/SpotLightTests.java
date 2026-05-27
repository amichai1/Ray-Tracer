package renderer;

import lighting.SpotLight;
import org.junit.jupiter.api.Test;
import primitives.Color;
import primitives.Point;
import primitives.Vector;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link SpotLight}.
 *
 * @author Amichai Mukades
 */
class SpotLightTests {

    /**
     * Default constructor to satisfy JavaDoc generator
     */
    SpotLightTests() { /* to satisfy JavaDoc generator */ }

    /**
     * Raw intensity
     */
    private static final Color INTENSITY = new Color(400, 300, 200);
    /**
     * Light position at origin
     */
    private static final Point POSITION = new Point(0, 0, 0);
    /**
     * Spotlight points in the +Z direction
     */
    private static final Vector DIRECTION = new Vector(0, 0, 1);

    /**
     * EP01 – getL returns normalized direction from position to point
     */
    @Test
    void testGetL() {
        SpotLight light = new SpotLight(INTENSITY, POSITION, DIRECTION);
        Point p = new Point(3, 4, 0);
        Vector expected = new Vector(3, 4, 0).normalize();
        assertEquals(expected, light.getL(p),
                "getL should delegate to PointLight: normalized (p - position)");
    }

    /**
     * BV01 – getL at the light position throws IllegalArgumentException
     */
    @Test
    void testGetLAtPosition() {
        SpotLight light = new SpotLight(INTENSITY, POSITION, DIRECTION);
        assertThrows(IllegalArgumentException.class,
                () -> light.getL(POSITION),
                "getL at the light position should throw (zero vector)");
    }

    /**
     * EP01 – point in front of spotlight (dir·l > 0) → attenuated intensity
     */
    @Test
    void testGetIntensityInFront() {
        SpotLight light = new SpotLight(INTENSITY, POSITION, DIRECTION);
        // p is directly in front: l = (0,0,1), dir = (0,0,1), dir·l = 1
        Point p = new Point(0, 0, 10);
        assertEquals(INTENSITY, light.getIntensity(p),
                "Point directly in front: beam factor=1, default no attenuation → I₀");
    }

    /**
     * EP02 – point behind spotlight (dir·l ≤ 0) → Color.BLACK
     */
    @Test
    void testGetIntensityBehind() {
        SpotLight light = new SpotLight(INTENSITY, POSITION, DIRECTION);
        // p is behind the spotlight: l = normalize((0,0,-5)) = (0,0,-1)
        // dir=(0,0,1), dir·l = -1 ≤ 0
        Point p = new Point(0, 0, -5);
        assertEquals(Color.BLACK, light.getIntensity(p),
                "Point behind spotlight should receive no light");
    }

    /**
     * BV01 – point coincides with light position: d=0, denominator=kC=1 → I₀ * beamFactor
     */
    @Test
    void testGetIntensityAtPosition() {
        SpotLight light = new SpotLight(INTENSITY, POSITION, DIRECTION);
        // getL(POSITION) throws, so this tests getIntensity at same location.
        // The PointLight denominator is kC=1, but getL(POSITION) throws internally.
        // Result is expected to throw since getL is called inside getIntensity.
        assertThrows(IllegalArgumentException.class,
                () -> light.getIntensity(POSITION),
                "getIntensity at the light position should throw via getL");
    }

    /**
     * BV02 – point at 90° to spotlight direction → Color.BLACK (dir·l = 0)
     */
    @Test
    @SuppressWarnings("java:S109")
    void testGetIntensityPerpendicular() {
        SpotLight light = new SpotLight(INTENSITY, POSITION, DIRECTION);
        // dir=(0,0,1), l = normalize((1,0,0)) = (1,0,0), dir·l = 0
        Point p = new Point(5, 0, 0);
        assertEquals(Color.BLACK, light.getIntensity(p),
                "Point at 90° to spotlight direction should receive no light");
    }
}
