package game.jli170.com.gamepvz.utils;

import org.cocos2d.actions.base.CCAction;
import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.interval.CCAnimate;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.layers.CCTMXObjectGroup;
import org.cocos2d.layers.CCTMXTiledMap;
import org.cocos2d.nodes.CCAnimation;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.transitions.CCFlipXTransition;
import org.cocos2d.types.CGPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Tylor on 6/21/2016.
 */
public class CommonUtils {
    /**
     * switch layer by flipping
     * @param newLayer
     */
    public static void changeLayer(CCLayer newLayer){
        CCScene scene = CCScene.node();
        scene.addChild(newLayer);
        CCFlipXTransition transition = CCFlipXTransition.transition(0.5f, scene, 1);
        CCDirector.sharedDirector().replaceScene(transition); //replace scene
    }

    /**
     * parse the map with an object name
     * @param map
     * @param name
     * @return
     */
    public static List<CGPoint> parseMapToGetPoints(CCTMXTiledMap map, String name){
        ArrayList<CGPoint> points = new ArrayList<>();
        //parse the map
        CCTMXObjectGroup objectGroup = map.objectGroupNamed(name);
        ArrayList<HashMap<String, String>> objects = objectGroup.objects;
        for (HashMap<String, String> hashMap : objects) {
            int x = Integer.parseInt(hashMap.get("x"));
            int y = Integer.parseInt(hashMap.get("y"));
            CGPoint cgPoint = CCNode.ccp(x, y);
            points.add(cgPoint);
        }
        return  points;
    }

    /**
     *
     * @param format    string path in format
     * @param num   frame number
     * @param isFoever  whether to run the animation continuously
     * @return
     */
    public static CCAction getAnimate(String format,int num, boolean isFoever){
        //create loading frame
        ArrayList<CCSpriteFrame> frames = new ArrayList<CCSpriteFrame>();
        for (int i = 1; i <= num; i++) {
            CCSpriteFrame displayedFrame = CCSprite.sprite(String.format(format, i)).displayedFrame();
            frames.add(displayedFrame);
        }

        //run animation
        CCAnimation animation = CCAnimation.animation("loading", 0.2f, frames);//tag name, seconds per frame, frames
        CCAnimate anim = CCAnimate.action(animation, isFoever);
        if (isFoever){
            CCAnimate animate = CCAnimate.action(animation);
            CCRepeatForever foever = CCRepeatForever.action(animate);
            return foever;
        }else {
            //by default, frames are running continuously. If adding an additional "false" param, it can run only 1 time
            CCAnimate animate = CCAnimate.action(animation,false);
            return animate;
        }
    }
}
