package jme3test.jaimesascent;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.Trigger;

/**
 * @author capdevon
 */
public class PhysxDebugState extends BaseAppState implements ActionListener {

    private static final String TOGGLE_PHYSICS_DEBUG = "TOGGLE_PHYSICS_DEBUG";

    private InputManager inputManager;
    private BulletAppState bulletAppState;

    @Override
    protected void initialize(Application app) {
        this.inputManager = app.getInputManager();
        this.bulletAppState = getState(BulletAppState.class, true);
    }

    @Override
    protected void cleanup(Application app) {
    }

    @Override
    protected void onEnable() {
        addMapping(TOGGLE_PHYSICS_DEBUG, new KeyTrigger(KeyInput.KEY_0));
    }

    private void addMapping(String mappingName, Trigger... triggers) {
        inputManager.addMapping(mappingName, triggers);
        inputManager.addListener(this, mappingName);
    }

    @Override
    protected void onDisable() {
        inputManager.deleteMapping(TOGGLE_PHYSICS_DEBUG);
        inputManager.removeListener(this);
    }

    @Override
    public void onAction(String action, boolean keyPressed, float tpf) {
        if (keyPressed) {
            if (action.equals(TOGGLE_PHYSICS_DEBUG)) {
                boolean debug = bulletAppState.isDebugEnabled();
                bulletAppState.setDebugEnabled(!debug);
            }
        }
    }

}
