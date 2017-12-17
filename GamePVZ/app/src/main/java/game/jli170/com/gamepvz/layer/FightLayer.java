package game.jli170.com.gamepvz.layer;

import android.view.MotionEvent;

import org.cocos2d.actions.base.CCAction;
import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.interval.CCAnimate;
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCMoveBy;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCTMXTiledMap;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import game.jli170.com.gamepvz.bean.ShowPlant;
import game.jli170.com.gamepvz.bean.ShowZombies;
import game.jli170.com.gamepvz.engine.GameController;
import game.jli170.com.gamepvz.utils.CommonUtils;

/**
 * Created by Tylor on 6/21/2016.
 */
public class FightLayer extends BaseLayer {
    public static final int TAG_CHOSE = 10;
    private CCTMXTiledMap map;
    private List<CGPoint> zombiesPoints;
    private CCSprite chose; //chosen plaints
    private CCSprite choose; // plaints to be selected
    private CCSprite start;

    public FightLayer() {
        init();
    }

    private void init() {
        loadMap();
        parseMap(); //let zombies stand upon the right side
        showZombies();
        moveMap();
    }

    private void loadMap() {
        map = CCTMXTiledMap.tiledMap("image/fight/map_day.tmx");
        map.setAnchorPoint(0.5f, 0.5f);
        map.setPosition(map.getContentSize().getWidth() / 2, map.getContentSize().getHeight() / 2);
        this.addChild(map);
    }

    private void parseMap() {
        zombiesPoints = CommonUtils.parseMapToGetPoints(map, "zombies");
    }
    // after loading, let map shift rightward first
    private void moveMap() {
        int x = (int) (winSize.getWidth() - map.getContentSize().width);
        CCMoveBy moveBy = CCMoveBy.action(2, ccp(x, 0));
        CCSequence actions = CCSequence.actions(CCDelayTime.action(4), moveBy, CCDelayTime.action(2), CCCallFunc.action(this,"loadContainer"));
        map.runAction(actions);
    }

    public void loadContainer(){ //invoke through reflection CCCallFunc
        chose = CCSprite.sprite("image/fight/chose/fight_chose.png");
        chose.setAnchorPoint(0, 1);
        chose.setPosition(0, winSize.height);
        this.addChild(chose,0,TAG_CHOSE);

        choose = CCSprite.sprite("image/fight/chose/fight_choose.png");
        choose.setAnchorPoint(0, 0);
//        chose.setPosition(0,0);
        this.addChild(choose);

        loadShowPlant();

        // load  the start button
        start = CCSprite.sprite("image/fight/chose/fight_start.png");
        start.setPosition(choose.getContentSize().width / 2, 30);
        choose.addChild(start);
    }

    private void showZombies() {
        for (int i = 0; i < zombiesPoints.size(); i++){
            CGPoint cgPoint = zombiesPoints.get(i);
            ShowZombies showZombies = new ShowZombies();
            showZombies.setPosition(cgPoint);
            map.addChild(showZombies);
        }
    }

    private List<ShowPlant> showPlants; // the set to show plants
    private void loadShowPlant() {
        showPlants = new ArrayList<ShowPlant>();
        for (int i = 1; i <= 9; i++) {
            ShowPlant plant = new ShowPlant(i);

            CCSprite bgSprite = plant.getBgSprite(); // lower plants created first
            bgSprite.setPosition(16 + ((i - 1) % 4) * 56,
                    175 - ((i - 1) / 4) * 60);
            choose.addChild(bgSprite);

            CCSprite sprite = plant.getSprite(); // upper plants
            sprite.setPosition(16 + ((i - 1) % 4) * 56,
                    175 - ((i - 1) / 4) * 60);
            choose.addChild(sprite);

            showPlants.add(plant);
        }
        setIsTouchEnabled(true); //turn on the touchable switch (do this as late as possible)
    }

