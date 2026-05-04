package renderer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import geometries.api.Intersectable;
import geometries.impl.Plane;
import geometries.impl.Sphere;
import geometries.impl.Triangle;
import primitives.Point;
import primitives.Vector;

/**
 * Integration tests for {@link Camera} ray construction combined with
 * ray–geometry intersection calculations.
 * <p>
 * Each test method places a camera at the origin facing the negative Z axis
 * with a 3×3 view plane (size 3×3, distance 1) and verifies that the total
 * number of intersections produced by all nine constructed rays matches the
 * expected count for each geometric scenario.
 * </p>
 *
 * @author Amichai Mukades
 */
class CameraIntersectionIntegration {

    /** Default constructor to satisfy documentation tools. */
    CameraIntersectionIntegration() {}

    /** Resolution used for all integration-test cameras. */
    private static final int RESOLUTION = 3;

    /** View-plane size used for all integration-test cameras. */
    private static final double VP_SIZE = 3d;

    /** View-plane distance used for all integration-test cameras. */
    private static final double VP_DISTANCE = 1d;

    /**
     * Camera at the origin looking toward −Z with a 3×3 view plane.
     * Used for all integration scenarios.
     */
    private static final Camera CAMERA = Camera.getBuilder()
            .setLocation(Point.ZERO)
            .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
            .setVpSize(VP_SIZE, VP_SIZE)
            .setVpDistance(VP_DISTANCE)
            .setResolution(RESOLUTION, RESOLUTION)
            .build();

    /**
     * Counts the total number of intersection points produced by rays through
     * all pixels of {@code camera}'s view plane against {@code body}, and
     * asserts equality with {@code expected}.
     *
     * @param  camera    the camera whose rays are tested
     * @param  body      the intersectable geometry
     * @param  expected  the expected total number of intersection points
     * @param  testName  label used in the failure message
     */
    private void assertIntersectionsCount(Camera camera, Intersectable body,
                                          int expected, String testName) {
        int count = 0;
        for (int i = 0; i < RESOLUTION; i++)
            for (int j = 0; j < RESOLUTION; j++) {
                var intersections = body.findIntersections(camera.constructRay(j, i));
                count += intersections == null ? 0 : intersections.size();
            }
        assertEquals(expected, count, testName);
    }

    /**
     * Integration tests for camera rays intersecting with spheres.
     * <p>
     * Camera is at origin, facing −Z, with a 3×3 view plane at distance 1.
     * Five scenarios cover: small sphere (center ray only), large sphere (all 9 rays,
     * 2 intersections each), medium sphere (5 rays hit), camera-inside sphere, and
     * sphere behind the camera.
     * </p>
     */
    @Test
    void testCameraRaySphereIntegration() {
        // TC01: Small sphere directly in front – only the center ray hits it twice
        assertIntersectionsCount(CAMERA,
                new Sphere(new Point(0, 0, -3), 1),
                2, "TC01: Small sphere – center ray only");

        // TC02: Large sphere far enough that the camera is outside it – all 9 rays
        //       enter and exit (18 intersections total)
        assertIntersectionsCount(CAMERA,
                new Sphere(new Point(0, 0, -3), 2.5),
                18, "TC02: Large sphere – all 9 rays hit twice");

        // TC03: Medium sphere – only center ray and the four axis-aligned side rays
        //       hit (5 rays × 2 = 10 intersections); corner rays miss
        assertIntersectionsCount(CAMERA,
                new Sphere(new Point(0, 0, -2), 1.5),
                10, "TC03: Medium sphere – 5 rays hit twice");

        // TC04: Very large sphere – camera is inside; every ray exits once (9 total)
        assertIntersectionsCount(CAMERA,
                new Sphere(new Point(0, 0, -1), 4),
                9, "TC04: Camera inside sphere – 9 rays hit once");

        // TC05: Sphere behind the camera – no ray reaches it
        assertIntersectionsCount(CAMERA,
                new Sphere(new Point(0, 0, 1), 0.5),
                0, "TC05: Sphere behind camera – 0 intersections");
    }

    /**
     * Integration tests for camera rays intersecting with planes.
     * <p>
     * Four scenarios: perpendicular plane (all 9 rays), slightly tilted plane
     * (all 9 rays), steeply tilted plane where the top row is parallel to the
     * plane (6 intersections), and a plane located behind the camera (0 intersections).
     * </p>
     */
    @Test
    void testCameraRayPlaneIntegration() {
        // TC01: Plane perpendicular to the viewing direction – all 9 rays intersect
        assertIntersectionsCount(CAMERA,
                new Plane(new Point(0, 0, -5), new Vector(0, 0, 1)),
                9, "TC01: Perpendicular plane – 9 intersections");

        // TC02: Slightly tilted plane – all 9 rays still intersect
        assertIntersectionsCount(CAMERA,
                new Plane(new Point(0, 0, -5), new Vector(0, 1, 9)),
                9, "TC02: Slightly tilted plane – 9 intersections");

        // TC03: Steeply tilted plane (normal (0,1,1)) – the entire top row of rays
        //       is parallel to the plane and produces no intersections (6 total)
        assertIntersectionsCount(CAMERA,
                new Plane(new Point(0, 0, -5), new Vector(0, 1, 1)),
                6, "TC03: Steeply tilted plane – 6 intersections");

        // BV01: Plane behind the camera – all rays travel in the −Z direction and
        //       can never reach z=1, so no intersections occur
        assertIntersectionsCount(CAMERA,
                new Plane(new Point(0, 0, 1), new Vector(0, 0, 1)),
                0, "BV01: Plane behind camera – 0 intersections");
    }

    /**
     * Integration tests for camera rays intersecting with triangles.
     * <p>
     * Three scenarios: a small triangle hit only by the center ray, a taller
     * triangle also hit by the top-middle ray, and a triangle placed entirely
     * outside the field of view (0 intersections).
     * </p>
     */
    @Test
    void testCameraRayTriangleIntegration() {
        // TC01: Small triangle – only the center ray intersects it
        assertIntersectionsCount(CAMERA,
                new Triangle(new Point(0, 1, -2),
                             new Point(-1, -0.5, -2),
                             new Point(1, -0.5, -2)),
                1, "TC01: Small triangle – center ray only");

        // TC02: Tall triangle – center ray and top-middle ray both intersect it
        assertIntersectionsCount(CAMERA,
                new Triangle(new Point(0, 20, -2),
                             new Point(-1, -0.5, -2),
                             new Point(1, -0.5, -2)),
                2, "TC02: Tall triangle – center + top-middle rays");

        // BV01: Triangle completely outside the field of view – no ray reaches it
        assertIntersectionsCount(CAMERA,
                new Triangle(new Point(10, 10, -2),
                             new Point(11, 10, -2),
                             new Point(10, 11, -2)),
                0, "BV01: Triangle outside field of view – 0 intersections");
    }
}
