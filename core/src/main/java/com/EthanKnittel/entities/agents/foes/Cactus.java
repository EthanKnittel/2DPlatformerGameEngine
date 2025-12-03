package com.EthanKnittel.entities.agents.foes;

import com.EthanKnittel.ai.ChaseStrategy;
import com.EthanKnittel.entities.agents.Foe;
import com.EthanKnittel.entities.agents.Player;
import com.EthanKnittel.game.GameScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Cactus extends Foe {
    private TextureAtlas atlas;
    private Animation<TextureRegion> idleAnim, runAnim, hitAnim, fallAnim, jumpAnim;

    public Cactus(float x, float y, Player target){
        super(x,y,48f/ GameScreen.getPixelsPerBlocks(), 48f/GameScreen.getPixelsPerBlocks(), 50, 10, target);
        this.setStrategy(new ChaseStrategy());
        loadAnimations();

        this.setMoveSpeed(150f/GameScreen.getPixelsPerBlocks());
        this.setJumpSpeed(300f/GameScreen.getPixelsPerBlocks());
    }

    private void loadAnimations(){
        try{
            atlas = new TextureAtlas(Gdx.files.internal("Ennemies/cactus/Cactus.atlas"));

            idleAnim = new Animation<>(0.1f, atlas.findRegions("IDLE"), Animation.PlayMode.LOOP);
            runAnim = new Animation<>(0.1f, atlas.findRegions("RUNNING"), Animation.PlayMode.LOOP);
            hitAnim = new Animation<>(0.1f, atlas.findRegions("HIT"), Animation.PlayMode.LOOP);
            fallAnim = new Animation<>(0.1f, atlas.findRegions("FALLING"), Animation.PlayMode.LOOP);
            jumpAnim = new Animation<>(0.1f, atlas.findRegions("JUMPING"), Animation.PlayMode.LOOP);

            setAnimation(idleAnim);
        } catch(Exception e){
            Gdx.app.error("Cactus", "Erreur de chargement des animations!", e);
        }
    }

    @Override
    public void update(float deltaTime){
        super.update(deltaTime);
        if (!getGrounded()){
            setAnimation(fallAnim);
        } else if (getVelocity().x !=0){
            setAnimation(runAnim);
        } else {
            setAnimation(idleAnim);
        }
    }

    @Override
    public void dispose(){
        if (atlas != null){
            atlas.dispose();
        }
    }
}
