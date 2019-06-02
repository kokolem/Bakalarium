package cz.vitek.bakalarium.utils;

import android.graphics.drawable.Drawable;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

public class MaterialLetterIcon {

    public static Drawable build(String letters) {
        return TextDrawable.builder().buildRound(letters, ColorGenerator.MATERIAL.getColor(letters));
    }

}
