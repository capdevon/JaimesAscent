package jme3test.jaimesascent.controls;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author capdevon
 */
public class RotatingControl extends AbstractControl {

    private final Vector3f rotationAxis = new Vector3f(0, 1, 0);
    private float rotationSpeed = 1.0f;

    /**
     * Constructs a new RotatingControl.
     */
    public RotatingControl() {
    }

    public RotatingControl(Vector3f rotationAxis) {
        this.rotationAxis.set(rotationAxis);
    }

    @Override
    protected void controlUpdate(float tpf) {
        float rotationAmount = rotationSpeed * tpf;
        spatial.rotate(rotationAxis.x * rotationAmount, rotationAxis.y * rotationAmount, rotationAxis.z * rotationAmount);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        // Nothing to render
    }

    public Vector3f getRotationAxis() {
        return rotationAxis;
    }

    public void setRotationAxis(Vector3f rotationAxis) {
        this.rotationAxis.set(rotationAxis);
    }

    public float getRotationSpeed() {
        return rotationSpeed;
    }

    public void setRotationSpeed(float rotationSpeed) {
        this.rotationSpeed = rotationSpeed;
    }

}
