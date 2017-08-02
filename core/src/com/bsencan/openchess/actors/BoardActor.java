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

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.bsencan.openchess.OpenChess;

import android.support.annotation.NonNull;

import entity.*;
import gamemechanics.controller.tavlei.TavleiControllerManager;
import gamemechanics.model.Board;
import gamemechanics.model.event.EventManager;
import gamemechanics.model.tavlei.TavleiPiece;

/**
 * A chess board with pieceActors on it. Every instance of <code>BoardActor</code> is
 * actually a playable chess game.
 * 
 * @author Baris Sencan
 */
public class BoardActor extends Table {

	public TavleiControllerManager getControllerManager() {
		return controllerManager;
	}

	private TavleiControllerManager controllerManager;
	public PieceActor selectedPieceActor;

	/**
	 * Pointers to tiles for easy access.
	 */
	private final Tile[][] tiles;

	/**
	 * Pointers to pieceActors for easy access.
	 */
	private final PieceActor[][] pieceActors;

	/* -- Getters -- */

	public Tile getTileAt(@NonNull Position position) {
		return this.tiles[position.getCol()][position.getRow()];
	}

	public PieceActor getPieceAt(@NonNull Position position) {
		return this.pieceActors[position.getCol()][position.getRow()];
	}

	public Board getBoardModel()
	{
		return controllerManager.getBoard();
	}
	public byte getSize()
	{
		return	9;//for speed
	}
	/**
	 * Creates an empty board.
	 */
	public BoardActor() {
		EventManager.destroyEventSpace("default");
		controllerManager=new TavleiControllerManager();

		switch (OpenChess.gameModeType)
		{
			case PLAY_FROM_ONE_COMPUTER:controllerManager.playWithLocalUser(); break;
			case PLAY_WITH_AI:controllerManager.playWithCompute(OpenChess.mySide);break;
		}

		this.tiles=new Tile[getSize()][getSize()];
		this.pieceActors=new PieceActor[getSize()][getSize()];

		/* Basic board setup. */
		this.setBounds(0, 0, getSize(),  getSize());
		this.setClip(true);
		this.addListener(new BoardController(this));

		/* Add tiles. */
		for (int i = 0; i < this.tiles.length; i++) {
			for (int j = 0; j < this.tiles.length; j++) {
				if (isSpecialTile(i,j)) {
					this.tiles[i][j] = new SpecialTile(i, j);
				}
				else
				{
					this.tiles[i][j] = new Tile(i, j, ((i + j) % 2) == 0);
				}
				this.addActor(this.tiles[i][j]);
			}
		}
		start();
	}
	private boolean isSpecialTile(int x, int y)
	{
		return x==4&&y==4 || x==0&&y==0 || x==8 &&y==0 || x==0&&y==8 || x==8&&y==8;
	}
	private void start()
	{
		controllerManager.startGame();
		this.fill();
	}

	/**
	 * Fill the <code>BoardActor</code> with tavlei pieceActors in order to prepare it
	 * for a standard game.
	 */

	public void fill() {
		this.controllerManager.getBoard().getAllActivePiecesPositions().forEach((piece, position) ->
			this.addPiece(new PieceActor(position, (TavleiPiece) piece)));
	}

	/**
	 * Places a chess pieceActor on this <code>BoardActor</code>.
	 * 
	 * @param pieceActor
	 *            PieceActor to place.
	 */
	public void addPiece(@NonNull PieceActor pieceActor) {
		this.addActor(pieceActor);
		this.pieceActors[(int) pieceActor.getX()][(int) pieceActor.getY()] = pieceActor;
	}

	/**
	 * Changes the location of a piece.
	 *
	 */
	public void relocatePieceAt(@NonNull Move move) {
		Position start=move.getStart();
		Position destination=move.getDestination();
		PieceActor pieceActor = getPieceAt(move.getStart());
		if (pieceActor==null) return;

		this.pieceActors[start.getCol()][start.getRow()] = null;
		this.pieceActors[destination.getCol()][destination.getRow()] = pieceActor;

		pieceActor.setPosition(destination);

		//Remove captured pieces
		move.getDefeated().forEach(this::removePieceAt);
	}
	/**
	 * Changes the location of a piece. Doesn't check if a piece exists at the
	 * given location.
	 *
	 */
	public void removePieceAt(@NonNull Position position) {

		PieceActor pieceActor = getPieceAt(position);
		if (pieceActor==null) return;
		pieceActor.remove();
		this.pieceActors[position.getCol()][position.getRow()] = null;
	}




}
