package game.jli170.com.gamepvz.layer;

import android.widget.Toast;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.menus.CCMenu;
import org.cocos2d.menus.CCMenuItem;
import org.cocos2d.menus.CCMenuItemSprite;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;

import game.jli170.com.gamepvz.utils.CommonUtils;

/**
 * Created by Tylor on 6/21/2016.
 */
public class MenuLayer extends BaseLayer {
    public MenuLayer() {
        init();
    }

    private void init() {
        CCSprite sprite = CCSprite.sprite("image/menu/main_menu_bg.jpg");
        sprite.setAnchorPoint(0, 0);
        this.addChild(sprite);

        CCSprite normalSprite = CCSprite.sprite("image/menu/start_adventure_default.png");
        CCSprite pressedSprite = CCSprite.sprite("image/menu/start_adventure_press.png");
        CCMenuItem items = CCMenuItemSprite.item(normalSprite, pressedSprite, this, "click");

        CCMenu menu = CCMenu.menu(items);
        menu.setScale(0.5f);
        menu.setPosition(winSize.width/2 - 25, winSize.height/2 - 110);
        menu.setRotation(4.5f);
        this.addChild(menu);
    }

    // invoke by reflection, must have an object parameter
    public void click(Object object){ //object: which item in the menu
        System.out.println("clicked2");
        CommonUtils.changeLayer(new FightLayer());
    }
}
