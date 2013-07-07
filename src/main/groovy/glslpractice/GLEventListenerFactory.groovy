package glslpractice

import groovy.transform.Immutable

import javax.media.opengl.GLAutoDrawable
import javax.media.opengl.GLEventListener

@Immutable
class GLEventListenerFactory {
    static GLEventListener createListener(Map arg) {
        new GLEventListener() {
                    def displayClos = arg.display?:{}
                    def disposeClos = arg.dispose?:{}
                    def initClos    = arg.init?:{}
                    def reshapeClos = arg.reshape?:{drawable, x, y, width, height -> }
                    @Override
                    public void display(GLAutoDrawable drawable) {
                        displayClos drawable
                    }

                    @Override
                    public void dispose(GLAutoDrawable drawable) {
                        disposeClos drawable
                    }

                    @Override
                    public void init(GLAutoDrawable drawable) {
                        initClos drawable
                    }

                    @Override
                    public void reshape(GLAutoDrawable drawable, int x, int y,
                            int width, int height) {
                        reshapeClos drawable, x, y, width, height
                    }
                }
    }
}
