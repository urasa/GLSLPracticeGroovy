package glslpractice

import groovy.swing.SwingBuilder

import java.nio.FloatBuffer

import javax.media.opengl.GLAutoDrawable
import javax.media.opengl.GLCapabilities
import javax.media.opengl.GLEventListener
import javax.media.opengl.GLProfile
import javax.media.opengl.awt.GLJPanel
import javax.media.opengl.glu.GLU
import javax.swing.WindowConstants

import com.jogamp.opengl.util.FPSAnimator
import com.jogamp.opengl.util.gl2.GLUT


GLUT glut = new GLUT()
GLU glu = new GLU()
FPSAnimator animator
int fps = 60
Camera camera = new Camera(80d, 80d, 100d, 0d, 0d, 0d)

GLEventListener glEventListener =
        new GLEventListener() {
            int program = 0
            final float size = 2f
            final double distance = 3.5d
            final int n = 11
            def displaylists = [:]
            float[] middle = [1.0f, 0.0f, 0.0f] as float[]
            float[] modelview = new float[16]
            int frame = 0
            int vsMiddleLocation
            int vsModelviewLocation

            void display(GLAutoDrawable drawable) {
                drawable.getGL().getGL2().with { gl2 ->
                    glUseProgram program
                    glClear GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT
                    glMatrixMode GL_MODELVIEW
                    glLoadIdentity()
                    camera.look glu

                    middle[0] = 30*Math.sin(frame/300d*2*Math.PI)
                    middle[1] = 30*Math.cos(frame/300d*3*Math.PI)
                    middle[2] = 30*Math.sin(frame/300d*Math.PI)
                    glUniform3fv(vsMiddleLocation, 1, middle, 0)
                    //                    println "location: ${vsMiddleLocation}, middle: ${middle}"


                    glPushMatrix()
                    glTranslatef(middle[0], middle[1], middle[2])
                    glColor3f(0f, 0f, 0f)
                    glUseProgram 0
                    glut.glutSolidSphere(2d, 30, 30)
                    glUseProgram program
                    glGetFloatv(GL_MODELVIEW_MATRIX, FloatBuffer.wrap(modelview))
                    glUniformMatrix4fv(vsModelviewLocation, 1, false, modelview, 0)
                    glPopMatrix()

                    def offset = -(n-1)/2f*distance
                    glTranslated(offset, offset, offset)
                    glCallList(displaylists['cubes'])
                    glUseProgram 0
                    glFlush()
                    frame++
                }
            }
            void dispose(GLAutoDrawable drawable) {
            }

            void init(GLAutoDrawable drawable) {
                drawable.getGL().getGL2().with { gl2 ->
                    glEnable GL_BLEND
                    glBlendFunc GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA
                    glEnable GL_DEPTH_TEST
                    glClearColor(0.2f, 0.2f, 0.2f, 1.0f)
                    program = GLSLUtils.createShader(gl2, 'glsl/depth.vert', null)
                    vsMiddleLocation = glGetUniformLocation(program, "middle")
                    if (vsMiddleLocation == -1) {
                        println "cant get a location for middle"
                    }
                    vsModelviewLocation = glGetUniformLocation(program, "modelview")
                    if (vsModelviewLocation == -1) {
                        println "cant get a location for modelview"
                    }

                    displaylists['cubes'] = glGenLists(1)
                    glNewList(displaylists['cubes'], GL_COMPILE)
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
                    glEndList()
                }
            }

            def makeLists = { gl2 ->
                gl2.with {
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


GLCapabilities caps = new GLCapabilities(GLProfile.getDefault())
caps.with {
    setNumSamples 4
    setSampleBuffers true
}

new SwingBuilder().edt {
    frame(title:'Sample3D', size:[400, 400], show:true,
    defaultCloseOperation:WindowConstants.EXIT_ON_CLOSE) {
        borderLayout()
        new GLJPanel(caps).with { panel ->
            addGLEventListener glEventListener
            addMouseListener camera
            addMouseMotionListener camera
            widget panel
            new FPSAnimator(panel, fps).start()
        }
    }.addKeyListener camera
}
