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
package jme3test.jaimesascent;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.DirectionalLight;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphIterator;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;

/**
 *
 * This is the 'level'. Handles scene loading and spatials in the scene (except
 * player).
 *
 * @author rickard
 */
public class SceneState extends BaseAppState {

    private final BulletAppState physicsState;
    private Node rootNode;

    private Node scene;

    private Spatial statics;
    private Spatial ballShooter;
    private Spatial propellerOne;
    private Spatial propellerTwo;
    private Spatial propellerThree;
    private Spatial platformOne;
    private Spatial platformTwo;
    private Spatial platformThree;
    private Spatial platformFour;
    private Spatial platformFive;

    private Spatial ballGeometry;

    public SceneState(BulletAppState physicsState) {
        this.physicsState = physicsState;
    }

    @Override
    protected void initialize(Application app) {
        final AssetManager assetManager = app.getAssetManager();
        rootNode = ((SimpleApplication) app).getRootNode();

        scene = (Node) assetManager.loadModel("Scenes/labyrinth.j3o");

        statics = scene.getChild("Statics");
        statics.addControl(new RigidBodyControl(0));
        statics.getControl(RigidBodyControl.class).setFriction(0.01f);
                
        ballShooter = scene.getChild("BallShooter");
        
        propellerOne = scene.getChild("Propeller.1");
        propellerOne.addControl(new RigidBodyControl(0));
        propellerOne.getControl(RigidBodyControl.class).setKinematic(true);
        propellerOne.addControl(new PropellerControl(Vector3f.UNIT_Z));
        
        propellerTwo = scene.getChild("Propeller.2");
        propellerTwo.addControl(new RigidBodyControl(0));
        propellerTwo.getControl(RigidBodyControl.class).setKinematic(true);
        propellerTwo.addControl(new PropellerControl(Vector3f.UNIT_Z));
        
        propellerThree = scene.getChild("Propeller.3");
        propellerThree.addControl(new RigidBodyControl(0));
        propellerThree.getControl(RigidBodyControl.class).setKinematic(true);
        propellerThree.addControl(new PropellerControl(Vector3f.UNIT_X));

        platformOne = ((Node) scene.getChild("FloatingPlatform.1")).getChild(0);
        platformOne.addControl(new RigidBodyControl(0));
        platformOne.getControl(RigidBodyControl.class).setKinematic(true);
        platformOne.addControl(new PlatformControl());

        platformTwo = ((Node) scene.getChild("FloatingPlatform.2")).getChild(0);
        platformTwo.addControl(new RigidBodyControl(0));
        platformTwo.getControl(RigidBodyControl.class).setKinematic(true);
        platformTwo.addControl(new PlatformControl());

        platformThree = ((Node) scene.getChild("FloatingPlatform.3")).getChild(0);
        platformThree.addControl(new RigidBodyControl(0));
        platformThree.getControl(RigidBodyControl.class).setKinematic(true);
        platformThree.addControl(new PlatformControl());
        
        platformFour = ((Node) scene.getChild("FloatingPlatform.4")).getChild(0);
        platformFour.addControl(new RigidBodyControl(0));
        platformFour.getControl(RigidBodyControl.class).setKinematic(true);
        platformFour.addControl(new PlatformControl());
        
        platformFive = ((Node) scene.getChild("FloatingPlatform.5")).getChild(0);
        platformFive.addControl(new RigidBodyControl(0));
        platformFive.getControl(RigidBodyControl.class).setKinematic(true);
        platformFive.addControl(new PlatformControl());
        
        DirectionalLight light = new DirectionalLight(new Vector3f(0.5f, -0.5f, 0f));
        rootNode.addLight(light);

        FilterPostProcessor processor = new FilterPostProcessor(assetManager);
        DirectionalLightShadowFilter filter = new DirectionalLightShadowFilter(assetManager, 2048, 1);
        filter.setLight(light);
        processor.addFilter(filter);
        app.getViewPort().addProcessor(processor);

        Sphere sphere = new Sphere(16, 16, 0.9f);
        ballGeometry = new Geometry("Wrecking ball", sphere);
        ballGeometry.setMaterial(assetManager.loadMaterial("Materials/BallMaterial.j3m"));
        ballGeometry.setLocalTranslation(new Vector3f(0, 6, 0));
        ballGeometry.addControl(new RigidBodyControl(400f));
        ballGeometry.getControl(RigidBodyControl.class).setRestitution(1);

        SceneGraphIterator it = new SceneGraphIterator(rootNode);
        it.forEach(spatial -> spatial.setShadowMode(ShadowMode.CastAndReceive));

        loadSky(assetManager);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        if (ballGeometry.getParent() == null) {
            resetBall();
        } else if (ballGeometry.getLocalTranslation().y < -10) {
            rootNode.detachChild(ballGeometry);
            physicsState.getPhysicsSpace().remove(ballGeometry);
        }
    }

    @Override
    protected void cleanup(Application aplctn) {
    }

    @Override
    protected void onEnable() {
        physicsState.getPhysicsSpace().add(statics);
        physicsState.getPhysicsSpace().add(propellerOne);
        physicsState.getPhysicsSpace().add(propellerTwo);
        physicsState.getPhysicsSpace().add(propellerThree);
        physicsState.getPhysicsSpace().add(platformOne);
        physicsState.getPhysicsSpace().add(platformTwo);
        physicsState.getPhysicsSpace().add(platformThree);
        physicsState.getPhysicsSpace().add(platformFour);
        physicsState.getPhysicsSpace().add(platformFive);
        rootNode.attachChild(scene);
    }

    @Override
    protected void onDisable() {
        if (physicsState.getPhysicsSpace() == null) {
            // no physicsState when exiting app
            return;
        }
        physicsState.getPhysicsSpace().remove(statics);
        physicsState.getPhysicsSpace().remove(propellerOne);
        physicsState.getPhysicsSpace().remove(propellerTwo);
        physicsState.getPhysicsSpace().remove(propellerThree);
        physicsState.getPhysicsSpace().remove(platformOne);
        physicsState.getPhysicsSpace().remove(platformTwo);
        physicsState.getPhysicsSpace().remove(platformThree);
        physicsState.getPhysicsSpace().remove(platformFour);
        physicsState.getPhysicsSpace().remove(platformFive);
        rootNode.detachChild(scene);
        rootNode.detachChild(ballGeometry);
        physicsState.getPhysicsSpace().remove(ballGeometry);
    }

    private void resetBall() {
        ballGeometry.getControl(RigidBodyControl.class).clearForces();
        ballGeometry.getControl(RigidBodyControl.class).setLinearVelocity(Vector3f.ZERO.clone());
        ballGeometry.getControl(RigidBodyControl.class).setAngularVelocity(Vector3f.ZERO.clone());
        ballGeometry.getControl(RigidBodyControl.class).setPhysicsLocation(ballShooter.getLocalTranslation());
        ballGeometry.getControl(RigidBodyControl.class).applyCentralImpulse(new Vector3f(0, 0, -1800f));
        rootNode.attachChild(ballGeometry);
        physicsState.getPhysicsSpace().add(ballGeometry);
    }

    private void loadSky(AssetManager assetManager) {
        Texture west = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_west.jpg");
        Texture east = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_east.jpg");
        Texture north = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_north.jpg");
        Texture south = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_south.jpg");
        Texture up = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_up.jpg");
        Texture down = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_down.jpg");

        rootNode.attachChild(SkyFactory.createSky(assetManager, west, east, north, south, up, down));
    }
}
