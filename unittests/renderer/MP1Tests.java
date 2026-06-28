package renderer;

import geometries.api.Geometry;
import geometries.impl.Plane;
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
 * Demonstration tests for Mini-Project 1: super-sampling improvements.
 * <p>Each improvement is tested with two renders of the same scene:
 * <ol>
 *   <li>Improvement disabled - one ray per pixel (baseline)</li>
 *   <li>Improvement enabled - super-sampled beam, colors averaged</li>
 * </ol>
 * <p>Render times are printed so the performance trade-off is visible.
 *
 * @author Gemini
 */
class MP1Tests {

    // ==========================================================
    // SHARED SCENE BUILDER
    // ==========================================================

    /**
     * Builds the shared demonstration scene featuring a sunset beach.
     * <p>The scene contains multiple geometries arranged to test rendering performance
     * and visual effects (Anti-Aliasing and Depth of Field).
     * <ul>
     *   <li>1 Sand plane</li>
     *   <li>2 Sea (triangles)</li>
     *   <li>1 Setting sun (sphere)</li>
     *   <li>2 Umbrellas (built completely from triangles)</li>
     *   <li>1 Green woven mat (built from 1,000 triangles for BVH stress testing)</li>
     *   <li>1 Wooden sun lounger (built from triangles)</li>
     * </ul>
     * <p>Lights: 2 Directional lights + 2 Point lights + 1 Spot light.
     *
     * @param scene the scene to be configured
     */
    private void buildSharedSunsetBeach(Scene scene) {
        scene.setBackground(new Color(255, 100, 50)); // Deep sunset sky
        scene.setAmbientLight(new AmbientLight(new Color(20, 15, 15)));

        Material sandMat = new Material().setKD(0.7).setKS(0.1).setShininess(5);
        Material waterMat = new Material().setKD(0.1).setKS(0.9).setShininess(200).setKR(0.7);
        Material woodMat = new Material().setKD(0.6).setKS(0.2).setShininess(10);
        Material fabricMat = new Material().setKD(0.8).setKS(0.1).setShininess(5);

        java.util.function.Consumer<Geometry> addGeo = g -> scene.geometries.add(g);

        // --- Ground plane (Sand) ---
        addGeo.accept(new Plane(new Point(0, 0, 0), new Vector(0, 1, 0))
                .setEmission(new Color(150, 110, 50)).setMaterial(sandMat));

        // --- Sea (Water plane built from triangles) ---
        addGeo.accept(new Triangle(new Point(-1000, 0.2, -10), new Point(1000, 0.2, -10), new Point(0, 0.2, -2000))
                .setEmission(new Color(10, 20, 60)).setMaterial(waterMat));

        // --- Setting Sun ---
        addGeo.accept(new Sphere(new Point(0, 20, -500), 80)
                .setEmission(new Color(255, 180, 50)));

        // --- Umbrellas with huge orange poles ---
        Color orangePole = new Color(255, 80, 0);
        Point[] umbrellaCenters = {new Point(-20, 0, 25), new Point(20, 0, 25)};

        for (Point center : umbrellaCenters) {
            double cx = center.getX();
            double cz = center.getZ();
            double w = 1.5;

            // Pillar Base vertices
            Point b1 = new Point(cx - w, 0, cz - w); Point b2 = new Point(cx + w, 0, cz - w);
            Point b3 = new Point(cx + w, 0, cz + w); Point b4 = new Point(cx - w, 0, cz + w);
            // Pillar Top vertices
            Point t1 = new Point(cx - w, 22, cz - w); Point t2 = new Point(cx + w, 22, cz - w);
            Point t3 = new Point(cx + w, 22, cz + w); Point t4 = new Point(cx - w, 22, cz + w);

            // Front face
            addGeo.accept(new Triangle(b1, b2, t2).setEmission(orangePole).setMaterial(woodMat));
            addGeo.accept(new Triangle(b1, t2, t1).setEmission(orangePole).setMaterial(woodMat));
            // Back face
            addGeo.accept(new Triangle(b3, b4, t4).setEmission(orangePole).setMaterial(woodMat));
            addGeo.accept(new Triangle(b3, t4, t3).setEmission(orangePole).setMaterial(woodMat));
            // Right face
            addGeo.accept(new Triangle(b2, b3, t3).setEmission(orangePole).setMaterial(woodMat));
            addGeo.accept(new Triangle(b2, t3, t2).setEmission(orangePole).setMaterial(woodMat));
            // Left face
            addGeo.accept(new Triangle(b4, b1, t1).setEmission(orangePole).setMaterial(woodMat));
            addGeo.accept(new Triangle(b4, t1, t4).setEmission(orangePole).setMaterial(woodMat));

            // Top Tip
            addGeo.accept(new Sphere(new Point(cx, 24, cz), 3).setEmission(orangePole).setMaterial(woodMat));

            // Canopy
            Point top = new Point(cx, 22, cz);
            for (int angle = 0; angle < 360; angle += 15) {
                double rad1 = Math.toRadians(angle);
                double rad2 = Math.toRadians(angle + 15);
                Point p1 = new Point(cx + Math.cos(rad1) * 18, 15, cz + Math.sin(rad1) * 18);
                Point p2 = new Point(cx + Math.cos(rad2) * 18, 15, cz + Math.sin(rad2) * 18);

                Color uColor = (angle % 30 == 0) ? new Color(220, 30, 30) : new Color(250, 250, 250);
                addGeo.accept(new Triangle(top, p1, p2).setEmission(uColor).setMaterial(fabricMat));
            }
        }

        // --- Green Woven Mat (Built with 1,000 Triangles) ---
        Color greenMatColor = new Color(20, 160, 20);
        double matStartX = -10;
        double matStartZ = 40;
        int cols = 25;
        int rows = 20;
        double tileW = 20.0 / cols;
        double tileH = 35.0 / rows;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                double x = matStartX + col * tileW;
                double z = matStartZ - row * tileH;
                Point p1 = new Point(x, 0.3, z);
                Point p2 = new Point(x + tileW, 0.3, z);
                Point p3 = new Point(x + tileW, 0.3, z - tileH);
                Point p4 = new Point(x, 0.3, z - tileH);

                addGeo.accept(new Triangle(p1, p2, p3).setEmission(greenMatColor).setMaterial(fabricMat));
                addGeo.accept(new Triangle(p1, p3, p4).setEmission(greenMatColor).setMaterial(fabricMat));
            }
        }

        // --- Wooden Sun Lounger ---
        Color loungerColor = new Color(220, 220, 220);
        Point s1 = new Point(12, 1, 35); Point s2 = new Point(18, 1, 35);
        Point s3 = new Point(18, 1, 20); Point s4 = new Point(12, 1, 20);
        addGeo.accept(new Triangle(s1, s2, s3).setEmission(loungerColor).setMaterial(woodMat));
        addGeo.accept(new Triangle(s1, s3, s4).setEmission(loungerColor).setMaterial(woodMat));

        Point bl1 = new Point(12, 1, 35); Point bl2 = new Point(18, 1, 35);
        Point bl3 = new Point(18, 6, 40); Point bl4 = new Point(12, 6, 40);
        addGeo.accept(new Triangle(bl1, bl2, bl3).setEmission(loungerColor).setMaterial(woodMat));
        addGeo.accept(new Triangle(bl1, bl3, bl4).setEmission(loungerColor).setMaterial(woodMat));

        // --- Lights ---
        scene.lights.add(new DirectionalLight(new Color(150, 100, 50), new Vector(0, -0.2, -1)));
        scene.lights.add(new DirectionalLight(new Color(30, 40, 60), new Vector(0, -1, 0)));
        scene.lights.add(new PointLight(new Color(255, 150, 50), new Point(0, 30, -350)).setKl(0.0001).setKq(0.00001));
        scene.lights.add(new PointLight(new Color(80, 80, 80), new Point(0, 50, 100)).setKl(0.001).setKq(0.0001));
        scene.lights.add(new SpotLight(new Color(100, 100, 100), new Point(-20, 60, 60), new Vector(1, -1, -0.5)).setKl(0.001).setKq(0.0001));
    }

    // ==========================================================
    // TESTS
    // ==========================================================

    /**
     * Renders the AA scene without anti-aliasing (single ray per pixel).
     */
    @Test
    void antiAliasingOff() {
        Scene scene = new Scene("MP1 AA Off");
        buildSharedSunsetBeach(scene);

        long startTime = System.currentTimeMillis();
        Camera.getBuilder()
                .setLocation(new Point(0, 15, 75))
                .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
                .setVpDistance(50)
                .setVpSize(150, 150)
                .setResolution(800, 800)
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .setAntiAliasing(1)
                .setDebugPrint(1)
                .build()
                .renderImage()
                .writeToImage("mp1_aa_off_beach");
        System.out.printf("AA off    render time: %d ms%n", System.currentTimeMillis() - startTime);
    }

    /**
     * Renders the AA scene with anti-aliasing enabled (9x9 = 81 rays per pixel).
     */
    @Test
    void antiAliasingOn() {
        Scene scene = new Scene("MP1 AA On");
        buildSharedSunsetBeach(scene);

        long startTime = System.currentTimeMillis();
        Camera.getBuilder()
                .setLocation(new Point(0, 15, 75))
                .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
                .setVpDistance(50)
                .setVpSize(150, 150)
                .setResolution(800, 800)
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .setAntiAliasing(9)
                .setMultithreading(-2)
                .setDebugPrint(1)
                .build()
                .renderImage()
                .writeToImage("mp1_aa_on_beach");
        System.out.printf("AA on     render time: %d ms%n", System.currentTimeMillis() - startTime);
    }

    /**
     * Renders the DOF scene without depth-of-field (all objects equally sharp).
     */
    @Test
    void depthOfFieldOff() {
        Scene scene = new Scene("MP1 DOF Off");
        buildSharedSunsetBeach(scene);

        long startTime = System.currentTimeMillis();
        Camera.getBuilder()
                .setLocation(new Point(0, 15, 75))
                .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
                .setVpDistance(50)
                .setVpSize(150, 150)
                .setResolution(800, 800)
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .setDebugPrint(1)
                .build()
                .renderImage()
                .writeToImage("mp1_dof_off_beach");
        System.out.printf("DOF off   render time: %d ms%n", System.currentTimeMillis() - startTime);
    }

    /**
     * Renders the DOF scene with depth-of-field enabled.
     * <p>Camera is at z=75; focal distance is set to 54 units, bringing the umbrellas
     * perfectly into focus while blurring the distant sea and sun.
     * <p>Aperture size = 0.3, samples = 9x9 = 81 rays per pixel.
     */
    @Test
    void depthOfFieldOn() {
        Scene scene = new Scene("MP1 DOF On");
        buildSharedSunsetBeach(scene);

        long startTime = System.currentTimeMillis();
        Camera.getBuilder()
                .setLocation(new Point(0, 15, 75))
                .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
                .setVpDistance(50)
                .setVpSize(150, 150)
                .setResolution(800, 800)
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .setDepthOfField(54, 0.3, 9)
                .setMultithreading(-2)
                .setDebugPrint(1)
                .build()
                .renderImage()
                .writeToImage("mp1_dof_on_beach");
        System.out.printf("DOF on    render time: %d ms%n", System.currentTimeMillis() - startTime);
    }
}