package game.jli170.com.gamepvz.engine;

import org.cocos2d.actions.CCProgressTimer;
import org.cocos2d.actions.CCScheduler;
import org.cocos2d.layers.CCTMXTiledMap;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import game.jli170.com.gamepvz.base.Plant;
import game.jli170.com.gamepvz.bean.Nut;
import game.jli170.com.gamepvz.bean.PeasePlant;
import game.jli170.com.gamepvz.bean.PrimaryZombie;
import game.jli170.com.gamepvz.bean.ShowPlant;
import game.jli170.com.gamepvz.layer.FightLayer;
import game.jli170.com.gamepvz.utils.CommonUtils;

/**
 * handle game fight
 */
public class GameController {
    private GameController() {
    }
    private static GameController controller = new GameController();

    public static GameController getInstance() {
        return controller;
    }


    private static List<FightLine> lines;
    private List<CGPoint> roadPoints;

    static{
        lines = new ArrayList<>();
        for (int i=0;i<5;i++){
            FightLine fightLine = new FightLine(i);
            lines.add(fightLine);
        }
    }

    public static boolean isStart;
    public CCTMXTiledMap map;
    public List<ShowPlant> selectedPlants;

    /**
     * start game
     * @param map
     * @param selectedPlants
     */
    public void startGame(CCTMXTiledMap map, List<ShowPlant> selectedPlants){
        isStart = true;
        this.map = map;
        this.selectedPlants = selectedPlants;
        loadMap();
        //place zombies
        //place plants
        //zombies attack plants
        //plants attack zombies

        // 定时器
        // 参数1 方法名(方法带float类型的参数) 参数2 调用方法的对象 参数3 间隔时间 参数4 是否暂停
        CCScheduler.sharedScheduler().schedule("addZombies", this, 4,false);

        progress();
    }

    CGPoint[][] towers = new CGPoint[5][9]; // to store plants' locations
    private void loadMap() {
        roadPoints = CommonUtils.parseMapToGetPoints(map, "road");
        for (int i = 1; i <= 5; i++) {
            List<CGPoint> mapPoints = CommonUtils.parseMapToGetPoints(map,
                    String.format("tower%02d", i));
            for (int j = 0; j < mapPoints.size(); j++) {
                towers[i - 1][j] = mapPoints.get(j);
            }
        }

    }

    public void addZombies(float t){
        Random random = new Random();
        int lineNum = random.nextInt(5);// [0-5)
        PrimaryZombie primaryZombies = new PrimaryZombie(
                roadPoints.get(lineNum * 2), roadPoints.get(lineNum * 2 + 1));
        map.addChild(primaryZombies,1);// 1: 让僵尸一直在植物的上面

        lines.get(lineNum).addZombies(primaryZombies);// 把僵尸记录到行战场中

        progress += 5;
        progressTimer.setPercentage(progress);
    }


    private ShowPlant selectPlant; // 玩家选择的植物
    private Plant installPlant;
    /**
     * 当游戏开始后处理点击事件的方法
     *
     * @param point
     *            点击到的点
     */
    public void handleTouch(CGPoint point) {
        CCSprite chose = (CCSprite) map.getParent().getChildByTag(
                FightLayer.TAG_CHOSE);
        if (CGRect.containsPoint(chose.getBoundingBox(), point)) {
            // 认为玩家有可能选择了植物
            if (selectPlant != null) {
                selectPlant.getSprite().setOpacity(255);
                selectPlant = null;
            }
            for (ShowPlant plant : selectedPlants) {
                CGRect boundingBox = plant.getSprite().getBoundingBox();
                if (CGRect.containsPoint(boundingBox, point)) {
                    // 玩家选择了植物
                    selectPlant = plant;
                    selectPlant.getSprite().setOpacity(150);
                    int id = selectPlant.getId();
                    switch (id) {
                        case 1:
                            installPlant =new PeasePlant();
                            break;
                        case 4:
                            installPlant = new Nut();
                            break;
                        default:
                            break;
                    }
                }
            }
        } else {
            // 玩家有可能安放植物
            if (selectPlant != null) {
                int row = (int) (point.x / 46) - 1; // 1-9 0-8
                int line = (int) ((CCDirector.sharedDirector().getWinSize().height - point.y) / 54) - 1;// 1-5
                // 0-4
                // 限制安放的植物的范围
                if (row >= 0 && row <= 8 && line >= 0 && line <= 4) {
//
//                    // 安放植物
////                     selectPlant.getSprite().setPosition(point);
////                     installPlant.setPosition(point); // 坐标需要修改
                    installPlant.setLine(line);// 设置植物的行号
                    installPlant.setRow(row); // 设置植物的列号
//
                    installPlant.setPosition(towers[line][row]); // 修正了植物的坐标
                    FightLine fightLine = lines.get(line);
                    if (!fightLine.containsRow(row)) {  // 判断当前列是否已经添加了植物 如果添加了 就不能再添加了
                        fightLine.addPlant(installPlant);// 把植物记录到了行战场中
                        map.addChild(installPlant);
                    }
//                    installPlant.setPosition(point);
//                    map.addChild(installPlant);
                }
                installPlant = null;
                selectPlant.getSprite().setOpacity(255);
                selectPlant = null;// 下次安装需要重新选择


//                installPlant = null;
//                selectPlant = null;

//                selectPlant.getSprite().setPosition(point);
//                selectPlant = null;
            }
        }
    }


    public void endGame(){
        isStart = false;
    }


    CCProgressTimer progressTimer;
    int  progress=0;
    private void progress() {
        // 创建了进度条
        progressTimer = CCProgressTimer.progressWithFile("image/fight/progress.png");
        // 设置进度条的位置
        progressTimer.setPosition(CCDirector.sharedDirector().getWinSize().width - 80, 13);
        map.getParent().addChild(progressTimer); //图层添加了进度条
        progressTimer.setScale(0.6f);  //  设置了缩放

        progressTimer.setPercentage(0);// 每增加一个僵尸需要调整进度，增加5
        progressTimer.setType(CCProgressTimer.kCCProgressTimerTypeHorizontalBarRL);  // 进度的样式

        CCSprite sprite = CCSprite.sprite("image/fight/flagmeter.png");
        sprite.setPosition(CCDirector.sharedDirector().getWinSize().width - 80, 13);
        map.getParent().addChild(sprite);
        sprite.setScale(0.6f);
        CCSprite name = CCSprite.sprite("image/fight/FlagMeterLevelProgress.png");
        name.setPosition(CCDirector.sharedDirector().getWinSize().width - 80, 5);
        map.getParent().addChild(name);
        name.setScale(0.6f);
    }
}
