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

import jme3test.jaimesascent.controls.PlatformControl;
import jme3test.jaimesascent.controls.RotatingControl;
import com.jme3.app.Application;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.objects.PhysicsBody;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.post.filters.TranslucentBucketFilter;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import jme3test.jaimesascent.controls.RollingBall;

/**
 *
 * This is the 'level'. Handles scene loading and spatials in the scene (except
 * player).
 *
 * @author rickard
 * @author capdevon
 */
public class SceneState extends SimpleAppState {

    private FilterPostProcessor fpp;
    private BulletAppState physicsState;
    private Node rootNode;
    private Node scene;

    @Override
    protected void initialize(Application app) {
        refreshCacheFields(app);
        this.physicsState = getState(BulletAppState.class, true);
        this.rootNode = getRootNode();

        setupLights();
        setupScene();
        loadSky();
    }

    private void loadSky() {
        String texture = "Textures/Sky/Lagoon/lagoon_";
        Texture west    = assetManager.loadTexture(texture + "west.jpg");
        Texture east    = assetManager.loadTexture(texture + "east.jpg");
        Texture north   = assetManager.loadTexture(texture + "north.jpg");
        Texture south   = assetManager.loadTexture(texture + "south.jpg");
        Texture up      = assetManager.loadTexture(texture + "up.jpg");
        Texture down    = assetManager.loadTexture(texture + "down.jpg");

        Spatial sky = SkyFactory.createSky(assetManager, west, east, north, south, up, down);
        sky.setShadowMode(ShadowMode.Off);
        rootNode.attachChild(sky);
    }

    private void setupLights() {
        AmbientLight ambient = new AmbientLight();
        rootNode.addLight(ambient);
        
        DirectionalLight light = new DirectionalLight(new Vector3f(0.5f, -0.5f, 0f));
        rootNode.addLight(light);

        fpp = new FilterPostProcessor(assetManager);
        DirectionalLightShadowFilter dlsf = new DirectionalLightShadowFilter(assetManager, 2048, 1);
        dlsf.setLight(light);
        fpp.addFilter(dlsf);

        fpp.addFilter(new FXAAFilter());
        fpp.addFilter(new TranslucentBucketFilter(true)); // enable Soft Particles
        viewPort.addProcessor(fpp);
    }

    private void setupScene() {
        scene = (Node) assetManager.loadModel("Scenes/labyrinth.j3o");
        scene.setShadowMode(ShadowMode.CastAndReceive);

        Spatial statics = scene.getChild("Statics");
        addStaticRigidBody(statics).setFriction(0.01f);

        // FIX
        Spatial cube = scene.getChild("Cube.005");
        addStaticRigidBody(cube).setFriction(0.01f);

        Spatial ballShooter = scene.getChild("BallShooter");
        setupWreckingBall(ballShooter.getLocalTranslation());

        Spatial propeller1 = scene.getChild("Propeller.1");
        setupPropeller(propeller1, Vector3f.UNIT_Z);

        Spatial propeller2 = scene.getChild("Propeller.2");
        setupPropeller(propeller2, Vector3f.UNIT_Z);

        Spatial propeller3 = scene.getChild("Propeller.3");
        setupPropeller(propeller3, Vector3f.UNIT_X);

        for (int i = 1; i <= 5; i++) {
            Spatial platform = ((Node) scene.getChild("FloatingPlatform." + i)).getChild(0);
            setupPlatform(platform);
        }
        
        rootNode.attachChild(scene);
        physicsState.getPhysicsSpace().addAll(scene);
    }

    @Override
    protected void cleanup(Application app) {
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }

    private void setupWreckingBall(Vector3f startPosition) {

        ParticleEmitter emitter = createPerticleEmitter();

        Sphere sphere = new Sphere(16, 16, 0.9f);
        Geometry geo = new Geometry("Sphere", sphere);
        Material mat = assetManager.loadMaterial("Materials/BallMaterial.j3m");
        geo.setMaterial(mat);

        Node node = new Node("RollingBall");
        node.attachChild(emitter);
        node.attachChild(geo);
        node.setLocalTranslation(new Vector3f(0, -20f, 0));

        CollisionShape collShape = new SphereCollisionShape(sphere.getRadius());
        RigidBodyControl rigidbody = new RigidBodyControl(collShape, 400f);
        node.addControl(rigidbody);
        rigidbody.setRestitution(1);

        RollingBall ball = new RollingBall();
        node.addControl(ball);
        ball.setHeight(-10f);
        ball.setStartPosition(startPosition);
        ball.setImpulse(new Vector3f(0, 0, -1800f));

        rootNode.attachChild(node);
        physicsState.getPhysicsSpace().add(rigidbody);
    }

    private ParticleEmitter createPerticleEmitter() {
        /**
         * Uses Texture from jme3-test-data library!
         */
        ParticleEmitter emitter = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 30);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        mat.setFloat("Softness", 3f);
        mat.setTexture("Texture", assetManager.loadTexture("Effects/Explosion/flame.png"));
        emitter.setMaterial(mat);
        emitter.setShape(new EmitterSphereShape(Vector3f.ZERO, 0.6f));
        emitter.setImagesX(2);
        emitter.setImagesY(2); // 2x2 texture animation
        emitter.setStartColor(new ColorRGBA(0.1f, 0.1f, 0.1f, 1f)); // dark gray
        emitter.setEndColor(new ColorRGBA(0.5f, 0.5f, 0.5f, 0.3f)); // gray      
        emitter.setStartSize(0.8f);
        emitter.setEndSize(0.1f);
        emitter.setGravity(0f, 0f, 0f);
        emitter.setLowLife(0.5f);
        emitter.setHighLife(3f);
        emitter.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 2, 0));
        emitter.getParticleInfluencer().setVelocityVariation(0.3f);
        return emitter;
    }

    private RigidBodyControl addStaticRigidBody(Spatial sp) {
        CollisionShape collShape = CollisionShapeFactory.createMeshShape(sp);
        RigidBodyControl rb = new RigidBodyControl(collShape, PhysicsBody.massForStatic);
        sp.addControl(rb);
        return rb;
    }

    private void setupPropeller(Spatial sp, Vector3f rotAxis) {
        RigidBodyControl rb = addStaticRigidBody(sp);
        rb.setKinematic(true);
        sp.addControl(new RotatingControl(rotAxis));
    }

    private void setupPlatform(Spatial sp) {
        RigidBodyControl rb = addStaticRigidBody(sp);
        rb.setKinematic(true);
        sp.addControl(new PlatformControl());
    }
}
