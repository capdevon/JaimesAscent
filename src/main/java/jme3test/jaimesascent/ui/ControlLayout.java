package jme3test.jaimesascent.ui;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.component.AbstractGuiComponent;
import com.simsilica.lemur.core.GuiControl;
import com.simsilica.lemur.core.GuiLayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A `ControlLayout` object is a controlled layout manager.
 * <p>
 * It treats each control independently with respect to the parent component.
 * <p>
 * This layout allows components to be positioned freely in the plane relative
 * to a specified alignment. This is ideal for resizing components or adapting
 * them to different screen resolutions.
 * <p>
 * The `ControlLayout` is ideal for creating user interface components that run
 * on different platforms, i.e., on different screen resolutions as they adapt
 * to them.
 * <p>
 * To initialize a layout control, it is sufficient to instantiate an object of
 * this class, defining an ideal resolution for the program's execution.
 *
 * FOR EXAMPLE:
 * <pre><code>
 * public class GuiApplication extends SimpleApplication {
 *  @Override
 *  public void simpleInitApp() {
 *   ...
 *   Container rootPane = new Container();
 *   guiNode.attachChild(rootPane);
 *
 *   ControlLayout controlLayout = new ControlLayout(ControlLayout.onCreateRootPane(new Vector3f(settings.getWidth(), settings.getHeight(), 1), new Vector3f(1024, 576, 1)));
 *   rootPane.setPreferredSize(controlLayout.getRootPane().getWindow());
 *   rootPane.setLayout(controlLayout);
 *   ControlLayout.setLocationRelativeTo(rootPane.getControl(GuiControl.class), controlLayout.getRootPane());
 *   ...
 *  }
 * }
 * </code></pre> With the definition of the root container, we can add the necessary components to create the user interface.
 *
 * @author wil
 * @version 1.0-SNAPSHOT
 * @since 1.0.0
 */
public class ControlLayout extends AbstractGuiComponent implements GuiLayout {

    /**
     * Método encargado de centrar un componete en la resolución definida.
     *
     * @param gc contro a centrar.
     * @param rp datos de la resolución.
     */
    public static void setLocationRelativeTo(GuiControl gc, RootPane rp) {
        final Node node = gc.getNode();
        if (node == null || rp == null) {
            return;
        }

        final Vector3f fac = rp.getScaleFactor();
        final Vector3f mySize = gc.getPreferredSize();

        node.setLocalTranslation((rp.getWindow().x / 2.0F) * fac.x, (rp.getWindow().y / 2.0F), node.getLocalTranslation().z);
        node.move(-(mySize.x * fac.x) * 0.5F, mySize.y * 0.5F, 0.0F);
    }

    /**
     * Método encargado de crera un nuevo {@link RootPane} con los datos dado.
     *
     * @param window resolucion de la ventana.
     * @param resolution resolucion de pantalla ideal.
     * @return un {@link RootPane} generado.
     */
    public static RootPane onCreateRootPane(Vector3f window, Vector3f resolution) {
        final CanvasLayer layer = new CanvasLayer();
        layer.window = window;
        layer.resolution = resolution;
        return layer;
    }

    /**
     * Encargado de determinar si un el padre de un componentes utiliza el
     * diseño {@link ControlLayout} o no.
     *
     * @param control control a gestionar.
     * @return <code>true</code> si el control es {@link ControlLayout} de lo
     * contrario sera <code>false</code>.
     */
    private static boolean useParentControlLayout(GuiControl control) {
        if (control == null) {
            return false;
        }
        GuiLayout layout = control.getLayout();
        return (layout instanceof ControlLayout);
    }

    /**
     * Logger de la clase .
     */
    private static final Logger LOG = Logger.getLogger(ControlLayout.class.getName());

    /*
        Constantes a utilizar para establecer los valores de los atributos.
     */
    public static final String POSITION = "Position";              // Posición del componente.
    public static final String ALIGNMENT = "Alignment";             // Alineamiento.
    public static final String FONT_SIZE = "FontSize";              // Tamaño de la funete(Si exitien de la clase 'Label').
    public static final String LOCK_SCALING = "Lockscaling";      // Para definir la escala cerrada o libre.
    public static final String DEPTH_POSITION = "DepthPosition";    // Produndidad del componente.

