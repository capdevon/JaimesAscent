/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jme3test.jaimesascent;

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
    private final float maxDistance = 2f;
    private final float velocity = 1f;

    @Override
    protected void controlUpdate(float tpf) {
        if (directionUp && spatial.getLocalTranslation().y < maxDistance) {
            spatial.move(0, velocity * tpf, 0);
            return;
        }
        if (!directionUp && spatial.getLocalTranslation().y > -maxDistance) {
            spatial.move(0, -velocity * tpf, 0);
            return;
        }
        directionUp = !directionUp;
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

}
