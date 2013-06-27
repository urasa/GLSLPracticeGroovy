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
        gljpanel.addGLEventListener(new glslTriangleHelper())
        widget(gljpanel)
    }
}

class glslTriangleHelper implements GLEventListener {
    int program = 0
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2()
        gl.glUseProgram(program)
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT)
        drawTriangle(gl)
        gl.glUseProgram(0)
        gl.glFlush()
    }
    private void drawTriangle(GL2 gl) {
        gl.glLineWidth(10.0f)
        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE)
        gl.glBegin(GL2.GL_TRIANGLES)
        gl.glColor3f(1f,0f,0f); gl.glVertex2f(-0.75f, -0.75f)
        gl.glColor3f(0f,1f,0f); gl.glVertex2f( 0.75f, -0.75f)
        gl.glColor3f(0f,0f,1f); gl.glVertex2f( 0.00f,  0.75f)
        gl.glEnd()
    }

    public void dispose(GLAutoDrawable drawable) {
    }

    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2()
        gl.glClearColor(0.2f, 0.2f, 0.2f, 0.0f)
        program = GLSLUtils.createShader(gl, 'glsl/red.vert', null)
    }

    public void reshape(GLAutoDrawable drawable, int arg1, int arg2, int arg3, int arg4) {
    }
}
