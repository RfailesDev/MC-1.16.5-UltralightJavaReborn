package com.example.examplemod.Rendering.web;



import com.example.examplemod.Rendering.shaders.WebDrawParameters;
import com.example.examplemod.Rendering.surface.GlfwSurface;
import com.example.examplemod.Rendering.web.listener.WebViewListener;
import com.example.examplemod.Rendering.web.listener.WebViewLoadListener;
import com.example.examplemod.Rendering.window.Window;
import com.example.examplemod.Rendering.window.WindowStateListener;
import net.janrupf.ujr.api.UltralightKeyEventBuilder;
import net.janrupf.ujr.api.UltralightMouseEventBuilder;
import net.janrupf.ujr.api.UltralightScrollEventBuilder;
import net.janrupf.ujr.api.UltralightView;
import net.janrupf.ujr.api.event.UlKeyEvent;
import net.janrupf.ujr.api.event.UlKeyEventType;
import net.janrupf.ujr.api.event.UlMouseButton;
import net.janrupf.ujr.api.event.UlScrollEventType;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL30;


/**
 * Composite wrapper around a GLFW window and an Ultralight view
 * that allows for easy rendering of the view to the window.
 */
public class WebWindow implements WindowStateListener {
    private final Window inner;
    private final UltralightView view;

    private WebDrawParameters drawParameters;

    private double mouseX;
    private double mouseY;

    public WebWindow(Window inner, UltralightView view) {
        this.inner = inner;
        this.view = view;

        // Make sure we receive all events
        this.inner.setStateListener(this);
        this.view.setViewListener(new WebViewListener(this));
        this.view.setLoadListener(new WebViewLoadListener());
    }

    public void renderToFramebuffer() {
        // When this method is called, the current active context is
        // the root context. For this reason, we need to switch to
        // the window specific context before we can render to the
        // window framebuffer.

        // Get the surface of the view - as we have set a custom surface factory,
        // this will be a GlfwSurface
        GlfwSurface surface = (GlfwSurface) view.surface();

        // Switch to the window specific context
        inner.activateContext();

        if (drawParameters == null) {
            // Initialize the draw parameters if we haven't done that yet
            drawParameters = new WebDrawParameters();
        }

        // Activate the shader program
        drawParameters.activateShaderProgram();

        // Bind the surface texture
        GL30.glActiveTexture(GL30.GL_TEXTURE0);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, surface.getTexture());

        // Tell our shader which texture to draw
        drawParameters.setUniforms(0);

        // Draw a triangle
        GL30.glDrawArrays(GL30.GL_TRIANGLES, 0, 6);
        inner.swapBuffers();
    }

    public UltralightView getView() {
        return view;
    }

    public Window getWindow() {
        return inner;
    }

    @Override
    public void onFramebufferSizeChange(int width, int height) {
        view.resize(width, height);
    }

    @Override
    public void onCursorPos(double x, double y) {
        this.mouseX = x;
        this.mouseY = y;

        // We need mouse buttons here in order for drag events to properly work
        UlMouseButton button = UlMouseButton.NONE;
        if (inner.isMouseDown(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
            button = UlMouseButton.LEFT;
        } else if (inner.isMouseDown(GLFW.GLFW_MOUSE_BUTTON_RIGHT)) {
            button = UlMouseButton.RIGHT;
        } else if (inner.isMouseDown(GLFW.GLFW_MOUSE_BUTTON_MIDDLE)) {
            button = UlMouseButton.MIDDLE;
        }

        view.fireMouseEvent(
                UltralightMouseEventBuilder.moved()
                        .x((int) x)
                        .y((int) y)
                        .button(button)
                        .build()
        );
    }

    @Override
    public void onMouseButton(int button, int action, int mods) {
        UlMouseButton ulButton;

        switch (button) {
            case GLFW.GLFW_MOUSE_BUTTON_LEFT:
                ulButton = UlMouseButton.LEFT;
                break;

            case GLFW.GLFW_MOUSE_BUTTON_RIGHT:
                ulButton = UlMouseButton.RIGHT;
                break;

            case GLFW.GLFW_MOUSE_BUTTON_MIDDLE:
                ulButton = UlMouseButton.MIDDLE;
                break;

            default:
                return;
        }

        UltralightMouseEventBuilder builder;

        if (action == GLFW.GLFW_PRESS) {
            builder = UltralightMouseEventBuilder.down(ulButton);
        } else if (action == GLFW.GLFW_RELEASE) {
            builder = UltralightMouseEventBuilder.up(ulButton);
        } else {
            return;
        }

        view.fireMouseEvent(
                builder
                        .x((int) this.mouseX)
                        .y((int) this.mouseY)
                        .build()
        );
    }

    @Override
    public void onScroll(double x, double y) {
        // The 50 down below is an arbitrary value that seems to work well,
        // feel free to adjust this value as "scroll sensitivity"

        view.fireScrollEvent(
                new UltralightScrollEventBuilder(UlScrollEventType.BY_PIXEL)
                        .deltaY((int) (x * 50))
                        .deltaY((int) (y * 50))
                        .build()
        );
    }

    @Override
    public void onCharMods(int codepoint, int mods) {
        String text = new String(Character.toChars(codepoint));

        view.fireKeyEvent(
                UltralightKeyEventBuilder.character()
                        .unmodifiedText(text)
                        .text(text)
                        .build()
        );
    }

    @Override
    public void onKey(int key, int scancode, int action, int mods) {
        UltralightKeyEventBuilder builder;

        if (action == GLFW.GLFW_PRESS) {
            builder = UltralightKeyEventBuilder.rawDown();
        } else if (action == GLFW.GLFW_RELEASE) {
            builder = UltralightKeyEventBuilder.up();
        } else {
            return;
        }

        builder.nativeKeyCode(scancode)
                .virtualKeyCode(KeyboardTranslator.glfwKeyToUltralight(key))
                .keyIdentifier(UlKeyEvent.keyIdentifierFromVirtualKeyCode(builder.virtualKeyCode))
                .modifiers(KeyboardTranslator.glfwModifiersToUltralight(mods));


        view.fireKeyEvent(builder.build());

        // Manually synthesize enter and tab
        if (builder.type == UlKeyEventType.RAW_DOWN && (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_TAB)) {
            view.fireKeyEvent(
                    UltralightKeyEventBuilder.character()
                            .unmodifiedText(key == GLFW.GLFW_KEY_ENTER ? "\n" : "\t")
                            .text(key == GLFW.GLFW_KEY_ENTER ? "\n" : "\t")
            );
        }
    }

    @Override
    public void onFocusChange(boolean isFocused) {
        if (isFocused) {
            view.focus();
        } else {
            view.unfocus();
        }
    }
}