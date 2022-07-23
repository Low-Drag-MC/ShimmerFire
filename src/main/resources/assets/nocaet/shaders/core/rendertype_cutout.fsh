#version 330 core

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform float GameTime;
uniform float nocaetProgress;
uniform float isBloom;

in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;
in vec4 normal;
in float nocaetNoise;

layout (location = 0) out vec4 fragColor;
layout (location = 1) out vec4 bloomColor;
/* https://stackoverflow.com/a/17897228 Licensed under WTFPL */
vec3 hsv2rgb(vec3 c)
{
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

void main() {
    vec4 color = texture(Sampler0, texCoord0) * vertexColor * ColorModulator;
    if (color.a < 0.1) {
        discard;
    }
    float timeVary = abs(1.0 - mod(nocaetNoise + GameTime * 600.0, 2.0)) + 0.000001;
    vec3 hsvColor = vec3((1.0 / 6.0) - clamp((timeVary + 1.0) / 2.0, 0.0, 1.0) / 6.0 * nocaetProgress, 0.875, 1.0);
    vec3 rgbColor = hsv2rgb(hsvColor);
    fragColor = linear_fog(color * vec4(rgbColor, 1.0), vertexDistance, FogStart, FogEnd, FogColor);
    if (isBloom > 0.5) {
        bloomColor = fragColor;
    } else {
        bloomColor = vec4(0.);
    }
}
