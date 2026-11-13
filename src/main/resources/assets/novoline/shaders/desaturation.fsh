#version 120

uniform sampler2D u_texture;
uniform float u_factor;
uniform vec3 u_color;

void main() {
  vec3 color = texture2D( u_texture , gl_TexCoord[0].st ).rgb;
  float grayFactor = dot( color , vec3( 0.2126 , 0.7152 , 0.0722 ) );
  vec3 target = u_color * grayFactor;
  gl_FragColor = vec4( mix( color , target , u_factor ) , 1.0 );
}