    /**
     * Mapa de controles encargado de gestionar los componentes de este diseño.
     */
    private final Map<Node, Control> children = new HashMap<>();

    /**
     * Un <code>Alignment</code> se encarga de definir un alineamiento para los
     * componentes que se agregan al diseño {@link ControlLayout}.
     */
    public static enum Alignment {

        /**
         * Centrado en el contenedor padre.
         * <pre><code>
         * +-------------+
         * |             |
         * |    +----+   |
         * |    |    |   |
         * |    +----+   |
         * |             |
         * +-------------+
         * </code></pre>
         */
        Center,
        /**
         * Centrado en la parte superios.
         * <pre><code>
         * +-------------+
         * |    +----+   |
         * |    |    |   |
         * |    +----+   |
         * |             |
         * |             |
         * +-------------+
         * </code></pre>
         */
        CenterTop,
        /**
         * Centrado en la parte inferior.
         * <pre><code>
         * +-------------+
         * |             |
         * |             |
         * |    +----+   |
         * |    |    |   |
         * |    +----+   |
         * +-------------+
         * </code></pre>
         */
        CenterBottom,
        /**
         * Centrado en la derecha.
         * <pre><code>
         * +-------------+
         * |             |
         * |       +----+|
         * |       |    ||
         * |       +----+|
         * |             |
         * +-------------+
         * </code></pre>
         */
        RightCenter,
        /**
         * Centrado en la parte superior derecho.
         * <pre><code>
         * +-------------+
         * |       +----+|
         * |       |    ||
         * |       +----+|
         * |             |
         * |             |
         * +-------------+
         * </code></pre>
         */
        RightTop,
        /**
         * Centrado en la parte inferior derecho.
         * <pre><code>
         * +-------------+
         * |             |
         * |             |
         * |       +----+|
         * |       |    ||
         * |       +----+|
         * +-------------+
         * </code></pre>
         */
        RightBottom,
        /**
         * Centrado en la izquierda.
         * <pre><code>
         * +-------------+
         * |             |
         * |+----+       |
         * ||    |       |
         * |+----+       |
         * |             |
         * +-------------+
         * </code></pre>
         */
        LeftCenter,
        /**
         * Centrado en la parte superior izquierdo.
         * <pre><code>
         * +-------------+
         * |+----+       |
         * ||    |       |
         * |+----+       |
         * |             |
         * |             |
         * +-------------+
         * </code></pre>
         */
        LeftTop,
        /**
         * Centrado en parte inferior izquierdo.
         * <pre><code>
         * +-------------+
         * |             |
         * |             |
         * |+----+       |
         * ||    |       |
         * |+----+       |
         * +-------------+
         * </code></pre>
         */
        LeftBottom;
    }

    /**
     * Clave atributos encargado de gestionar las propiedades de los componentes
     * que se agregan al diseño.
     */
    static class Attributes {

        // Propiedades de la clase.
        public Boolean lockscaling;
        public Vector3f originalPos;

        public Alignment alignment;
        public float fontsize;

        /*
            Constructor de la clase.
         */
        public Attributes(Object... constraints) {
            for (final Object element : constraints) {
                if (element == null) {
                    continue;
                }

                if ((element instanceof Boolean)
                        && (this.lockscaling == null)) {
                    this.lockscaling = (Boolean) element;
                } else if ((element instanceof Alignment)
                        && (this.alignment == null)) {
                    this.alignment = (Alignment) element;
                }

                if (this.alignment != null
                        && this.lockscaling != null) {
                    break;
                }
            }

            if (this.lockscaling == null) {
                this.lockscaling = Boolean.FALSE;
            }

            if (this.alignment == null) {
                this.alignment = Alignment.Center;
            }

            this.originalPos = new Vector3f();
        }

        /*
            Setters.
         */
        void setLockscaling(Boolean lockscaling) {
            this.lockscaling = lockscaling;
        }

        void setOriginalPos(Vector3f originalPos) {
            this.originalPos = originalPos;
        }

        void setAlignment(Alignment alignment) {
            this.alignment = alignment;
        }

        void setFontsize(float fontsize) {
            this.fontsize = fontsize;
        }
    }

