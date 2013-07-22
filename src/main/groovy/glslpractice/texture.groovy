package glslpractice

import groovy.swing.SwingBuilder

import javax.media.opengl.GLAutoDrawable
import javax.media.opengl.GLCapabilities
import javax.media.opengl.GLEventListener
import javax.media.opengl.GLProfile
import javax.media.opengl.awt.GLJPanel
import javax.media.opengl.glu.GLU
import javax.swing.WindowConstants

import com.jogamp.opengl.util.FPSAnimator
import com.jogamp.opengl.util.gl2.GLUT

def makeTextureData256(int sNum, int tNum) {
	
}

GLUT glut = new GLUT()
GLU glu = new GLU()
int fps = 60
FPSAnimator animator = new FPSAnimator(fps)
Camera camera = new Camera(0d, 0d, 100d, 0d, 0d, 0d)
def displaylists = [:]

///// GLEventListener Implementations ///////////////////////
def init = { GLAutoDrawable drawable ->
    drawable.getGL().getGL2().with { gl2 ->
		glEnable GL_DEPTH_TEST
		glClearColor(0.2f, 0.2f, 0.2f, 0.0f)

        displaylists.grid = glGenLists(1)
        glNewList(displaylists.grid, GL_COMPILE)
        GLSLUtils.drawGrid gl2
		glRotated(90d, 0d, 1d, 0d)
        GLSLUtils.drawGrid gl2
        glEndList()
    }
}
final def cycleTime = 5d
final def rotationAnglePerFrame = (360d / fps) / cycleTime
def display = { GLAutoDrawable drawable ->
    drawable.getGL().getGL2().with { gl2 ->
		glClear GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT
		glMatrixMode GL_MODELVIEW
		glLoadIdentity()
		camera.look glu

		glPushMatrix()
		glRotated(rotationAnglePerFrame*animator.getTotalFPSFrames(), 0d, 1d, 0d)
		glut.glutSolidSphere(30d, 20, 20)
		glTranslated(40d, 0d, 0d)
		glRotated(rotationAnglePerFrame*animator.getTotalFPSFrames(), 0d, 1d, 0d)
		glut.glutSolidCube 10f
		glPopMatrix()

		glUseProgram 0
		glColor3f(0.1f, 0.1f, 0.1f)
		glCallList displaylists.grid

    }
}
def reshape = { GLAutoDrawable drawable, int x, int y, int width, int height ->
    drawable.getGL().getGL2().with { gl2 ->
		glMatrixMode GL_PROJECTION
		glLoadIdentity()
		glu.gluPerspective(30f, (float)width/(float)height, 1f, 10000f);
    }
}


///// Frame Construction ////////////////////////////////////
GLCapabilities caps = new GLCapabilities(GLProfile.getDefault())
caps.with {
    setNumSamples 4
    setSampleBuffers true
}

new SwingBuilder().edt {
    frame(title:'テクスチャ', size:[400, 400], show:true,
    defaultCloseOperation:WindowConstants.EXIT_ON_CLOSE) {
        borderLayout()
        new GLJPanel(caps).with { panel ->
            GLEventListener listener = GLEventListenerFactory.createListener(
                    init: init, display: display, reshape: reshape)
            addGLEventListener listener
            addMouseListener camera
            addMouseMotionListener camera
            animator.add panel
            widget panel
        }
    }.addKeyListener camera
}
animator.start()
