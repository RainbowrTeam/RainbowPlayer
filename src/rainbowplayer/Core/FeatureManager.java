/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rainbowplayer.Core;

import java.util.ArrayList;
import rainbowplayer.Core.Features.Feature;
import rainbowplayer.Core.Features.*;

/**
 *
 * @author Tim Weiß
 */
public class FeatureManager {
    private static FeatureManager instance;
    
    private ArrayList<Feature> featureCollection;
    
    private FeatureManager(){}
    
    public static synchronized FeatureManager getInstance(){
        if(FeatureManager.instance == null){
            FeatureManager.instance = new FeatureManager();
        }
        
        return FeatureManager.instance;
    }
    
    public void initializeFeatures(){
        featureCollection = new ArrayList<>();
        registerFeature(new TestFeature());
        registerFeature(new PlaylistExporter());
        registerFeature(new PlaylistImporter());
    }
    
    public void registerFeature(Feature feature){
        if(feature.isAvailable()){
            featureCollection.add(feature);
        }
    }
    
    public Feature useFeature(String name){
        for (Feature f : featureCollection) {
            if (f.getName().equals(name)) {
                return f;
            }
        }
        
        return null;
    }
}