    //CopyOnWriteArrayList allows add and delete while running iteration
    private List<ShowPlant> selectedPlants = new CopyOnWriteArrayList<>();
    boolean isLock;
    boolean isDel;
    @Override
    public boolean ccTouchesBegan(MotionEvent event) {
        CGPoint cgPoint = this.convertTouchToNodeSpace(event); //convert android coordinate to cocos2d's
        if(GameController.isStart){// 如果游戏开始了 把触摸事件交给GameCtoller 处理
            GameController.getInstance().handleTouch(cgPoint);

            return super.ccTouchesBegan(event);
        }

        CGRect boundingBox = choose.getBoundingBox();
        CGRect choosenBox = chose.getBoundingBox();
        //deselect plants
        if (CGRect.containsPoint(choosenBox,cgPoint)){
            isDel = false;
            for (ShowPlant plant : selectedPlants){
                CGRect boundingBox1 = plant.getSprite().getBoundingBox();
                if (CGRect.containsPoint(boundingBox1,cgPoint)){
                    CCMoveTo moveTo = CCMoveTo.action(0.2f, plant.getBgSprite().getPosition());
                    plant.getSprite().runAction(moveTo);
                    selectedPlants.remove(plant);  // up to here shows the plant has been deselected
                    isDel = true; // confirm to deselect the plant
                    continue;
                }
                if (isDel){ //if deselect the plant, move all right side plants to one grid left
                    CCMoveBy moveBy = CCMoveBy.action(0.2f, ccp(-53, 0));
                    plant.getSprite().runAction(moveBy);
                }
            }
        }
        // select plants
        else if (CGRect.containsPoint(boundingBox,cgPoint)){
            // user wants to start
            if (CGRect.containsPoint(start.getBoundingBox(),cgPoint)){
                ready();
            }

            // user wants to select plants
            if (selectedPlants.size() < 5 && !isLock) { //selected plants less than 5
                //enter here means clicking inside the choose panel
                for (ShowPlant plant : showPlants){
                    CGRect plantRect = plant.getSprite().getBoundingBox();
                    if (CGRect.containsPoint(plantRect,cgPoint)){
                        System.out.println("plant selected");
                        isLock = true;

                        CCMoveTo moveTo = CCMoveTo.action(0.2f, ccp(70 + selectedPlants.size() * 53, 250));
                        //only after the moveTo action is done should the lock be reset to false
                        CCSequence sequence = CCSequence.actions(moveTo, CCCallFunc.action(this, "unlock"));
                        plant.getSprite().runAction(sequence);
                        selectedPlants.add(plant);
                    }
                }
            }
        }

        return super.ccTouchesBegan(event);
    }


    public void unlock(){
        isLock = false;
    }

    private void ready() {
        choose.removeSelf(); //remove the big block

        // NOTE: once the father object (choose) has gone, all its children(ShowPlant) will also dismiss
        // refill the selected plants to the container
        for(ShowPlant plant:selectedPlants){

            plant.getSprite().setScale(0.60f);// shrink the plants to match the container

            plant.getSprite().setPosition(
                    plant.getSprite().getPosition().x * 0.60f + 5,
                    plant.getSprite().getPosition().y

                            + (CCDirector.sharedDirector().getWinSize().height - plant

                            .getSprite().getPosition().y)
                            * 0.35f + 2);// set coordinate
            this.addChild(plant.getSprite());
        }

        // translate the map
        int x = (int) ( map.getContentSize().width - winSize.getWidth());
        CCMoveBy moveBy = CCMoveBy.action(1, ccp(x, 0));
        CCSequence actions = CCSequence.actions(moveBy, CCCallFunc.action(this, "preGame"));
        map.runAction(actions);

        chose.setScale(0.6f); //shrink the block

    }

    private CCSprite ready;
    public void preGame(){
        ready = CCSprite.sprite("image/fight/startready_01.png");
        ready.setPosition(winSize.width / 2, winSize.height/2);
        this.addChild(ready);
        String format = "image/fight/startready_%02d.png";
        CCAction animate = CommonUtils.getAnimate(format, 3, false); //if it is false(not forever, animate can be cast)
        CCSequence sequence = CCSequence.actions((CCAnimate) animate, CCCallFunc.action(this, "startGame"));
        ready.runAction(sequence);
    }

    public void startGame(){
        ready.removeSelf();
        //place zombies
        //place plants
        //zombies attack plants
        //plants attack zombies
        GameController controller = GameController.getInstance();
        controller.startGame(map,selectedPlants);
    }
}
