/*
 * シェーダを使用してライティングしてみる
 * また、つぎのシェーディングをためす
 * 1. フラット・シェーディング
 * 2. グーロー・シェーディング（フォンの修正反射モデルver.）
 * 3. グーロー・シェーディング
 * 4. フォン・シェーディング
 */
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
int fps = 60
FPSAnimator animator = new FPSAnimator(fps)
Camera camera = new Camera(0d, 0d, 100d, 0d, 0d, 0d)
def shaders = [:].withDefault {0}

///// GLEventListener Implementations ///////////////////////
def init = { GLAutoDrawable drawable ->
    drawable.getGL().getGL2().with { gl2 ->
        glEnable GL_DEPTH_TEST
        glClearColor(0.2f, 0.2f, 0.2f, 0.0f)
        shaders.gourarud = GLSLUtils.createShader(gl2,
                'glsl/lighting/gouraud.vert', null)
        shaders.phong = GLSLUtils.createShader(gl2,
                'glsl/lighting/phong.vert', 'glsl/lighting/phong.frag')
        glShadeModel GL_SMOOTH
        // 固定機能パイプライン使用時には必要: GL_LIGHT0, GL_LIGHTING
        glEnable GL_LIGHTING
        glEnable GL_LIGHT0
        glLightfv GL_LIGHT0, GL_POSITION, FloatBuffer.wrap([100f, 100f, 0f, 0f] as float[])
    }
}
final def cycleTime = 5d
final def rotationAnglePerFrame = (360d / fps) / cycleTime
def display = { GLAutoDrawable drawable ->
    drawable.getGL().getGL2().with { gl2 ->
        //glUseProgram shaders.gouraud
        glUseProgram 0
        glClear GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT
        glMatrixMode GL_MODELVIEW
        glLoadIdentity()
        camera.look glu

        glPushMatrix()
        glRotated(rotationAnglePerFrame*animator.getTotalFPSFrames(), 0d, 1d, 0d)
        glut.glutSolidSphere(30d, 10, 10)
        glPopMatrix()

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
