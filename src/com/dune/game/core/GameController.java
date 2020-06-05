package com.dune.game.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.dune.game.core.units.AbstractUnit;
import com.dune.game.core.units.BattleTank;
import com.dune.game.screens.ScreenManager;

import java.util.ArrayList;
import java.util.List;

public class GameController {
    private BattleMap map;
    private PlayerLogic playerLogic;
    private ProjectilesController projectilesController;
    private UnitsController unitsController;
    private Vector2 tmp;
    private Vector2 selectionStart;
    private Vector2 mouse;
    private Collider collider;

    private List<AbstractUnit> selectedUnits;

    public UnitsController getUnitsController() {
        return unitsController;
    }

    public List<AbstractUnit> getSelectedUnits() {
        return selectedUnits;
    }

    public Vector2 getMouse() {
        return mouse;
    }

    public ProjectilesController getProjectilesController() {
        return projectilesController;
    }

    public BattleMap getMap() {
        return map;
    }

    public GameController() {
        this.mouse = new Vector2();
        this.tmp = new Vector2();
        this.playerLogic = new PlayerLogic(this);
        this.collider = new Collider(this);
        this.selectionStart = new Vector2();
        this.selectedUnits = new ArrayList<>();
        this.map = new BattleMap();
        this.projectilesController = new ProjectilesController(this);
        this.unitsController = new UnitsController(this);
        prepareInput();
    }

    public void update(float dt) {
        mouse.set(Gdx.input.getX(), Gdx.input.getY());
        ScreenManager.getInstance().getViewport().unproject(mouse);
        unitsController.update(dt);
        playerLogic.update(dt);
        projectilesController.update(dt);
        map.update(dt);
        collider.checkCollisions();
    }

    public boolean isUnitSelected(AbstractUnit abstractUnit) {
        return selectedUnits.contains(abstractUnit);
    }

    public void prepareInput() {
        InputProcessor ip = new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (button == Input.Buttons.LEFT) {
                    selectionStart.set(mouse);
                }
                return true;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                if (button == Input.Buttons.LEFT) {
                    tmp.set(mouse);

                    if (tmp.x < selectionStart.x) {
                        float t = tmp.x;
                        tmp.x = selectionStart.x;
                        selectionStart.x = t;
                    }
                    if (tmp.y > selectionStart.y) {
                        float t = tmp.y;
                        tmp.y = selectionStart.y;
                        selectionStart.y = t;
                    }

                    selectedUnits.clear();
                    if (Math.abs(tmp.x - selectionStart.x) > 20 & Math.abs(tmp.y - selectionStart.y) > 20) {
                        for (int i = 0; i < unitsController.getPlayerUnits().size(); i++) {
                            AbstractUnit t = unitsController.getPlayerUnits().get(i);
                            if (t.getPosition().x > selectionStart.x && t.getPosition().x < tmp.x
                                    && t.getPosition().y > tmp.y && t.getPosition().y < selectionStart.y
                            ) {
                                selectedUnits.add(t);
                            }
                        }
                    } else {
                        for (int i = 0; i < unitsController.getUnits().size(); i++) {
                            AbstractUnit t = unitsController.getUnits().get(i);
                            if (t.getPosition().dst(tmp) < 30.0f) {
                                selectedUnits.add(t);
                            }
                        }
                    }
                }
                return true;
            }
        };
        Gdx.input.setInputProcessor(ip);
    }
}
