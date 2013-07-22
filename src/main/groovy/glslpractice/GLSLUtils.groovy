package glslpractice

import java.nio.ByteBuffer
import java.nio.IntBuffer

import javax.media.opengl.GL2


class GLSLUtils {
    private GLSLUtils() {
    }
    static int createShader(GL2 gl,
            String vertexFile, String fragmentFile) {
        gl.with {
            int shaderProgram = glCreateProgram()

            if (vertexFile) {
                int v = glCreateShader GL_VERTEX_SHADER
                if (compileShader(gl, v, vertexFile)) {
                    glAttachShader shaderProgram, v
                    glDeleteShader v
                }
            }
            if (fragmentFile) {
                int f = glCreateShader GL_FRAGMENT_SHADER
                if (compileShader(gl, f, fragmentFile)) {
                    glAttachShader shaderProgram, f
                    glDeleteShader f
                }
            }

            glLinkProgram shaderProgram
            int[] linkStatus  = new int[1]
            glGetProgramiv(shaderProgram, GL_LINK_STATUS,
                    IntBuffer.wrap(linkStatus))
            if (linkStatus[0] == GL_FALSE) {
                // exception
            }
            glValidateProgram shaderProgram
            return shaderProgram
        }
    }

    private static boolean compileShader(GL2 gl,
            int shader, String sourceFile) {
        String[] shaderCode = [
            new File(Thread.currentThread().getContextClassLoader()
            .getResource(sourceFile).toURI()).text
        ] as String[]
        println "shaderCode:\n${shaderCode[0]}"
        gl.with {
            glShaderSource shader, 1, shaderCode, null
            glCompileShader shader
            int[] status = [1] as int[]
            glGetShaderiv shader, GL_COMPILE_STATUS,
                    IntBuffer.wrap(status)
            if (status[0] == GL_FALSE) {
                println getInfoLog(gl, shader)
                return false
            }
            else {
                return true
            }
        }
    }

    private static String getInfoLog(GL2 gl, int shader) {
        int[] infologLength = new int[1]
        gl.with {
            glGetShaderiv shader, GL_INFO_LOG_LENGTH,
                    IntBuffer.wrap(infologLength)
            byte[] infolog = new byte[infologLength[0]]
            glGetShaderInfoLog shader, infologLength[0],
                    null, ByteBuffer.wrap(infolog)
            new String(infolog)
        }
    }

	static def drawGrid(GL2 gl2) {
		gl2.with {
			final float size = 1000f
			final int step = 25
	        glBegin GL_LINES
	        glVertex3f(0f, 0f, -size)
	        glVertex3f(0f, 0f, size)
	        (0..size).step(step) { x ->
	            glVertex3f(x, 0f, -size)
	            glVertex3f(x, 0f, size)
	            glVertex3f(-x, 0f, -size)
	            glVertex3f(-x, 0f, size)
	        }
	        glEnd()
	    }
	}
}
