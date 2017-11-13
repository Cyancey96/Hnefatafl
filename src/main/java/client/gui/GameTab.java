package client.gui;

import client.MoveListener;
import client.game.PieceGUI;
import client.game.PieceTypeGUI;
import client.game.TileGUI;
import common.game.Match;
import common.game.Piece;
import common.game.Tile;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

import java.util.ArrayList;
import java.util.Collections;


public class GameTab extends Tab {

    private Match match;
    private GridPane grid;

    private final ArrayList<MoveListener> moveListeners = new ArrayList<>();

    private final int userId;


    /**
     * Constructs a match view pane for a given user
     *
     * @param title
     * @param userId
     */
    public GameTab(String title, int userId) {
        super(title);
        this.userId = userId;

        initGrid();
    }

    /**
     * Initializes the grid object. Called by the constructor
     */
    private void initGrid() {
        grid = new GridPane();
        grid.setGridLinesVisible(true);
        grid.getColumnConstraints().addAll(Collections.nCopies(11, new ColumnConstraints(50, 100, 1000, Priority.ALWAYS, HPos.CENTER, true)));
        grid.getRowConstraints().addAll(Collections.nCopies(11, new RowConstraints(50, 100, 1000, Priority.ALWAYS, VPos.CENTER, true)));
        setContent(grid);

        grid.setStyle("-fx-background-color: gray;");
    }

    /**
     * Sets the current match and updates the view accordingly
     *
     * @param match
     */
    public void setMatch(Match match) {
        this.match = match;

        updateMatchView();
    }

    /**
     *
     * @return The match that is currently being displayed
     */
    public Match getMatch() {
        return match;
    }

    /**
     *
     * @return The id of the local user
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Updates the view to match the state of the current match
     */
    private void updateMatchView() {
        initGrid();
        Tile[][] tiles = match.getBoard().getTiles();

        for (int row = 0; row < tiles.length; row++) {
            for (int col = 0; col < tiles[0].length; col++) {
                TileGUI tileGUI = initTileGUI(row, col);

                Piece piece = tiles[col][row].getPiece();
                if (piece != null) {
                    if (match.getCurrentPlayer() == getUserId() && piece.getUser() == getUserId()) {
                        tileGUI.setBackgroundColor("yellow");
                    }

                    PieceGUI pieceGUI = initPieceGUI(row, col, piece);
                    tileGUI.setPiece(pieceGUI);
                }
            }
        }
    }

    /**
     * Factory method for creating a PieceGUI for this match view
     *
     * @param row Row index of the piece
     * @param col Column index of the piece
     * @param piece Piece contained in this PieceGUI
     * @return
     */
    private PieceGUI initPieceGUI(int row, int col, Piece piece) {
        PieceTypeGUI type = PieceTypeGUI.ATTACKER;
        if (piece.isKing()) {
            type = PieceTypeGUI.KING;
        } else if (piece.getUser() == match.getDefender()) {
            type = PieceTypeGUI.DEFENDER;
        }
        PieceGUI pieceGUI = new PieceGUI(row, col, type);
        pieceGUI.setOnDragDetected(event -> {
            if (match.getCurrentPlayer() == getUserId()) {
                //Start drag
                Dragboard db = pieceGUI.startDragAndDrop(TransferMode.ANY);
                ClipboardContent cc = new ClipboardContent();
                cc.putString(pieceGUI.toString());
                db.setContent(cc);

                final ArrayList<Tile> moves = match.getAvaiableMoves(match.getBoard().getTiles()[pieceGUI.getyCoord()][pieceGUI.getxCoord()]);
                for (Node node : grid.getChildren()) {
                    if (node instanceof TileGUI) {
                        if (moves.contains(match.getBoard().getTiles()[((TileGUI) node).getyCoord()][((TileGUI) node).getxCoord()])) {
                            ((TileGUI) node).setBackgroundColor("green");
                        }
                    }
                }
            }

            event.consume();
        });
        pieceGUI.setOnDragDone(event -> {
            for (Node node : grid.getChildren()) {
                if (node instanceof TileGUI && !match.getBoard().getTiles()[((TileGUI) node).getyCoord()][((TileGUI) node).getxCoord()].hasPiece()) {
                    ((TileGUI) node).setBackgroundColor("transparent");
                }
            }
        });
        return pieceGUI;
    }

    /**
     * Factory method that creates a TileGUI for a given index
     *
     * @param row
     * @param col
     * @return
     */
    private TileGUI initTileGUI(int row, int col) {
        TileGUI tileGUI = new TileGUI(row, col);
        tileGUI.setOnDragOver(event -> {
            if (event.getGestureSource() instanceof PieceGUI && !event.getGestureSource().equals(tileGUI.getPiece())) {
                event.acceptTransferModes(TransferMode.ANY);
            }
            event.consume();
        });
        tileGUI.setOnDragDropped(event -> {
            PieceGUI piece = (PieceGUI) event.getGestureSource();

            moveListeners.forEach(listener -> listener.playerRequestedMove(match, piece.getyCoord(), piece.getxCoord(), tileGUI.getyCoord(), tileGUI.getxCoord()));
        });
        grid.add(tileGUI, col, row);
        return tileGUI;
    }

    /**
     * Registers a move listener to this match view
     *
     * @param listener
     * @return
     */
    public boolean addMoveListener(MoveListener listener) {
        return moveListeners.add(listener);
    }

    /**
     * Unregisters a move listener from this match view
     *
     * @param listener
     * @return
     */
    public boolean removeMoveListener(MoveListener listener) {
        return moveListeners.remove(listener);
    }

}