    /**
     * Un objeto de la clase <code>Control</code> se encarga de administrar el
     * componente hijo del deiseño.
     * <p>
     * Este objeto es el encargado de redimensionar y posicionar los componentes
     * en el contenedor padre.
     * </p>
     */
    final class Control {

        /**
         * Atributos del control.
         */
        private final Attributes attributes;

        /**
         * Control grafíco a gestionar.
         */
        private final GuiControl gc;

        /**
         * Constructor de la clase interna <code>Control</code>.
         *
         * @param gc contro de la interfaz de usuario.
         * @param constraints propiedades para este nuevo contro.
         */
        public Control(GuiControl gc, Object... constraints) {
            this.attributes = new Attributes(constraints);
            this.gc = gc;
        }

        /**
         * Establece la fuente a un control de etiqueta.
         */
        void setFontSize() {
            if ((gc.getNode()) instanceof Label) {
                ((Label) gc.getNode()).setFontSize(attributes.fontsize * rootPane.getScaleFactor().y);
            } else {
                LOG.log(Level.WARNING, "GuiControl({0}) :It is not a label control.", gc.getClass());
            }
        }

        /**
         * Método encargado de redimensionar y posicionar los componentes según
         * sus propiedades establecidas.
         */
        void resize() {
            Vector3f mySize = new Vector3f();
            Vector3f prefSize = gc.getPreferredSize();

            final Vector3f fac = rootPane.getScaleFactor();
            mySize.x = attributes.lockscaling ? prefSize.x * fac.y : prefSize.x * fac.x;
            mySize.y = prefSize.y * fac.y;
            mySize.z = prefSize.z * fac.z;

            // establecemos las nuevas caracteristicas
            // sobre el control del componente.
            gc.setSize(mySize);

            // centramos este componente hijo en el contenedor padre.
            final Node nodeControl = gc.getNode();
            final Vector3f parentSize = getParentSize();

            nodeControl.setLocalTranslation(parentSize.x / 2.0F, -parentSize.y / 2.0F, parentSize.z / 2.0F);
            nodeControl.move(-mySize.x * 0.5F, mySize.y * 0.5F, -mySize.z * 0.5F);

            // Calculamos la nueva posición según el diseño.
            nodeControl.move(calculatePosition());
        }

