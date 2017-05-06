/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rainbowplayer.Core.Features;

/**
 *
 * @author tim
 */
public class TestFeature extends Feature {

    @Override
    public boolean isAvailable() {
        System.out.println("okay okay okay");
        
        return true;
    }
    
    public void runSomething(){
        System.out.println("b a s s");
    }

    @Override
    public String getName() {
        return "TestFeature";
    }
}
