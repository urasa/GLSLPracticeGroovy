package sample

import groovy.swing.SwingBuilder

import javax.media.opengl.GL2
import javax.media.opengl.GLAutoDrawable
import javax.media.opengl.GLEventListener
import javax.media.opengl.awt.GLJPanel
import javax.swing.WindowConstants




GLEventListener glEventListener =
        new GLEventListener() {
            void display(GLAutoDrawable drawable) {
                drawable.getGL().getGL2().with { glClear GL2.GL_COLOR_BUFFER_BIT }
            }
            void dispose(GLAutoDrawable drawable) {
            }

            void init(GLAutoDrawable drawable) {
                drawable.getGL().getGL2().with {
                    glClearColor(0.2f, 0.2f, 0.2f, 1.0f)
                }
            }

            void reshape(GLAutoDrawable drawable, int arg1, int arg2, int arg3, int arg4) {
            }
        }

new SwingBuilder().edt {
    frame(title:'Sample3D', size:[400, 400], show:true,
    defaultCloseOperation:WindowConstants.EXIT_ON_CLOSE) {
        borderLayout()
        GLJPanel gljpanel = new GLJPanel()
        gljpanel.addGLEventListener glEventListener
        widget gljpanel
    }
}