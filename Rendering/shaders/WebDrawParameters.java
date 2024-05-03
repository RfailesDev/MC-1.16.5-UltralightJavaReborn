package com.example.examplemod.Rendering.shaders;


import org.lwjgl.opengl.GL30;

import java.io.IOException;
import java.io.InputStream;

public class WebDrawParameters {
    /**
     * This is a simple triangle which covers the whole screen.
     */
    private static final float[] VERTEX_DATA = {
            -1.0f, -1.0f, 0.0f,
            1.0f, -1.0f, 0.0f,
            -1.0f, 1.0f, 0.0f,

            -1.0f, 1.0f, 0.0f,
            1.0f, 1.0f, 0.0f,
            1.0f, -1.0f, 0.0f
    };

    private final int shaderProgram;
    private final int vertexArray;
    private final int vertexBuffer;

    private final int textureUniform;

    public WebDrawParameters() {
        // Read the vertex and fragment shader sources
        String vertexShaderSource = readShaderSource("web.vert");
        String fragmentShaderSource = readShaderSource("web.frag");

        // Compile both shaders
        int vertexShader = compileShader(vertexShaderSource, GL30.GL_VERTEX_SHADER);
        int fragmentShader = compileShader(fragmentShaderSource, GL30.GL_FRAGMENT_SHADER);

        // Link the shaders into a program
        int program = GL30.glCreateProgram();
        GL30.glAttachShader(program, vertexShader);
        GL30.glAttachShader(program, fragmentShader);
        GL30.glLinkProgram(program);

        // We can delete the shader objects after linking
        GL30.glDeleteShader(vertexShader);
        GL30.glDeleteShader(fragmentShader);

        // Make sure the program has linked successfully
        int status = GL30.glGetProgrami(program, GL30.GL_LINK_STATUS);
        if (status == GL30.GL_FALSE) {
            String infoLog = GL30.glGetProgramInfoLog(program);
            throw new RuntimeException("Could not link program: " + infoLog);
        }

        this.shaderProgram = program;

        this.textureUniform = GL30.glGetUniformLocation(shaderProgram, "Texture");

        // We need a vertex array object
        this.vertexArray = GL30.glGenVertexArrays();

        // We also need a buffer which contains our vertex coordinates
        this.vertexBuffer = GL30.glGenBuffers();

        // Bind the buffer, the vertex data is always the same for our case
        GL30.glBindVertexArray(vertexArray);
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vertexBuffer);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, VERTEX_DATA, GL30.GL_STATIC_DRAW);
    }

    public void activateShaderProgram() {
        // Set the vertex data as the first vertex attribute
        GL30.glVertexAttribPointer(0, 3, GL30.GL_FLOAT, false, 3 * Float.BYTES, 0);
        GL30.glEnableVertexAttribArray(0);

        // Use the shader program
        GL30.glUseProgram(shaderProgram);
        GL30.glBindVertexArray(vertexArray);
    }

    public void setUniforms(
            int texture
    ) {
        GL30.glUniform1i(textureUniform, texture);
    }

    public int getShaderProgram() {
        return shaderProgram;
    }

    private static int compileShader(String source, int type) {
        int shader = GL30.glCreateShader(type);
        GL30.glShaderSource(shader, source);
        GL30.glCompileShader(shader);

        int status = GL30.glGetShaderi(shader, GL30.GL_COMPILE_STATUS);

        if (status == GL30.GL_FALSE) {
            String infoLog = GL30.glGetShaderInfoLog(shader);
            throw new RuntimeException("Could not compile shader: " + infoLog);
        }

        return shader;
    }

    private static String readShaderSource(String name) {
        try (InputStream inputStream = WebDrawParameters.class.getResourceAsStream("/shaders/" + name)) {
            if (inputStream == null) {
                throw new RuntimeException("Could not find shader source " + name);
            }

            byte[] buffer = new byte[1024];
            StringBuilder stringBuilder = new StringBuilder();
            int read;

            while ((read = inputStream.read(buffer)) != -1) {
                stringBuilder.append(new String(buffer, 0, read));
            }

            return stringBuilder.toString();
        } catch (IOException e) {
            throw new RuntimeException("Could not read shader source " + name, e);
        }
    }
}
