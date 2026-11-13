#version 120

uniform sampler2D u_texture;
uniform vec2 u_texelSize;
uniform vec2 u_direction;

uniform float u_radius;
uniform float u_sigma;
uniform float u_gamma;

uniform vec3 u_color;

float calcGauss(float x) {
    float sq = x / u_sigma;
    return 1.0 / (abs(u_sigma) * 2.50662827) * exp(-0.5 * sq * sq);
}

void main() {
    vec4 color = vec4(0);
    for (float x = -u_radius; x <= u_radius; x++) {
        color += texture2D(u_texture, gl_TexCoord[0].xy + vec2(u_texelSize.x * u_direction.x, u_texelSize.y * u_direction.y) * x) * calcGauss(x);
    }
    gl_FragColor = vec4(pow(u_color.rgb, vec3(1.0/u_gamma)), pow(color.a, 1.0/u_gamma));
}