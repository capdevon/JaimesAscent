package jme3test.jaimesascent.player;

import com.jme3.input.ChaseCamera;
import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;

/**
 *
 * @author capdevon
 */
public class CustomChaseCamera extends ChaseCamera {

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
