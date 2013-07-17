uniform vec3 middle;
uniform mat4 modelview;

void main(void)
{
    gl_Position  = ftransform();
    vec3 light_position = (gl_ProjectionMatrix * modelview * vec4(vec3(0), 1)).xyz;
    vec3 distanceToLight = (gl_Position.xyz - light_position);
	float r = dot(distanceToLight, distanceToLight);
	gl_FrontColor = vec4(0.8 - (vec3(r) / 20000.0), 1.0);
//	gl_FrontColor = vec4(middle, 1.0);
}
