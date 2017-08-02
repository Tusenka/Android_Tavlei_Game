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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.utils.Array;
import com.bsencan.openchess.OpenChess;


import java.util.LinkedHashSet;
import java.util.Set;

import entity.Move;
import entity.Position;
import entity.Side;
import entity.event.GameEvent;
import entity.event.GameMechanicEventType;
import gamemechanics.controller.tavlei.TavleiController;
import gamemechanics.model.event.EventManager;
import generated.GameModeType;


/**
 * BoardActor Controller. Represents higher level of abstraction.
 */
public class BoardController extends ActorGestureListener{

	private static final String TAG = "Chess";
	private final BoardActor boardActor;
	private final Array<Tile> highlightedTiles = new Array<Tile>();
	private Move lastMove;

	public BoardController(BoardActor boardActor) {
		super();
		this.boardActor = boardActor;
		subscribeEvents();
	}
	private boolean checkSide(PieceActor pieceActor)
	{
		return (OpenChess.gameModeType==GameModeType.PLAY_FROM_ONE_COMPUTER || pieceActor.getSide()==OpenChess.mySide);
	}
	private boolean checkSide(Move move)
	{
		return (OpenChess.gameModeType==GameModeType.PLAY_FROM_ONE_COMPUTER || boardActor.getPieceAt(move.getStart()).getSide()==OpenChess.mySide);
	}
	public boolean checkTurn(Move move)
	{
		return checkSide(move)&&getValidMoves(boardActor.getPieceAt(move.getStart())).stream().anyMatch(move::equals);
	}
	@Override
	public void tap(InputEvent event, float x, float y, int count, int button) {
		Actor target = event.getTarget(); // Tapped actor.
		int tx = (int) target.getX(); // Tapped tile x.
		int ty = (int) target.getY(); // Tapped tile y.
		Position destination=new Position((int)target.getY(), (int)target.getX());

		if (target instanceof PieceActor) {
			PieceActor pieceActor = (PieceActor) target;
			if (checkSide(pieceActor)) {
				this.selectPiece(pieceActor);
			}
		} else {
			this.localMovePiece(this.boardActor.selectedPieceActor, destination);
		}
	}

	/*
	Low level move for the local users made turn. Check turn and control highlight
	 */
	private void localMovePiece(PieceActor pieceActor, Position destination) {
		if (pieceActor == null) {
			return;
		}
		Move move=new Move(pieceActor.getPosition(), destination);
		if (!checkTurn(move)) return;

		this.lastMove=move;
		boardActor.getControllerManager().getBoard().addInfoMove(move, pieceActor.getSide());

		/* Remove highlights. */
		this.removeMoveHighlights();

		/* Move */
		this.boardActor.relocatePieceAt(move);


		Side side=this.boardActor.selectedPieceActor.getSide();
		/* Deselect and advance round. */
		this.boardActor.selectedPieceActor = null;

		getEventManager().triggerEvent(new GameEvent(GameMechanicEventType.PROPOSE_MOVE, lastMove).setSourceSide(side));
	}

	private void selectPiece(PieceActor pieceActor) {
		this.removeMoveHighlights();
		this.boardActor.selectedPieceActor = pieceActor;
		this.addMoveHighlightsForPiece(pieceActor);
	}

	public Set<Move> getValidMoves(PieceActor pieceActor)
	{
		Set<Move> moves;
		try {
			TavleiController controller = boardActor.getControllerManager().getCurrentController();

			moves= controller.getMovesForPieceAt(pieceActor.getPosition());
			if (moves==null) moves=new LinkedHashSet<>();
			return moves;
		}
		catch (NullPointerException e)
		{
			Gdx.app.debug(TAG, "Trying to move between games. Mustn't affect on game"+e);
			return new LinkedHashSet<>();
		}
	}
	/**
	 * Returns tiles on a boardActor that can be accessed by this
	 * <code>PieceActor<code> instance according to its <code>validMoves</code>
	 * array.
	 **            The <code>BoardActor</code> instance to fetch tiles from.
	 * @return Resulting tile array.
	 */
	public Array<Tile> getValidMoveTiles(PieceActor pieceActor) {
		Array<Tile> tiles = new Array<>();
		for (entity.Move move : getValidMoves( pieceActor)) {
			tiles.add(boardActor.getTileAt(move.getDestination()));
		}
		return tiles;
	}

	public Array<Tile> getCaptureMoveTiles(PieceActor pieceActor) {
		Array<Tile> tiles = new Array<Tile>();
		for (entity.Move move : getValidMoves( pieceActor)) {
			move.getDefeated().forEach(position -> tiles.add(boardActor.getTileAt(position)));
		}
		return tiles;
	}
	// TODO: Complete before writing javadoc comments for this.
	private void addMoveHighlightsForPiece(PieceActor pieceActor) {
		getValidMoveTiles(pieceActor).forEach(tile -> {
			this.highlightedTiles.add(tile);
			tile.highlightMove();
		});
		getCaptureMoveTiles(pieceActor).forEach(tile -> {
			this.highlightedTiles.add(tile);
			tile.highlightCapture();
		});
	}

	private void removeMoveHighlights() {
		while (this.highlightedTiles.size > 0) {
			this.highlightedTiles.pop().removeHighlight();
		}
	}

	private EventManager getEventManager() {
		return EventManager.getEventListenerForMe(null, this);
	}

	/**
	 * Tries to undo move. If it doesn't possible, do nothing.
	 */
	public void undoMove()
	{
		try {
			this.localMovePiece(boardActor.getPieceAt(lastMove.getDestination()), lastMove.getStart());
		}
		catch (NullPointerException e)
		{
			Gdx.app.error(TAG, "Can not undo move: "+e.toString());
		}
	}
	private void onMoveMake(GameEvent event)
	{
		Move move= (Move) event.getData();
		this.boardActor.relocatePieceAt(move);
		Gdx.graphics.requestRendering();
	}
	private void onGameRulesError(GameEvent event)
	{
		Gdx.app.error(TAG, "Game rule broken:"+event.getData().toString());
		undoMove();
	}
	private void onGameOver(GameEvent event)
	{
		boardActor.fire(new GameOverEventActor(event));
	}

	private void subscribeEvents()
	{
		getEventManager().unsubscribeMe();
		getEventManager().addListener(GameMechanicEventType.MOVE, this::onMoveMake);
		getEventManager().addListener(GameMechanicEventType.GAME_RULES_ERROR, this::onGameRulesError);
		getEventManager().addListener(GameMechanicEventType.GAME_OVER, this::onGameOver);
	}
	/*@Override
	protected void finalize() throws Throwable {
		super.finalize();
		getEventManager().unsubscribeMe();
	}*/


}
