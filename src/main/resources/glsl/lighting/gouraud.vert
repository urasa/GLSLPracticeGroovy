
void main() {
    // 視点に対する座標
    vec3 P = vec3(gl_ModelViewMatrix * gl_Vertex);
    // 単位法線ベクトル
    vec3 N = normalize(gl_NormalMatrix * gl_Normal);
    // 頂点から光源への単位方向ベクトル
    vec3 L = normalize(gl_LightSource[0].position.xyz - P);
    // 正規化した正反射ベクトル
    vec3 R = 2.0 * dot(L, N) * N - L;
    // 視点への単位方向ベクトル
    vec3 V = normalize(-P);
    // 鏡面反射指数
    float n = gl_FrontMaterial.shininess;

    // gl_FrontLightProduct[0].ambientとも書ける
    vec4 ambient = gl_LightSource[0].ambient * gl_FrontMaterial.ambient;
    vec4 diffuse = gl_FrontLightProduct[0].diffuse * max(dot(N, L), 0.0);
    vec4 specular = gl_FrontLightProduct[0].specular * pow(dot(R, V), n);

    gl_FrontColor = ambient + diffuse + specular;
    
    gl_Position = ftransform();
}

