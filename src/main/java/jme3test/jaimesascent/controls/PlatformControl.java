package jme3test.jaimesascent.controls;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * Moves platforms up and down
 *
 * @author rickard
 */
public class PlatformControl extends AbstractControl {

    private boolean directionUp = false;
    private float maxDistance = 2f;
    private float velocity = 1f;

    @Override
    protected void controlUpdate(float tpf) {
        if (directionUp && spatial.getLocalTranslation().y < maxDistance) {
            spatial.move(0, velocity * tpf, 0);
            
        } else if (!directionUp && spatial.getLocalTranslation().y > -maxDistance) {
            spatial.move(0, -velocity * tpf, 0);
            
        } else {
            directionUp = !directionUp;
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    public float getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(float maxDistance) {
        this.maxDistance = maxDistance;
    }

    public float getVelocity() {
        return velocity;
    }

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }

}
