/*
 * オフスクリーンバッファに描画して
 * それをテクスチャとして貼り付ける。
 */
package glslpractice

import groovy.swing.SwingBuilder

import java.nio.FloatBuffer
import java.nio.IntBuffer

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
int fps = 60
FPSAnimator animator = new FPSAnimator(fps)
Camera camera = new Camera(0d, 0d, 100d, 0d, 0d, 0d)
def shaders = [:].withDefault { 0 }
def displaylists = [:]
def textures = [:]

final int texWidth = 1000
final int texHeight = 1000


///// GLEventListener Implementations ///////////////////////
def init = { GLAutoDrawable drawable ->
    drawable.getGL().getGL2().with { gl2 ->
        glEnable GL_DEPTH_TEST
        glClearColor(0.2f, 0.2f, 0.2f, 0.0f)

        shaders.S_GOURAUD = GLSLUtils.createShader(gl2,
                'glsl/lighting/simplifiedGouraud.vert', null)
        shaders.GOURAUD = GLSLUtils.createShader(gl2,
                'glsl/lighting/gouraud.vert', null)
        shaders.PHONG = GLSLUtils.createShader(gl2,
                'glsl/lighting/phong.vert', 'glsl/lighting/phong.frag')
        shaders.FLAT = shaders.S_GOURAUD
        shaders.tex = GLSLUtils.createShader gl2,
                'glsl/texture/texture.vert', 'glsl/offscr/offscr.frag'

        displaylists.grid = glGenLists(1)
        glNewList(displaylists.grid, GL_COMPILE)
        GLSLUtils.drawGrid gl2
        glRotated(90d, 0d, 1d, 0d)
        GLSLUtils.drawGrid gl2
        glEndList()

        glMaterialfv GL_FRONT, GL_AMBIENT_AND_DIFFUSE,
                FloatBuffer.wrap([0.8f, 0.2f, 0.2f, 1.0f] as float[])
        glMaterialfv GL_FRONT, GL_SPECULAR,
                FloatBuffer.wrap([0.5f, 0.5f, 0.5f, 1.0f] as float[])
        glMaterialf GL_FRONT, GL_SHININESS, 100

        glPixelStorei GL_UNPACK_ALIGNMENT, 1
        int[] texname = [-1]
        glGenTextures(1, IntBuffer.wrap(texname))
        textures.offscr = texname[0]
        glBindTexture GL_TEXTURE_2D, textures.offscr
        glTexImage2D GL_TEXTURE_2D, 0, GL_RGBA, texWidth, texHeight, 0,
                GL_RGBA, GL_INT, null
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glBindTexture GL_TEXTURE_2D, 0
    }
}
def (frameWidth, frameHeight) = [200, 200]
def reshape = { GLAutoDrawable drawable, int x, int y, int width, int height ->
    drawable.getGL().getGL2().with { gl2 ->
        (frameWidth, frameHeight) = [width, height]
        glViewport(x, y, width, height)
        glMatrixMode GL_PROJECTION
        glLoadIdentity()
        glu.gluPerspective(30f, (float)width/(float)height, 1f, 10000f);
    }
}
final def cycleTime = 5d
final def rotationAnglePerFrame = (360d / fps) / cycleTime
def drawTexScene = { GLAutoDrawable drawable, int width, int height ->
    drawable.getGL().getGL2().with { gl2 ->
        glViewport(0, 0, width, height)
        glMatrixMode GL_PROJECTION
        glLoadIdentity()
        glu.gluPerspective(30f, (float)width/(float)height, 1f, 10000f);
        // シェーディングモデルの適用
        glShadeModel GL_FLAT
        glUseProgram shaders.FLAT
        //        gl2.glShadeModel GL_SMOOTH
        //        gl2.glUseProgram shaders.S_GOURAUD
        glClearColor(1f, 1f, 1f, 1f)
        glClear GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT
        glMatrixMode GL_MODELVIEW
        glLoadIdentity()
        camera.look glu
        glLightfv GL_LIGHT0, GL_POSITION, FloatBuffer.wrap([100f, 100f, 0f, 1f] as float[])

        glPushMatrix()
        glRotated(rotationAnglePerFrame*animator.getTotalFPSFrames(), 0d, 1d, 0d)
        glut.glutSolidSphere(30d, 20, 20)
        glTranslated(40d, 0d, 0d)
        glRotated(rotationAnglePerFrame*animator.getTotalFPSFrames(), 0d, 1d, 0d)
        glut.glutSolidCube 10f
        glPopMatrix()
    }
}
def display = { GLAutoDrawable drawable ->
    drawable.getGL().getGL2().with { gl2 ->
        // 1. テクスチャとなる画像の描画
        glBindTexture GL_TEXTURE_2D, textures.offscr
        drawTexScene drawable, texWidth, texHeight
        glCopyTexSubImage2D GL_TEXTURE_2D, 0, 0, 0, 0, 0, texWidth, texHeight
        glBindTexture GL_TEXTURE_2D, 0
        //        /*
        // 2. テクスチャを面に貼り付ける
        glShadeModel GL_SMOOTH
        reshape.call(drawable, 0, 0, frameWidth, frameHeight)
        glClearColor(0.2f, 0.2f, 0.2f, 1f)
        glClear GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT
        glMatrixMode GL_MODELVIEW
        glLoadIdentity()
        glu.gluLookAt(0, 0, 100, 0, 0, 0, 0, 1, 0)
        glActiveTexture GL_TEXTURE0
        glBindTexture GL_TEXTURE_2D, textures.offscr
        glUseProgram shaders.tex
        def samplerLocation = glGetUniformLocation shaders.tex, 'sampler'
        glUniform1i samplerLocation, 0
        glEnable GL_TEXTURE_2D
        glBegin GL_QUADS
        glTexCoord2f(0f, 0f); glVertex3f(-50f, -50f, -10f)
        glTexCoord2f(1f, 0f); glVertex3f( 50f, -50f, -10f)
        glTexCoord2f(1f, 1f); glVertex3f( 50f,  50f, -10f)
        glTexCoord2f(0f, 1f); glVertex3f(-50f,  50f, -10f)
        glEnd()
        glDisable GL_TEXTURE_2D
        glBegin GL_QUADS
        glVertex3f( 10f, 10f, 0f)
        glVertex3f( 20f, 10f, 0f)
        glVertex3f( 20f, 20f, 0f)
        glVertex3f( 10f, 20f, 0f)
        glEnd()
        //         */
        glUseProgram 0
        glColor3f(0.1f, 0.1f, 0.1f)
        glCallList displaylists.grid
    }
}


///// Frame Construction ////////////////////////////////////
GLCapabilities caps = new GLCapabilities(GLProfile.getDefault())
caps.with {
    setNumSamples 4
    setSampleBuffers true
}

new SwingBuilder().edt {
    frame(title:'ライティング', size:[400, 400], show:true,
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
