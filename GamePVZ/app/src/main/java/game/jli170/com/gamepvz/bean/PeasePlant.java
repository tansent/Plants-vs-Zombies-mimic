package game.jli170.com.gamepvz.bean;

import org.cocos2d.actions.base.CCAction;
import org.cocos2d.nodes.CCNode;

import game.jli170.com.gamepvz.base.AttackPlant;
import game.jli170.com.gamepvz.base.Bullet;
import game.jli170.com.gamepvz.utils.CommonUtils;


public class PeasePlant extends AttackPlant {

    public PeasePlant() {
        super("image/plant/pease/p_2_01.png");
        baseAction();
    }

    @Override
    public Bullet createBullet() {
        if(bullets.size()<1){// 证明之前没有创建子弹
            final Pease pease=new Pease();
            pease.setPosition(CCNode.ccp(this.getPosition().x+20, this.getPosition().y+40));
            this.getParent().addChild(pease); //use the layer to add the bullet
            pease.setDieListener(new DieListener() {

                @Override
                public void die() { //when bullet disappears
                    bullets.remove(pease);
                }
            });
            bullets.add(pease);

            pease.move();
        }
        return null;
    }

    @Override
    public void baseAction() {
        CCAction animate = CommonUtils.getAnimate("image/plant/pease/p_2_%02d.png", 8, true);
        this.runAction(animate);
    }

}
