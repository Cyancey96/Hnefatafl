package edu.colostate.cs.cs414.hnefatafl.client.gui.gamepieces;

import edu.colostate.cs.cs414.hnefatafl.client.gui.GameTab;
import edu.colostate.cs.cs414.hnefatafl.common.game.Tile;
import edu.colostate.cs.cs414.hnefatafl.common.game.TileType;
import javafx.scene.layout.BorderPane;

public class TileGUI extends BorderPane {

    private Tile tile;
    private PieceGUI piece;
    private int xCoord, yCoord;

    // Create a tile in the appropriate space, and set color to gray
    public TileGUI(Tile tile, int x_coord, int y_coord) {
        this.xCoord = x_coord;
        this.yCoord = y_coord;
    }

    public void resetColor() {
        if (!hasPiece()) {
            if ((xCoord+yCoord) % 2 == 0) setBackgroundColor(GameTab.TILE_COLOR_ALT);
            else setBackgroundColor(GameTab.TILE_COLOR);

            if (getTile() != null && getTile().getType() == TileType.THRONE) setBackgroundColor(GameTab.TILE_COLOR_THRONE);
        }
    }

    public boolean hasPiece() {
        return piece != null;
    }

    public PieceGUI getPiece() {
        return piece;
    }

    public void setPiece(PieceGUI p) {
        piece = p;
        setCenter(p);
    }

    public Tile getTile() {
        return tile;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }

    public int getyCoord() {
        return yCoord;
    }

    public int getxCoord() {
        return xCoord;
    }

    public void setBackgroundColor(String color) {
        setStyle("-fx-background-color: " + color + ";");
    }

}

