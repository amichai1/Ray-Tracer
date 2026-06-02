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
     * Diffuse reflection coefficient.
     */
    public Double3 kD = Double3.ZERO;

    /**
     * Specular reflection coefficient.
     */
    public Double3 kS = Double3.ZERO;

    /**
     * Shininess exponent for the specular highlight.
     */
    public int nShininess = 0;

    /**
     * Transparency attenuation factor (0 = opaque, 1 = fully transparent).
     */
    public Double3 kT = Double3.ZERO;

    /**
     * Reflection attenuation factor (0 = no reflection, 1 = perfect mirror).
     */
    public Double3 kR = Double3.ZERO;

    /**
     * Sets the ambient attenuation factor (kA).
     *
     * @param  kA the ambient attenuation factor as Double3
     * @return    this material (for chaining)
     */
    public Material setKA(Double3 kA) {
        this.kA = kA;
        return this;
    }

    /**
     * Sets the ambient attenuation factor (kA) using a single double.
     *
     * @param  kA the ambient attenuation factor
     * @return    this material (for chaining)
     */
    public Material setKA(double kA) {
        this.kA = new Double3(kA);
        return this;
    }

    /**
     * Sets the diffuse reflection coefficient.
     *
     * @param  kD the diffuse coefficient as Double3
     * @return    this material (for chaining)
     */
    public Material setKD(Double3 kD) {
        this.kD = kD;
        return this;
    }

    /**
     * Sets the diffuse reflection coefficient using a single double.
     *
     * @param  kD the diffuse coefficient
     * @return    this material (for chaining)
     */
    public Material setKD(double kD) {
        this.kD = new Double3(kD);
        return this;
    }

    /**
     * Sets the specular reflection coefficient.
     *
     * @param  kS the specular coefficient as Double3
     * @return    this material (for chaining)
     */
    public Material setKS(Double3 kS) {
        this.kS = kS;
        return this;
    }

    /**
     * Sets the specular reflection coefficient using a single double.
     *
     * @param  kS the specular coefficient
     * @return    this material (for chaining)
     */
    public Material setKS(double kS) {
        this.kS = new Double3(kS);
        return this;
    }

    /**
     * Sets the shininess exponent.
     *
     * @param  n the shininess exponent
     * @return   this material (for chaining)
     */
    public Material setShininess(int n) {
        this.nShininess = n;
        return this;
    }

    /**
     * Sets the transparency coefficient.
     *
     * @param  kT the transparency factor as Double3
     * @return    this material (for chaining)
     */
    public Material setKT(Double3 kT) {
        this.kT = kT;
        return this;
    }

    /**
     * Sets the transparency coefficient using a single double.
     *
     * @param  kT the transparency factor
     * @return    this material (for chaining)
     */
    public Material setKT(double kT) {
        this.kT = new Double3(kT);
        return this;
    }

    /**
     * Sets the reflection coefficient.
     *
     * @param  kR the reflection factor as Double3
     * @return    this material (for chaining)
     */
    public Material setKR(Double3 kR) {
        this.kR = kR;
        return this;
    }

    /**
     * Sets the reflection coefficient using a single double.
     *
     * @param  kR the reflection factor
     * @return    this material (for chaining)
     */
    public Material setKR(double kR) {
        this.kR = new Double3(kR);
        return this;
    }
}