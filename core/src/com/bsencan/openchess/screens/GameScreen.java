/*
 * Copyright 2013 Baris Sencan (baris.sencan@me.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.bsencan.openchess.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.bsencan.openchess.Assets;
import com.bsencan.openchess.actors.BoardActor;
import com.bsencan.openchess.view.GameRenderer;

/**
 * Main game screen. Creates a new chess board and a game renderer, then tells
 * the renderer to render that board.
 * 
 * @author Baris Sencan
 */
public class GameScreen implements Screen {

	private GameRenderer renderer;

	@Override
	public void render(float delta) {
		this.renderer.render(delta);
	}

	@Override
	public void resize(int width, int height) {
		this.renderer.setSize(width, height);
	}

	@Override
	public void show() {
		BoardActor boardActor; // Can't call the constructor here. Assets have to be
						// loaded first.

		Assets.loadGame();
		boardActor = new BoardActor();
		this.renderer = new GameRenderer(boardActor);
		this.renderer
				.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	@Override
	public void hide() {
		this.renderer.dispose();
		Assets.disposeGame();
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
	} // Never called automatically.

}
