package renderer;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import lighting.PointLight;
import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * Unit tests for {@link PointLight}.
 *
 * @author Amichai Mukades
 */
class PointLightTests {

    /** Default constructor to satisfy JavaDoc generator */
    PointLightTests() { /* to satisfy JavaDoc generator */ }

    /** Raw intensity */
    private static final Color INTENSITY = new Color(400, 300, 200);
    /** Light position */
    private static final Point POSITION  = new Point(0, 0, 0);

    /** EP01 – getL returns the normalized direction from position to point */
    @Test
    void testGetL() {
        PointLight light = new PointLight(INTENSITY, POSITION);
        Point p = new Point(3, 4, 0);                       // distance = 5
        Vector expected = new Vector(3, 4, 0).normalize();
        assertEquals(expected, light.getL(p),
                "getL should return normalized (p - position)");
    }

    /** BV01 – getL at the light position itself throws IllegalArgumentException */
    @Test
    void testGetLAtPosition() {
        PointLight light = new PointLight(INTENSITY, POSITION);
        assertThrows(IllegalArgumentException.class,
                () -> light.getL(POSITION),
                "getL at the light position should throw (zero vector)");
    }

    /** EP01 – getIntensity at known distance d=5, default kC=1, kL=kQ=0 → I₀ */
    @Test
    void testGetIntensity() {
        PointLight light = new PointLight(INTENSITY, POSITION);
        Point p = new Point(3, 4, 0);   // d = 5
        // default: kC=1, kL=0, kQ=0  → denominator=1 → result = I₀
        assertEquals(INTENSITY, light.getIntensity(p),
                "Default attenuation (kC=1,kL=0,kQ=0) should return I₀");
    }

    /** EP02 – getIntensity with non-trivial attenuation coefficients */
    @Test
    @SuppressWarnings("java:S109")
    void testGetIntensityWithAttenuation() {
        PointLight light = new PointLight(INTENSITY, POSITION)
                .setKl(0.1).setKq(0.01);
        Point p = new Point(0, 0, 10);  // d = 10
        // denominator = 1 + 0.1*10 + 0.01*100 = 1 + 1 + 1 = 3
        Color expected = INTENSITY.scale(1.0 / 3.0);
        assertEquals(expected, light.getIntensity(p),
                "Intensity should be I₀ / (kC + kL·d + kQ·d²)");
    }

    /** BV01 – getIntensity at the light position itself: d=0, denominator=kC=1 → I₀ */
    @Test
    void testGetIntensityAtPosition() {
        PointLight light = new PointLight(INTENSITY, POSITION);
        assertEquals(INTENSITY, light.getIntensity(POSITION),
                "At distance 0 with default kC=1, intensity should equal I₀");
    }
}
