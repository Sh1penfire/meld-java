package meld.graphics;

import arc.math.Interp;
import arc.math.Mathf;

public class MeldInterps {
    public static Interp
    trueSin = Mathf::sin,
    trueCos = Mathf::cos,
    compressedSin = f -> Mathf.sin(f * Mathf.PI2),
    compressedCos = f -> Mathf.cos(f * Mathf.PI2)
            ;
}
