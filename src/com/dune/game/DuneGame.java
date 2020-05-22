package com.dune.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class DuneGame extends ApplicationAdapter {
	private static class Tank {
		private Vector2 position;
		private Texture texture;
		private float angle;
		private float speed;

		public Tank(float x, float y) {
			this.position = new Vector2(x, y);
			this.texture = new Texture("tank.png");
			this.speed = 200.0f;
		}

		public void update(float dt) {
			if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
				angle += 180.0f * dt;
			}
			if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
				angle -= 180.0f * dt;
			}
			if (Gdx.input.isKeyPressed(Input.Keys.UP)
			&& (position.x - texture.getWidth()/2 + speed * MathUtils.cosDeg(angle) * dt)>=0 && (position.y - texture.getHeight()/2 + speed * MathUtils.sinDeg(angle) * dt) >=0
					&& (position.x + texture.getWidth()/2+ speed * MathUtils.cosDeg(angle) * dt) <= 1280 && (position.y + texture.getHeight()/2 + speed * MathUtils.sinDeg(angle) * dt) <=720

			) {
				position.x += speed * MathUtils.cosDeg(angle) * dt;
				position.y += speed * MathUtils.sinDeg(angle) * dt;
			}
		}

		public void render(SpriteBatch batch) {
			batch.draw(texture, position.x - 40, position.y - 40, 40, 40, 80, 80, 1, 1, angle, 0, 0, 80, 80, false, false);
		}

		public void dispose() {
			texture.dispose();
		}
	}
	private static class Stone {
		private Vector2 position;
		private Texture texture;

		public Stone(float x, float y) {
			this.position = new Vector2(x, y);
			this.texture = new Texture("circle.png");
		}
		public void update(double x, double y){
			position.x = (float) (Math.random()*x);
			position.y = (float)(Math.random()*y);
		}
		public void render(SpriteBatch batch){
			batch.draw(texture, position.x - 40, position.y-40);
		}
		public void dispose() {
			texture.dispose();
		}
	}

	private SpriteBatch batch;
	private Tank tank;
	private Stone stone;

	@Override
	public void create() {
		batch = new SpriteBatch();
		tank = new Tank(200, 200);
		stone = new Stone(100,100);
	}

	@Override
	public void render() {
		float dt = Gdx.graphics.getDeltaTime();
		update(dt);
		Gdx.gl.glClearColor(0, 0.4f, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		tank.render(batch);
		stone.render(batch);

		batch.end();
	}

	public void update(float dt) {
		tank.update(dt);
		collision();
	}

	@Override
	public void dispose() {
		batch.dispose();
		tank.dispose();
		stone.dispose();
	}

	public void collision (){
		if((tank.position.x + tank.texture.getWidth() -5 ) >= (stone.position.x )
				&& (tank.position.x <= (stone.position.x + stone.texture.getWidth()-5))
		&& (tank.position.y + tank.texture.getHeight()-5 ) >= (stone.position.y )
				&& (tank.position.y <= (stone.position.y + stone.texture.getHeight()-5))
		 ){
			stone.update(1280,720);
		}
	}

	// Домашнее задание:
	// - Задать координаты точки, и нарисовать в ней круг (любой круг, радиусом пикселей 50)
	// - Если "танк" подъедет вплотную к кругу, то круг должен переместиться в случайную точку
	// - * Нельзя давать танку заезжать за экран
}