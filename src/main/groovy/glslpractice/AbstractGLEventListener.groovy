package glslpractice

import javax.media.opengl.GLAutoDrawable
import javax.media.opengl.GLEventListener

class AbstractGLEventListener implements GLEventListener{
    Closure displayClos
    Closure disposeClos
    Closure initClos
    Closure reshapeClos
    AbstractGLEventListener() {
        this({},{},{},{drawable, x, y, width, height -> })
    }
    AbstractGLEventListener(Closure display, Closure dispose, Closure init, Closure reshape) {
        setDisplay display
        setDispose dispose
        setInit init
        setReshape reshape
    }
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
    def setDisplay(Closure clos) {
        displayClos = clos
        return this
    }
    def setDispose(Closure clos) {
        disposeClos = clos
        return this
    }
    def setInit(Closure clos) {
        initClos = clos
        return this
    }
    def setReshape(Closure clos) {
        reshapeClos = clos
        return this
    }
}
