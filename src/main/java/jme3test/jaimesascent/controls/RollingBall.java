package jme3test.jaimesascent.controls;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import java.util.Objects;

/**
 *
 * @author capdevon
 */
public class RollingBall extends AbstractControl {

    private RigidBodyControl rb;
    private final Vector3f startPosition = new Vector3f();
    private final Vector3f impulse = new Vector3f(0, 0, -1f);
    private float height = -10f;

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        if (spatial != null) {
            rb = spatial.getControl(RigidBodyControl.class);
            Objects.requireNonNull(rb, "RigidBodyControl not found: " + spatial);
        }
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (spatial.getWorldTranslation().y < height) {
            resetBall();
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    private void resetBall() {
        rb.clearForces();
        rb.setLinearVelocity(Vector3f.ZERO);
        rb.setAngularVelocity(Vector3f.ZERO);
        rb.setPhysicsLocation(startPosition);
        rb.applyCentralImpulse(impulse);
    }

    public Vector3f getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(Vector3f startPosition) {
        this.startPosition.set(startPosition);
    }

    public Vector3f getImpulse() {
        return impulse;
    }

    public void setImpulse(Vector3f impulse) {
        this.impulse.set(impulse);
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }
    
}
