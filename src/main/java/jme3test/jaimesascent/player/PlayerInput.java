package jme3test.jaimesascent.player;

import com.jme3.input.controls.ActionListener;
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