        /**
         * Calcula la posición del componente.
         *
         * @return nueva posición.
         */
        final Vector3f calculatePosition() {
            float width = gc.getSize().x,
                    height = gc.getSize().y;

            final Vector3f myPos = attributes.originalPos;
            final Vector3f fac = rootPane.getScaleFactor();

            float offsetX = myPos.getX();
            float offsetY = myPos.getY();

            float xPos, yPos, zPos = myPos.z * fac.z;
            switch (attributes.alignment) {
                case Center:
                    if (hasParentAndLockscaling()) {
                        xPos = (offsetX * fac.y);
                        yPos = (offsetY * fac.y);
                    } else {
                        xPos = (offsetX * fac.x);
                        yPos = (offsetY * fac.y);
                    }
                    return new Vector3f(xPos, yPos, zPos);
                case CenterBottom:
                    if (hasParentAndLockscaling()) {
                        xPos = offsetX * fac.y;
                        yPos = -(getParentSize().y * 0.5f) + (height * 0.5f) + (offsetY * fac.y);
                    } else {
                        xPos = offsetX * fac.x;
                        yPos = -(getParentSize().y * 0.5f) + (height * 0.5f) + (offsetY * fac.y);
                    }
                    return new Vector3f(xPos, yPos, zPos);
                case CenterTop:
                    if (hasParentAndLockscaling()) {
                        xPos = offsetX * fac.y;
                        yPos = (getParentSize().y * 0.5f) - (height * 0.5f) - (offsetY * fac.y);
                    } else {
                        xPos = offsetX * fac.x;
                        yPos = (getParentSize().y * 0.5f) - (height * 0.5f) - (offsetY * fac.y);
                    }
                    return new Vector3f(xPos, yPos, zPos);
                case LeftBottom:
                    if (hasParentAndLockscaling()) {
                        xPos = -(getParentSize().x * 0.5f) + (width * 0.5f) + offsetX * fac.y;
                        yPos = -(getParentSize().y * 0.5f) + (height * 0.5f) + (offsetY * fac.y);
                    } else {
                        xPos = -(getParentSize().x * 0.5f) + (width * 0.5f) + offsetX * fac.x;
                        yPos = -(getParentSize().y * 0.5f) + (height * 0.5f) + (offsetY * fac.y);
                    }
                    return new Vector3f(xPos, yPos, zPos);
                case LeftCenter:
                    if (hasParentAndLockscaling()) {
                        xPos = -(getParentSize().x * 0.5f) + (width * 0.5f) + offsetX * fac.y;
                        yPos = (offsetY * fac.y);
                    } else {
                        xPos = -(getParentSize().x * 0.5f) + (width * 0.5f) + offsetX * fac.x;
                        yPos = (offsetY * fac.y);
                    }
                    return new Vector3f(xPos, yPos, zPos);
                case LeftTop:
                    if (hasParentAndLockscaling()) {
                        xPos = -(getParentSize().x * 0.5f) + (width * 0.5f) + offsetX * fac.y;
                        yPos = (getParentSize().y * 0.5f) - (height * 0.5f) - (offsetY * fac.y);
                    } else {
                        xPos = -(getParentSize().x * 0.5f) + (width * 0.5f) + offsetX * fac.x;
                        yPos = (getParentSize().y * 0.5f) - (height * 0.5f) - (offsetY * fac.y);
                    }
                    return new Vector3f(xPos, yPos, zPos);
                case RightBottom:
                    if (hasParentAndLockscaling()) {
                        xPos = (getParentSize().x * 0.5f) - (width * 0.5f) - (offsetX * fac.y);
                        yPos = -(getParentSize().y * 0.5f) + (height * 0.5f) + (offsetY * fac.y);
                    } else {
                        xPos = (getParentSize().x * 0.5f) - (width * 0.5f) - (offsetX * fac.x);
                        yPos = -(getParentSize().y * 0.5f) + (height * 0.5f) + (offsetY * fac.y);
                    }
                    return new Vector3f(xPos, yPos, zPos);
                case RightCenter:
                    if (hasParentAndLockscaling()) {
                        xPos = (getParentSize().x * 0.5f) - (width * 0.5f) - (offsetX * fac.y);
                        yPos = (offsetY * fac.y);
                    } else {
                        xPos = (getParentSize().x * 0.5f) - (width * 0.5f) - (offsetX * fac.x);
                        yPos = (offsetY * fac.y);
                    }
                    return new Vector3f(xPos, yPos, zPos);
                case RightTop:
                    if (hasParentAndLockscaling()) {
                        xPos = (getParentSize().x * 0.5f) - (width * 0.5f) - (offsetX * fac.y);
                        yPos = (getParentSize().y * 0.5f) - (height * 0.5f) - (offsetY * fac.y);
                    } else {
                        xPos = (getParentSize().x * 0.5f) - (width * 0.5f) - (offsetX * fac.x);
                        yPos = (getParentSize().y * 0.5f) - (height * 0.5f) - (offsetY * fac.y);
                    }
                    return new Vector3f(xPos, yPos, zPos);
                default:
                    throw new AssertionError();
            }
        }

        /*
            Getter.
         */
        private Vector3f getParentSize() {
            return getGuiControl().getSize();
        }
    }

    /**
     * Clase interna que implementa la interfaz {@link RootPane}.
     */
    private static final class CanvasLayer implements RootPane {

        public Vector3f window;
        public Vector3f resolution;

        public CanvasLayer() {
        }

        @Override
        public Vector3f getScaleFactor() {
            float rx = resolution.x == 0 ? 0 : window.x / resolution.x,
                    ry = resolution.y == 0 ? 0 : window.y / resolution.y,
                    rz = resolution.z == 0 ? 0 : resolution.z / window.z;
            return new Vector3f(rx, ry, rz);
        }

        @Override
        public Vector3f getWindow() {
            return window;
        }

        @Override
        public Vector3f getResolution() {
            return resolution;
        }
    }

    /**
     * Un <code>RootPane</code> se encarga de listar los métodos encargados de
     * gestionar el factor de escala para los componentes del diseño
     * {@link ControlLayout}.
     */
    public interface RootPane {

        Vector3f getScaleFactor();

