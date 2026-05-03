package meld.core;

import java.util.*;
import arc.*;
import arc.files.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import meld.*;
import mindustry.*;
import mindustry.ctype.*;
import mindustry.mod.*;

// still very hacky, dont do this
public final class Bundles{
    private static final Locale shitpostLocale = new Locale("shitpost");
    
    private Bundles(){}
    
    public static void load(){
        var enabled = Core.settings.getBool(SettingKeys.shitpostBundles, false);
        shitpost(enabled);
    }
    
    public static void shitpost(boolean enable){
        // since when is this valid syntax????
        if(enable) enable(); else disable();
        
        reloadModBundles();
        reloadContentFields();
    }
    
    private static void enable(){
        if(isShitpost()) return;
        
        var bundle = I18NBundle.createEmptyBundle();
        Reflect.set(bundle, "locale", shitpostLocale);
        Reflect.set(bundle, "parent", Core.bundle);
        Reflect.set(bundle, "formatter", new TextFormatter(shitpostLocale, !I18NBundle.getSimpleFormatter()));
        Core.bundle = bundle;
    }
    
    private static void disable(){
        if(!isShitpost()) return;
        
        Core.bundle = Reflect.get(Core.bundle, "parent");
    }
    
    private static void reloadModBundles(){
        var bundles = Reflect.<ObjectMap<String, Seq<Fi>>>get(Vars.mods, "bundles");
        
        /// 1:1 copied from {@link Mods#buildFiles()}
        I18NBundle bundle = Core.bundle;
        while(bundle != null){
            String str = bundle.getLocale().toString();
            String locale = "bundle" + (str.isEmpty() ? "" : "_" + str);
            for(Fi file : bundles.get(locale, Seq::new)){
                try{
                    PropertiesUtils.load(bundle.getProperties(), file.reader());
                }catch(Throwable e){
                    Log.err("Error loading bundle: " + file + "/" + locale, e);
                }
            }
            bundle = bundle.getParent();
        }
    }
    
    /// 1:1 copied from {@link UnlockableContent#UnlockableContent(String)}
    private static void reloadContentFields(){
        Vars.content.each(it -> {
            if(it instanceof UnlockableContent uc){
                uc.localizedName = Core.bundle.get(uc.getContentType() + "." + uc.name + ".name", uc.name);
                uc.description = Core.bundle.getOrNull(uc.getContentType() + "." + uc.name + ".description");
                uc.details = Core.bundle.getOrNull(uc.getContentType() + "." + uc.name + ".details");
                uc.credit = Core.bundle.getOrNull(uc.getContentType() + "." + uc.name + ".credit");
            }
        });
    }
    
    private static boolean isShitpost(){
        return Core.bundle.getLocale() == shitpostLocale;
    }
}
