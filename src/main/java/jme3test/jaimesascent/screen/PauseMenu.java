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
package jme3test.jaimesascent.screen;

import com.jme3.app.Application;
import com.jme3.font.BitmapFont;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.HAlignment;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.VAlignment;
import com.simsilica.lemur.style.ElementId;
import jme3test.jaimesascent.ui.ControlLayout;
import jme3test.jaimesascent.ui.UIImage;
import jme3test.jaimesascent.ui.Window;

/**
 * @author wil
 */
public class PauseMenu extends Window {

    public PauseMenu(ControlLayout.RootPane pane, Application app) {
        super(pane, app);
        initComponents();
    }

    private void initComponents() {
        BitmapFont buttonFont = GuiGlobals.getInstance().loadFont("Interface/Fonts/OrbitronSemiBold.fnt");
        BitmapFont labelFont = GuiGlobals.getInstance().loadFont("Interface/Fonts/OrbitronBlack.fnt");

        ControlLayout layout = new ControlLayout(pane);

        Container leftPanel = new Container(layout);
        leftPanel.setBackground(new UIImage("Interface/UI/panel_01.png"));
        leftPanel.setPreferredSize(new Vector3f(500, 700, 0));
        getRootPane().addChild(leftPanel, ControlLayout.Alignment.LeftCenter, false);

        // create labels
        Label title = createLabel("Jaime Jump", labelFont);
        title.setPreferredSize(new Vector3f(leftPanel.getPreferredSize().x - 25, 50, 0));
        title.setColor(ColorRGBA.White);

        leftPanel.addChild(title, ControlLayout.Alignment.LeftTop, false);
        layout.setAttribute(ControlLayout.POSITION, title, new Vector3f(25, 80, 1));
        layout.setAttribute(ControlLayout.FONT_SIZE, title, 35.0f);

        Label subtitle = createLabel("Pause Menu", labelFont);
        subtitle.setPreferredSize(new Vector3f(leftPanel.getPreferredSize().x - 25, 50, 0));
        subtitle.setColor(new ColorRGBA(0.412f, 0.424f, 0.463f, 1.0f));

        leftPanel.addChild(subtitle, ControlLayout.Alignment.LeftTop, false);
        layout.setAttribute(ControlLayout.POSITION, subtitle, new Vector3f(25, 115, 1));
        layout.setAttribute(ControlLayout.FONT_SIZE, subtitle, 25.0f);

        // create buttons
        Button resumeBtn = createButton("Resume", buttonFont);
        resumeBtn.addClickCommands((source) -> {
            setVisible(false);
        });

        leftPanel.addChild(resumeBtn, ControlLayout.Alignment.LeftCenter, false);
        layout.setAttribute(ControlLayout.POSITION, resumeBtn, new Vector3f(50, 30, 1));
        layout.setAttribute(ControlLayout.FONT_SIZE, resumeBtn, 20.0f);

        Button exitBtn = createButton("Exit", buttonFont);
        exitBtn.addClickCommands((source) -> {
            app.stop(true);
        });

        leftPanel.addChild(exitBtn, ControlLayout.Alignment.LeftCenter, false);
        layout.setAttribute(ControlLayout.POSITION, exitBtn, new Vector3f(50, -30, 1));
        layout.setAttribute(ControlLayout.FONT_SIZE, exitBtn, 20.0f);
        setAlpha(0);
    }
    
    private Label createLabel(String name, BitmapFont font) {
        Label label = new Label(name);
        label.setFont(font);
        label.setTextHAlignment(HAlignment.Left);
        label.setTextVAlignment(VAlignment.Center);
        return label;
    }

    private Button createButton(String name, BitmapFont font) {
        Button button = new Button(name, new ElementId("MyButton"));
        button.setBackground(new UIImage("Interface/UI/button_01.png"));
        button.setPreferredSize(new Vector3f(300, 45, 0));
        button.setFont(font);
        button.setColor(new ColorRGBA(0.522f, 0.537f, 0.584f, 1.0f));
        button.setTextHAlignment(HAlignment.Center);
        button.setTextVAlignment(VAlignment.Center);
        return button;
    }
}