        Vector3f getWindow();

        Vector3f getResolution();
    }

    /**
     * {@link RootPane} de este {@link ControlLayout}.
     */
    private final RootPane rootPane;

    /**
     * Constructor de la clase <code>ControlLayout</code>.
     *
     * @param rootPane root-pane para este diseño.
     */
    public ControlLayout(RootPane rootPane) {
        this.rootPane = rootPane;
    }

    /**
     * Devuelve el {@link RootPane} que utiliza este contenedor.
     *
     * @return root-pane.
     */
    public RootPane getRootPane() {
        return rootPane;
    }

    /**
     * (non-JavaDoc)
     *
     * @param size vector-3f
     * @see GuiLayout#calculatePreferredSize(com.jme3.math.Vector3f)
     */
    @Override
    public void calculatePreferredSize(Vector3f size) {
        float w = 0,
                h = 0,
                z = 0;

        for (final Map.Entry<Node, Control> entry : children.entrySet()) {
            Control c = entry.getValue();
            Vector3f d = c.gc.getPreferredSize();
            if (d.x > w) {
                w = d.x;
            }
            if (d.y > h) {
                h = d.z;
            }
            if (d.z > z) {
                z = d.z;
            }
        }
        size.set(w, h, z);
    }

    /**
     * Método encargado de determinar si el padre del nodo de este diseño
     * utiliza el mismo diseño {@link ControlLayout}.
     * <p>
     * De ser verdadera se busca si el nodo de este diseño utiliza una escala
     * cerrada.
     * </p>
     *
     * @return un valor booleano.
     */
    private boolean hasParentAndLockscaling() {
        final Node node = getNode().getParent();
        if (node == null) {
            return false;
        }

        if (useParentControlLayout(node.getControl(GuiControl.class))) {
            ControlLayout layout = node.getControl(GuiControl.class).getLayout();
            return layout.isLockscalingChild(this.getNode());
        }
        return false;
    }

    /**
     * Determina si un nodo hijo utiliza una escala cerrada.
     *
     * @param target nodo objetivo.
     * @return un valor boolean.
     */
    private boolean isLockscalingChild(Node target) {
        //for (final Map.Entry<Node, Control> entry : children.entrySet()) {
        //    if (entry.getKey() == target) {
        //        return entry.getValue().attributes.lockscaling;
        //    }
        //}        
        final Control cl = children.get(target);
        if (cl != null) {
            return cl.attributes.lockscaling;
        }
        return false;
    }

