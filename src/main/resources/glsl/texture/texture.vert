varying vec3 P;
varying vec3 N;

void main() {
    gl_Position = ftransform();
    // 視点に対する座標
    P = vec3(gl_ModelViewMatrix * gl_Vertex);
    // 単位法線ベクトル
    N = normalize(gl_NormalMatrix * gl_Normal);
    gl_TexCoord[0] = gl_MultiTexCoord0;
}
