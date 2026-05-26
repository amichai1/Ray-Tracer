package renderer;

import static java.awt.Color.BLUE;

import org.junit.jupiter.api.Test;

import geometries.impl.Sphere;
import geometries.impl.Triangle;
import lighting.AmbientLight;
import lighting.DirectionalLight;
import lighting.PointLight;
import lighting.SpotLight;
import primitives.Color;
import primitives.Double3;
import primitives.Material;
import primitives.Point;
import primitives.Vector;
import scene.Scene;

/**
 * Integration tests for multiple simultaneous light sources using the Phong model.
 * <p>
 * Each test creates its own scene and geometry (non-static) to avoid shared state
 * between test executions.
 * </p>
 *
 * @author Amichai Mukades
 */
@SuppressWarnings("java:S109")
class MultiLightTests {

    /** Default constructor to satisfy JavaDoc generator */
    MultiLightTests() { /* to satisfy JavaDoc generator */ }

    /**
     * Renders two triangles illuminated simultaneously by a directional light
     * (blue), a point light (red), and a spotlight (green).
     */
    @Test
    void testTrianglesMultiLights() {
        Scene scene = new Scene("Multi-light triangles test")
                .setAmbientLight(new AmbientLight(new Color(30, 30, 30)));

        Material mat = new Material()
                .setKD(new Double3(0.4, 0.4, 0.4))
                .setKS(new Double3(0.3, 0.3, 0.3))
                .setShininess(100);

        Point v0 = new Point(-110, -110, -150);
        Point v1 = new Point(95,   100,  -150);
        Point v2 = new Point(110,  -110, -150);
        Point v3 = new Point(-75,  78,   100);

        scene.geometries.add(
                new Triangle(v0, v1, v2).setMaterial(mat),
                new Triangle(v0, v1, v3).setMaterial(mat));

        // Blue directional light from above-left
        scene.lights.add(new DirectionalLight(new Color(0, 0, 600), new Vector(-1, -1, -2)));
        // Red point light near the surface
        scene.lights.add(new PointLight(new Color(600, 0, 0), new Point(60, 50, -50))
                .setKl(0.0005).setKq(0.00005));
        // Green spotlight aimed at the center of the triangles
        scene.lights.add(new SpotLight(new Color(0, 600, 0), new Point(-30, 50, 80),
                new Vector(1, -1, -3))
                .setKl(0.0002).setKq(0.00002));

        Camera.getBuilder()
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .setLocation(new Point(0, 0, 1000))
                .setDirection(Point.ZERO, Vector.AXIS_Y)
                .setVpSize(200, 200).setVpDistance(1000)
                .setResolution(500, 500)
                .build()
                .renderImage()
                .writeToImage("multiLightTriangles");
    }

    /**
     * Renders a sphere illuminated simultaneously by a directional light (blue),
     * a point light (red), and a spotlight (green).
     */
    @Test
    void testSphereMultiLights() {
        Scene scene = new Scene("Multi-light sphere test");

        Material mat = new Material()
                .setKD(0.5)
                .setKS(0.5)
                .setShininess(300);

        scene.geometries.add(
                new Sphere(new Point(0, 0, -50), 50d)
                        .setEmission(new Color(BLUE).reduce(2))
                        .setMaterial(mat));

        // Blue directional light from the right
        scene.lights.add(new DirectionalLight(new Color(0, 100, 600), new Vector(1, 0, -1)));
        // Red point light to the lower-left
        scene.lights.add(new PointLight(new Color(600, 100, 0), new Point(-70, -70, 30))
                .setKl(0.001).setKq(0.0002));
        // Green spotlight aimed at the sphere from above
        scene.lights.add(new SpotLight(new Color(0, 600, 100), new Point(40, 80, 50),
                new Vector(-1, -2, -2))
                .setKl(0.001).setKq(0.0001));

        Camera.getBuilder()
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .setLocation(new Point(0, 0, 1000))
                .setDirection(Point.ZERO, Vector.AXIS_Y)
                .setVpSize(150, 150).setVpDistance(1000)
                .setResolution(500, 500)
                .build()
                .renderImage()
                .writeToImage("multiLightSphere");
    }
}
