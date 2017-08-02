package com.bsencan.openchess.actors;

import com.bsencan.openchess.Assets;

/**
 * Created by Irina on 02.08.2017.
 */
//TODO:
public class SpecialTile extends Tile {
    /**
     * Creates a board tile.
     *
     * @param x      Horizontal index of the tile.
     * @param y      Vertical index of the tile.
     */
    public SpecialTile(int x, int y) {
        super(x,y);
        textures.put(StateType.NONE, Assets.gameAtlas.findRegion("tile-3"));
        textures.put(StateType.HIGHLIGHTED_MOVE, Assets.gameAtlas
                    .findRegion("tile-3-highlighted"));
        textures.put(StateType.HIGHLIGHTED_CAPTURE, Assets.gameAtlas
                .findRegion("tile-captured"));

    }

}
