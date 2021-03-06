package sample

import glslpractice.GLSLUtils
import groovy.swing.SwingBuilder

import javax.media.opengl.GL2
import javax.media.opengl.GLAutoDrawable
import javax.media.opengl.GLEventListener
import javax.media.opengl.awt.GLJPanel
import javax.swing.WindowConstants

new SwingBuilder().edt {
    frame(title:"Triangle", size:[400, 400], show:true,
    defaultCloseOperation:WindowConstants.EXIT_ON_CLOSE) {
        borderLayout()
        GLJPanel gljpanel = new GLJPanel()
        gljpanel.addGLEventListener new glslTriangleHelper()
        widget gljpanel
    }
}

GLAutoDrawable.metaClass.gl2with = { Closure clos ->
    clos.delegate = delegate.getGL().getGL2()
    clos.call()
}

class glslTriangleHelper implements GLEventListener {
    int program = 0
    public void display(GLAutoDrawable drawable) {
        drawable.gl2with {
            glUseProgram program
            glClear GL2.GL_COLOR_BUFFER_BIT
            drawTriangle delegate
            glUseProgram 0
            glFlush()
        }
    }
    private void drawTriangle(GL2 gl2) {
        gl2.with {
            glLineWidth 10.0f
            glPolygonMode GL2.GL_FRONT_AND_BACK, GL2.GL_LINE
            glBegin GL2.GL_TRIANGLES
            glColor3f(1f,0f,0f); glVertex2f(-0.75f, -0.75f)
            glColor3f(0f,1f,0f); glVertex2f( 0.75f, -0.75f)
            glColor3f(0f,0f,1f); glVertex2f( 0.00f,  0.75f)
            glEnd()
        }
    }

    public void dispose(GLAutoDrawable drawable) {
    }

    public void init(GLAutoDrawable drawable) {
        drawable.gl2with {
            glClearColor(0.2f, 0.2f, 0.2f, 0.0f)
            program = GLSLUtils.createShader delegate, 'glsl/red.vert', null
        }
    }

    public void reshape(GLAutoDrawable drawable, int arg1, int arg2, int arg3, int arg4) {
    }
}
