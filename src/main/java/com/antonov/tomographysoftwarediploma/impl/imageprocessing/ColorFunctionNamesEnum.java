/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.antonov.tomographysoftwarediploma.impl.imageprocessing;

/**
 *
 * @author Antonov
 */
public enum ColorFunctionNamesEnum {
    
    red ("red"),
    green ("green"),
    blue ("blue"),
    cyan ("cyan"),
    magenta ("magenta"),
    yellow ("yellow"),
    gray ("gray"),
    invGray ("invGray"),
    gray2levels ("gray2levels"),
    gray4levels ("gray4levels"),
    gray8levels ("gray8levels"),
    gray16levels ("gray16levels"),
    gray32levels ("gray32levels"),
    gray64levels ("gray64levels"),
    gray128levels ("gray128levels"),
    red_cyan ("red_cyan"),
    green_magenta ("green_magenta"),
    blue_yellow ("blue_yellow"),
    sin_rgb ("sin_rgb"),
    sin_rbg ("sin_rbg"),
    sin_grb ("sin_grb"),
    sin_gbr ("sin_gbr"),
    sin_brg ("sin_brg"),
    sin_bgr ("sin_bgr"),
    sin_rgb_0 ("sin_rgb_0"),
    sin_rbg_0 ("sin_rbg_0"),
    sin_grb_0 ("sin_grb_0"),
    sin_gbr_0 ("sin_gbr_0"),
    sin_brg_0 ("sin_brg_0"),
    sin_bgr_0 ("sin_bgr_0"),
    sqrt_rgb ("sqrt_rgb"),
    sqrt_rbg ("sqrt_rbg"),
    sqrt_brg("sqrt_brg"),
    sqrt_grb ("sqrt_grb"),
    sqrt_gbr ("sqrt_gbr"),
    sqrt_bgr ("sqrt_bgr"),
    sqrt_rgb_0 ("sqrt_rgb_0"),
    sqrt_rbg_0 ("sqrt_rbg_0"),
    sqrt_brg_0 ("sqrt_brg_0"),
    sqrt_bgr_0 ("sqrt_bgr_0"),
    sqrt_gbr_0 ("sqrt_gbr_0"),
    sqrt_grb_0 ("sqrt_grb_0"),
    hue_rgb ("hue_rgb"),
    hue_rbg ("hue_rbg"),
    hue_grb ("hue_grb"),
    hue_gbr ("hue_gbr"),
    hue_brg ("hue_brg"),
    hue_bgr ("hue_bgr"),
    hue_rgb_0 ("hue_rgb_0"),
    hue_rbg_0 ("hue_rbg_0"),
    hue_grb_0 ("hue_grb_0"),
    hue_gbr_0 ("hue_gbr_0"),
    hue_brg_0 ("hue_brg_0"),
    hue_bgr_0 ("hue_bgr_0"),
    red_saw_2 ("red_saw_2"),
    red_saw_4 ("red_saw_4"),
    red_saw_8 ("red_saw_8"),
    green_saw_2 ("green_saw_2"),
    green_saw_4 ("green_saw_4"),
    green_saw_8 ("green_saw_8"),
    blue_saw_2 ("blue_saw_2"),
    blue_saw_4("blue_saw_4"),
    blue_saw_8("blue_saw_8"),
    red_green_saw_2("red_green_saw_2"),
    red_green_saw_4("red_green_saw_4"),
    red_green_saw_8("red_green_saw_4"),
    red_blue_saw_2("red_blue_saw_2"),
    red_blue_saw_4("red_blue_saw_4"),
    red_blue_saw_8("red_blue_saw_8"),
    green_blue_saw_2("green_blue_saw_2"),
    green_blue_saw_4("green_blue_saw_4"),
    green_blue_saw_8("green_blue_saw_8"),
    random_256("random_256"),
    random_32 ("random_32"),
    random_8 ("random_8"),
    none ("none")
    ;

    private final String name;
    
    private ColorFunctionNamesEnum(String s) {
        
        name = s;
    }

    @Override
    public String toString(){
       return name;
    }
}
