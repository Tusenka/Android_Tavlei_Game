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

package com.bsencan.openchess.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.bsencan.openchess.Assets;
import com.bsencan.openchess.OpenChess;
import com.bsencan.openchess.actors.BoardActor;
import com.bsencan.openchess.actors.GameOverEventActor;
import com.bsencan.openchess.screens.GameScreen;

import de.tomgrill.gdxdialogs.core.GDXDialogs;
import de.tomgrill.gdxdialogs.core.GDXDialogsSystem;
import de.tomgrill.gdxdialogs.core.dialogs.GDXButtonDialog;
import entity.Side;
import entity.TavleiState;
import entity.event.GameEvent;
import generated.GameModeType;

/**
 * A {@link Renderer} for {@link GameScreen}.
 * 
 * @author Baris Sencan
 */
public class GameRenderer implements Renderer {

	private final Stage stage;
	private Table hud;
	private TextButton playAIButton;
	private TextButton playHumanButton;
	private final int boardSize;

	public GameRenderer(BoardActor boardActor) {
		boardSize=boardActor.getSize();
		stage = new Stage(new FitViewport(boardSize+1, boardSize+2));
		Gdx.input.setInputProcessor(this.stage);
		this.stage.addActor(boardActor);
		this.initUI();
		boardActor.addListener(event ->{
			if (event instanceof GameOverEventActor)
			{
				this.showWinDialog(((GameOverEventActor) event).getEvent());
			}
			return true;
		}
		);

	}

	private void initUI() {
		this.hud = new Table(Assets.skin);

		this.playAIButton = new TextButton(" Play with AI ", Assets.skin);
		this.playHumanButton = new TextButton(" Play with human ", Assets.skin);
		this.playAIButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				OpenChess.gameModeType=GameModeType.PLAY_WITH_AI;
				OpenChess.mySide= Side.WHITE;
				OpenChess.game.setScreen(new GameScreen());
			}
		});
		this.playHumanButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				OpenChess.gameModeType=GameModeType.PLAY_FROM_ONE_COMPUTER;
				OpenChess.game.setScreen(new GameScreen());
			}
		});

		this.hud.add(this.playAIButton);
		this.hud.add(this.playHumanButton);
		this.hud.setTransform(true);
		this.hud.setScale(1 / this.playAIButton.getHeight());
		this.hud.setPosition((int) (boardSize/2)+1, boardSize+1);
		this.stage.addActor(this.hud);
	}
	private void showWinDialog(GameEvent event)
	{
		TavleiState state= (TavleiState) event.getData();
		Side winner;
		String message;
		switch (state)
		{
			case WHITE_WINS:
				winner=Side.WHITE;
				message="Defenders win! Prince was escaped!";
				break;
			case BLACK_WINS:
				winner=Side.BLACK;
				message="Vikings win! Prince was surrounded!";
				break;
			default://null
				winner=null;
				message="Stalemate";
				break;
		}

		boolean isWinner=OpenChess.gameModeType.equals(GameModeType.PLAY_FROM_ONE_COMPUTER) || (OpenChess.mySide.equals(winner));
		GDXDialogs dialogs = GDXDialogsSystem.install();
		GDXButtonDialog endDialog = dialogs.newDialog(GDXButtonDialog.class);
		endDialog.setTitle(isWinner?"You win!!!!":"Game End");
		endDialog.setMessage(message);
		endDialog.addButton("OK");
		endDialog.build().show();

	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(.3f, .3f, .4f, 1);
		Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
		this.stage.draw();
	}

	@Override
	public void setSize(int width, int height) {
		this.stage.getViewport().update(width, height, false);
		Gdx.graphics.requestRendering();
	}

	@Override
	public void dispose() {
		this.stage.dispose();
	}

}
