package jme3test.jaimesascent.controls;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author capdevon
 */
public class Checkpoint extends AbstractControl {

    private float activationRadius = 1f;
    private Spatial target;
    private ScriptObject actionScript;

    @Override
    protected void controlUpdate(float tpf) {
        if (target != null) {
            if (spatial.getWorldTranslation().distance(target.getWorldTranslation()) < activationRadius) {
                actionScript.execute();
            }
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    public void destroy() {
        spatial.removeFromParent();
    }

    public float getActivationRadius() {
        return activationRadius;
    }

    public void setActivationRadius(float activationRadius) {
        this.activationRadius = activationRadius;
    }

    public Spatial getTarget() {
        return target;
    }

    public void setTarget(Spatial target) {
        this.target = target;
    }

    public ScriptObject getActionScript() {
        return actionScript;
    }

    public void setActionScript(ScriptObject actionScript) {
        this.actionScript = actionScript;
    }

}
