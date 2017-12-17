package game.jli170.com.gamepvz.bean;

import org.cocos2d.nodes.CCSprite;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tylor on 6/23/2016.
 */
public class ShowPlant {

    static Map<Integer, HashMap<String, String>> db;

    static{
        //using a map to emulate database at first
        db=new HashMap<Integer, HashMap<String,String>>();
        String format= "image/fight/chose/choose_default%02d.png";
        for(int i=1;i<=9;i++){
            HashMap<String, String> value=new HashMap<String, String>();
            value.put("path",String.format(format, i));
            value.put("sun", 50+"");
            db.put(i, value);

        }
    }

    private CCSprite sprite;
    private CCSprite bgSprite;
    private int id;

    public ShowPlant(int id){
        this.id = id;
        HashMap<String, String> map = db.get(id);
        String path = map.get("path");
        sprite = CCSprite.sprite(path);
        sprite.setAnchorPoint(0, 0);

        bgSprite = CCSprite.sprite(path);
        bgSprite.setOpacity(150);//set visibility
        bgSprite.setAnchorPoint(0, 0);
    }

    public CCSprite getSprite() {
        return sprite;
    }

    public CCSprite getBgSprite() {
        return bgSprite;
    }

    public int getId() {
        return id;
    }
}
