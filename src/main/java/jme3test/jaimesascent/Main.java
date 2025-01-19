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

import jme3test.jaimesascent.states.SceneState;
import jme3test.jaimesascent.states.GameState;
import com.jme3.app.FlyCamAppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.cursors.plugins.JmeCursor;
import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeVersion;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.util.BufferUtils;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import jme3test.jaimesascent.screen.GameScreen;
import jme3test.jaimesascent.ui.LemurGuiStyle;

/**
 * Chasecam example game with animations, physics and mouse look. Use the mouse
 * to look around, and WASD or arrow keys to move.
 *
 * @author rickard
 */
public class Main extends GameApplication {

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        Main app = new Main();
        AppSettings settings = new AppSettings(true);
        settings.setTitle("Jaime Jump: " + JmeVersion.FULL_NAME);
        settings.setResolution(1280, 720);
        settings.setFrameRate(60);
//        settings.setUseJoysticks(true);
//        settings.setRenderer(AppSettings.LWJGL_OPENGL32);
//        settings.setAudioRenderer(AppSettings.LWJGL_OPENAL);
        app.setShowSettings(false);
        app.setSettings(settings);

        app.start();
    }

    @Override
    public void simpleInitApp() {
        // disable the default 1st-person flyCam!
        stateManager.detach(stateManager.getState(FlyCamAppState.class));
        flyCam.setEnabled(false);
        
        JmeCursor cursor = createJmeCursor("Interface/UI/cursor_g.png");
        inputManager.setMouseCursor(cursor);

        LemurGuiStyle.initialize(this);

        stateManager.attach(new BulletAppState());
        stateManager.attach(new PhysxDebugState());
        stateManager.attach(new GameScreen());
        stateManager.attach(new SceneState());
        stateManager.attach(new GameState());
    }

    @Override
    public void simpleUpdate(float tpf) {
    }

    @Override
    public void simpleRender(RenderManager rm) {
    }
    
    protected JmeCursor createJmeCursor(String texturePath) {
        Texture texture = assetManager.loadTexture(texturePath);

        Image image = texture.getImage();
        ByteBuffer imgByteBuff = (ByteBuffer) image.getData(0).rewind();
        IntBuffer curIntBuff = BufferUtils.createIntBuffer(image.getHeight() * image.getWidth());

        while (imgByteBuff.hasRemaining()) {
            int rgba = imgByteBuff.getInt();
            /*int argb = ((rgba & 255) << 24) | (rgba >> 8);*/
            curIntBuff.put(rgba);
        }

        JmeCursor cursor = new JmeCursor();
        cursor.setHeight(image.getHeight());
        cursor.setWidth(image.getWidth());
        cursor.setNumImages(1);
        cursor.setyHotSpot(image.getHeight() - 3);
        cursor.setxHotSpot(3);
        cursor.setImagesData((IntBuffer) curIntBuff.rewind());
        return cursor;
    }
}
