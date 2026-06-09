package renderer;

import geometries.impl.Plane;
import geometries.impl.Polygon;
import geometries.impl.Sphere;
import geometries.impl.Triangle;
import lighting.AmbientLight;
import lighting.DirectionalLight;
import lighting.PointLight;
import lighting.SpotLight;
import org.junit.jupiter.api.Test;
import primitives.Color;
import primitives.Material;
import primitives.Point;
import primitives.Vector;
import scene.Scene;

/**
 * Bonus 1 – an impressive "Crystal Gallery" scene demonstrating all stage-8
 * rendering effects: ambient light, emission, diffuse/specular shading,
 * shadows, reflection, and transparency.
 * <p>
 * 12 objects: 2 Planes, 5 Spheres, 3 Triangles, 2 Polygons.
 * 4 light sources: ambient, two spot lights, one point light,
 * one directional light.
 * </p>
 *
 * @author Amichai Mukades
 */
@SuppressWarnings("java:S109")
class BonusSceneTest {

    /**
     * Default constructor to satisfy JavaDoc generator
     */
    BonusSceneTest() { /* to satisfy JavaDoc generator */ }

    /**
     * Renders the Crystal Gallery and writes the result to
     * {@code images/crystalGallery.png}.
     */
    @Test
    void crystalGallery() {
        Scene scene = new Scene("Crystal Gallery");

        // ── Planes ───────────────────────────────────────────────────────────

        // 1. Mirror floor
        scene.geometries.add(
                new Plane(new Point(0, -3, 0), new Vector(0, 1, 0))
                        .setEmission(new Color(5, 5, 10))
                        .setMaterial(new Material()
                                .setKD(0.4).setKS(0.5).setShininess(60).setKR(0.3)));

        // 2. Dark back wall
        scene.geometries.add(
                new Plane(new Point(0, 0, -22), new Vector(0, 0, 1))
                        .setEmission(new Color(5, 5, 20))
                        .setMaterial(new Material()
                                .setKD(0.5).setKS(0.3).setShininess(20).setKR(0.1)));

        // ── Spheres ───────────────────────────────────────────────────────────

        // 3. Large central glass sphere (transparency)
        scene.geometries.add(
                new Sphere(new Point(0, 0, -5), 2.5)
                        .setEmission(new Color(10, 20, 60))
                        .setMaterial(new Material()
                                .setKD(0.25).setKS(0.5).setShininess(100).setKT(0.5)));

        // 4. Left silver mirror sphere (reflection)
        scene.geometries.add(
                new Sphere(new Point(-6, -1, -4), 2)
                        .setEmission(new Color(10, 10, 10))
                        .setMaterial(new Material()
                                .setKD(0.2).setKS(0.5).setShininess(150).setKR(0.5)));

        // 5. Right red crystal sphere (partial transparency)
        scene.geometries.add(
                new Sphere(new Point(6, -1, -4), 2)
                        .setEmission(new Color(50, 0, 0))
                        .setMaterial(new Material()
                                .setKD(0.35).setKS(0.4).setShininess(60).setKT(0.35)));

        // 6. Back-left small emerald sphere
        scene.geometries.add(
                new Sphere(new Point(-4, 1, -13), 1.2)
                        .setEmission(new Color(0, 40, 10))
                        .setMaterial(new Material()
                                .setKD(0.3).setKS(0.5).setShininess(80).setKT(0.3)));

        // 7. Back-right small gold sphere
        scene.geometries.add(
                new Sphere(new Point(4, 1, -13), 1.2)
                        .setEmission(new Color(80, 55, 0))
                        .setMaterial(new Material()
                                .setKD(0.3).setKS(0.8).setShininess(250)));

        // ── Triangles ─────────────────────────────────────────────────────────

        // 8. Left arch triangle
        scene.geometries.add(
                new Triangle(
                        new Point(-14, 12, -18),
                        new Point(-14, -3, -18),
                        new Point(0, -3, -18))
                        .setEmission(new Color(40, 0, 70))
                        .setMaterial(new Material()
                                .setKD(0.4).setKS(0.4).setShininess(40).setKR(0.15)));

        // 9. Right arch triangle
        scene.geometries.add(
                new Triangle(
                        new Point(14, 12, -18),
                        new Point(14, -3, -18),
                        new Point(0, -3, -18))
                        .setEmission(new Color(40, 0, 70))
                        .setMaterial(new Material()
                                .setKD(0.4).setKS(0.4).setShininess(40).setKR(0.15)));

        // 10. Front decorative accent triangle
        scene.geometries.add(
                new Triangle(
                        new Point(-2.5, -3, 1),
                        new Point(2.5, -3, 1),
                        new Point(0, -3, -2))
                        .setEmission(new Color(90, 45, 0))
                        .setMaterial(new Material()
                                .setKD(0.5).setKS(0.5).setShininess(60)));

        // ── Polygons ──────────────────────────────────────────────────────────

        // 11. Central dark marble pedestal platform
        scene.geometries.add(
                new Polygon(
                        new Point(-3, -3, -3),
                        new Point(3, -3, -3),
                        new Point(3, -3, -10),
                        new Point(-3, -3, -10))
                        .setEmission(new Color(15, 10, 30))
                        .setMaterial(new Material()
                                .setKD(0.4).setKS(0.7).setShininess(100).setKR(0.25)));

        // 12. Left side decorative wall panel
        scene.geometries.add(
                new Polygon(
                        new Point(-13, 10, -17),
                        new Point(-13, -3, -17),
                        new Point(-5, -3, -17),
                        new Point(-5, 10, -17))
                        .setEmission(new Color(20, 20, 50))
                        .setMaterial(new Material()
                                .setKD(0.3).setKS(0.5).setShininess(50).setKR(0.3)));

        // ── Lighting ──────────────────────────────────────────────────────────

        scene.setAmbientLight(new AmbientLight(new Color(20, 20, 25)));

        // Main warm spotlight (upper right front)
        scene.lights.add(
                new SpotLight(new Color(300, 220, 120), new Point(12, 18, 12),
                        new Vector(-1, -2, -2))
                        .setKl(0.001).setKq(0.0002));

        // Cool blue fill spotlight (upper left front)
        scene.lights.add(
                new SpotLight(new Color(80, 120, 300), new Point(-12, 14, 10),
                        new Vector(1, -2, -2))
                        .setKl(0.001).setKq(0.0002));

        // Soft center point light
        scene.lights.add(
                new PointLight(new Color(150, 120, 80), new Point(0, 5, 0))
                        .setKl(0.005).setKq(0.001));

        // Subtle directional light from above-right
        scene.lights.add(new DirectionalLight(new Color(15, 15, 25), new Vector(1, -2, -1)));

        // ── Camera & Render ───────────────────────────────────────────────────

        Camera.getBuilder()
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .setLocation(new Point(0, 4, 20))
                .setDirection(new Point(0, 0, -5), Vector.AXIS_Y)
                .setVpDistance(20)
                .setVpSize(22, 22)
                .setResolution(3200, 3200)
                .build()
                .renderImage()
                .writeToImage("crystalGallery");
    }
}
