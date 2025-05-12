/*
 * Copyright (c) 2024 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package jme3test.jaimesascent.player;

import com.jme3.anim.AnimComposer;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import java.util.Objects;

/**
 * Receives input and handles player movement and animations
 *
 * @author rickard
 * @author capdevon
 */
public class PlayerControl extends AbstractControl {

    private Camera camera;
    private BetterCharacterControl bcc;
    private AnimComposer animComposer;

    private final Vector3f walkDirection = new Vector3f(0, 0, 0);
    private final Vector3f camDir = new Vector3f();
    private final Vector3f camLeft = new Vector3f();
    private float moveSpeed = 3f;
    private float fallingTime = 0f;

    protected boolean leftStrafe;
    protected boolean rightStrafe;
    protected boolean forward;
    protected boolean backward;

    private String currentAnimation = "";

    public PlayerControl(Camera cam) {
        this.camera = cam;
    }

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        if (spatial != null) {
            bcc = spatial.getControl(BetterCharacterControl.class);
            Objects.requireNonNull(bcc, "BetterCharacterControl not found: " + spatial);

            animComposer = spatial.getControl(AnimComposer.class);
            Objects.requireNonNull(animComposer, "AnimComposer not found: " + spatial);
        }
    }

    @Override
    public void controlUpdate(float tpf) {
        
        camera.getDirection(camDir).setY(0);
        camera.getLeft(camLeft).setY(0);

        walkDirection.set(Vector3f.ZERO);

        if (leftStrafe) {
            walkDirection.addLocal(camLeft);
        } else if (rightStrafe) {
            walkDirection.addLocal(camLeft.negateLocal());
        }
        if (forward) {
            walkDirection.addLocal(camDir);
        } else if (backward) {
            walkDirection.addLocal(camDir.negateLocal());
        }
        
        walkDirection.normalizeLocal();
        boolean isMoving = walkDirection.lengthSquared() > 0;
        if (isMoving) {
            bcc.setViewDirection(walkDirection);
        }

        bcc.setWalkDirection(walkDirection.multLocal(moveSpeed));

        if (!bcc.isOnGround()) {
            fallingTime += tpf;
        } else {
            fallingTime = 0f;
        }

        updateAnimation();
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    private void updateAnimation() {
        if (fallingTime > 0.25f) {
            setAnimation("Jumping");
        } else if (currentAnimation.equals("JumpStart")) {
            // do nothing
        } else if (leftStrafe || rightStrafe || forward || backward) {
            setAnimation("Walk");
        } else {
            setAnimation("Idle");
        }
    }
    
    private void setAnimation(String animName) {
        if (!Objects.equals(currentAnimation, animName)) {
            currentAnimation = animName;
            animComposer.setCurrentAction(currentAnimation);
        }
    }
    
    public void jump() {
        bcc.jump();
        setAnimation("JumpStart");
    }

    public float getMoveSpeed() {
        return moveSpeed;
    }

    public void setMoveSpeed(float moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

}
