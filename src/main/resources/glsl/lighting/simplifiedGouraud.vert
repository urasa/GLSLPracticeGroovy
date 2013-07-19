
void main() {
    // ���_�ɑ΂�����W
    vec3 P = vec3(gl_ModelViewMatrix * gl_Vertex);
    // �P�ʖ@���x�N�g��
    vec3 N = normalize(gl_NormalMatrix * gl_Normal);
    // ���_��������ւ̒P�ʕ����x�N�g��
    vec3 L = normalize(gl_LightSource[0].position.xyz - P);
    // ���_�ւ̒P�ʕ����x�N�g��
    vec3 V = normalize(-P);
    // �n�[�t�x�N�g��
    vec3 H = normalize(V + L);
    // ���ʔ��ˎw��
    float n = gl_FrontMaterial.shininess;

    // gl_FrontLightProduct[0].ambient�Ƃ�������
    vec4 ambient = gl_LightSource[0].ambient * gl_FrontMaterial.ambient;
    vec4 diffuse = gl_FrontLightProduct[0].diffuse * max(dot(N, L), 0.0);
    vec4 specular = gl_FrontLightProduct[0].specular * pow(max(dot(H, N), 0.0), n);

    gl_FrontColor = ambient + diffuse + specular;
    
    gl_Position = ftransform();
}

