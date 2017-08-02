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

package com.bsencan.openchess.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.bsencan.openchess.Assets;

import java.util.EnumMap;

/**
 * Represents a single tile on a chess board.
 * 
 * @author Baris Sencan
 */
public class Tile extends Actor {

	protected enum StateType{
			NONE,
			HIGHLIGHTED_MOVE,
			HIGHLIGHTED_CAPTURE
	};
	protected EnumMap<StateType, TextureRegion> textures= new EnumMap<>(StateType.class);
	StateType highState=StateType.NONE;

	/**
	 * Creates a board tile.
	 * 
	 * @param x
	 *            Horizontal index of the tile.
	 * @param y
	 *            Vertical index of the tile.
	 * @param isDark
	 *            Determines whether the tile will be light or dark colored.
	 */
	public Tile(int x, int y, boolean isDark) {

		this(x,y);
		if (isDark) {
			textures.put(StateType.NONE, Assets.gameAtlas.findRegion("tile-2"));
			textures.put(StateType.HIGHLIGHTED_MOVE, Assets.gameAtlas
					.findRegion("tile-2-highlighted"));
		} else {
			textures.put(StateType.NONE, Assets.gameAtlas.findRegion("tile-1"));
			textures.put(StateType.HIGHLIGHTED_MOVE, Assets.gameAtlas
					.findRegion("tile-1-highlighted"));
		}
		textures.put(StateType.HIGHLIGHTED_CAPTURE, Assets.gameAtlas
				.findRegion("tile-captured"));
	}

	public Tile(int x, int y) {
		this.setBounds( x, y,1,1);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		batch.draw(this.textures.get(this.highState), this.getX(), this.getY(),1,1);
	}
	public void highlightMove()
	{
		highState=StateType.HIGHLIGHTED_MOVE;
	}
	public void highlightCapture()
	{
		highState=StateType.HIGHLIGHTED_CAPTURE;
	}
	public void removeHighlight()
	{
		highState=StateType.NONE;
	}

}
