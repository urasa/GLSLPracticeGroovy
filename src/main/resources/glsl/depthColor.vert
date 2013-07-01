void main(void)
{
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
//	gl_FrontColor = vec4(gl_Vertex.x / 5.0 + 0.5, gl_Vertex.y / 5.0 + 0.5, gl_Vertex.z / 5.0 + 0.5, 1.0);
	gl_FrontColor = vec4(1.0 - (vec3(gl_Position.z) / 200), 1.0);
}
