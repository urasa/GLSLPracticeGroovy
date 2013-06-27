package sample;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLJPanel;
import javax.swing.JFrame;

public class GLTriangle extends JFrame {
	public GLTriangle() {
		GLJPanel panel = new GLJPanel();
		GLEventListener listener = new GLHandler();
		panel.addGLEventListener(listener);

		getContentPane().add(panel);

		setSize(400, 400);
		setTitle(this.getClass().getName());
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
	}
	private class GLHandler implements GLEventListener {
		public void display(GLAutoDrawable drawable) {
			GL2 gl = drawable.getGL().getGL2();

			gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
			drawTriangle(gl);
			gl.glFlush();
		}
		private void drawTriangle(GL2 gl) {
			gl.glLineWidth(10.0f);
			gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
			gl.glBegin(GL2.GL_TRIANGLES);
				gl.glColor3f(1f,0f,0f); gl.glVertex2f(-0.75f, -0.75f);
				gl.glColor3f(0f,1f,0f); gl.glVertex2f( 0.75f, -0.75f);
				gl.glColor3f(0f,0f,1f); gl.glVertex2f( 0.00f,  0.75f);
			gl.glEnd();
		}

		public void dispose(GLAutoDrawable drawable) {}

		public void init(GLAutoDrawable drawable) {
			GL2 gl = drawable.getGL().getGL2();
			gl.glClearColor(0.2f, 0.2f, 0.2f, 0.0f);
		}

		public void reshape(GLAutoDrawable drawable, int arg1, int arg2, int arg3, int arg4) {}

	}
	public static void main(String[] args) {
		new GLTriangle();
	}
}

