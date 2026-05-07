uniform sampler2D u_texture;
uniform sampler2D u_lights;
uniform sampler2D u_exclusion;

uniform vec2 u_resolution;
uniform vec2 u_campos;

uniform vec4 u_ambient;

varying vec2 v_texCoords;

#define banding 8.0
#define banding2 3.0

void main(){

    //Exclusion
    vec4 exclusion = texture2D(u_exclusion, v_texCoords);

    vec4 positionAmbient = clamp(vec4(u_ambient.rgb - exclusion.rgb * exclusion.a, u_ambient.a), 0.0, 1.0);

    //Light color
    vec4 color = texture2D(u_lights, v_texCoords);

    float alpha = color.a;

    //Get the scaling right here...
    alpha = clamp(floor((alpha) * banding)/banding, 0.0, 1.0) * 0.3 + clamp(floor(alpha * banding2)/banding2, 0.0, 1.0) * 0.3 + alpha * 0.4;

    vec4 totalColor = clamp(vec4(color.rgb * alpha, 1.0) + vec4(positionAmbient.rgb * positionAmbient.a, 1.0), 0.0, 1.0);

    vec4 background = clamp(texture2D(u_texture, v_texCoords), 0.0, 1.0);

    gl_FragColor = clamp(background * totalColor, 0.0, 1.0);

    /*
    float max = max(gl_FragColor.r, gl_FragColor.g, gl_FragColor.b);
    float min = min(gl_FragColor.r, gl_FragColor.g, gl_FragColor.b);
    float chrom = max - min;
    float L = (max + min)/2;
    */
}