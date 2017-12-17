package game.jli170.com.gamepvz;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.opengl.CCGLSurfaceView;

import game.jli170.com.gamepvz.layer.FightLayer;
import game.jli170.com.gamepvz.layer.WelcomeLayer;

public class MainActivity extends AppCompatActivity {

    //CCDirector controls the threads, the director should have the same lifecycle as the activity
    CCDirector director;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CCGLSurfaceView surfaceView = new CCGLSurfaceView(this); //this includes surfaceView and surfaceViewHolder
        setContentView(surfaceView);

        director = CCDirector.sharedDirector();//create a director
        director.attachInView(surfaceView); // start threads

        //-----settings-------
        director.setDeviceOrientation(CCDirector.kCCDeviceOrientationLandscapeLeft); //set to landscape
        director.setDisplayFPS(true);
        director.setAnimationInterval(1.0f / 30); //set frame

        //this method can adjust the screen
        director.setScreenSize(480, 318); // 678, 320
        //--------------------

        CCScene ccScene = CCScene.node(); //create a scene

//        ccScene.addChild(new WelcomeLayer()); //add layers to the scene
        ccScene.addChild(new FightLayer());
        director.runWithScene(ccScene);   //director runs the scene


    }


    @Override
    protected void onResume() {
        super.onResume();
        director.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        director.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        director.end();
    }

}