    /**
     * (non-JavaDoc)
     *
     * @param pos vector-3f
     * @param size vector-3f
     * @see GuiLayout#reshape(com.jme3.math.Vector3f, com.jme3.math.Vector3f)
     */
    @Override
    public void reshape(Vector3f pos, Vector3f size) {
        for (final Map.Entry<Node, Control> entry : children.entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }

            Control control = entry.getValue();
            control.resize();
        }
    }

    /**
     * Método encargado de devolver un atributo que usa un nodo hijo
     * perteneciente a este diseño {@link ControlLayout}.
     *
     * @param <T> tipo de datos.
     * @param ac clave del atributo a buscar.
     * @param tar nodo hijo - objetivo.
     * @return valor atributo.
     */
    @SuppressWarnings(value = {"unchecked"})
    public <T extends Object> T getAttribute(String ac, Node tar) {
        if (ac == null || tar == null) {
            throw new IllegalArgumentException();
        }
        final Control cl = children.get(tar);
        if (cl == null) {
            return null;
        }
        switch (ac) {
            case ALIGNMENT:
                return (T) cl.attributes.alignment;
            case DEPTH_POSITION:
                return (T) Float.valueOf(cl.attributes.originalPos.z);
            case FONT_SIZE:
                return (T) Float.valueOf(cl.attributes.fontsize);
            case LOCK_SCALING:
                return (T) cl.attributes.lockscaling;
            case POSITION:
                return (T) cl.attributes.originalPos;
            default:
                throw new AssertionError();
        }
    }

    /**
     * Método encargado de establecer un nuevo valora uno de los atributos que
     * utiliza {@link Control} para gestionar los componentes hijos.
     *
     * @param <T> tipo de datos.
     * @param ac clave o nombre del atributo.
     * @param tar objetio a aplicar las propiedades.
     * @param value valor del atributo o propiedad.
     * @return <code>true</code> si se aplico correctamente los datos, de lo
     * contrario será <code>false</code>.
     */
    @SuppressWarnings(value = {"unchecked"})
    public <T extends Object> boolean setAttribute(String ac, Node tar, T value) {
        if (ac == null || tar == null) {
            throw new IllegalArgumentException();
        }
        final Control cl = children.get(tar);
        if (cl == null) {
            return false;
        }
        switch (ac) {
            case ALIGNMENT:
                if (value instanceof Alignment) {
                    cl.attributes.setAlignment((Alignment) value);
                    cl.gc.invalidate();
                    return true;
                }
                return true;
            case DEPTH_POSITION:
                if (value instanceof Number) {
                    cl.attributes.originalPos.setZ(((Number) value).floatValue());
                    cl.gc.invalidate();
                    return true;
                }
                return false;
            case FONT_SIZE:
                if (value instanceof Number) {
                    cl.attributes.setFontsize(((Number) value).floatValue());
                    cl.setFontSize();
                    return true;
                }
                return false;
            case LOCK_SCALING:
                if (value instanceof Boolean) {
                    cl.attributes.setLockscaling((Boolean) value);
                    cl.gc.invalidate();
                    return true;
                }
                return false;
            case POSITION:
                if (value instanceof Vector3f) {
                    cl.attributes.setOriginalPos((Vector3f) value);
                    cl.gc.invalidate();
                    return true;
                }
                return false;
            default:
                throw new AssertionError();
        }
    }

    /**
     * (non-JavaDoc)
     *
     * @see GuiLayout#addChild(com.jme3.scene.Node, java.lang.Object...)
     *
     * @param <T> tipo-componente
     * @param n componente
     * @param constraints parámetros
     * @return componente
     */
    @Override
    public <T extends Node> T addChild(T n, Object... constraints) {
        if (n != null && n.getControl(GuiControl.class) == null) {
            throw new IllegalArgumentException("Child is not GUI element.");
        }

        if (n == null) {
            return null;
        }
        if (children.containsKey(n)) {
            removeChild(n);
        }

        children.put(n, new Control(n.getControl(GuiControl.class), constraints));
        if (isAttached()) {
            getNode().attachChild(n);
        }
        invalidate();
        return n;
    }

    /**
     * (non-JavaDoc)
     *
     * @param n nodo
     * @see GuiLayout#removeChild(com.jme3.scene.Node)
     */
    @Override
    public void removeChild(Node n) {
        Control c = children.remove(n);
        if (c != null) {
            c.gc.getNode().removeFromParent();
            invalidate();
        }
    }

    /**
     * (non-JavaDoc)
     *
     * @see GuiLayout#getChildren()
     * @see CardLayout#getLayoutChildren()
     *
     * @return list
     */
    @Override
    public Collection<Node> getChildren() {
        return Collections.unmodifiableSet(children.keySet());
    }

    /**
     * (non-JavaDoc)
     *
     * @see GuiLayout#clearChildren()
     */
    @Override
    public void clearChildren() {
        for (final Node n : getChildren()) {
            n.removeFromParent();
        }
        children.clear();
        invalidate();
    }

    /**
     * (non-JavaDoc)
     *
     * @see GuiLayout#detach(com.simsilica.lemur.core.GuiControl)
     * @param parent gui-control
     */
    @Override
    public void detach(GuiControl parent) {
        super.detach(parent);
        Collection<Node> copy = new ArrayList<>(getChildren());
        for (Node n : copy) {
            n.removeFromParent();
        }
    }

    /**
     * (non-JavaDoc)
     *
     * @see GuiLayout#attach(com.simsilica.lemur.core.GuiControl)
     * @param parent gui-control
     */
    @Override
    public void attach(GuiControl parent) {
        super.attach(parent);
        for (Node n : getChildren()) {
            getNode().attachChild(n);
        }
    }

    /**
     * (non-JavaDoc)
     *
     * @see GuiLayout#clone()
     * @return <code>null</code>.
     * @throws UnsupportedOperationException Este método no soporta la clonación
     * de clases u objetos.
     */
    @Override
    public GuiLayout clone() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
