uniform vec3 middle;
uniform mat4 modelview;

void main(void)
{
    gl_Position  = ftransform();
    vec3 middle2 = (gl_ProjectionMatrix * modelview * vec4(vec3(0), 1)).xyz;
    vec3 D = (gl_Position.xyz - middle2);
	float r = dot(D, D);
//	gl_FrontColor = vec4(0.8 - (vec3(r) / 20000), 0.3);
	gl_FrontColor = vec4(vec3(0.6), clamp(1.0 - (r / 10000), 0.1, 0.9));
//	gl_FrontColor = vec4(clamp(1.0 - (r / 20000), 0.1, 0.9));
}
