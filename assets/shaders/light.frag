uniform sampler2D u_texture;
uniform sampler2D u_lights;
uniform sampler2D u_exclusion;

uniform vec2 u_resolution;
uniform vec2 u_campos;

uniform vec4 u_ambient;

varying vec2 v_texCoords;

void main(){

    //Exclusion
    vec4 exclusion = clamp(texture2D(u_exclusion, v_texCoords), 0.0, 1.1);

    vec4 positionAmbient = u_ambient;

    //Light color
    vec4 color = clamp(texture2D(u_lights, v_texCoords) + vec4(positionAmbient.rgb * positionAmbient.a, 1), 0.0, 1.0);


    vec4 background = clamp(texture2D(u_texture, v_texCoords), 0.0, 1.0);

    float alpha = (u_ambient.a - color.a);
    alpha = clamp(floor(alpha * 8.0)/8.0, 0.0, 1.0) * 0.3 + clamp(floor(alpha * 3.0)/3.0, 0.0, 1.0) * 0.5 + alpha * 0.2 + color.a/4.0;

    gl_FragColor = vec4(background.rgba);

    gl_FragColor = background * color;

    //clamp(vec4(mix(u_ambient.rgb, color.rgb, color.a), alpha), 0.0, 1.0)
}