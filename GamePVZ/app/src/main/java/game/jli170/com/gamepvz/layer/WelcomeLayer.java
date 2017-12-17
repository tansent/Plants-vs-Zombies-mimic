package game.jli170.com.gamepvz.layer;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.view.MotionEvent;

import org.cocos2d.actions.base.CCAction;
import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.instant.CCHide;
import org.cocos2d.actions.interval.CCAnimate;
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.nodes.CCAnimation;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;

import java.util.ArrayList;

import game.jli170.com.gamepvz.utils.CommonUtils;

/**
 * Created by Tylor on 6/15/2016.
 */
public class WelcomeLayer extends BaseLayer {

    CCSprite start;
    public WelcomeLayer() {

        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                SystemClock.sleep(8000); //emulate loading backend data
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                start.setVisible(true);
                setIsTouchEnabled(true); //turn on the touchEnable switch as late as possible
            }
        }.execute();

        init();
    }

    @Override
    public boolean ccTouchesBegan(MotionEvent event) {
        CGPoint cgPoint = this.convertTouchToNodeSpace(event); //transfer android coordinate to Cocos2d coordinate
        CGRect boundingBox = start.getBoundingBox(); //get sprite's bounding
        if (CGRect.containsPoint(boundingBox,cgPoint)){
            //handle click event
            System.out.println("clicked");

            CommonUtils.changeLayer(new MenuLayer());
        }

        return super.ccTouchesBegan(event);
    }


    private void init() {
        CCSprite logo = CCSprite.sprite("image/popcap_logo.png"); //cut after 'assets'

        logo.setPosition(winSize.width / 2, winSize.height / 2);
        this.addChild(logo);

        CCHide hide = CCHide.action(); //hide
        CCDelayTime showTime = CCDelayTime.action(2); //delay 2s
        CCDelayTime blankTime = CCDelayTime.action(1);
//        CCFadeOut quit = CCFadeOut.action(1);
//        logo.setOpacityModifyRGB(true);
        CCSequence ccSequence = CCSequence.actions(showTime, hide, blankTime, CCCallFunc.action(this, "loadWelcome"));
        logo.runAction(ccSequence);
//        this.addChild(logo);  //it's ok to put it here
    }

    // invoked by reflection
    public void loadWelcome(){
        CCSprite welcomePage = CCSprite.sprite("image/welcome.jpg");
        welcomePage.setAnchorPoint(0,0);
        this.addChild(welcomePage);
        loading();
    }

    private void loading(){
        CCSprite loading = CCSprite.sprite("image/loading/loading_01.png");
        loading.setPosition(winSize.width/2 , 30);
        this.addChild(loading);


//        //create loading frame
//        ArrayList<CCSpriteFrame> frames = new ArrayList<CCSpriteFrame>();
//        String format = "image/loading/loading_%02d.png"; //%02d: there are 2 digits, if less than 2, use 0 to occupy the upper digit
//        for (int i = 1; i <= 9; i++) {
//            CCSpriteFrame displayedFrame = CCSprite.sprite(String.format(format, i)).displayedFrame();
//            frames.add(displayedFrame);
//        }
//
//        //run animation
//        CCAnimation animation = CCAnimation.animation("loading", 0.5f, frames);//tag name, seconds per frame, frames
//        CCAnimate anim = CCAnimate.action(animation, false);
//        loading.runAction(anim);

        CCAction animate = CommonUtils.getAnimate("image/loading/loading_%02d.png", 9, false);
        loading.runAction(animate);

        //last image
        start = CCSprite.sprite("image/loading/loading_start.png");
        start.setPosition(winSize.width / 2, 30);
        start.setVisible(false); //after loading finished, make it visible
        this.addChild(start);
    }

}
