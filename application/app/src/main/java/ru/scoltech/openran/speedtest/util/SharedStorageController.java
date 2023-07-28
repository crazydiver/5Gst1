package ru.scoltech.openran.speedtest.util;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import kotlin.collections.SetsKt;
import ru.scoltech.openran.speedtest.R;

//TODO: эедательно нормально реализовать контроллер shared prefs:
// сделать интерфейс для них, от него наследовать класс PipelineSharedStorageController
public class SharedStorageController {

        public static final String STORAGE_NAME = "Storage";

        private static SharedPreferences settings = null;
        private static SharedPreferences.Editor editor = null;
        @SuppressLint("StaticFieldLeak")
        private static Context context = null;

        public static void init( Context cntxt ){
            context = cntxt;
        }

        private static void init(String storageName){
            settings = context.getSharedPreferences(storageName, Context.MODE_PRIVATE);
            editor = settings.edit();
            if (storageName ==  context.getResources().getString(R.string.pipeline_shared_storage_name)){
                editor.putString("Download SpeedTest", "-u -R -P 10 -b 120m" + "|" + "-u");
                editor.putString("Upload SpeedTest", "-P 10 -b 120m" + "|" + " ");
                editor.putString("Uplowegad SpeedTest", "-P egeg10 -b 120m" + "|" + " ");
            }
            editor.commit();
        }

        public static void addProperty(String storageName,  String propertyName, String value ){
            if( settings == null ){
                init(storageName);
            }
            editor = settings.edit();
            editor.putString( propertyName, value );
            editor.commit();
        }

    public static void addPipeline( Pipeline pipeline ){
        if( settings == null ){
            init(context.getResources().getString(R.string.pipeline_shared_storage_name));
        }
        editor = settings.edit();

        editor.putString( pipeline.getName(), pipeline.getDevicePrefs() + "|" + pipeline.getServerPrefs() );
        editor.commit();
    }
    public static ArrayList<Pipeline> getAllPipelines(String storageName){
        if( settings == null ){
            init(storageName);
        }

        ArrayList<Pipeline> res = new ArrayList<>();
        HashMap<String, String> pipelines = (HashMap<String, String>) settings.getAll();
        for (HashMap.Entry<String, String> i : pipelines.entrySet()) {
            String aa = i.getValue();
            //TODO: починить парсер shared storage так, чтобы можно было закинуть в него пару,
            // в которой параметрый девайса и/или сервера пустые
            res.add(new Pipeline(i.getKey(), aa.split("\\|")[0],
                    aa.split("\\|")[1]));
        }
        return res;
    }
        public static Set<String> getProperty(String storageName, String propertyName ){
            if( settings == null ){
                init(storageName);
            }
            return settings.getStringSet( propertyName, null );
        }

        public static void removeProperty(String storageName, String propertyName){
            editor = settings.edit();
            editor.remove(propertyName);
            editor.apply();
        }
        public static void removePipeline(Pipeline pipeline){
            editor = settings.edit();
            editor.remove(pipeline.getName());
            editor.apply();
        }
    }
