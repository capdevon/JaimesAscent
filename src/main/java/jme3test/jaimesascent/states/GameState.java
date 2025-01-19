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
package jme3test.jaimesascent.states;

import jme3test.jaimesascent.player.PlayerInput;
import jme3test.jaimesascent.player.CustomChaseCamera;
import jme3test.jaimesascent.player.PlayerControl;
import com.jme3.anim.util.AnimMigrationUtils;
import com.jme3.app.Application;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.input.ChaseCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.jme3.scene.debug.WireBox;
import jme3test.jaimesascent.GameApplication;
import jme3test.jaimesascent.KeyMapping;
import jme3test.jaimesascent.controls.Checkpoint;
import jme3test.jaimesascent.controls.RotatingControl;
import jme3test.jaimesascent.controls.ScriptObject;
import jme3test.jaimesascent.screen.GameScreen;
import jme3test.jaimesascent.ui.WindowListener;

/**
 *
 * Input handling and camera. Loads player character
 *
 * @author rickard
 * @author capdevon
 */
public class GameState extends SimpleAppState {

    private final Vector3f startPosition = new Vector3f(0f, 2f, 0f);
    private final float startRotation = FastMath.PI * 0.5f;
    
    private BulletAppState physicsState;
    private BetterCharacterControl physicsCharacter;
    private Node playerNode;
    private PlayerInput playerInput;
    private ChaseCamera chaseCam;
    
    private GameScreen uiScreen;
    private boolean gamePaused;

    @Override
    protected void initialize(Application app) {
        refreshCacheFields(app);
        this.physicsState = getState(BulletAppState.class, true);

        uiScreen = getState(GameScreen.class);
        uiScreen.getWindow().addWindowListener(windowListener);

        setupCharacter();
        
        setupChaseCam(playerNode);
        
        createCheckpoint();
        
        setupKeys();
    }
    
    /**
     */
    private final WindowListener windowListener = new WindowListener() {
        @Override
        public void windowOpened() {
            gamePaused = true;
            setGameEnabled(false);
            inputManager.setCursorVisible(true);
        }

        @Override
        public void windowClosed() {
            gamePaused = false;
            setGameEnabled(true);
            inputManager.setCursorVisible(false);
        }
    };

    private void setGameEnabled(boolean enabled) {
        int numControls = playerNode.getNumControls();
        for (int i = 0; i < numControls; i++) {
            Control control = playerNode.getControl(i);
            if (control instanceof AbstractControl c) {
                c.setEnabled(enabled);
            } else if (control instanceof ChaseCamera camera) {
                camera.setEnabled(enabled);
            }
        }
        
        // Toggle the animation and physics simulation: paused/running.
        ((GameApplication) getApplication()).togglePause();
    }

    @Override
    public void update(float tpf) {
        if (playerNode.getWorldTranslation().y < -20f) {
            physicsCharacter.warp(startPosition);
            chaseCam.setDefaultHorizontalRotation(startRotation);
        }
    }

    @Override
    protected void cleanup(Application app) {
    }

    @Override
    protected void onEnable() {
        uiScreen.setEnabled(true);
    }

    @Override
    protected void onDisable() {
        uiScreen.setEnabled(false);
    }

    private void setupKeys() {
        addMapping(KeyMapping.MOVE_LEFT,
                new KeyTrigger(KeyInput.KEY_A),
                new KeyTrigger(KeyInput.KEY_LEFT));

        addMapping(KeyMapping.MOVE_RIGHT,
                new KeyTrigger(KeyInput.KEY_D),
                new KeyTrigger(KeyInput.KEY_RIGHT));

        addMapping(KeyMapping.MOVE_FORWARD,
                new KeyTrigger(KeyInput.KEY_W),
                new KeyTrigger(KeyInput.KEY_UP));

        addMapping(KeyMapping.MOVE_BACKWARD,
                new KeyTrigger(KeyInput.KEY_S),
                new KeyTrigger(KeyInput.KEY_DOWN));

        addMapping(KeyMapping.JUMP,
                new KeyTrigger(KeyInput.KEY_F),
                new KeyTrigger(KeyInput.KEY_SPACE));
        
        inputManager.addMapping("Pause", new KeyTrigger(KeyInput.KEY_P));
        inputManager.addListener(new ActionListener() {
            @Override
            public void onAction(String action, boolean isPressed, float tpf) {
                if ("Pause".equals(action) && isPressed && !gamePaused) {
                    uiScreen.getWindow().setVisible(true);
                }
            }
        }, "Pause");
    }

    private void addMapping(String mappingName, Trigger... triggers) {
        inputManager.addMapping(mappingName, triggers);
        inputManager.addListener(playerInput, mappingName);
    }

    private void setupChaseCam(Spatial target) {
        chaseCam = new CustomChaseCamera(cam, target, inputManager);
        chaseCam.setLookAtOffset(new Vector3f(0f, 2f, 0f));
        chaseCam.setMaxDistance(10f);
        chaseCam.setMinDistance(4f);
        chaseCam.setDefaultDistance(7f);
        chaseCam.setMaxVerticalRotation(FastMath.QUARTER_PI);
        chaseCam.setMinVerticalRotation(-FastMath.QUARTER_PI);
        chaseCam.setRotationSensitivity(1.5f);
        chaseCam.setZoomSensitivity(4f);
        chaseCam.setDragToRotate(false);
        chaseCam.setDownRotateOnCloseViewOnly(false);
        chaseCam.setDefaultHorizontalRotation(startRotation);
    }

    private void setupCharacter() {
        playerNode = (Node) assetManager.loadModel("Models/Jaime/Jaime.j3o");
        playerNode.setLocalScale(1.50f);
        playerNode.setLocalTranslation(startPosition);
        playerNode.setShadowMode(ShadowMode.CastAndReceive);

        // Since Jaime was created using the old animation system
        // it needs to be converted to the new one.
        AnimMigrationUtils.migrate(playerNode);
        
        physicsCharacter = new BetterCharacterControl(0.5f, 2.2f, 1f);
        playerNode.addControl(physicsCharacter);
        physicsState.getPhysicsSpace().add(physicsCharacter);

        playerInput = new PlayerInput();
        playerNode.addControl(new PlayerControl(cam));
        playerNode.addControl(playerInput);
        getRootNode().attachChild(playerNode);
    }

    private void createCheckpoint() {
        float size = 1.0f;
        Geometry geo = makeGeometry("Checkpoint", new WireBox(size, size, size), ColorRGBA.Green);
        geo.addControl(new RotatingControl(Vector3f.UNIT_Y));
        
        Checkpoint checkpoint = new Checkpoint();
        geo.addControl(checkpoint);
        checkpoint.setTarget(playerNode);
        checkpoint.setActionScript(new ScriptObject() {
            @Override
            public void execute() {
                startPosition.set(geo.getWorldTranslation());
                checkpoint.destroy();
            }
        });
        
        geo.setLocalTranslation(4.5f, 12.25f, -5.55f);
        getRootNode().attachChild(geo);
    }

    private Geometry makeGeometry(String name, Mesh mesh, ColorRGBA color) {
        Geometry geo = new Geometry(name, mesh);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        geo.setMaterial(mat);
        geo.setShadowMode(ShadowMode.Off);
        return geo;
    }

}
