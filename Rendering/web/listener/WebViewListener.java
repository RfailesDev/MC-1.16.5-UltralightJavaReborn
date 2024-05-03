package com.example.examplemod.Rendering.web.listener;

import com.example.examplemod.Rendering.web.CursorTranslator;
import com.example.examplemod.Rendering.web.WebWindow;
import net.janrupf.ujr.api.UltralightView;
import net.janrupf.ujr.api.cursor.UlCursor;
import net.janrupf.ujr.api.listener.UltralightViewListener;

public class WebViewListener implements UltralightViewListener {
    private final WebWindow window;

    public WebViewListener(WebWindow window) {
        this.window = window;
    }

    @Override
    public void onChangeCursor(UltralightView view, UlCursor cursor) {
        int cursorId = CursorTranslator.ultralightToGlfwCursor(cursor);
        window.getWindow().setCursor(cursorId);
    }
}
