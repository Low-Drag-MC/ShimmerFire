#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;
uniform sampler2D Sampler1; //DissolveNoiseTexture

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;
in vec4 normal;
in vec3 worldPosition;
in float dissolveProgress;

out vec4 fragColor;

void main() {
    if (dissolveProgress > 0.001 && (texture(Sampler1,worldPosition.xy + worldPosition.zz)).r < dissolveProgress){
                discard;
    }
    vec4 color = texture(Sampler0, texCoord0) * vertexColor * ColorModulator;
    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}
