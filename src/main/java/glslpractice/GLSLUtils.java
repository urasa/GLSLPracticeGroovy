package glslpractice;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Scanner;

import javax.media.opengl.GL2;

public class GLSLUtils {
    /**
     * 指定されたInputStremから読み出しStringとして返す
     */
    public static String readFromStream(InputStream in) {
        if (in == null) {
            throw new NullPointerException("argument `in` is null");
        }
        StringBuilder buf = new StringBuilder();
        Scanner scanner = new Scanner(in);
        try {
            while (scanner.hasNextLine()) {
                buf.append(scanner.nextLine() + "\n");
            }
        }
        finally {
            scanner.close();
        }
        return buf.toString();
    }

    /**
     * @throws IOException
     * 
     */
    public static int createShader(GL2 gl, String vertexFile,
            String fragmentFile) {

        int shaderProgram = gl.glCreateProgram();

        if (vertexFile != null) {
            int v = gl.glCreateShader(GL2.GL_VERTEX_SHADER);
            if (compileShader(gl, v, vertexFile)) {
                gl.glAttachShader(shaderProgram, v);
                gl.glDeleteShader(v);
            }
            else {
            }
        }

        if (fragmentFile != null) {
            int f = gl.glCreateShader(GL2.GL_FRAGMENT_SHADER);
            if (compileShader(gl, f, fragmentFile)) {
                gl.glAttachShader(shaderProgram, f);
                gl.glDeleteShader(f);
            }
            else {
            }
        }

        gl.glLinkProgram(shaderProgram);
        int[] linkStatus = new int[1];
        gl.glGetProgramiv(shaderProgram, GL2.GL_LINK_STATUS,
                IntBuffer.wrap(linkStatus));
        if (linkStatus[0] == GL2.GL_FALSE) {
            // exeption
        }
        gl.glValidateProgram(shaderProgram);

        return shaderProgram;
    }

    private static boolean compileShader(GL2 gl, int shader,
            String sourceFile) {
        String shaderCode = readFromStream(Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(sourceFile));
        gl.glShaderSource(shader, 1, new String[]{shaderCode}, null);
        gl.glCompileShader(shader);
        int[] status = new int[1];
        gl.glGetShaderiv(shader, GL2.GL_COMPILE_STATUS,
                IntBuffer.wrap(status));
        switch (status[0]) {
        case GL2.GL_TRUE:
            return true;
        case GL2.GL_FALSE:
            // output error log
            String infolog = getInfoLog(gl, shader);
            System.out.println(infolog);
            return false;
        default:
            return false;
        }
    }

    private static String getInfoLog(GL2 gl, int shader) {
        int[] infologLength = new int[1];
        gl.glGetShaderiv(shader, GL2.GL_INFO_LOG_LENGTH,
                IntBuffer.wrap(infologLength));
        byte[] infolog = new byte[infologLength[0]];
        gl.glGetShaderInfoLog(shader, infologLength[0], null,
                ByteBuffer.wrap(infolog));
        return new String(infolog);
    }
}
