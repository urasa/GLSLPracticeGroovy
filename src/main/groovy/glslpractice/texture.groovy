package glslpractice

import groovy.swing.SwingBuilder

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


/**
 * 正方形のチェック柄のテクスチャデータを作成する
 * @param size テクスチャの一辺のサイズ
 * @param sNum チェック柄の横方向の分割数
 * @param tNum チェック柄の縦方向の分割数
 * @return チェック柄のデータ
 */
def makeTextureData(int size, int sNum, int tNum) {
    final int WIDTH = size
    final int HEIGHT = size
    int[] data = new int[WIDTH*HEIGHT]
    final int black = 0x000000ff
    final int white = 0xffffffff
    HEIGHT.times { int j ->
        WIDTH.times { i ->
            int n = j*WIDTH+i
            if (i.intdiv(sNum)%2 == j.intdiv(tNum)%2) {
                data[n] =  white
            }
            else {
                data[n] =  black
            }
        }
    }
    return data
}

GLUT glut = new GLUT()
GLU glu = new GLU()
int fps = 60
FPSAnimator animator = new FPSAnimator(fps)
Camera camera = new Camera(0d, 0d, 100d, 0d, 0d, 0d)
def shaders = [:]
def displaylists = [:]
class TextureData {
    int name
    int[] data
    TextureData(Map map) {
        this.name = map.name
        this.data = map.data
    }
}
def textures = [:].withDefault { new TextureData(name:0, data:null) }

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

        shaders.check = GLSLUtils.createShader gl2,
                'glsl/texture/texture.vert', 'glsl/texture/texture.frag'

        textures.check.data = makeTextureData(64, 8, 8)
        //      println textures.check.data
        glPixelStorei GL_UNPACK_ALIGNMENT, 1
        int[] texname = [-1]
        glGenTextures(1, IntBuffer.wrap(texname))
        textures.check.name = texname[0]
        println "texname: ${texname}, textures.check.name: ${textures.check.name}"
        glBindTexture GL_TEXTURE_2D, textures.check.name
        glTexImage2D GL_TEXTURE_2D, 0, GL_RGBA,
                64, 64, 0, GL_RGBA, GL_UNSIGNED_INT_8_8_8_8, IntBuffer.wrap(textures.check.data)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glBindTexture GL_TEXTURE_2D, 0

        int[] a = [0]
        glGetIntegerv(GL_MAX_TEXTURE_UNITS, IntBuffer.wrap(a))
        println "max texture units: $a"
    }
}
final def cycleTime = 5d
final def rotationAnglePerFrame = (360d / fps) / cycleTime
def display = { GLAutoDrawable drawable ->
    drawable.getGL().getGL2().with { gl2 ->
        glActiveTexture GL_TEXTURE0
        glBindTexture GL_TEXTURE_2D, textures.check.name

        glUseProgram shaders.check
        def samplerLocation = glGetUniformLocation shaders.check, 'sampler'
        glUniform1i samplerLocation, 0

        glClear GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT
        glMatrixMode GL_MODELVIEW
        glLoadIdentity()
        camera.look glu

        glColor3f(0.7f, 0.1f, 0.1f)

        glEnable GL_TEXTURE_2D
        glBegin GL_TRIANGLES
        glNormal3d(0d, 0d, 1d)
        glTexCoord2d(0d, 0d); glVertex3d(-50d, 0d, 0d)
        glTexCoord2d(1d, 0d); glVertex3d( 50d, 0d, 0d)
        glTexCoord2d(0d, 1d); glVertex3d(-50d, 100d, 0d)
        glEnd()
        glDisable GL_TEXTURE_2D

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
