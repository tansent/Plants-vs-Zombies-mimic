package game.jli170.com.gamepvz.bean;

import org.cocos2d.actions.base.CCAction;

import game.jli170.com.gamepvz.base.DefancePlant;
import game.jli170.com.gamepvz.utils.CommonUtils;


public class Nut extends DefancePlant {

	public Nut() {
		super("image/plant/nut/p_3_01.png");
		baseAction();
	}

	@Override
	public void baseAction() {
		CCAction animate = CommonUtils.getAnimate("image/plant/nut/p_3_%02d.png", 11, true);
		this.runAction(animate);
	}

}
