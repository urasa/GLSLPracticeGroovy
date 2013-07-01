package sample

import glslpractice.Camera
import groovy.swing.SwingBuilder

import javax.media.opengl.GLAutoDrawable
import javax.media.opengl.GLEventListener
import javax.media.opengl.awt.GLJPanel
import javax.media.opengl.glu.GLU
import javax.swing.WindowConstants

import com.jogamp.opengl.util.FPSAnimator
import com.jogamp.opengl.util.gl2.GLUT


GLUT glut = new GLUT()
GLU glu = new GLU()
FPSAnimator animator
int fps = 30
Camera camera = new Camera(0d, 30d, -45d, 0d, 0d, 0d)

GLEventListener glEventListener =
        new GLEventListener() {
            void display(GLAutoDrawable drawable) {
                drawable.getGL().getGL2().with {
                    glClear GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT

                    glMatrixMode GL_MODELVIEW
                    glLoadIdentity()

                    camera.look glu

                    glut.glutSolidCube 10f

                    glFlush()
                }
            }
            void dispose(GLAutoDrawable drawable) {
            }

            void init(GLAutoDrawable drawable) {
                drawable.getGL().getGL2().with {
                    glClearColor(0.2f, 0.2f, 0.2f, 1.0f)
                }
            }

            void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
                drawable.getGL().getGL2().with {
                    glMatrixMode GL_PROJECTION
                    glLoadIdentity()
                    glu.gluPerspective(30f, (float)width/(float)height, 1f, 10000f);
                }
            }
        }

new SwingBuilder().edt {
    frame(title:'Sample3D', size:[400, 400], show:true,
    defaultCloseOperation:WindowConstants.EXIT_ON_CLOSE) {
        borderLayout()
        new GLJPanel().with { panel ->
            addGLEventListener glEventListener
            addMouseListener camera
            addMouseMotionListener camera
            widget panel
            new FPSAnimator(panel, fps).start()
        }
    }.addKeyListener camera
}
