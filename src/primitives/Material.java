package primitives;

/**
 * PDS class representing the material of a geometry.
 */
public class Material {
    /**
     * Ambient light attenuation factor.
     */
    public Double3 kA = Double3.ONE;

    /**
     * Sets the ambient attenuation factor (kA).
     * * @param kA the ambient attenuation factor as Double3
     *
     * @return this material (for chaining)
     */
    public Material setKA(Double3 kA) {
        this.kA = kA;
        return this;
    }

    /**
     * Sets the ambient attenuation factor (kA) using a single double.
     * * @param kA the ambient attenuation factor
     *
     * @return this material (for chaining)
     */
    public Material setKA(double kA) {
        this.kA = new Double3(kA);
        return this;
    }
}