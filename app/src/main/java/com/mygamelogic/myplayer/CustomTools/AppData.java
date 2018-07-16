package com.mygamelogic.myplayer.CustomTools;

import com.mygamelogic.myplayer.Response.SearchMusicResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 16/07/18.
 */

public class AppData {
    private Map<String,SearchMusicResponse> responseMap=new HashMap<>();
    private static AppData instance=null;
    public static AppData getInstance() {
        if(instance == null) {
            synchronized (AppData.class) {
                if(instance == null) {
                    instance = new AppData();
                }
            }
        }
        return instance;
    }
    private void addData(String searchStr, SearchMusicResponse searchMusicResponse){
        responseMap.put(searchStr,searchMusicResponse);
    }

    public Map<String, SearchMusicResponse> getResponseMap() {
        return responseMap;
    }

    public void setResponseMap(Map<String, SearchMusicResponse> responseMap) {
        this.responseMap = responseMap;
    }

    public static void setInstance(AppData instance) {
        AppData.instance = instance;
    }
}
