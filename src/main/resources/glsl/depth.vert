uniform vec3 middle;
uniform mat4 modelview;

void main(void)
{
    gl_Position  = ftransform();
    vec3 middle2 = (gl_ProjectionMatrix * modelview * vec4(vec3(0), 1)).xyz;
    vec3 D = (gl_Position.xyz - middle2);
	float r = dot(D, D);
	gl_FrontColor = vec4(0.8 - (vec3(r) / 20000.0), 1.0);
//	gl_FrontColor = vec4(middle, 1.0);
}
