package game.jli170.com.gamepvz.bean;

import org.cocos2d.actions.CCScheduler;
import org.cocos2d.actions.base.CCAction;
import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.util.CGPointUtil;

import game.jli170.com.gamepvz.base.BaseElement;
import game.jli170.com.gamepvz.base.Plant;
import game.jli170.com.gamepvz.base.Zombies;
import game.jli170.com.gamepvz.utils.CommonUtils;

/**
 * Created by Tylor on 6/28/2016.
 */
public class PrimaryZombie extends Zombies{

    public PrimaryZombie(CGPoint startPoint, CGPoint endPoint) {
        super("image/zombies/zombies_1/walk/z_1_01.png");
        this.startPoint = startPoint;
        this.endPoint = endPoint;

        setPosition(startPoint);
        move();
    }

    @Override
    public void move() {
        CCAction animate = CommonUtils.getAnimate(
                "image/zombies/zombies_1/walk/z_1_%02d.png", 7, true);
        this.runAction(animate);
        float t = CGPointUtil.distance(getPosition(), endPoint) / speed;
        CCMoveTo moveTo = CCMoveTo.action(t, endPoint);
        CCSequence sequence = CCSequence.actions(moveTo,
                CCCallFunc.action(this, "endGame"));
        this.runAction(sequence);
    }

    //remove self
    public void endGame() {
        this.destroy();
    }


    Plant targetPlant;// 要攻击的目标
    @Override
    public void attack(BaseElement element) {
        if (element instanceof Plant) {
            Plant plant = (Plant) element;
            if (targetPlant == null) {//如果已经锁定目标了 就不要再调用下面的方法了
                targetPlant = plant;// 锁定目标

                stopAllActions();
                // 切换成攻击模式
                CCAction animate = CommonUtils.getAnimate(
                        "image/zombies/zombies_1/attack/z_1_attack_%02d.png",
                        10, true);
                this.runAction(animate);
                //  让植物持续掉血
                CCScheduler.sharedScheduler().schedule("attackPlant", this, 0.5f, false);
            }
        }
    }

    public void attackPlant(float t){
        targetPlant.attacked(attack);
        if (targetPlant.getLife() < 0){
            targetPlant = null;
            CCScheduler.sharedScheduler().unschedule("attackPlant", this); //unleash the method
            stopAllActions();
            move();
        }
    }

    @Override
    public void attacked(int attack) {
        life -= attack;
        if (life <0){
            destroy();
        }
    }

    @Override
    public void baseAction() {

    }
}
