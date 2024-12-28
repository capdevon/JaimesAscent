package jme3test.jaimesascent.controls;

import com.jme3.bounding.BoundingVolume;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author capdevon
 */
public class EnterableTrigger extends AbstractControl {

    private Spatial target;
    private boolean inside;
    private final List<TriggerListener> listeners = new ArrayList<>();

    private float frequency = 1 / 60;
    private float checkTimer = 0;

    @Override
    protected void controlUpdate(float tpf) {
        if (target == null) {
            return;
        }
        checkTimer += tpf;
        if (checkTimer >= frequency) {
            checkTimer = 0;
            onTrigger();
        }
    }

    private void onTrigger() {
        BoundingVolume volume = spatial.getWorldBound();
        boolean contains = volume.contains(target.getWorldTranslation());
        if (!inside && contains) {
            notifyTriggerEnter(this);

        } else if (inside && !contains) {
            notifyTriggerExit(this);
        }
        inside = contains;
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    protected void notifyTriggerEnter(EnterableTrigger trigger) {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).onTriggerEnter(trigger);
        }
    }

    protected void notifyTriggerExit(EnterableTrigger trigger) {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).onTriggerExit(trigger);
        }
    }

    public void addListener(TriggerListener listener) {
        if (listeners.contains(listener)) {
            throw new IllegalArgumentException("The given listener is already registed at this Trigger");
        }
        listeners.add(listener);
    }

    public void removeListener(TriggerListener listener) {
        if (!listeners.remove(listener)) {
            throw new IllegalArgumentException("The given listener is not registed at this Trigger");
        }
    }

    public Spatial getTarget() {
        return target;
    }

    public void setTarget(Spatial target) {
        this.target = target;
    }
    
    public String getName() {
        return Objects.toString(spatial);
    }
    
    public void destroy() {
        spatial.removeFromParent();
    }

}
