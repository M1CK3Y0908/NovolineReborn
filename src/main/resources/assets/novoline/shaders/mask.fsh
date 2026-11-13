#version 120

uniform sampler2D u_texture;
uniform vec2      u_texture_offset;
uniform vec2      u_texture_ratio;
uniform sampler2D u_mask_texture;
uniform float u_alpha;

void main() {
    vec2 screenC = gl_TexCoord[0].st;
    vec4 maskCol = texture2D(u_mask_texture, screenC);
    vec2 texC = vec2(u_texture_offset.x + screenC.x * u_texture_ratio.x, u_texture_offset.y + screenC.y * u_texture_ratio.y);
    vec4 texCol = texture2D(u_texture, texC);

    gl_FragColor = vec4(texCol.xyz, maskCol.w * u_alpha);
}