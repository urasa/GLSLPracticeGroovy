package sample

import glslpractice.Camera
import glslpractice.GLSLUtils
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
Camera camera = new Camera(0d, 60d, -100d, 0d, 0d, 0d)

GLEventListener glEventListener =
        new GLEventListener() {
            int program = 0
            def size = 3f
            def distance = 5d
            def n = 9
            void display(GLAutoDrawable drawable) {
                drawable.getGL().getGL2().with { gl2 ->
                    glClear GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT
                    glUseProgram program
                    glMatrixMode GL_MODELVIEW
                    glLoadIdentity()
                    camera.look glu

                    //glColor3d(1.0, 1.0, 1.0)
                    def offset = -(n-1)/2*distance
                    glTranslated(offset, offset, offset)
                    n.times { x ->
                        n.times { y ->
                            n.times { z ->
                                glPushMatrix()
                                glTranslated(distance*x, distance*y, distance*z)
                                glut.glutSolidCube size
                                glPopMatrix()
                            }
                        }
                    }
                    glUseProgram 0
                    glFlush()
                }
            }
            void dispose(GLAutoDrawable drawable) {
            }

            void init(GLAutoDrawable drawable) {
                drawable.getGL().getGL2().with { gl2 ->
                    glEnable GL_DEPTH_TEST
                    glClearColor(0.2f, 0.2f, 0.2f, 1.0f)
                    program = GLSLUtils.createShader(gl2, 'glsl/depthColor.vert', null)
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

