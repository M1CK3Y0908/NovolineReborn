package cc.novoline.utils.shader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.lwjgl.opengl.GL20.*;

public class GLSLSandboxShader {
    private final int programId;
    private final int timeUniform;
    private final int mouseUniform;
    private final int resolutionUniform;

    public GLSLSandboxShader(String fragmentShaderLocation) throws IOException {
        int program = glCreateProgram();
        if (program == 0) {
            throw new IOException("Failed to create shader program");
        }

        int vertShader = 0;
        try {
            InputStream vertStream = GLSLSandboxShader.class.getResourceAsStream("/assets/minecraft/shaders/program/novoline_vector.vsh");
            if (vertStream != null) {
                vertShader = createShader(vertStream, GL_VERTEX_SHADER);
                if (vertShader != 0) {
                    glAttachShader(program, vertShader);
                }
            }
        } catch (Throwable t) {
            // Vertex shader fallback
        }

        InputStream fragStream = GLSLSandboxShader.class.getResourceAsStream(fragmentShaderLocation);
        if (fragStream == null) {
            glDeleteProgram(program);
            throw new IOException("Fragment shader resource not found: " + fragmentShaderLocation);
        }

        int fragShader = createShader(fragStream, GL_FRAGMENT_SHADER);
        glAttachShader(program, fragShader);

        glLinkProgram(program);

        int linked = glGetProgrami(program, GL_LINK_STATUS);

        // If linking failed
        if (linked == 0) {
            String log = glGetProgramInfoLog(program, glGetProgrami(program, GL_INFO_LOG_LENGTH));
            System.err.println("GLSLSandboxShader link error: " + log);
            glDeleteProgram(program);
            throw new IOException("Shader link failed: " + log);
        }

        this.programId = program;

        // Setup uniforms
        glUseProgram(program);

        this.timeUniform = glGetUniformLocation(program, "time");
        this.mouseUniform = glGetUniformLocation(program, "mouse");
        this.resolutionUniform = glGetUniformLocation(program, "resolution");

        glUseProgram(0);
    }

    public void useShader(int width, int height, float mouseX, float mouseY, float time) {
        if (this.programId == 0) return;
        glUseProgram(this.programId);

        glUniform2f(this.resolutionUniform, width, height);
        glUniform2f(this.mouseUniform, mouseX / width, 1.0f - mouseY / height);
        glUniform1f(this.timeUniform, time);
    }

    private int createShader(InputStream inputStream, int shaderType) throws IOException {
        int shader = glCreateShader(shaderType);
        if (shader == 0) {
            throw new IOException("glCreateShader failed");
        }

        glShaderSource(shader, readStreamToString(inputStream));

        glCompileShader(shader);

        int compiled = glGetShaderi(shader, GL_COMPILE_STATUS);

        // If compilation failed
        if (compiled == 0) {
            String log = glGetShaderInfoLog(shader, glGetShaderi(shader, GL_INFO_LOG_LENGTH));
            System.err.println("GLSLSandboxShader compile error: " + log);
            glDeleteShader(shader);
            throw new IOException("Shader compile failed: " + log);
        }

        return shader;
    }

    private String readStreamToString(InputStream inputStream) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        byte[] buffer = new byte[512];

        int read;

        while ((read = inputStream.read(buffer, 0, buffer.length)) != -1) {
            out.write(buffer, 0, read);
        }

        return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }
}