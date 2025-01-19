package jme3test.jaimesascent.player;

import com.jme3.input.ChaseCamera;
import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;

/**
 * A camera that follows a spatial object and can rotate around it by dragging
 * the mouse.
 *
 * @author capdevon
 */
public class CustomChaseCamera extends ChaseCamera {

    /**
     * Constructs a CustomChaseCamera and registers input controls.
     *
     * @param cam the application camera
     * @param target the spatial object to follow
     * @param inputManager the input manager of the application to register inputs
     */
    public CustomChaseCamera(Camera cam, Spatial target, InputManager inputManager) {
        super(cam, target, inputManager);
    }

    @Override
    public void onAction(String name, boolean keyPressed, float tpf) {
        if (isEnabled()) {
            super.onAction(name, keyPressed, tpf);
        }
    }

    @Override
    public void onAnalog(String name, float value, float tpf) {
        if (isEnabled()) {
            super.onAnalog(name, value, tpf);
        }
    }

}
