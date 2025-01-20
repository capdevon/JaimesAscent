package jme3test.jaimesascent.player;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import java.util.Objects;
import jme3test.jaimesascent.KeyMapping;

/**
 *
 * @author capdevon
 */
public class PlayerInput extends AbstractControl implements ActionListener {

    private PlayerControl player;
    private InputManager inputManager;

    private final String[] mappings = {
        KeyMapping.MOVE_LEFT,
        KeyMapping.MOVE_RIGHT,
        KeyMapping.MOVE_FORWARD,
        KeyMapping.MOVE_BACKWARD,
        KeyMapping.JUMP
    };

    public PlayerInput() {
    }

    /**
     * Constructor for PlayerInput that registers input mappings with the given
     * InputManager.
     *
     * @param inputManager the InputManager to register with
     */
    public PlayerInput(InputManager inputManager) {
        registerWithInput(inputManager);
    }

    /**
     * Registers the input mappings with the given InputManager.
     *
     * @param inputManager the InputManager to register with
     */
    public final void registerWithInput(InputManager inputManager) {
        this.inputManager = inputManager;
        inputManager.addMapping(KeyMapping.MOVE_LEFT,
                new KeyTrigger(KeyInput.KEY_A),
                new KeyTrigger(KeyInput.KEY_LEFT));

        inputManager.addMapping(KeyMapping.MOVE_RIGHT,
                new KeyTrigger(KeyInput.KEY_D),
                new KeyTrigger(KeyInput.KEY_RIGHT));

        inputManager.addMapping(KeyMapping.MOVE_FORWARD,
                new KeyTrigger(KeyInput.KEY_W),
                new KeyTrigger(KeyInput.KEY_UP));

        inputManager.addMapping(KeyMapping.MOVE_BACKWARD,
                new KeyTrigger(KeyInput.KEY_S),
                new KeyTrigger(KeyInput.KEY_DOWN));

        inputManager.addMapping(KeyMapping.JUMP,
                new KeyTrigger(KeyInput.KEY_F),
                new KeyTrigger(KeyInput.KEY_SPACE));

        inputManager.addListener(this, mappings);
    }

    /**
     * Unregisters the input mappings from the InputManager.
     */
    public final void unregisterInput() {
        if (inputManager != null) {
            for (String name : mappings) {
                if (inputManager.hasMapping(name)) {
                    inputManager.deleteMapping(name);
                }
            }
            inputManager.removeListener(this);
        }
    }

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        if (spatial != null) {
            player = spatial.getControl(PlayerControl.class);
            Objects.requireNonNull(player, "PlayerControl not found: " + spatial);
        }
    }

    @Override
    protected void controlUpdate(float tpf) {
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    @Override
    public void onAction(String action, boolean keyPressed, float tpf) {
        if (!isEnabled()) {
            return;
        }

        if (action.equals(KeyMapping.MOVE_LEFT)) {
            player.leftStrafe = keyPressed;
        } else if (action.equals(KeyMapping.MOVE_RIGHT)) {
            player.rightStrafe = keyPressed;
        } else if (action.equals(KeyMapping.MOVE_FORWARD)) {
            player.forward = keyPressed;
        } else if (action.equals(KeyMapping.MOVE_BACKWARD)) {
            player.backward = keyPressed;
        } else if (action.equals(KeyMapping.JUMP) && keyPressed) {
            player.jump();
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            resetInput();
        }
    }

    private void resetInput() {
        player.leftStrafe = false;
        player.rightStrafe = false;
        player.forward = false;
        player.backward = false;
    }

}
