#version 120

uniform sampler2D u_bloom_texture;
uniform sampler2D u_source_texture;
uniform float u_source_alpha;
uniform float u_exposure;
uniform float u_gamma;

void main() {
    vec2 texCords = gl_TexCoord[0].st;
    vec4 source = texture2D(u_source_texture, texCords);

    vec3 rawBloom = texture2D(u_bloom_texture, texCords).rgb;
    rawBloom = vec3(1.0) - exp(-rawBloom * u_exposure);
    rawBloom = pow(rawBloom, vec3(1.0 / u_gamma));
    rawBloom = clamp(rawBloom, 0.0, 1.0);
    float maxColorChannel = max(max(rawBloom.r, rawBloom.g), rawBloom.b);
    float bloomScale = 1.0 / maxColorChannel;
    vec4 bloom = vec4(rawBloom * bloomScale, maxColorChannel);

    float alphaScale = 1.0 - (1.0 - u_source_alpha) * source.a;
    gl_FragColor = vec4(source.rgb * source.a + bloom.rgb * (1.0 - source.a), (source.a + bloom.a * (1.0 - source.a)) * alphaScale);
}