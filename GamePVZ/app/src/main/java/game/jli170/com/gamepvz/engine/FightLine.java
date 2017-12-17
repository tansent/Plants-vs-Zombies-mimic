package game.jli170.com.gamepvz.engine;

import org.cocos2d.actions.CCScheduler;
import org.cocos2d.types.CGPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import game.jli170.com.gamepvz.base.AttackPlant;
import game.jli170.com.gamepvz.base.BaseElement;
import game.jli170.com.gamepvz.base.Bullet;
import game.jli170.com.gamepvz.base.Plant;
import game.jli170.com.gamepvz.base.Zombies;

/**
 * every line is relatively separated when fighting
 */
public class FightLine {
    private int linenum;

    public FightLine(int linenum) {
        this.linenum = linenum;

        CCScheduler.sharedScheduler().schedule("attackPlant", this, 0.2f, false);
        CCScheduler.sharedScheduler().schedule("createBullet", this, 0.2f, false);
        CCScheduler.sharedScheduler().schedule("attackZombies", this, 0.1f, false);
    }

    private List<Zombies> zombiesLists = new ArrayList<Zombies>();
    //to record plant's position
    private Map<Integer, Plant> plants = new HashMap<Integer, Plant>();
    private List<AttackPlant> attackPlants = new ArrayList<>();

    public void addZombies(final Zombies zombies) {
        zombiesLists.add(zombies);
        zombies.setDieListener(new BaseElement.DieListener() {
            //when a zombie dies
            @Override
            public void die() {
                zombiesLists.remove(zombies);
            }
        });
    }

    public void addPlant(final Plant plant){
        plants.put(plant.getRow(), plant);

        //if the plant belong to the AttackPlant, add it to the list
        if (plant instanceof AttackPlant){
            attackPlants.add((AttackPlant) plant);
        }

        plant.setDieListener(new BaseElement.DieListener() {
            //when a plant dies
            @Override
            public void die() {
                plants.remove(plant.getRow());
                if (plant instanceof AttackPlant){
                    attackPlants.remove((AttackPlant)plant);
                }
            }
        });

    }

    public void createBullet(float t){
        if(zombiesLists.size() > 0 && attackPlants.size() > 0){

            for (AttackPlant attackPlant:attackPlants){
                attackPlant.createBullet();
            }
        }
    }

    /**
     * judge if there is a plant over the row
     * @param row
     * @return
     */
    public boolean containsRow(int row) {
        return plants.containsKey(row);
    }

    /**
     * judge if the bullets and zombies collide, if they do, invoke zombie.attacked method
     */
    public void attackZombies(float t){
        if (zombiesLists.size() > 0 && attackPlants.size() >0){
            for (Zombies zombie:zombiesLists){
                float x = zombie.getPosition().x;
                float left = x-20;
                float right = x +20;
                for (AttackPlant attackPlant:attackPlants){
                    List<Bullet> bullets = attackPlant.getBullets();
                    for (Bullet bullet:bullets){
                        float bulletX = bullet.getPosition().x;
                        if (bulletX>left && bulletX<right){
                            zombie.attacked(bullet.getAttack());
//                            bullet.removeSelf();
                            bullet.setVisible(false);
                            bullet.setAttack(0);
                        }
                    }


                }
            }
        }
    }


    public void attackPlant(float t) {
        if (zombiesLists.size() > 0 && plants.size() > 0) { // 保证当前行上既有僵尸又有植物
            for (Zombies zombies : zombiesLists) {
                CGPoint position = zombies.getPosition();
                int row = (int) (position.x / 46 - 1); // 获取到僵尸所在的列
                Plant plant = plants.get(row);
                if (plant != null) { //there is a plant on the collum
                    zombies.attack(plant);
                }
            }
        }
    }
}
