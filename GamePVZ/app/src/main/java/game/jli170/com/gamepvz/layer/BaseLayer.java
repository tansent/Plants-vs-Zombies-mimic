package game.jli170.com.gamepvz.layer;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.types.CGSize;

/**
 * Created by Tylor on 6/21/2016.
 */
public class BaseLayer extends CCLayer {
    protected CGSize winSize;
    public BaseLayer() {
        winSize = CCDirector.sharedDirector().winSize();
    }
}
