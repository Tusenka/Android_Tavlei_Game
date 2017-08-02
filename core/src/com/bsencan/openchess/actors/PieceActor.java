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


import java.util.Random;

import entity.Position;
import entity.Side;
import gamemechanics.model.tavlei.TavleiPiece;
import gamemechanics.model.tavlei.TavleiPieceType;


/**
 * <code>PieceActor</code> is a simple {@link Actor} implementation of a chess piece.
 * 
 * @author Baris Sencan
 */
public class PieceActor extends Actor {

	//public boolean isWhite;
	private TavleiPiece piece;


	private final TextureRegion textureRegion;


	public Side getSide(){
		return piece.getSide();
	}
	private static String getTextureName(TavleiPiece tavleiPiece)
	{
		byte num;
		if (tavleiPiece.getType()== TavleiPieceType.ROOK) num= (byte) (new Random().nextInt(4)+1);
		else num= (byte) (new Random().nextInt(1)+1);

		return (tavleiPiece.getSide().toString()+"-"+tavleiPiece.getType().toString()).toLowerCase()+num;
	}
	public PieceActor(entity.Position position, TavleiPiece tavleiPiece) {
		this.setBounds(position.getCol(), position.getRow(), 1, 1);
		this.piece = tavleiPiece;
		this.textureRegion = Assets.gameAtlas.findRegion(PieceActor.getTextureName(tavleiPiece));
	}


	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		batch.draw(this.textureRegion, this.getX(), this.getY(), 1, 1);
	}
	public entity.Position getPosition()
	{
		return new entity.Position((int)this.getY(),(int)getX());
	}

	public PieceActor setPosition(Position position){
		this.setX(position.getCol());
		this.setY(position.getRow());
		return this;
	}
}
