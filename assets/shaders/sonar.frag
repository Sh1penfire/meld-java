uniform sampler2D u_texture;

uniform vec2 u_resolution;
uniform vec2 u_campos;

uniform vec4 u_color;

uniform float u_time;

varying vec2 v_texCoords;

void main() {

    vec4 color = texture2D(u_texture, v_texCoords);
    gl_FragColor = color;

    if(color.r == 1 && color.g + color.b > 0){

        color.a *= 0.5 * abs(sin((v_texCoords.x + v_texCoords.y) * 20.0 + u_time/20.0));

        gl_FragColor = color;
    }
    else gl_FragColor = vec4(color.rgb, 0);
}