package jme3test.jaimesascent.states;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;
import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;

/**
 *
 * @author capdevon
 */
public abstract class SimpleAppState extends BaseAppState {
    
    protected AppSettings settings;
    protected AssetManager assetManager;
    protected InputManager inputManager;
    protected AudioRenderer audioRenderer;
    protected RenderManager renderManager;
    protected ViewPort viewPort;
    protected Camera cam;
    
    protected void refreshCacheFields(Application app) {
        this.settings       = app.getContext().getSettings();
        this.assetManager   = app.getAssetManager();
        this.inputManager   = app.getInputManager();
        this.audioRenderer  = app.getAudioRenderer();
        this.renderManager  = app.getRenderManager();
        this.viewPort       = app.getViewPort();
        this.cam            = app.getCamera();
    }

    public Node getRootNode() {
        return ((SimpleApplication) getApplication()).getRootNode();
    }

    public Node getGuiNode() {
        return ((SimpleApplication) getApplication()).getGuiNode();
    }

}
