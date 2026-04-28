package meld.core;

import java.util.*;
import arc.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import meld.*;
import mindustry.*;
import mindustry.ui.dialogs.*;

import static arc.Core.*;

// very hacky dont do this
public final class Bundles{
    private static final Locale locale = new Locale("shitpost");
    
    private Bundles(){}
    
    public static void load(){
        setupLocale();
        loadBundle();
    }
    
    private static void setupLocale(){
        Vars.locales = new Seq<>(Vars.locales).add(locale).toArray();
        LanguageDialog.displayNames.put(locale.toString(), "Meld Shitpost");
    }
    
    private static void loadBundle(){
        if(!settings.getString("locale").equals(locale.toString())) return;
        Log.info("loading meld shitpost bundle");
        
        var modRoot = Vars.mods.getMod(Meld.name).root;
        var bundleFile = modRoot.child("bundles").child("bundle_" + locale + ".properties");
        if(!bundleFile.exists()){
            Log.warn("couldn't find shitpost bundle");
            return;
        }
        
        Reflect.set(bundle, "locale", locale);
        PropertiesUtils.load(bundle.getProperties(), bundleFile.reader());
    }
}
