/*
 * Original shader from: https://www.shadertoy.com/view/NscXR8
 */

#version 120
#ifdef GL_ES
precision mediump float;
#endif

// glslsandbox uniforms
uniform float time;
uniform vec2 resolution;

// shadertoy emulation
#define iTime time
#define iResolution vec3(resolution,1.0)

// --------[ Original ShaderToy begins here ]---------- //
// 240 chars by Xor (with aspect ratio fix)
void mainImage(out vec4 O, vec2 I)
{
    vec3 p = iResolution;
    vec3 d = -0.5 * vec3(I + I - p.xy, p.z) / p.x;
    vec3 c = vec3(0.0);
    vec3 i = vec3(0.0);
    for(int x = 0; x < 200; ++x) {
        if (i.x >= 1.0) break;
        p = c;
        p.z -= iTime + (i.x += 0.01);
        vec4 rot = sin((p.z * 0.1) + vec4(0.0, 11.0, 33.0, 0.0));
        p.xy = mat2(rot.x, rot.y, rot.z, rot.w) * p.xy;
        c += length(sin(p.yx) + cos(p.xz + iTime)) * d;
    }
    O = vec4(10.0, 0.0, 2.5, 9.0) / length(c);
}

// --------[ Original ShaderToy ends here ]---------- //

void main(void)
{
    mainImage(gl_FragColor, gl_FragCoord.xy);
    gl_FragColor.a = 1.0;
}
