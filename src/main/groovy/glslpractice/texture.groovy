package glslpractice

import groovy.swing.SwingBuilder

import java.nio.DoubleBuffer;
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

def makeTextureData(int size, int sNum, int tNum) {
	final int WIDTH = size
	final int HEIGHT = size
	int[] data = new int[WIDTH*HEIGHT]
	HEIGHT.times { j ->
		WIDTH.times { i ->
			int n = j*WIDTH+i
			int val = ((i.intdiv(sNum)+1)%2)*255
			val = 255 + (val << 8) + (val << 16) + (val << 24)
			data[n] = val
//			println "[j:$j,i:$i]"
//			3.times { data[n+it] = val }
//			data[n+3] = 255
		}
	}
	// TODO ボーダーからチェックへんこうする
	return data
}

GLUT glut = new GLUT()
GLU glu = new GLU()
int fps = 60
FPSAnimator animator = new FPSAnimator(fps)
Camera camera = new Camera(0d, 0d, 100d, 0d, 0d, 0d)
def shaders = [:]
def displaylists = [:]
class Texture {
	int name
	int[] data
	Texture(Map map) {
		this.name = map.name
		this.data = map.data
	}
}
def textures = [:].withDefault { new Texture(name:0, data:null) }

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
		
		shaders.tex = GLSLUtils.createShader gl2,
				'glsl/texture/texture.vert', 'glsl/texture/texture.frag'
		
		textures.tex.data = makeTextureData(64, 8, 8)
//		println textures.tex.data
		glPixelStorei GL_UNPACK_ALIGNMENT, 1
		int[] texname = [-1]
		glGenTextures(1, IntBuffer.wrap(texname))
		textures.tex.name = texname[0]
		println "texname: ${texname}, textures.tex.name: ${textures.tex.name}"
		glBindTexture GL_TEXTURE_2D, textures.tex.name
		glTexImage2D GL_TEXTURE_2D, 0, GL_RGBA,
				64, 64, 0, GL_RGBA, GL_UNSIGNED_INT_8_8_8_8, IntBuffer.wrap(textures.tex.data)
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glBindTexture GL_TEXTURE_2D, 0

//		/* 頂点のオブジェクト空間における座標値をテクスチャ座標に使う */
//		glTexGeni GL_S, GL_TEXTURE_GEN_MODE, GL_OBJECT_LINEAR
//		glTexGeni GL_T, GL_TEXTURE_GEN_MODE, GL_OBJECT_LINEAR
//		/* テクスチャ座標生成関数の設定 */
//		glTexGendv GL_S, GL_OBJECT_PLANE, DoubleBuffer.wrap([1.0, 0.0, 0.0] as double[])
//		glTexGendv GL_T, GL_OBJECT_PLANE, DoubleBuffer.wrap([0.0, 1.0, 0.0] as double[])
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
		glBindTexture GL_TEXTURE_2D, textures.tex.name
		
		glUseProgram shaders.tex
		def samplerLocation = glGetUniformLocation shaders.tex, 'sampler'
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
		
		/* テクスチャ座標の自動生成を有効にする */
//		glEnable GL_TEXTURE_GEN_S
//		glEnable GL_TEXTURE_GEN_T
		
		glPushMatrix()
		glRotated(rotationAnglePerFrame*animator.getTotalFPSFrames(), 0d, 1d, 0d)
//		glut.glutSolidSphere(30d, 20, 20)
		glTranslated(40d, 0d, 0d)
		glRotated(rotationAnglePerFrame*animator.getTotalFPSFrames(), 0d, 1d, 0d)
		glut.glutSolidCube 10f
		glPopMatrix()

		/* テクスチャ座標の自動生成を無効にする */
//		glDisable GL_TEXTURE_GEN_S
//		glDisable GL_TEXTURE_GEN_T
	  
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
