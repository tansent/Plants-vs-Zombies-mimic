package game.jli170.com.gamepvz.bean;

import org.cocos2d.actions.base.CCAction;
import org.cocos2d.nodes.CCSprite;

import game.jli170.com.gamepvz.utils.CommonUtils;

/**
 * Created by Tylor on 6/21/2016.
 */
public class ShowZombies extends CCSprite {
    public ShowZombies() {
        super("image/zombies/zombies_1/shake/z_1_01.png");
        setScale(0.6);
        setAnchorPoint(0.5f, 0);
        CCAction animate = CommonUtils.getAnimate("image/zombies/zombies_1/shake/z_1_%02d.png", 2, true);
        this.runAction(animate);
    }
}
