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
package jme3test.jaimesascent.ui;

import com.jme3.app.Application;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.Panel;
import com.simsilica.lemur.anim.Animation;
import com.simsilica.lemur.anim.PanelTweens;
import com.simsilica.lemur.anim.TweenAnimation;
import com.simsilica.lemur.anim.Tweens;
import com.simsilica.lemur.core.AbstractGuiControlListener;
import com.simsilica.lemur.core.GuiControl;
import com.simsilica.lemur.effect.AbstractEffect;
import com.simsilica.lemur.effect.Effect;
import com.simsilica.lemur.effect.EffectInfo;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a window in the application. It extends the `Panel`
 * class and provides functionalities to manage the window's visibility, add
 * listeners for window events, and manage its content pane.
 *
 * @author wil
 */
public class Window extends Panel {

    /**
     * This class is responsible for creating a fading effect when the window is
     * opened or closed.
     */
    private class Fader extends Panel {

        public Fader() {
            initComponents();
        }

        private void initComponents() {
            setBackground(new UIImage("Interface/UI/fill.png", new ColorRGBA(0, 0, 0, 1)));
            setAlpha(0.8f);
        }

        @Override
        public void setAlpha(float alpha, boolean recursive) {
            if (alpha <= 0.8f) {
                super.setAlpha(alpha, recursive);
            }
        }
    }

    /**
     * This class implements the `AbstractGuiControlListener` interface and
     * listens for resize events on the window.
     */
    private class ResizeListener extends AbstractGuiControlListener {

        @Override
        public void reshape(GuiControl source, Vector3f pos, Vector3f size) {
            resetStateView();
        }
    }

    private Fader fader;
    private boolean visible;
    private final List<WindowListener> windowListeners;

    protected Container rootPane;
    protected ControlLayout.RootPane pane;
    protected Application app;

    /**
     * Creates a new window with the specified root pane and application.
     *
     * @param pane The root pane of the window.
     * @param app The application that the window belongs to.
     */
    public Window(ControlLayout.RootPane pane, Application app) {
        this.windowListeners = new ArrayList<>();
        this.app = app;
        this.pane = pane;
        initComponents();
    }

    /**
     * Resets the size of the fader and root pane to the preferred size of the
     * window.
     */
    protected void resetStateView() {
        Vector3f prefSize = getPreferredSize();
        fader.setPreferredSize(prefSize.clone());
        rootPane.setPreferredSize(prefSize.clone());
    }

    /**
     * Notifies all registered window listeners that the window has been opened.
     */
    protected final void notifyWindowOpened() {
        for (WindowListener listener : windowListeners) {
            listener.windowOpened();
        }
    }

    /**
     * Notifies all registered window listeners that the window has been closed.
     */
    protected final void notifyWindowClosed() {
        for (WindowListener listener : windowListeners) {
            listener.windowClosed();
        }
    }

    /**
     * Adds a window listener to the list of listeners.
     *
     * @param listener The window listener to add.
     */
    public void addWindowListener(WindowListener listener) {
        this.windowListeners.add(listener);
    }

    /**
     * Removes a window listener from the list of listeners.
     *
     * @param listener The window listener to remove.
     */
    public void removeWindowListener(WindowListener listener) {
        this.windowListeners.remove(listener);
    }

    private void initComponents() {
        setBackground(null);

        ControlLayout layout = new ControlLayout(pane);
        GuiControl control = getControl(GuiControl.class);

        fader = new Fader();
        rootPane = new Container(new ControlLayout(pane));

        control.addListener(new ResizeListener());
        control.setLayout(layout);

        layout.addChild(fader, ControlLayout.Alignment.Center, false);
        layout.addChild(rootPane, ControlLayout.Alignment.Center, false);
        layout.setAttribute(ControlLayout.POSITION, rootPane, new Vector3f(0, 0, 1));

        // Show effect
        Effect<Panel> show = new AbstractEffect<Panel>("open/close") {
            @Override
            public Animation create(Panel t, EffectInfo ei) {
                return new TweenAnimation(Tweens.callMethod(t, "notifyWindowOpened"),
                        Tweens.sequence(PanelTweens.fade(t, 0f, 1f, 1)));
            }
        };
        // Close effect
        Effect<Panel> close = new AbstractEffect<Panel>("open/close") {
            @Override
            public Animation create(Panel t, EffectInfo ei) {
                return new TweenAnimation(Tweens.sequence(PanelTweens.fade(t, 1f, 0f, 1),
                        Tweens.callMethod(t, "notifyWindowClosed")));
            }
        };
        addEffect("show", show);
        addEffect("close", close);
    }

    /**
     * Returns whether the window is visible.
     *
     * @return true if the window is visible, false otherwise.
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Sets the visibility of the window.
     *
     * @param visible true to make the window visible, false to hide it.
     */
    public void setVisible(boolean visible) {
        if (this.visible == visible) {
            return;
        }
        runEffect(visible ? "show" : "close");
        this.visible = visible;
        //this.app.getInputManager().setCursorVisible(visible);
    }

    /**
     * Returns the root container of the window where child components can be
     * added.
     *
     * @return The root container of the window.
     */
    public Container getRootPane() {
        return rootPane;
    }
}
