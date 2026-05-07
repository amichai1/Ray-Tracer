package scene;

import geometries.impl.Geometries;
import lighting.AmbientLight;
import primitives.Color;

/**
 * Represents a 3D scene containing geometries, lighting, and background settings.
 * <p>
 * This is a Passive Data Structure (PDS): all fields are public and accessed directly.
 * Fluent setter methods allow convenient construction via method chaining.
 * </p>
 *
 * @author Amichai Mukades
 */
public class Scene {

    /**
     * The name of the scene.
     */
    public final String name;

    /**
     * The background color rendered for rays that hit no geometry.
     * Defaults to black.
     */
    public Color background = Color.BLACK;

    /**
     * The ambient light applied uniformly to all surfaces in the scene.
     * Defaults to no ambient light.
     */
    public AmbientLight ambientLight = AmbientLight.NONE;

    /**
     * The collection of geometric bodies in the scene.
     */
    public Geometries geometries = new Geometries();

    /**
     * Constructs a scene with the given name and default settings.
     *
     * @param name the name of the scene
     */
    public Scene(String name) {
        this.name = name;
    }

    /**
     * Sets the background color of the scene.
     *
     * @param background the background color
     * @return this scene (for method chaining)
     */
    public Scene setBackground(Color background) {
        this.background = background;
        return this;
    }

    /**
     * Sets the ambient light of the scene.
     *
     * @param ambientLight the ambient light
     * @return this scene (for method chaining)
     */
    public Scene setAmbientLight(AmbientLight ambientLight) {
        this.ambientLight = ambientLight;
        return this;
    }

    /**
     * Sets the geometries of the scene.
     *
     * @param geometries the collection of geometries
     * @return this scene (for method chaining)
     */
    public Scene setGeometries(Geometries geometries) {
        this.geometries = geometries;
        return this;
    }
}
