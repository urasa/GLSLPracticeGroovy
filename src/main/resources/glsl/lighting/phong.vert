varying vec3 P;
varying vec3 N;

void main() {
    gl_Position = ftransform();
    // ���_�ɑ΂�����W
    P = vec3(gl_ModelViewMatrix * gl_Vertex);
    // �P�ʖ@���x�N�g��
    N = normalize(gl_NormalMatrix * gl_Normal);
}

