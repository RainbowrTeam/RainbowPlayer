/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rainbowplayer.Core.Features;

/**
 *
 * @author Tim Weiß
 */
public abstract class Feature {
    public abstract boolean isAvailable();
    public abstract String getName();
}
