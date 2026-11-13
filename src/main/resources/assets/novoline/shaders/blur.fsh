#version 120

uniform sampler2D u_texture;
uniform vec2      u_textureSize;
uniform float     u_sigma;
uniform vec2      u_dir;
uniform int       u_radius;
uniform int       u_step;

float CalcGauss(float x, float sigma) {
    if (sigma <= 0.0)
    return 0.0;
    return exp(-(x*x) / (2.0 * sigma)) / (2.0 * 3.14157 * sigma);
}

void main() {
    vec2 texC = gl_TexCoord[0].st;
    vec4 texCol = texture2D(u_texture, texC);
    vec4 gaussCol = vec4(texCol.rgb, 1.0);
    vec2 dir = u_dir / u_textureSize;

    for (int i = 1; i <= u_radius; i += u_step)
    {
        float weight = u_step * CalcGauss(float(i) / u_radius, u_sigma * 0.5);
        if (weight < 1.0/255.0)
        break;
        texCol     = texture2D(u_texture, texC + dir * float(i));
        gaussCol  += vec4(texCol.rgb * weight, weight);
        texCol     = texture2D(u_texture, texC - dir * float(i));
        gaussCol  += vec4(texCol.rgb * weight, weight);
    }
    gl_FragColor = vec4(clamp(gaussCol.rgb / gaussCol.w, 0.0, 1.0), 1.0);
}