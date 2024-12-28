package jme3test.jaimesascent.player;

import com.jme3.bullet.control.BetterCharacterControl;
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
public class RespawnPlayer extends AbstractControl {

    private BetterCharacterControl bcc;
    private final Vector3f spawnPoint = new Vector3f(0, 0, 0);
    private float height = -20f;

    @Override
    public void setSpatial(Spatial sp) {
        super.setSpatial(sp);
        if (spatial != null) {
            this.bcc = spatial.getControl(BetterCharacterControl.class);
            Objects.requireNonNull(bcc, "BetterCharacterControl not found: " + spatial);
        }
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (spatial.getWorldTranslation().y < height) {
            bcc.warp(spawnPoint);
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    public Vector3f getSpawnPoint() {
        return spawnPoint;
    }

    public void setSpawnPoint(Vector3f spawnPoint) {
        this.spawnPoint.set(spawnPoint);
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

}
