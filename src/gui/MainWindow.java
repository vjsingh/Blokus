/*
 * MainWindow.java
 *
 * Created on Apr 26, 2010, 11:09:07 AM
 */
package gui;

import blokus.*;
import network.*;
import ai.AIPlayer;
import com.sun.j3d.*;

import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.pickfast.PickCanvas;
//import com.sun.j3d.utils.picking.PickIntersection;
import com.sun.j3d.utils.pickfast.PickIntersection;
import com.sun.j3d.utils.universe.SimpleUniverse;

import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.j3d.*;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingWorker;
import javax.swing.text.*;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.vecmath.*;
import static blokus.Shape.*;
import static blokus.Face.*;
import static blokus.Color.*;

/**
 *
 * @author ahills
 */
public class MainWindow extends javax.swing.JFrame {

    /**My Variables**/
    private LinkedList<Player> _players;
    private PiecesView _p1PiecesView;
    private PreviousMovesList previousMovesList;
    private Board _board;
    private Canvas3D _can;
    private Transform3D _transform3D = new Transform3D();
    private SimpleUniverse _universe;
    private BranchGroup _group;
    private TransformGroup _mover;
    private BlokusMain _main;
    private Game _game;
    private BranchGroup _mainCube;
    private BranchGroup _gamePieces;
    private BranchGroup _mousePiece;
    private PickCanvas _pickCanvas;
    private Box _myCube;
    private Shape3D _front;
    private Shape3D _back;
    private Shape3D _top;
    private Shape3D _bottom;
    private Shape3D _left;
    private Shape3D _right;
    private Move _curMove;
    public Move _sentMove;
    public boolean _getMove = false;
    public boolean _reverted=false;
    public MainWindow t;
    private boolean _isHost;
    private PieceDisplay[] _pieceDisplays;
    private JLabel[] _playerLabels;
    private JPanel[] _pieceContainers;
    private Player[] _viewPlayers;
    private int _moveNum;
    private boolean shouldRateMoves;
    private BlokusMain _pastMain;
    private GameRunner _gameRunner;


    public class Mover extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                setStatus(_board.isValidMessage(_curMove).getMessage(), _curMove.getColor());
                if ((_getMove) && (_curMove != null) && (_board.isValidBoolean(_curMove))) {
                    sendMove(_curMove);
                    _getMove = false;
                }
            }
        }
    }

    public class Picker extends MouseMotionAdapter {
        @Override
        public void mouseMoved(MouseEvent e) {

            if ((!_reverted)&&(_players != null))
            {
            Color c=_game.getCurrentTurn();
            java.awt.Color col;
        if (c == Color.BLUE) {
            col = java.awt.Color.BLUE;
        } else if (c == Color.GREEN) {
            col = java.awt.Color.GREEN;
        } else if (c == Color.RED) {
            col = java.awt.Color.RED;
        } else {
            col = java.awt.Color.YELLOW;
        }

            boolean isHuman=false;
            Iterator iter=_players.iterator();
            while(iter.hasNext())
            {
                Player p = (Player) iter.next();
                if ((p.getColor()==c)&&(p instanceof HumanPlayer))
                {
                    isHuman=true;
                }
            }
            if ((_p1PiecesView.getColor()==col)) {
                _pickCanvas.setShapeLocation(e);
                PickInfo[] result = _pickCanvas.pickAllSorted();
                if (result != null) {
                    PickIntersection intersect = null;
                    Square sq = null;
                    for (int i = 0; i < result.length; i++) {

                        if (result[i].getNode().equals(_left)) {
                            intersect = new PickIntersection(result[i].getLocalToVWorld(), result[i].getIntersectionInfos()[0]);
                            Point3d point = intersect.getPointCoordinates();
                            sq = getSquareIntersect(LEFT, point);
                            break;
                        }
                        if (result[i].getNode().equals(_right)) {
                            intersect = new PickIntersection(result[i].getLocalToVWorld(), result[i].getIntersectionInfos()[0]);
                            Point3d point = intersect.getPointCoordinates();
                            sq = getSquareIntersect(RIGHT, point);
                            break;
                        }
                        if (result[i].getNode().equals(_top)) {
                            intersect = new PickIntersection(result[i].getLocalToVWorld(), result[i].getIntersectionInfos()[0]);
                            Point3d point = intersect.getPointCoordinates();
                            sq = getSquareIntersect(TOP, point);
                            break;
                        }
                        if (result[i].getNode().equals(_bottom)) {
                            intersect = new PickIntersection(result[i].getLocalToVWorld(), result[i].getIntersectionInfos()[0]);
                            Point3d point = intersect.getPointCoordinates();
                            sq = getSquareIntersect(BOTTOM, point);
                            break;
                        }
                        if (result[i].getNode().equals(_front)) {
                            intersect = new PickIntersection(result[i].getLocalToVWorld(), result[i].getIntersectionInfos()[0]);
                            Point3d point = intersect.getPointCoordinates();
                            sq = getSquareIntersect(FRONT, point);
                            break;
                        }
                        if (result[i].getNode().equals(_back)) {
                            intersect = new PickIntersection(result[i].getLocalToVWorld(), result[i].getIntersectionInfos()[0]);
                            Point3d point = intersect.getPointCoordinates();
                            sq = getSquareIntersect(BACK, point);
                            break;
                        }
                    }
                    if ((sq != null) && (-1 < sq.getRow()) && (sq.getRow() < 9) && (sq.getColumn() < 9) && (sq.getColumn() > -1)) {
                        if (_p1PiecesView != null) {
                            Piece curPiece = _p1PiecesView.getSelected();
                            if (_p1PiecesView.getColor() == java.awt.Color.blue) {
                                c = BLUE;
                            } else if (_p1PiecesView.getColor() == java.awt.Color.yellow) {
                                c = YELLOW;
                            } else if (_p1PiecesView.getColor() == java.awt.Color.red) {
                                c = RED;
                            } else {
                                c = GREEN;
                            }
                            _curMove = new Move(sq, curPiece, c);
                            drawMyMove(_curMove);
                        }
                    } else {
                        if ((_mousePiece != null) && (_mousePiece.isLive())) {
                            _mousePiece.detach();
                        }
                        _mousePiece = null;
                        _curMove = null;
                    }
                }
            }
            else {
                        if ((_mousePiece != null) && (_mousePiece.isLive())) {
                            _mousePiece.detach();
                        }
                        _mousePiece = null;
                        _curMove = null;
                    }
        }
        }

        public void drawMyMove(Move mv) {
            if ((_mousePiece != null) && (_mousePiece.isLive())) {
                _mousePiece.detach();
            }
            _mousePiece = new BranchGroup();
            _mousePiece.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
            _mousePiece.setCapability(BranchGroup.ALLOW_DETACH);
            _mousePiece.setCapability(BranchGroup.ALLOW_PICKABLE_READ);
            _mousePiece.setCapability(BranchGroup.ALLOW_PICKABLE_WRITE);
            _mousePiece.setCapability(BranchGroup.ENABLE_PICK_REPORTING);
            _mousePiece.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
            LinkedList<Square> squares = mv.getSquares();
            Iterator i = squares.iterator();
            Color c = mv.getColor();
            setStatus(_board.isValidMessage(mv).getMessage(), mv.getColor());
            if (!_board.isValidBoolean(mv)) {
                c = null;
            }
            while (i.hasNext()) {
                Square s = (Square) i.next();
                addSquare(s, c, _mousePiece);
            }
            _mover.addChild(_mousePiece);
        }

        public void drawMySquare(Square sq) {
            if ((_mousePiece != null) && (_mousePiece.isLive())) {
                _mousePiece.detach();
            }
            _mousePiece = new BranchGroup();
            _mousePiece.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
            _mousePiece.setCapability(BranchGroup.ALLOW_DETACH);
            _mousePiece.setCapability(BranchGroup.ALLOW_PICKABLE_READ);
            _mousePiece.setCapability(BranchGroup.ALLOW_PICKABLE_WRITE);
            _mousePiece.setCapability(BranchGroup.ENABLE_PICK_REPORTING);
            _mousePiece.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
            if ((-1 < sq.getRow()) && (sq.getRow() < 9) && (sq.getColumn() < 9) && (sq.getColumn() > -1)) {
                addSquare(sq, Color.RED, _mousePiece);
            }
        }

        public Square getSquareIntersect(Face face, Point3d point) {
            double x, y, z;
            x = point.x;
            y = point.y;
            z = point.z;
            double column;
            double row;
            int c;
            int r;
            if (face == Face.FRONT) {
                column = (x + 0.4) / 0.0888 - 1;
                row = (-y + 0.4) / 0.0888 - 1;
            } else if (face == Face.BACK) {
                column = (-x + 0.4) / 0.0888 - 1;
                row = (-y + 0.4) / 0.0888 - 1;
            } else if (face == Face.LEFT) {
                row = (-y + 0.4) / 0.0888 - 1;
                column = (z + 0.4) / 0.0888 - 1;
            } else if (face == Face.RIGHT) {
                row = (-y + 0.4) / 0.0888 - 1;
                column = (-z + 0.4) / 0.0888 - 1;
            } else if (face == Face.BOTTOM) {
                row = (-z + 0.4) / 0.0888 - 1;
                column = (x + 0.4) / 0.0888 - 1;
            } else {
                column = (x + 0.4) / 0.0888 - 1;
                row = (z + 0.4) / 0.0888 - 1;
            }
            c = (int) Math.ceil(column);
            r = (int) Math.ceil(row);
            return new Square(face, r, c);
        }
    }

    /** Creates new form MainWindow */
    public MainWindow(BlokusMain main) {
        t = this;
        initComponents();
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        _main = main;
        _can = new Canvas3D(config);
        CubePanel.setLayout(new BorderLayout());
        CubePanel.add("Center", _can);
        P1piecescontainer.setLayout(new BorderLayout());
        P2piecescontainer.setLayout(new BorderLayout());
        P3piecescontainer.setLayout(new BorderLayout());
        P4piecescontainer.setLayout(new BorderLayout());

        makeTestBoard();
        _universe = new SimpleUniverse(_can);
        setupUniverse();
        _pickCanvas = new PickCanvas(_can, _group);
        _pickCanvas.setMode(PickInfo.PICK_GEOMETRY);
        _pickCanvas.setFlags(PickInfo.CLOSEST_GEOM_INFO | PickInfo.NODE);
        _can.addMouseMotionListener(new Picker());
        _can.addMouseListener(new Mover());

        previousMovesList = new PreviousMovesList(this);
        previousMovesScrollPane.setViewportView(previousMovesList);
        
        _mousePiece = null;
        _pieceDisplays = new PieceDisplay[4];
        _playerLabels = new JLabel[4];
        _pieceContainers = new JPanel[4];
        _pieceContainers[0] = P1piecescontainer;
        _pieceContainers[1] = P2piecescontainer;
        _pieceContainers[2] = P3piecescontainer;
        _pieceContainers[3] = P4piecescontainer;
        
        //make About and Rules wrap
        AboutTextArea.setLineWrap(true);
        AboutTextArea.setWrapStyleWord(true);

        RulesTextArea.setLineWrap(true);
        RulesTextArea.setWrapStyleWord(true);

        //disable save until a game is started
        saveMenuItem.setEnabled(false);

    }

    public void tempRevert(Board board)
    {
        if ((_gamePieces != null) && (_gamePieces.isLive())) {
            _gamePieces.detach();
        }
        _gamePieces = new BranchGroup();
        _gamePieces.setCapability(BranchGroup.ALLOW_DETACH);
        _gamePieces.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        _gamePieces.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);

        _mover.addChild(_gamePieces);
        drawBoard(board);
    }

    private void revertBack()
    {
        if ((_gamePieces != null) && (_gamePieces.isLive())) {
            _gamePieces.detach();
        }
        _gamePieces = new BranchGroup();
        _gamePieces.setCapability(BranchGroup.ALLOW_DETACH);
        _gamePieces.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        _gamePieces.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);

        _mover.addChild(_gamePieces);
        drawBoard(_board);
    }

    private void sendMove(Move mv) {
        _sentMove = mv;
        _getMove = false;
    }

    public void getMove() {
        _getMove = true;
    }

    public void endGame() {
        int[] points = getPoints();
        EndGameP1Label.setText(P1namelabel.getText());
        EndGameP2Label.setText(P2namelabel.getText());
        EndGameP3Label.setText(P3namelabel.getText());
        EndGameP4Label.setText(P4namelabel.getText());
        /**
        EndGameP1Score.setText(points[0] + "");
        EndGameP2Score.setText(points[1] + "");
        EndGameP3Score.setText(points[2] + "");
        EndGameP4Score.setText(points[3] + "");
         * */
        String winner = EndGameP1Label.getText();
        int winpoints = points[0];
        if (points[1] > winpoints) {
            winner = EndGameP2Label.getText();
            winpoints = points[1];
        }
        if (points[2] > winpoints) {
            winner = EndGameP3Label.getText();
            winpoints = points[2];
        }
        if (points[3] > winpoints) {
            winner = EndGameP4Label.getText();
            winpoints = points[3];
        }
        EndGameWinnerLabel.setText(winner+" is the winner!");
        EndGameWindow.pack();
        EndGameWindow.setVisible(true);
    }

    private int[] getPoints() {
        int[] points = new int[4];
        points[0] = _players.get(0).getScore();
        points[1] = _players.get(1).getScore();
        points[2] = _players.get(2).getScore();
        points[3] = _players.get(3).getScore();

        if (points[0] == 89) {
            points[0] += 15;
        }
        if (points[1] == 89) {
            points[1] += 15;
        }
        if (points[2] == 89) {
            points[2] += 15;
        }
        if (points[3] == 89) {
            points[3] += 15;
        }
        return points;
    }

    private void drawBoard(Board board) {
        for (int j = 0; j < Constants.BOARD_SIZE; j++) {
            for (int k = 0; k < Constants.BOARD_SIZE; k++) {
                Square temp = new Square(Face.FRONT, j, k);
                Color c = board.getSquare(temp);
                if (c != null) {
                    addSquare(Face.FRONT, j, k, c);
                }
                temp = new Square(Face.BACK, j, k);
                c = board.getSquare(temp);
                if (c != null) {
                    addSquare(Face.BACK, j, k, c);
                }
                temp = new Square(Face.LEFT, j, k);
                c = board.getSquare(temp);
                if (c != null) {
                    addSquare(Face.LEFT, j, k, c);
                }
                temp = new Square(Face.RIGHT, j, k);
                c = board.getSquare(temp);
                if (c != null) {
                    addSquare(Face.RIGHT, j, k, c);
                }
                temp = new Square(Face.TOP, j, k);
                c = board.getSquare(temp);
                if (c != null) {
                    addSquare(Face.TOP, j, k, c);
                }
                temp = new Square(Face.BOTTOM, j, k);
                c = board.getSquare(temp);
                if (c != null) {
                    addSquare(Face.BOTTOM, j, k, c);
                }
            }
        }

    }

    public void drawMove(Move move) {
        LinkedList<Square> squares = move.getSquares();
        Iterator i = squares.iterator();
        while (i.hasNext()) {
            Square s = (Square) i.next();
            addSquare(s.getFace(), s.getRow(), s.getColumn(), move.getColor());
        }
        _moveNum++;
        String curName = "";
        Player player = null;
        java.awt.Color color = null;

        switch (move.getColor()) {
            case BLUE:
                player = _players.get(0);
                color = java.awt.Color.blue;
                break;
            case YELLOW:
                player = _players.get(1);
                color = java.awt.Color.yellow;
                break;
            case RED:
                player = _players.get(2);
                color = java.awt.Color.red;
                break;
            case GREEN:
                player = _players.get(3);
                color = java.awt.Color.green;
                break;
        }

        //if the move was made by a network player, don't swap the display
        if (! (player instanceof NetworkPlayer)) {
            this.swapOutDisplay();
        }
        curName = player.getName();
        if (player instanceof HumanPlayer) {
            rateMovesLabel.setText("Move Rating: " + Math.round(100*_game.getMoveRating(player.getColor()))+"%");
            if (shouldRateMoves) {
                rateMovesLabel.setForeground(color);
            }
        }
        PreviousMovePanel pan = new PreviousMovePanel(move, new Board(_board), curName, _moveNum, this);

        previousMovesList.add(pan);
        previousMovesList.revalidate();
        previousMovesList.repaint();
        revertBack();
    }

    private void addSquare(Square sq, Color col, BranchGroup square) {
        Face face = sq.getFace();
        int column = sq.getColumn();
        int row = sq.getRow();
        Material mat = new Material();
        if (col == null) {
            mat.setAmbientColor(new Color3f(0f, 0f, 0f));
            mat.setDiffuseColor(new Color3f(0f, 0f, 0f));
            mat.setSpecularColor(new Color3f(0f, 0f, 0f));
        }
        if (col == Color.RED) {
            mat.setAmbientColor(new Color3f(0.7f, 0f, 0f));
            mat.setDiffuseColor(new Color3f(1f, 0.2f, 0.2f));
            mat.setSpecularColor(new Color3f(1f, 0.2f, 0.2f));
        }
        if (col == Color.BLUE) {
            mat.setAmbientColor(new Color3f(0f, 0f, 0.7f));
            mat.setDiffuseColor(new Color3f(0.2f, 0.2f, 1f));
            mat.setSpecularColor(new Color3f(0.2f, 0.2f, 1f));
        }
        if (col == Color.GREEN) {
            mat.setAmbientColor(new Color3f(0f, 0.7f, 0f));
            mat.setDiffuseColor(new Color3f(0.2f, 1f, 0.2f));
            mat.setSpecularColor(new Color3f(0.2f, 1f, 0.2f));
        }
        if (col == Color.YELLOW) {
            mat.setAmbientColor(new Color3f(0.7f, 0.7f, 0f));
            mat.setDiffuseColor(new Color3f(0.7f, 0.7f, 0.2f));
            mat.setSpecularColor(new Color3f(0.7f, 0.7f, 0.2f));
        }
        Appearance ap = new Appearance();
        ap.setMaterial(mat);
        ap.setTransparencyAttributes(new TransparencyAttributes(TransparencyAttributes.NICEST, 0.15f));
        Box myCube = new Box(0.044f, 0.044f, 0.044f, ap);
        Matrix3d rot = new Matrix3d();
        rot.setIdentity();
        float x, y, z;
        x = y = z = 0f;
        if (face == Face.FRONT) {
            x = -0.4f + (column + 1) * (0.0888f) - 0.044f;
            y = (-0.4f + (row + 1) * (0.0888f) - 0.044f) * -1f;
            z = 0.4f - 0.02f;
        } else if (face == Face.BACK) {
            x = -(-0.4f + (column + 1) * (0.0888f) - 0.044f);
            y = (-0.4f + (row + 1) * (0.0888f) - 0.044f) * -1f;
            z = -0.4f + 0.02f;
        } else if (face == Face.LEFT) {
            y = (-0.4f + (row + 1) * (0.0888f) - 0.044f) * -1f;
            z = -0.4f + (column + 1) * (0.0888f) - 0.044f;
            x = -1f * (0.4f - 0.022f);
        } else if (face == Face.RIGHT) {
            y = (-0.4f + (row + 1) * (0.0888f) - 0.044f) * -1f;
            z = -(-0.4f + (column + 1) * (0.0888f) - 0.044f);
            x = (0.4f - 0.022f);
        } else if (face == Face.BOTTOM) {
            z = -(-0.4f + (row + 1) * (0.0888f) - 0.044f);
            x = -0.4f + (column + 1) * (0.0888f) - 0.044f;
            y = -1f * (0.4f - 0.022f);
        } else if (face == Face.TOP) {
            x = -0.4f + (column + 1) * (0.0888f) - 0.044f;
            z = (-0.4f + (row + 1) * (0.0888f) - 0.044f);
            y = (0.4f - 0.022f);
        }


        Vector3d v = new Vector3d(x, y, z);
        Transform3D t3d = new Transform3D(rot, v, 1);
        TransformGroup trans = new TransformGroup(t3d);
        trans.addChild(myCube);
        square.addChild(trans);
    }

    private void addSquare(Face face, int row, int column, Color col) {
        BranchGroup square = new BranchGroup();
        Material mat = new Material();
        if (col == Color.RED) {
            mat.setAmbientColor(new Color3f(0.7f, 0f, 0f));
            mat.setDiffuseColor(new Color3f(1f, 0.2f, 0.2f));
            mat.setSpecularColor(new Color3f(1f, 0.2f, 0.2f));
        }
        if (col == Color.BLUE) {
            mat.setAmbientColor(new Color3f(0f, 0f, 0.7f));
            mat.setDiffuseColor(new Color3f(0.2f, 0.2f, 1f));
            mat.setSpecularColor(new Color3f(0.2f, 0.2f, 1f));
        }
        if (col == Color.GREEN) {
            mat.setAmbientColor(new Color3f(0f, 0.7f, 0f));
            mat.setDiffuseColor(new Color3f(0.2f, 1f, 0.2f));
            mat.setSpecularColor(new Color3f(0.2f, 1f, 0.2f));
        }
        if (col == Color.YELLOW) {
            mat.setAmbientColor(new Color3f(0.7f, 0.7f, 0f));
            mat.setDiffuseColor(new Color3f(0.7f, 0.7f, 0.2f));
            mat.setSpecularColor(new Color3f(0.7f, 0.7f, 0.2f));
        }
        Appearance ap = new Appearance();
        ap.setMaterial(mat);
        ap.setTransparencyAttributes(new TransparencyAttributes(TransparencyAttributes.NICEST, 0.15f));
        Box myCube = new Box(0.044f, 0.044f, 0.044f, ap);
        Matrix3d rot = new Matrix3d();
        rot.setIdentity();
        float x, y, z;
        x = y = z = 0f;
        if (face == Face.FRONT) {
            x = -0.4f + (column + 1) * (0.0888f) - 0.044f;
            y = (-0.4f + (row + 1) * (0.0888f) - 0.044f) * -1f;
            z = 0.4f - 0.02f;
        } else if (face == Face.BACK) {
            x = -(-0.4f + (column + 1) * (0.0888f) - 0.044f);
            y = (-0.4f + (row + 1) * (0.0888f) - 0.044f) * -1f;
            z = -0.4f + 0.02f;
        } else if (face == Face.LEFT) {
            y = (-0.4f + (row + 1) * (0.0888f) - 0.044f) * -1f;
            z = -0.4f + (column + 1) * (0.0888f) - 0.044f;
            x = -1f * (0.4f - 0.022f);
        } else if (face == Face.RIGHT) {
            y = (-0.4f + (row + 1) * (0.0888f) - 0.044f) * -1f;
            z = -(-0.4f + (column + 1) * (0.0888f) - 0.044f);
            x = (0.4f - 0.022f);
        } else if (face == Face.BOTTOM) {
            z = -(-0.4f + (row + 1) * (0.0888f) - 0.044f);
            x = -0.4f + (column + 1) * (0.0888f) - 0.044f;
            y = -1f * (0.4f - 0.022f);
        } else if (face == Face.TOP) {
            x = -0.4f + (column + 1) * (0.0888f) - 0.044f;
            z = (-0.4f + (row + 1) * (0.0888f) - 0.044f);
            y = (0.4f - 0.022f);
        }


        Vector3d v = new Vector3d(x, y, z);
        Transform3D t3d = new Transform3D(rot, v, 1);
        TransformGroup trans = new TransformGroup(t3d);
        trans.addChild(myCube);
        square.addChild(trans);
        _gamePieces.addChild(square);
    }

    private void setupUniverse() {
        if ((_gamePieces != null) && (_gamePieces.isLive())) {
            _gamePieces.detach();
        }
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
        _group = new BranchGroup();
        _group.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        _group.setCapability(BranchGroup.ALLOW_DETACH);
        _mainCube = new BranchGroup();
        _mainCube.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        _mainCube.setCapability(BranchGroup.ALLOW_DETACH);
        _gamePieces = new BranchGroup();
        _gamePieces.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        _gamePieces.setCapability(BranchGroup.ALLOW_DETACH);
        _mover = new TransformGroup(_transform3D);
        _mover.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        _mover.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        _mover.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        _group.addChild(_mover);
        Material mat = new Material();
        mat.setAmbientColor(new Color3f(0.4f, 0.4f, 0.4f));
        mat.setDiffuseColor(new Color3f(0.7f, 0.7f, 0.7f));
        mat.setSpecularColor(new Color3f(0.7f, 0.7f, 0.7f));
        _myCube = new Box(0.4f, 0.4f, 0.4f, null);
        Appearance ap = new Appearance();
        ap.setMaterial(mat);
        PolygonAttributes polyatt = new PolygonAttributes();
        polyatt.setPolygonMode(PolygonAttributes.POLYGON_LINE);
        polyatt.setCullFace(PolygonAttributes.CULL_NONE);
        //ap.setPolygonAttributes(polyatt);
        _myCube.setAppearance(ap);
        enablePicking(_myCube);
        _front = _myCube.getShape(Box.FRONT);
        _back = _myCube.getShape(Box.BACK);
        _top = _myCube.getShape(Box.TOP);
        _bottom = _myCube.getShape(Box.BOTTOM);
        _left = _myCube.getShape(Box.LEFT);
        _right = _myCube.getShape(Box.RIGHT);
        _mainCube.addChild(_myCube);
        addGrid(_mainCube);
        // add the box to the branchgroup
        _mover.addChild(_mainCube);

        Color3f light1Color = new Color3f(.5f, .5f, .5f);

        Vector3f light1Direction = new Vector3f(4.0f, -7.0f, -12.0f);

        DirectionalLight light1 = new DirectionalLight(light1Color, light1Direction);

        //add the light in the bounding sphere
        light1.setInfluencingBounds(bounds);

        //add the light to the BranchGroup
        _group.addChild(light1);
        // --- END LIGHT


        AmbientLight amb = new AmbientLight(new Color3f(1.5f, 1.5f, 1.5f));
        amb.setInfluencingBounds(bounds);
        _group.addChild(amb);
        _mover.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        _mover.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        _mover.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
        _mover.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        MouseRot rota = new MouseRot();
        rota.setTransformGroup(_mover);
        _group.addChild(rota);
        _mover.addChild(_gamePieces);
        rota.setSchedulingBounds(bounds);
        enablePicking(_group);
        //_universe = new SimpleUniverse(_can);
        _universe.getViewingPlatform().setNominalViewingTransform();
        _universe.addBranchGraph(_group);
        drawBoard(_board);
    }

    public void enablePicking(Node node) {
        node.setPickable(true);
        node.setCapability(Node.ENABLE_PICK_REPORTING);
        try {
            Group group = (Group) node;
            for (Enumeration e = group.getAllChildren(); e.hasMoreElements();) {
                enablePicking((Node) e.nextElement());
            }
        } catch (ClassCastException e) {
            // if not a group node, there are no children so ignore exception
        }
        try {
            Shape3D shape = (Shape3D) node;
            for (Enumeration e = shape.getAllGeometries(); e.hasMoreElements();) {
                Geometry g = (Geometry) e.nextElement();
                g.setCapability(Geometry.ALLOW_INTERSECT);
            }
        } catch (ClassCastException e) {
            // not a Shape3D node ignore exception
        }
    }

    private void addGrid(BranchGroup group) {
        float gridWidth = .405f;
        Material mat = new Material();
        mat.setAmbientColor(new Color3f(0.2f, 0.2f, 0.2f));
        mat.setDiffuseColor(new Color3f(0.4f, 0.4f, 0.4f));
        mat.setSpecularColor(new Color3f(0.4f, 0.4f, 0.4f));
        Appearance ap = new Appearance();
        ap.setMaterial(mat);

        for (float i = -.39999f; i < 0.42f; i = i + 0.088f) {
            QuadArray polygon1 = new QuadArray(4, QuadArray.COORDINATES);
            polygon1.setCoordinate(0, new Point3f(-gridWidth, -gridWidth, i));
            polygon1.setCoordinate(1, new Point3f(gridWidth, -gridWidth, i));
            polygon1.setCoordinate(2, new Point3f(gridWidth, gridWidth, i));
            polygon1.setCoordinate(3, new Point3f(-gridWidth, gridWidth, i));

            group.addChild(new Shape3D(polygon1, ap));
            QuadArray polygon2 = new QuadArray(4, QuadArray.COORDINATES);

            polygon2.setCoordinate(0, new Point3f(-gridWidth, -gridWidth, i));
            polygon2.setCoordinate(1, new Point3f(-gridWidth, gridWidth, i));
            polygon2.setCoordinate(2, new Point3f(gridWidth, gridWidth, i));
            polygon2.setCoordinate(3, new Point3f(gridWidth, -gridWidth, i));
            group.addChild(new Shape3D(polygon2, ap));
        }

        for (float i = -.39999f; i < 0.42f; i = i + 0.0888f) {
            QuadArray polygon1 = new QuadArray(4, QuadArray.COORDINATES);
            polygon1.setCoordinate(0, new Point3f(-gridWidth, i, -gridWidth));
            polygon1.setCoordinate(1, new Point3f(gridWidth, i, -gridWidth));
            polygon1.setCoordinate(2, new Point3f(gridWidth, i, gridWidth));
            polygon1.setCoordinate(3, new Point3f(-gridWidth, i, gridWidth));

            group.addChild(new Shape3D(polygon1, ap));
            QuadArray polygon2 = new QuadArray(4, QuadArray.COORDINATES);

            polygon2.setCoordinate(0, new Point3f(-gridWidth, i, -gridWidth));
            polygon2.setCoordinate(1, new Point3f(-gridWidth, i, gridWidth));
            polygon2.setCoordinate(2, new Point3f(gridWidth, i, gridWidth));
            polygon2.setCoordinate(3, new Point3f(gridWidth, i, -gridWidth));
            group.addChild(new Shape3D(polygon2, ap));
        }

        for (float i = -.39999f; i < 0.42f; i = i + 0.0888f) {
            QuadArray polygon1 = new QuadArray(4, QuadArray.COORDINATES);
            polygon1.setCoordinate(0, new Point3f(i, -gridWidth, -gridWidth));
            polygon1.setCoordinate(1, new Point3f(i, gridWidth, -gridWidth));
            polygon1.setCoordinate(2, new Point3f(i, gridWidth, gridWidth));
            polygon1.setCoordinate(3, new Point3f(i, -gridWidth, gridWidth));

            group.addChild(new Shape3D(polygon1, ap));
            QuadArray polygon2 = new QuadArray(4, QuadArray.COORDINATES);

            polygon2.setCoordinate(0, new Point3f(i, -gridWidth, -gridWidth));
            polygon2.setCoordinate(1, new Point3f(i, -gridWidth, gridWidth));
            polygon2.setCoordinate(2, new Point3f(i, gridWidth, gridWidth));
            polygon2.setCoordinate(3, new Point3f(i, gridWidth, -gridWidth));
            group.addChild(new Shape3D(polygon2, ap));
        }

    }

    private void addSquaresTest() {
        addSquare(Face.LEFT, 0, 0, Color.YELLOW);
        addSquare(Face.LEFT, 2, 4, Color.BLUE);
        addSquare(Face.LEFT, 4, 6, Color.RED);
        addSquare(Face.LEFT, 0, 8, Color.GREEN);

        addSquare(Face.FRONT, 0, 0, Color.YELLOW);
        addSquare(Face.FRONT, 2, 4, Color.BLUE);
        addSquare(Face.FRONT, 4, 6, Color.RED);
        addSquare(Face.FRONT, 0, 8, Color.GREEN);

        addSquare(Face.RIGHT, 0, 0, Color.YELLOW);
        addSquare(Face.RIGHT, 2, 4, Color.BLUE);
        addSquare(Face.RIGHT, 4, 6, Color.RED);
        addSquare(Face.RIGHT, 0, 8, Color.GREEN);

        addSquare(Face.BACK, 0, 0, Color.YELLOW);
        addSquare(Face.BACK, 2, 4, Color.BLUE);
        addSquare(Face.BACK, 4, 6, Color.RED);
        addSquare(Face.BACK, 0, 8, Color.GREEN);

        addSquare(Face.TOP, 0, 0, Color.YELLOW);
        addSquare(Face.TOP, 2, 4, Color.BLUE);
        addSquare(Face.TOP, 4, 6, Color.RED);
        addSquare(Face.TOP, 0, 8, Color.GREEN);

        addSquare(Face.BOTTOM, 0, 0, Color.YELLOW);
        addSquare(Face.BOTTOM, 2, 4, Color.BLUE);
        addSquare(Face.BOTTOM, 4, 6, Color.RED);
        addSquare(Face.BOTTOM, 0, 8, Color.GREEN);

    }

    private void makeTestBoard() {
        Board state = new Board();
        state.setMove(new Move(new Square(FRONT, 2, 1), new Piece(cross), BLUE));
        state.setMove(new Move(new Square(BACK, 2, 1), new Piece(cross), YELLOW));
        state.setMove(new Move(new Square(RIGHT, 2, 1), new Piece(cross), RED));
        state.setMove(new Move(new Square(LEFT, 2, 1), new Piece(cross), GREEN));

        state.setMove(new Move(new Square(FRONT, 2, 4), new Piece(U), BLUE));
        state.setMove(new Move(new Square(BACK, 2, 4), new Piece(U), YELLOW));
        state.setMove(new Move(new Square(RIGHT, 2, 4), new Piece(U), RED));
        state.setMove(new Move(new Square(LEFT, 2, 4), new Piece(U), GREEN));

        state.setMove(new Move(new Square(FRONT, 1, 6), new Piece(F), BLUE));
        state.setMove(new Move(new Square(BACK, 1, 6), new Piece(F), YELLOW));
        state.setMove(new Move(new Square(RIGHT, 1, 6), new Piece(F), RED));
        state.setMove(new Move(new Square(LEFT, 1, 6), new Piece(F), GREEN));

        state.setMove(new Move(new Square(FRONT, 5, 3), new Piece(W), BLUE));
        state.setMove(new Move(new Square(BACK, 5, 3), new Piece(W), YELLOW));
        state.setMove(new Move(new Square(RIGHT, 5, 3), new Piece(W), RED));
        state.setMove(new Move(new Square(LEFT, 5, 3), new Piece(W), GREEN));

        state.setMove(new Move(new Square(FRONT, 4, 8), new Piece(Y), BLUE));
        state.setMove(new Move(new Square(BACK, 4, 8), new Piece(Y), YELLOW));
        state.setMove(new Move(new Square(RIGHT, 4, 8), new Piece(Y), RED));
        state.setMove(new Move(new Square(LEFT, 4, 8), new Piece(Y), GREEN));

        state.setMove(new Move(new Square(FRONT, 7, 1), new Piece(P), BLUE));
        state.setMove(new Move(new Square(BACK, 7, 1), new Piece(P), YELLOW));
        state.setMove(new Move(new Square(RIGHT, 7, 1), new Piece(P), RED));
        state.setMove(new Move(new Square(LEFT, 7, 1), new Piece(P), GREEN));

        state.setMove(new Move(new Square(FRONT, 5, 6), new Piece(fourSquare), BLUE));
        state.setMove(new Move(new Square(BACK, 5, 6), new Piece(fourSquare), YELLOW));
        state.setMove(new Move(new Square(RIGHT, 5, 6), new Piece(fourSquare), RED));
        state.setMove(new Move(new Square(LEFT, 5, 6), new Piece(fourSquare), GREEN));

        state.setMove(new Move(new Square(FRONT, 6, 7), new Piece(oneSquare), BLUE));
        state.setMove(new Move(new Square(BACK, 6, 7), new Piece(oneSquare), YELLOW));
        state.setMove(new Move(new Square(RIGHT, 6, 7), new Piece(oneSquare), RED));
        state.setMove(new Move(new Square(LEFT, 6, 7), new Piece(oneSquare), GREEN));
        _board = state;
    }

    public void appendChat(Color c, String text) {
        StyledDocument doc = chatArea.getStyledDocument();
        try {
            doc.insertString(doc.getLength(), text, null);

        } catch (BadLocationException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }

        java.awt.Color col;
        if (c == Color.BLUE) {
            col = java.awt.Color.BLUE;
        } else if (c == Color.GREEN) {
            col = java.awt.Color.GREEN;
        } else if (c == Color.RED) {
            col = java.awt.Color.RED;
        } else {
            col = java.awt.Color.YELLOW;
        }

        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, col);
        int l = chatArea.getDocument().getLength();
        chatArea.setCaretPosition(l);
        chatArea.setCharacterAttributes(aset, false);
        chatArea.replaceSelection(text);
        /*
        try { // Get the text pane's document
            JTextPane textPane = new JTextPane();
            StyledDocument doc = (StyledDocument)textPane.getDocument();
            // Create a style object and then set the style attributes
            Style style = doc.addStyle("StyleName", null);
            // Italic
            StyleConstants.setItalic(style, true);
            // Bold
            StyleConstants.setBold(style, true);
            // Font family
            StyleConstants.setFontFamily(style, "SansSerif");
            // Font size
            StyleConstants.setFontSize(style, 30);
            // Background color
            StyleConstants.setBackground(style, java.awt.Color.blue);
            // Foreground color
            StyleConstants.setForeground(style, java.awt.Color.white);
            // Append to document
            doc.insertString(doc.getLength(), "Some Text", style);
        }
        catch (BadLocationException e) { }*/
        

    }

    private void setPlayerLabels(boolean isHost) {
        for (int i = 0; i < _players.size(); i++) {
            _viewPlayers[i] = _players.get(i);
        }
        java.awt.Color[] javaColors = new java.awt.Color[4];
        for (int i = 0; i < _players.size(); i++) {
            switch (_players.get(i).getColor()) {
                case RED:
                    javaColors[i] = java.awt.Color.red;
                    break;
                case BLUE:
                    javaColors[i] = java.awt.Color.blue;
                    break;
                case GREEN:
                    javaColors[i] = java.awt.Color.green;
                    break;
                case YELLOW:
                    javaColors[i] = java.awt.Color.yellow;
                    break;
                default:
                    javaColors[i] = java.awt.Color.gray;
                    break;
            }
        }
        
        _playerLabels[0] = P1namelabel;
        _playerLabels[1] = P2namelabel;
        _playerLabels[2] = P3namelabel;
        _playerLabels[3] = P4namelabel;
        if (isHost) {
            //TODO: Alex, I changed this cause it wasn't working when loading
            //in games. Is there a reason they existed?
//            JLabel[] typeLabels = new JLabel[4];
//            typeLabels[0] = player1TypeLabel;
//            typeLabels[1] = player2TypeLabel;
//            typeLabels[2] = player3TypeLabel;
//            typeLabels[3] = player4TypeLabel;

            String[] typeLabels = new String[4];
            for (int i = 0; i < 4; i++) {
                String type = "ERROR";
                if (_players.get(i) instanceof HumanPlayer) {
                    typeLabels[i] = "(Human)";
                }
                else if (_players.get(i) instanceof AIPlayer) {
                    typeLabels[i] = "(AI)";
                }
                else if (_players.get(i) instanceof NetworkPlayer) {
                    typeLabels[i] = "(Remote)";
                }
                
            }

            _pieceDisplays[0] = new PiecesView(javaColors[0]);
            _p1PiecesView = (PiecesView) _pieceDisplays[0];
            for (int i = 1; i < _pieceDisplays.length; i++) {
                _pieceDisplays[i] = new OpponentView(javaColors[i]);
            }

            for (int i = 0; i < _players.size(); i++) {
                _playerLabels[i].setText(_players.get(i).getName() + " " + 
                        typeLabels[i]+
                        " Score: "+ _players.get(i).getScore());
                _pieceContainers[i].add("Center", _pieceDisplays[i]);
                _players.get(i).setPieceDisplay(_pieceDisplays[i]);
            }
        } else {
            int index = 0;
            for (int i = 0; i < _players.size(); i++) {
                if (_players.get(i) instanceof HumanPlayer) {
                    index = i;
                }
            }

            _playerLabels[0].setText(_players.get(index).getName() + " (Human) Score: "+0);
            _pieceDisplays[0] = new PiecesView(javaColors[index]);
            _p1PiecesView = (PiecesView) _pieceDisplays[0];
            _pieceContainers[0].add("Center", _pieceDisplays[0]);
            _players.get(index).setPieceDisplay(_pieceDisplays[0]);

            int i = 1;
            for (int j = index + 1; j < index + 4; j++) {
                int p = j % 4;
                _playerLabels[i].setText(_players.get(p).getName() + " (Remote) Score: "+0);
                _pieceDisplays[i] = new OpponentView(javaColors[p]);
                _pieceContainers[i].add("Center", _pieceDisplays[i]);
                _players.get(p).setPieceDisplay(_pieceDisplays[i]);
                i++;
            }
        }
    }

    /**
     * This only happens on the host machine.
     *
     * This swaps the views of the current human player with given color and the next
     * human player.
     * 
     * @param color - color of human player whose turn just happened
     */
    public void swapOutDisplay() {
        if (_isHost && (_viewPlayers[0] instanceof HumanPlayer)) {
            int count = 1;
            while (!(_viewPlayers[count % 4] instanceof HumanPlayer)) {
                count++;
            }
            count = count % 4;

            for (int i = 0; i < count; i++) {
                String tempText = _playerLabels[0].getText();
                PieceDisplay tempDisplay = _pieceDisplays[0];
                Player tempPlayer = _viewPlayers[0];

                _playerLabels[0].setText(_playerLabels[1].getText());
                _pieceContainers[0].remove(_pieceDisplays[0]);
                _pieceDisplays[0] = new PiecesView((OpponentView) _pieceDisplays[1]);
                _pieceContainers[0].add("Center", _pieceDisplays[0]);
                _viewPlayers[0] = _viewPlayers[1];

                _p1PiecesView = (PiecesView) _pieceDisplays[0];

                _playerLabels[1].setText(_playerLabels[2].getText());
                _pieceContainers[1].remove(_pieceDisplays[1]);
                _pieceDisplays[1] = _pieceDisplays[2];
                _pieceContainers[1].add("Center", _pieceDisplays[1]);
                _viewPlayers[1] = _viewPlayers[2];

                _playerLabels[2].setText(_playerLabels[3].getText());
                _pieceContainers[2].remove(_pieceDisplays[2]);
                _pieceDisplays[2] = _pieceDisplays[3];
                _pieceContainers[2].add("Center", _pieceDisplays[2]);
                _viewPlayers[2] = _viewPlayers[3];

                _playerLabels[3].setText(tempText);
                _pieceContainers[3].remove(_pieceDisplays[3]);
                _pieceDisplays[3] = new OpponentView((PiecesView) tempDisplay);
                _pieceContainers[3].add("Center", _pieceDisplays[3]);
                _viewPlayers[3] = tempPlayer;
            }
            for (int j = 0; j < _pieceDisplays.length; j++) {
                _pieceDisplays[j].setColor(_viewPlayers[j].getColor());
            }
        }
    }

    public void updateScores(){
        for(int i = 0; i < _viewPlayers.length; i++){
            int score = _viewPlayers[i].getScore();
            String newText = _playerLabels[i].getText();
            newText = newText.substring(0, newText.lastIndexOf(":"));
            newText = newText+": "+score;
            _playerLabels[i].setText(newText);
        }
    }

    public void displayError(String message){
        errorLabel.setText(message);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        NewGameWindow = new javax.swing.JFrame();
        jPanel2 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jSeparator5 = new javax.swing.JSeparator();
        jPanel4 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        newGameNumPlayers = new javax.swing.JComboBox();
        newGameNumAI = new javax.swing.JComboBox();
        jLabel16 = new javax.swing.JLabel();
        newGameNextButton = new javax.swing.JButton();
        JoinGameWindow = new javax.swing.JFrame();
        jPanel5 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jSeparator6 = new javax.swing.JSeparator();
        jPanel6 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        joinGameName = new javax.swing.JTextField();
        joinGameStartButton = new javax.swing.JButton();
        joinGameIP = new javax.swing.JTextField();
        NewGameNamesWindow = new javax.swing.JFrame();
        jPanel7 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        newGameStartButton = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        Player1Field = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        Player2Field = new javax.swing.JTextField();
        Player3Field = new javax.swing.JTextField();
        Player4Field = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        player1TypeLabel = new javax.swing.JLabel();
        player2TypeLabel = new javax.swing.JLabel();
        player3TypeLabel = new javax.swing.JLabel();
        player4TypeLabel = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        newGameBackButton = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JSeparator();
        EndGameWindow = new javax.swing.JFrame();
        jLabel4 = new javax.swing.JLabel();
        EndGameP1Label = new javax.swing.JLabel();
        EndGameP2Label = new javax.swing.JLabel();
        EndGameP3Label = new javax.swing.JLabel();
        EndGameP4Label = new javax.swing.JLabel();
        EndGameWinnerLabel = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        OpenGameWindow = new javax.swing.JFileChooser();
        AboutWindow = new javax.swing.JFrame();
        JFrame1 = new java.awt.Panel();
        About = new java.awt.Label();
        jSeparator7 = new javax.swing.JSeparator();
        jScrollPane2 = new javax.swing.JScrollPane();
        AboutTextArea = new javax.swing.JTextArea();
        RulesWindow = new javax.swing.JFrame();
        jScrollPane3 = new javax.swing.JScrollPane();
        RulesTextArea = new javax.swing.JTextArea();
        jPanel3 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        SaveGameWindow = new javax.swing.JFileChooser();
        MovesChatPanel = new javax.swing.JPanel();
        ChatPanel = new javax.swing.JPanel();
        chatField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        chatArea = new javax.swing.JTextPane();
        MovesPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        previousMovesScrollPane = new javax.swing.JScrollPane();
        PiecesPanel = new javax.swing.JPanel();
        P1namelabel = new javax.swing.JLabel();
        P1piecescontainer = new javax.swing.JPanel();
        PassButton = new javax.swing.JButton();
        statusLabel = new javax.swing.JLabel();
        errorLabel = new javax.swing.JLabel();
        OpponentPanel = new javax.swing.JPanel();
        OpponentPiecesPanel = new javax.swing.JPanel();
        P2piecespanel = new javax.swing.JPanel();
        P2namelabel = new javax.swing.JLabel();
        P2piecescontainer = new javax.swing.JPanel();
        P3piecespanel = new javax.swing.JPanel();
        P3namelabel = new javax.swing.JLabel();
        P3piecescontainer = new javax.swing.JPanel();
        P4piecespanel = new javax.swing.JPanel();
        P4namelabel = new javax.swing.JLabel();
        P4piecescontainer = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        MainPanel = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        CubePanel = new javax.swing.JPanel();
        rateMovesLabel = new javax.swing.JLabel();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        newGameMenuItem = new javax.swing.JMenuItem();
        joinMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        saveMenuItem = new javax.swing.JMenuItem();
        openMenuItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        exitMenuItem = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        chatWindowMenuItem = new javax.swing.JCheckBoxMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        rateMovesMenuItem = new javax.swing.JCheckBoxMenuItem();
        helpMenu = new javax.swing.JMenu();
        contentsMenuItem = new javax.swing.JMenuItem();
        tutorialMenuItem = new javax.swing.JMenuItem();
        aboutMenuItem = new javax.swing.JMenuItem();

        NewGameWindow.setMinimumSize(new java.awt.Dimension(297, 213));
        NewGameWindow.setResizable(false);

        jLabel9.setText("New Game");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(112, 112, 112)
                        .addComponent(jLabel9)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel4.setMinimumSize(new java.awt.Dimension(400, 152));

        jLabel11.setText("Local Players");

        newGameNumPlayers.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4" }));
        newGameNumPlayers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newGameNumPlayersActionPerformed(evt);
            }
        });

        newGameNumAI.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4" }));

        jLabel16.setText("Computer Players");

        newGameNextButton.setText("Next");
        newGameNextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newGameNextButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel16)
                            .addComponent(jLabel11))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(newGameNumPlayers, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(newGameNumAI, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(48, 48, 48)
                        .addComponent(newGameNextButton, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(33, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(newGameNumPlayers, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(newGameNumAI, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addGap(42, 42, 42)
                .addComponent(newGameNextButton, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout NewGameWindowLayout = new javax.swing.GroupLayout(NewGameWindow.getContentPane());
        NewGameWindow.getContentPane().setLayout(NewGameWindowLayout);
        NewGameWindowLayout.setHorizontalGroup(
            NewGameWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(NewGameWindowLayout.createSequentialGroup()
                .addGap(55, 55, 55)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        NewGameWindowLayout.setVerticalGroup(
            NewGameWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(NewGameWindowLayout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        JoinGameWindow.setMinimumSize(new java.awt.Dimension(424, 230));

        jLabel13.setText("Join Game");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSeparator6, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(153, Short.MAX_VALUE)
                .addComponent(jLabel13)
                .addGap(149, 149, 149))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jSeparator6, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel6.setMinimumSize(new java.awt.Dimension(400, 152));

        jLabel14.setText("Name");

        jLabel15.setText("Host's IP Address");

        joinGameName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                joinGameNameActionPerformed(evt);
            }
        });

        joinGameStartButton.setText("Start!");
        joinGameStartButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                joinGameStartButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel15)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addComponent(joinGameStartButton, javax.swing.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE)
                        .addGap(136, 136, 136))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(joinGameName, javax.swing.GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE)
                            .addComponent(joinGameIP, javax.swing.GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(joinGameName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(joinGameIP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                .addComponent(joinGameStartButton))
        );

        javax.swing.GroupLayout JoinGameWindowLayout = new javax.swing.GroupLayout(JoinGameWindow.getContentPane());
        JoinGameWindow.getContentPane().setLayout(JoinGameWindowLayout);
        JoinGameWindowLayout.setHorizontalGroup(
            JoinGameWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JoinGameWindowLayout.createSequentialGroup()
                .addGroup(JoinGameWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(JoinGameWindowLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 347, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        JoinGameWindowLayout.setVerticalGroup(
            JoinGameWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JoinGameWindowLayout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        NewGameNamesWindow.setMinimumSize(new java.awt.Dimension(383, 251));

        jLabel3.setText("New Game");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 383, Short.MAX_VALUE)
            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel7Layout.createSequentialGroup()
                    .addGap(0, 158, Short.MAX_VALUE)
                    .addComponent(jLabel3)
                    .addGap(0, 159, Short.MAX_VALUE)))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 35, Short.MAX_VALUE)
            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel7Layout.createSequentialGroup()
                    .addGap(0, 10, Short.MAX_VALUE)
                    .addComponent(jLabel3)
                    .addGap(0, 10, Short.MAX_VALUE)))
        );

        newGameStartButton.setText("Start!");
        newGameStartButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newGameStartButtonActionPerformed(evt);
            }
        });

        jLabel18.setText("Player 1");

        jLabel19.setText("Player 2");

        jLabel20.setText("Player 3");

        jLabel21.setText("Player 4");

        player1TypeLabel.setText("(Human)");

        player2TypeLabel.setText("(Human)");

        player3TypeLabel.setText("(Human)");

        player4TypeLabel.setText("(Human)");

        jLabel26.setText("Name");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(jLabel21)
                                .addGap(18, 18, 18)
                                .addComponent(player4TypeLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
                                .addComponent(Player4Field, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(jLabel19)
                                .addGap(18, 18, 18)
                                .addComponent(player2TypeLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
                                .addComponent(Player2Field, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel8Layout.createSequentialGroup()
                                        .addComponent(jLabel20)
                                        .addGap(18, 18, 18)
                                        .addComponent(player3TypeLabel))
                                    .addGroup(jPanel8Layout.createSequentialGroup()
                                        .addComponent(jLabel18)
                                        .addGap(18, 18, 18)
                                        .addComponent(player1TypeLabel)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
                                .addComponent(Player3Field, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(Player1Field, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel26)
                        .addGap(80, 80, 80))))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel26)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Player1Field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(player1TypeLabel)
                    .addComponent(jLabel18))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Player2Field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(player2TypeLabel)
                    .addComponent(jLabel19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Player3Field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(player3TypeLabel)
                    .addComponent(jLabel20))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(player4TypeLabel)
                    .addComponent(Player4Field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        newGameBackButton.setText("Back");
        newGameBackButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newGameBackButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout NewGameNamesWindowLayout = new javax.swing.GroupLayout(NewGameNamesWindow.getContentPane());
        NewGameNamesWindow.getContentPane().setLayout(NewGameNamesWindowLayout);
        NewGameNamesWindowLayout.setHorizontalGroup(
            NewGameNamesWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(NewGameNamesWindowLayout.createSequentialGroup()
                .addGap(88, 88, 88)
                .addComponent(newGameBackButton, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(newGameStartButton, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(84, Short.MAX_VALUE))
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(NewGameNamesWindowLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(NewGameNamesWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(NewGameNamesWindowLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jSeparator4, javax.swing.GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        NewGameNamesWindowLayout.setVerticalGroup(
            NewGameNamesWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(NewGameNamesWindowLayout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(NewGameNamesWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(newGameBackButton)
                    .addComponent(newGameStartButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(NewGameNamesWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(NewGameNamesWindowLayout.createSequentialGroup()
                    .addGap(30, 30, 30)
                    .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(215, Short.MAX_VALUE)))
        );

        EndGameWindow.setLocationByPlatform(true);
        EndGameWindow.setMinimumSize(new java.awt.Dimension(400, 236));

        jLabel4.setFont(new java.awt.Font("Ethnocentric", 0, 12));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Game OVER!");

        EndGameP1Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        EndGameP1Label.setText("jLabel6");

        EndGameP2Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        EndGameP2Label.setText("jLabel7");

        EndGameP3Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        EndGameP3Label.setText("jLabel8");

        EndGameP4Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        EndGameP4Label.setText("jLabel10");

        EndGameWinnerLabel.setFont(new java.awt.Font("Ethnocentric", 0, 12));
        EndGameWinnerLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        EndGameWinnerLabel.setText("jLabel7");

        jButton1.setText("Close");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout EndGameWindowLayout = new javax.swing.GroupLayout(EndGameWindow.getContentPane());
        EndGameWindow.getContentPane().setLayout(EndGameWindowLayout);
        EndGameWindowLayout.setHorizontalGroup(
            EndGameWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(EndGameWindowLayout.createSequentialGroup()
                .addGroup(EndGameWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(EndGameWindowLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(EndGameP1Label, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE))
                    .addGroup(EndGameWindowLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(EndGameP2Label, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE))
                    .addGroup(EndGameWindowLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(EndGameP3Label, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE))
                    .addGroup(EndGameWindowLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE))
                    .addGroup(EndGameWindowLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(EndGameWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(EndGameWinnerLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                            .addComponent(EndGameP4Label, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE))))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, EndGameWindowLayout.createSequentialGroup()
                .addContainerGap(178, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(176, 176, 176))
        );
        EndGameWindowLayout.setVerticalGroup(
            EndGameWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(EndGameWindowLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(EndGameP1Label)
                .addGap(18, 18, 18)
                .addComponent(EndGameP2Label)
                .addGap(18, 18, 18)
                .addComponent(EndGameP3Label)
                .addGap(18, 18, 18)
                .addComponent(EndGameP4Label, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(EndGameWinnerLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addContainerGap())
        );

        AboutWindow.setMinimumSize(new java.awt.Dimension(219, 365));

        About.setText("About");

        AboutTextArea.setColumns(20);
        AboutTextArea.setEditable(false);
        AboutTextArea.setRows(5);
        AboutTextArea.setText("Blackbird Blokus was brought to you by Alex Hills, Dan Kimmel, Varun Singh, and Greg Young. \n\nQuestions, comments, or praise for our amazing software? Email BlackbirdBlokus@gmail.com!\n \nThanks for playing our game");
        jScrollPane2.setViewportView(AboutTextArea);

        javax.swing.GroupLayout JFrame1Layout = new javax.swing.GroupLayout(JFrame1);
        JFrame1.setLayout(JFrame1Layout);
        JFrame1Layout.setHorizontalGroup(
            JFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JFrame1Layout.createSequentialGroup()
                .addGroup(JFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(JFrame1Layout.createSequentialGroup()
                        .addGap(81, 81, 81)
                        .addComponent(About, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(JFrame1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jSeparator7, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE))
                    .addGroup(JFrame1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)))
                .addContainerGap())
        );
        JFrame1Layout.setVerticalGroup(
            JFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JFrame1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(About, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator7)
                .addGap(12, 12, 12)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 286, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout AboutWindowLayout = new javax.swing.GroupLayout(AboutWindow.getContentPane());
        AboutWindow.getContentPane().setLayout(AboutWindowLayout);
        AboutWindowLayout.setHorizontalGroup(
            AboutWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(JFrame1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        AboutWindowLayout.setVerticalGroup(
            AboutWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AboutWindowLayout.createSequentialGroup()
                .addComponent(JFrame1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        RulesWindow.setMinimumSize(new java.awt.Dimension(400, 368));

        RulesTextArea.setColumns(20);
        RulesTextArea.setEditable(false);
        RulesTextArea.setRows(5);
        RulesTextArea.setText("Welcome to Blackbird Blokus! If you've played blokus before, the rules are almost the same. If not, read the rules below to get started.\n\nRules\n    There are 4 players, and each receives 21 pieces, all of different shapes (polyominoes made up of between 1 and 5 squares). The object of the game is for each player to play as many of their pieces as possible. The game begins with every player given the three squares adjacent to a corner on two diagonally opposite corners. The turns proceed from Player 1 to Player 4 (blue, yellow, red, green). Every new piece that is played must touch another piece of the same color (i.e. your own color), but only at the corners, never along the sides. \n\nWhen a player cannot place a piece down, they must pass. The play continues until the players can no longer play, either because they have no place to put their remaining pieces or because they have played all of their pieces, or if all 4 players pass consecutively.\n\nScoring\n    Every square on the board counts as one point, and an additional 15 points are awarded if all 21 pieces are played.\n\nSaving/Loading Games\nYou can save local (not networked) games to your computer to continue later. The game is automatically saved in your-current-directory/savedGames, and the title of the file is the current date and time + .blok\nFor example, if it is April 5, 2010, at 14:23:48, the file would be called: 04-05-2010--14:23:48.blok\nYou can then load these games in and continue playing.");
        jScrollPane3.setViewportView(RulesTextArea);

        jLabel8.setText("Rules");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 326, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addGap(0, 146, Short.MAX_VALUE)
                    .addComponent(jLabel8)
                    .addGap(0, 146, Short.MAX_VALUE)))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 46, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addGap(0, 15, Short.MAX_VALUE)
                    .addComponent(jLabel8)
                    .addGap(0, 16, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout RulesWindowLayout = new javax.swing.GroupLayout(RulesWindow.getContentPane());
        RulesWindow.getContentPane().setLayout(RulesWindowLayout);
        RulesWindowLayout.setHorizontalGroup(
            RulesWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(RulesWindowLayout.createSequentialGroup()
                .addGroup(RulesWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(RulesWindowLayout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(RulesWindowLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 362, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(26, Short.MAX_VALUE))
        );
        RulesWindowLayout.setVerticalGroup(
            RulesWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(RulesWindowLayout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 304, Short.MAX_VALUE)
                .addContainerGap())
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Blackbird Blokus");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setResizable(false);

        MovesChatPanel.setMinimumSize(new java.awt.Dimension(200, 620));
        MovesChatPanel.setPreferredSize(new java.awt.Dimension(250, 620));
        MovesChatPanel.setLayout(new java.awt.BorderLayout());

        ChatPanel.setBackground(new java.awt.Color(38, 123, 109));
        ChatPanel.setPreferredSize(new java.awt.Dimension(200, 200));

        chatField.setBackground(java.awt.Color.black);
        chatField.setForeground(java.awt.Color.white);
        chatField.setText("Enter chat here...");
        chatField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chatFieldActionPerformed(evt);
            }
        });
        chatField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                chatFieldKeyPressed(evt);
            }
        });

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Chat");

        chatArea.setBackground(java.awt.Color.black);
        chatArea.setEditable(false);
        chatArea.setForeground(java.awt.Color.white);
        chatArea.setCaretColor(new java.awt.Color(255, 255, 255));
        chatArea.setSelectedTextColor(new java.awt.Color(0, 0, 0));
        jScrollPane1.setViewportView(chatArea);

        javax.swing.GroupLayout ChatPanelLayout = new javax.swing.GroupLayout(ChatPanel);
        ChatPanel.setLayout(ChatPanelLayout);
        ChatPanelLayout.setHorizontalGroup(
            ChatPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ChatPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ChatPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE)
                    .addComponent(chatField, javax.swing.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE))
                .addContainerGap())
        );
        ChatPanelLayout.setVerticalGroup(
            ChatPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ChatPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chatField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        MovesChatPanel.add(ChatPanel, java.awt.BorderLayout.PAGE_END);

        MovesPanel.setBackground(new java.awt.Color(38, 123, 109));

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Recent Moves");

        previousMovesScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        previousMovesScrollPane.setAutoscrolls(true);
        previousMovesScrollPane.getVerticalScrollBar().setUnitIncrement(25);

        javax.swing.GroupLayout MovesPanelLayout = new javax.swing.GroupLayout(MovesPanel);
        MovesPanel.setLayout(MovesPanelLayout);
        MovesPanelLayout.setHorizontalGroup(
            MovesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, MovesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(MovesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE)
                    .addComponent(previousMovesScrollPane, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE))
                .addContainerGap())
        );
        MovesPanelLayout.setVerticalGroup(
            MovesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MovesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(previousMovesScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 371, Short.MAX_VALUE)
                .addContainerGap())
        );

        MovesChatPanel.add(MovesPanel, java.awt.BorderLayout.CENTER);

        getContentPane().add(MovesChatPanel, java.awt.BorderLayout.LINE_START);

        PiecesPanel.setBackground(new java.awt.Color(38, 123, 109));
        PiecesPanel.setPreferredSize(new java.awt.Dimension(1024, 150));

        P1namelabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        P1namelabel.setText("Player 1");

        P1piecescontainer.setBackground(new java.awt.Color(1, 1, 1));
        P1piecescontainer.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        PassButton.setText("Pass");
        PassButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PassButtonActionPerformed(evt);
            }
        });

        statusLabel.setBackground(java.awt.Color.black);
        statusLabel.setForeground(java.awt.Color.blue);

        errorLabel.setBackground(java.awt.Color.black);
        errorLabel.setForeground(java.awt.Color.white);
        errorLabel.setText("Welcome to Blackbird Blokus!");

        javax.swing.GroupLayout P1piecescontainerLayout = new javax.swing.GroupLayout(P1piecescontainer);
        P1piecescontainer.setLayout(P1piecescontainerLayout);
        P1piecescontainerLayout.setHorizontalGroup(
            P1piecescontainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(P1piecescontainerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(P1piecescontainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, P1piecescontainerLayout.createSequentialGroup()
                        .addComponent(statusLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 862, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(PassButton))
                    .addGroup(P1piecescontainerLayout.createSequentialGroup()
                        .addComponent(errorLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 862, Short.MAX_VALUE)
                        .addGap(60, 60, 60))))
        );
        P1piecescontainerLayout.setVerticalGroup(
            P1piecescontainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, P1piecescontainerLayout.createSequentialGroup()
                .addContainerGap(65, Short.MAX_VALUE)
                .addGroup(P1piecescontainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(P1piecescontainerLayout.createSequentialGroup()
                        .addComponent(statusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(errorLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(PassButton)))
        );

        javax.swing.GroupLayout PiecesPanelLayout = new javax.swing.GroupLayout(PiecesPanel);
        PiecesPanel.setLayout(PiecesPanelLayout);
        PiecesPanelLayout.setHorizontalGroup(
            PiecesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PiecesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PiecesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(P1namelabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 938, Short.MAX_VALUE)
                    .addComponent(P1piecescontainer, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        PiecesPanelLayout.setVerticalGroup(
            PiecesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PiecesPanelLayout.createSequentialGroup()
                .addComponent(P1namelabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(P1piecescontainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        getContentPane().add(PiecesPanel, java.awt.BorderLayout.PAGE_END);

        OpponentPanel.setBackground(new java.awt.Color(38, 123, 109));
        OpponentPanel.setMinimumSize(new java.awt.Dimension(200, 620));
        OpponentPanel.setPreferredSize(new java.awt.Dimension(200, 620));

        OpponentPiecesPanel.setLayout(new java.awt.GridLayout(3, 1));

        P2piecespanel.setBackground(new java.awt.Color(38, 123, 109));
        P2piecespanel.setMinimumSize(new java.awt.Dimension(188, 185));

        P2namelabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        P2namelabel.setText("Player 2");

        P2piecescontainer.setBackground(new java.awt.Color(1, 1, 1));
        P2piecescontainer.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        P2piecescontainer.setMinimumSize(new java.awt.Dimension(164, 152));

        javax.swing.GroupLayout P2piecescontainerLayout = new javax.swing.GroupLayout(P2piecescontainer);
        P2piecescontainer.setLayout(P2piecescontainerLayout);
        P2piecescontainerLayout.setHorizontalGroup(
            P2piecescontainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 160, Short.MAX_VALUE)
        );
        P2piecescontainerLayout.setVerticalGroup(
            P2piecescontainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 150, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout P2piecespanelLayout = new javax.swing.GroupLayout(P2piecespanel);
        P2piecespanel.setLayout(P2piecespanelLayout);
        P2piecespanelLayout.setHorizontalGroup(
            P2piecespanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, P2piecespanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(P2piecespanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(P2piecescontainer, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(P2namelabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE))
                .addContainerGap())
        );
        P2piecespanelLayout.setVerticalGroup(
            P2piecespanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(P2piecespanelLayout.createSequentialGroup()
                .addComponent(P2namelabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(P2piecescontainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        OpponentPiecesPanel.add(P2piecespanel);

        P3piecespanel.setBackground(new java.awt.Color(38, 123, 109));
        P3piecespanel.setMinimumSize(new java.awt.Dimension(188, 185));

        P3namelabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        P3namelabel.setText("Player 3");

        P3piecescontainer.setBackground(new java.awt.Color(0, 0, 0));
        P3piecescontainer.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        P3piecescontainer.setMinimumSize(new java.awt.Dimension(164, 152));

        javax.swing.GroupLayout P3piecescontainerLayout = new javax.swing.GroupLayout(P3piecescontainer);
        P3piecescontainer.setLayout(P3piecescontainerLayout);
        P3piecescontainerLayout.setHorizontalGroup(
            P3piecescontainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 160, Short.MAX_VALUE)
        );
        P3piecescontainerLayout.setVerticalGroup(
            P3piecescontainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 150, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout P3piecespanelLayout = new javax.swing.GroupLayout(P3piecespanel);
        P3piecespanel.setLayout(P3piecespanelLayout);
        P3piecespanelLayout.setHorizontalGroup(
            P3piecespanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, P3piecespanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(P3piecespanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(P3piecescontainer, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(P3namelabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE))
                .addContainerGap())
        );
        P3piecespanelLayout.setVerticalGroup(
            P3piecespanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(P3piecespanelLayout.createSequentialGroup()
                .addComponent(P3namelabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(P3piecescontainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        OpponentPiecesPanel.add(P3piecespanel);

        P4piecespanel.setBackground(new java.awt.Color(38, 123, 109));
        P4piecespanel.setMinimumSize(new java.awt.Dimension(188, 185));

        P4namelabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        P4namelabel.setText("Player 4");

        P4piecescontainer.setBackground(new java.awt.Color(0, 0, 0));
        P4piecescontainer.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        P4piecescontainer.setMinimumSize(new java.awt.Dimension(164, 152));

        javax.swing.GroupLayout P4piecescontainerLayout = new javax.swing.GroupLayout(P4piecescontainer);
        P4piecescontainer.setLayout(P4piecescontainerLayout);
        P4piecescontainerLayout.setHorizontalGroup(
            P4piecescontainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 160, Short.MAX_VALUE)
        );
        P4piecescontainerLayout.setVerticalGroup(
            P4piecescontainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 150, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout P4piecespanelLayout = new javax.swing.GroupLayout(P4piecespanel);
        P4piecespanel.setLayout(P4piecespanelLayout);
        P4piecespanelLayout.setHorizontalGroup(
            P4piecespanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, P4piecespanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(P4piecespanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(P4piecescontainer, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(P4namelabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE))
                .addContainerGap())
        );
        P4piecespanelLayout.setVerticalGroup(
            P4piecespanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(P4piecespanelLayout.createSequentialGroup()
                .addComponent(P4namelabel, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(P4piecescontainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        OpponentPiecesPanel.add(P4piecespanel);

        jLabel7.setText("Opponents");

        javax.swing.GroupLayout OpponentPanelLayout = new javax.swing.GroupLayout(OpponentPanel);
        OpponentPanel.setLayout(OpponentPanelLayout);
        OpponentPanelLayout.setHorizontalGroup(
            OpponentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(OpponentPanelLayout.createSequentialGroup()
                .addGroup(OpponentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(OpponentPiecesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(OpponentPanelLayout.createSequentialGroup()
                        .addGap(58, 58, 58)
                        .addComponent(jLabel7)))
                .addContainerGap())
        );
        OpponentPanelLayout.setVerticalGroup(
            OpponentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(OpponentPanelLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel7)
                .addGap(18, 18, 18)
                .addComponent(OpponentPiecesPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(OpponentPanel, java.awt.BorderLayout.LINE_END);

        MainPanel.setBackground(new java.awt.Color(38, 123, 109));

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Game View");

        CubePanel.setBackground(java.awt.Color.black);
        CubePanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        rateMovesLabel.setBackground(java.awt.Color.black);
        rateMovesLabel.setText(" ");
        rateMovesLabel.setForeground(java.awt.Color.black);

        javax.swing.GroupLayout CubePanelLayout = new javax.swing.GroupLayout(CubePanel);
        CubePanel.setLayout(CubePanelLayout);
        CubePanelLayout.setHorizontalGroup(
            CubePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CubePanelLayout.createSequentialGroup()
                .addContainerGap(352, Short.MAX_VALUE)
                .addComponent(rateMovesLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        CubePanelLayout.setVerticalGroup(
            CubePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CubePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rateMovesLabel)
                .addContainerGap(540, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout MainPanelLayout = new javax.swing.GroupLayout(MainPanel);
        MainPanel.setLayout(MainPanelLayout);
        MainPanelLayout.setHorizontalGroup(
            MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, MainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 488, Short.MAX_VALUE)
                    .addComponent(CubePanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        MainPanelLayout.setVerticalGroup(
            MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CubePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        getContentPane().add(MainPanel, java.awt.BorderLayout.CENTER);

        fileMenu.setText("File");

        newGameMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        newGameMenuItem.setText("New Game");
        newGameMenuItem.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                newGameMenuItemMouseClicked(evt);
            }
        });
        newGameMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newGameMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(newGameMenuItem);

        joinMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_J, java.awt.event.InputEvent.CTRL_MASK));
        joinMenuItem.setText("Join Game");
        joinMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                joinMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(joinMenuItem);
        fileMenu.add(jSeparator1);

        saveMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        saveMenuItem.setText("Save Game...");
        saveMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(saveMenuItem);

        openMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        openMenuItem.setText("Open Game...");
        openMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(openMenuItem);
        fileMenu.add(jSeparator2);

        exitMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        exitMenuItem.setText("Quit");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        jMenu1.setText("Piece");

        jMenuItem4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, 0));
        jMenuItem4.setText("Flip Vertical");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem4);

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, 0));
        jMenuItem1.setText("Flip Horizontal");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem5.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, 0));
        jMenuItem5.setText("Rotate Left");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem5);

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, 0));
        jMenuItem2.setText("Rotate Right");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        menuBar.add(jMenu1);

        editMenu.setText("Window");

        chatWindowMenuItem.setSelected(true);
        chatWindowMenuItem.setText("Chat Window");
        chatWindowMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chatWindowMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(chatWindowMenuItem);
        editMenu.add(jSeparator3);

        rateMovesMenuItem.setText("Rate Moves");
        rateMovesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rateMovesMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(rateMovesMenuItem);

        menuBar.add(editMenu);

        helpMenu.setText("Help");

        contentsMenuItem.setText("Rules");
        contentsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contentsMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(contentsMenuItem);

        tutorialMenuItem.setText("Tutorial");
        tutorialMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tutorialMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(tutorialMenuItem);

        aboutMenuItem.setText("About");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        System.exit(0);
    }//GEN-LAST:event_exitMenuItemActionPerformed

    private void chatFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chatFieldActionPerformed
        _main.sendMessage(chatField.getText());
        chatField.setText(null);
        chatArea.setCaretPosition(chatArea.getText().length());
    }//GEN-LAST:event_chatFieldActionPerformed

    private void newGameMenuItemMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_newGameMenuItemMouseClicked
    }//GEN-LAST:event_newGameMenuItemMouseClicked

    private void newGameMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newGameMenuItemActionPerformed
        NewGameWindow.pack();
        NewGameWindow.setVisible(true);
    }//GEN-LAST:event_newGameMenuItemActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        _p1PiecesView.flipVertical();
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void chatFieldKeyPressed(java.awt.event.KeyEvent evt) {

    }

    private void chatWindowMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chatWindowMenuItemActionPerformed
        if (ChatPanel.isVisible()) {
            ChatPanel.setVisible(false);
        } else {
            ChatPanel.setVisible(true);
        }
    }//GEN-LAST:event_chatWindowMenuItemActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        _p1PiecesView.flipHorizontal();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        _p1PiecesView.rotateCCW();
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        _p1PiecesView.rotateCW();
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void newGameNumPlayersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newGameNumPlayersActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_newGameNumPlayersActionPerformed

    private void newGameNextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newGameNextButtonActionPerformed
        int humans = newGameNumPlayers.getSelectedIndex();
        int ai = newGameNumAI.getSelectedIndex();

        if (humans + ai > 4) {
            this.displayError("A game cannot have more than four players total.");
            return;
        } else if (newGameNumAI.getSelectedIndex() + newGameNumPlayers.getSelectedIndex() < 1) {
            this.displayError("A game must have at least one player on the host machine.");
            return;
        }
        NewGameWindow.setVisible(false);
        javax.swing.JLabel[] playerLabels = new javax.swing.JLabel[4];
        playerLabels[0] = player1TypeLabel;
        playerLabels[1] = player2TypeLabel;
        playerLabels[2] = player3TypeLabel;
        playerLabels[3] = player4TypeLabel;
        javax.swing.JTextField[] nameFields = new javax.swing.JTextField[4];
        nameFields[0] = Player1Field;
        nameFields[1] = Player2Field;
        nameFields[2] = Player3Field;
        nameFields[3] = Player4Field;
        for (int i = 0; i < humans; i++) {
            playerLabels[i].setText("(Human)");
            nameFields[i].setEditable(true);
        }
        for (int j = humans; j < humans + ai; j++) {
            playerLabels[j].setText("(AI)");
            nameFields[j].setEditable(true);
        }
        for (int k = humans + ai; k < 4; k++) {
            playerLabels[k].setText("(Remote)");
            nameFields[k].setEditable(false);
        }
        NewGameNamesWindow.pack();
        NewGameNamesWindow.setVisible(true);
        if (humans + ai < 4) {
            saveMenuItem.setEnabled(false);
        }
        else {
            saveMenuItem.setEnabled(true);
        }
    }//GEN-LAST:event_newGameNextButtonActionPerformed

    private void joinGameNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_joinGameNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_joinGameNameActionPerformed

    private void joinGameStartButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_joinGameStartButtonActionPerformed
        String name = joinGameName.getText();
        String ip = joinGameIP.getText();
        JoinGameWindow.setVisible(false);
        LinkedList<Player> humanPlayers = new LinkedList<Player>();
        humanPlayers.add(new HumanPlayer(name, this));
        this.newGame(false, humanPlayers, ip);
    }//GEN-LAST:event_joinGameStartButtonActionPerformed

    public void setStatus(String message, Color color){
        java.awt.Color awtColor = java.awt.Color.blue;
        switch(color){
            case BLUE: awtColor = java.awt.Color.blue; break;
            case YELLOW: awtColor = java.awt.Color.yellow; break;
            case RED: awtColor = java.awt.Color.red; break;
            case GREEN: awtColor = java.awt.Color.green; break;
        }
        statusLabel.setForeground(awtColor);
        statusLabel.setText(message);
    }
    
    private void newGame(boolean isHost, LinkedList<Player> humanPlayers, String ip){
        if(isHost){
            this.displayError("Waiting for clients to connect...");
        }
        else{
            this.displayError("Waiting to connect to host...");
        }
        //_main
        _main.newGame();
        if (_gameRunner!=null)
            _gameRunner.cancel(true);
        _main = new BlokusMain(new Game());
        for(int i = 0; i < 4; i++){
            _pieceContainers[i].removeAll();
        }
        _pieceContainers[0].add(PassButton);
        _pieceContainers[0].add(statusLabel);
        _pieceContainers[0].add(errorLabel);
        errorLabel.setText("");
        _viewPlayers = new Player[4];
        _moveNum = 0;
        previousMovesList = new PreviousMovesList(this);
        previousMovesScrollPane.setViewportView(previousMovesList);
        _game = _main.takeSeat(isHost, humanPlayers, ip, this);
        for (int i = 0; i < _game.getPlayers().size(); i++) {
            if (_game.getPlayers().get(i).getName().equals("") || _game.getPlayers().get(i).getName() == null) {
                _game.getPlayers().get(i).setName(_game.getPlayers().get(i).getColor().toString());
            }
        }
        chatArea.setText("");
        _main.startChat(isHost, humanPlayers.getFirst(), ip, this);
        _players = _game.getPlayers();
        _board = _game.getBoard();
        this.setPlayerLabels(isHost);
        _isHost = isHost;

        if ((_gamePieces != null) && (_gamePieces.isLive())) {
            _gamePieces.detach();
        }
        _gamePieces = new BranchGroup();
        _gamePieces.setCapability(BranchGroup.ALLOW_DETACH);
        _gamePieces.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        _gamePieces.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);

        _mover.addChild(_gamePieces);
        drawBoard(_board);

        if(isHost){
            _gameRunner=new GameRunner();
            _gameRunner.execute();
        }
        else{
            _main.start(false, ip, this);
        }
        boolean saveEnabled = isHost;
        for(Player player : _players){
            if(player instanceof NetworkPlayer){
                saveEnabled = false;
            }
        }
        saveMenuItem.setEnabled(saveEnabled);
        this.displayError("");
    }
    
    private void newGameOld() {
        if ((_gamePieces != null) && (_gamePieces.isLive())) {
            _gamePieces.detach();
        }
        _gamePieces = new BranchGroup();
        _gamePieces.setCapability(BranchGroup.ALLOW_DETACH);
        _gamePieces.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        _gamePieces.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);

        _mover.addChild(_gamePieces);        

        _viewPlayers = new Player[4];
        _moveNum = 0;

        //this.previousMovesList.removeAllElements();
    }

    private void newGameBackButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newGameBackButtonActionPerformed
        NewGameNamesWindow.setVisible(false);
        NewGameWindow.pack();
        NewGameWindow.setVisible(true);
    }//GEN-LAST:event_newGameBackButtonActionPerformed

    class GameRunner extends SwingWorker<String, Object> {

        @Override
        public String doInBackground() {
            _main.start(true, "localhost", t);
            while(!isCancelled()){
                
            }
            _main.endGame();
            return "";

        }

        @Override
        protected void done() {
        }
    }

    private void newGameStartButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newGameStartButtonActionPerformed

        int humans = newGameNumPlayers.getSelectedIndex();
        int ai = newGameNumAI.getSelectedIndex();
        NewGameNamesWindow.setVisible(false);
        javax.swing.JLabel[] playerLabels = new javax.swing.JLabel[4];
        playerLabels[0] = player1TypeLabel;
        playerLabels[1] = player2TypeLabel;
        playerLabels[2] = player3TypeLabel;
        playerLabels[3] = player4TypeLabel;
        javax.swing.JTextField[] nameFields = new javax.swing.JTextField[4];
        nameFields[0] = Player1Field;
        nameFields[1] = Player2Field;
        nameFields[2] = Player3Field;
        nameFields[3] = Player4Field;
        LinkedList<Player> players = new LinkedList<Player>();
        for (int i = 0; i < humans; i++) {
            players.add(new HumanPlayer(nameFields[i].getText(), this));
        }
        for (int j = humans; j < ai + humans; j++) {
            players.add(new AIPlayer(nameFields[j].getText(), Constants.AIDifficulty));
        }
        this.newGame(true, players, "localhost");

    }//GEN-LAST:event_newGameStartButtonActionPerformed

    private void joinMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_joinMenuItemActionPerformed
        JoinGameWindow.pack();
        JoinGameWindow.setVisible(true);
    }//GEN-LAST:event_joinMenuItemActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        EndGameWindow.setVisible(false);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void PassButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PassButtonActionPerformed

        if (_getMove) {
            sendMove(new Move(_game.getCurrentTurn()));
            _getMove = false;
        }

    }//GEN-LAST:event_PassButtonActionPerformed

    private void saveMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveMenuItemActionPerformed
        //filter only .blok files
        //filter out the "." in ".blok"
        String extension = Constants.savedGamesExt.substring(1);
        FileNameExtensionFilter ft = new FileNameExtensionFilter(
                "Blokus Saved Files", extension);
        SaveGameWindow.addChoosableFileFilter(ft);

        //set the default location the default dir
        SaveGameWindow.setCurrentDirectory(
                new java.io.File(Constants.savedGamesDir));

        //Set the default fileName to be the auto-generated one from game
        String fileName = _game.generateSaveGameName();
        File file = new File(Constants.savedGamesDir + fileName);
        SaveGameWindow.setSelectedFile(file);

        int returnVal = SaveGameWindow.showSaveDialog(MainWindow.this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            fileName = SaveGameWindow.getSelectedFile().getName();
        } else {
            this.displayError("Invalid file.");

        }

        _game.saveGame(fileName);
    }//GEN-LAST:event_saveMenuItemActionPerformed

    private void openMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openMenuItemActionPerformed
        //filter only .blok files
        //filter out the "." in ".blok"
        String extension = Constants.savedGamesExt.substring(1);
        FileNameExtensionFilter ft = new FileNameExtensionFilter(
                "Blokus Saved Files", extension);
        OpenGameWindow.addChoosableFileFilter(ft);

        //set the default location the default dir
        OpenGameWindow.setCurrentDirectory(
                new java.io.File(Constants.savedGamesDir));

        int returnVal = OpenGameWindow.showOpenDialog(MainWindow.this);
        String fileName = "";

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            newGameOld();
            
            //get rid of any errors (like host has disconnected, etc.)
            this.displayError("");
            fileName = OpenGameWindow.getSelectedFile().getName();

            //the new Game() is irrelevant we just need some game object to run the
            //method on
            _game = new Game().loadGame(fileName);
            _main = new BlokusMain(_game);
            _board = _game.getBoard();

            //set _players in the correct order
            Color currColor = _game.getCurrentTurn();
            LinkedList<Player> players = _game.getPlayers();
            _players = new LinkedList<Player>();
            for (int i = currColor.ordinal(); i < currColor.ordinal() + 4; i++) {
                _players.add(players.get(i % 4));
            }

            //set previous moves panel
            previousMovesList = new PreviousMovesList(this);
            previousMovesScrollPane.setViewportView(previousMovesList);
            _moveNum = 0;
            List<Move> prevMoves = _game.getBoard().getAllMoves();
            _game.getBoard().rollBack(prevMoves.size());
            for (int i = 0; i < prevMoves.size(); i++) {
                _moveNum++;
                Move m = prevMoves.get(i);
                Board b = new Board(_game.getBoard()); //board in initial state
                b.rollForward(_moveNum);
                PreviousMovePanel pan = new PreviousMovePanel(m, b,
                        _game.getPlayers().get(m.getColor().ordinal()).getName(),
                        _moveNum, this);
                previousMovesList.add(pan);
                if (_moveNum > 4) {
                    JViewport v = previousMovesScrollPane.getViewport();
                    Point p = v.toViewCoordinates(new Point(0, 90 * (_moveNum - 4)));
                    v.setViewPosition(p);
                }
                previousMovesList.revalidate();
                previousMovesList.repaint();
            }

            _game.getBoard().rollForward(prevMoves.size());


            _isHost = true;
            this.setPlayerLabels(_isHost);

            //update the gui in all of the players (because we don't save it), and
            //update their piece displays.
            //All the players must be human or AI for a saved game
            for (Player player : _players) {
                player.updatePieceDisplay();
                if (player instanceof HumanPlayer) {
                    ((HumanPlayer) player).setGUI(this);
                }
            }

            drawBoard(_board);
            errorLabel.setText(""); //get rid of "welcome to blackbird blokus"
            _gameRunner = new GameRunner();
            _gameRunner.execute();
            saveMenuItem.setEnabled(true);
        } else {
            this.displayError("Invalid file.");
        }        
    }//GEN-LAST:event_openMenuItemActionPerformed

    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
        AboutWindow.pack();
        AboutWindow.setVisible(true);
    }//GEN-LAST:event_aboutMenuItemActionPerformed

    private void contentsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contentsMenuItemActionPerformed
        RulesWindow.pack();
        RulesWindow.setVisible(true);
    }//GEN-LAST:event_contentsMenuItemActionPerformed

    private void rateMovesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rateMovesMenuItemActionPerformed
        shouldRateMoves = rateMovesMenuItem.isSelected();
        if (!shouldRateMoves) {
            rateMovesLabel.setForeground(java.awt.Color.black);
        }
        // TODO: use if (player instanceof HumanPlayer) { rateMovePanel.setText(_game.getMoveRating(Color...)); }
    }//GEN-LAST:event_rateMovesMenuItemActionPerformed

    private void tutorialMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tutorialMenuItemActionPerformed
        JFrame f = new JFrame("Beginner's Tutorial");
        SlideshowPanel sp = new SlideshowPanel();
        String[] text = {"This is how the game begins. Each player occupies 2 radially opposite corners.",
                "To make your first move, simply select a piece in the bottom panel with your left mouse button.",
                "You can then rotate and flip the piece with the WASD keys, or through the \"Piece\" menu.",
                "You can then make your move with the left mouse button. Note that your piece must be adjacent diagonally, " +
                    "but not by sides, to one of your already placed squares.",
                "This is a good move because it uses a 5 square piece (getting you on the way to placing all of your pieces!), " +
                    "and begins to encroach on others\' territory.",
                "Note that your piece will be grayed out if it is an invalid move (see status at the bottom to understand why).",
                "Now, the game has progressed a bit. Let's examine the Recent Moves panel on the left.",
                "I clicked the first blue state, so the game has reverted to that state on the board.",
                "(Doesn't that look valid? but Yellow has a piece there!)",
                "You can revert the board to the current state by clicking the most recent (top-most) move, but you can also " +
                    "just make a move, and the board will automatically revert."};
        for (int i = 1; i <= 10; i++) {
            sp.add(new File("tutorialImages" + File.separator +
                    i + ".png"), text[i-1]);
        }
        sp.next();
        f.setLayout(new BorderLayout());
        f.add(BorderLayout.CENTER, sp);
        //f.setSize(400,400);
        f.pack();
        f.setResizable(false);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setVisible(true);
    }//GEN-LAST:event_tutorialMenuItemActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                //modify this later
                new MainWindow(new BlokusMain()).setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Label About;
    private javax.swing.JTextArea AboutTextArea;
    private javax.swing.JFrame AboutWindow;
    private javax.swing.JPanel ChatPanel;
    private javax.swing.JPanel CubePanel;
    private javax.swing.JLabel EndGameP1Label;
    private javax.swing.JLabel EndGameP2Label;
    private javax.swing.JLabel EndGameP3Label;
    private javax.swing.JLabel EndGameP4Label;
    private javax.swing.JFrame EndGameWindow;
    private javax.swing.JLabel EndGameWinnerLabel;
    private java.awt.Panel JFrame1;
    private javax.swing.JFrame JoinGameWindow;
    private javax.swing.JPanel MainPanel;
    private javax.swing.JPanel MovesChatPanel;
    private javax.swing.JPanel MovesPanel;
    private javax.swing.JFrame NewGameNamesWindow;
    private javax.swing.JFrame NewGameWindow;
    private javax.swing.JFileChooser OpenGameWindow;
    private javax.swing.JPanel OpponentPanel;
    private javax.swing.JPanel OpponentPiecesPanel;
    private javax.swing.JLabel P1namelabel;
    private javax.swing.JPanel P1piecescontainer;
    private javax.swing.JLabel P2namelabel;
    private javax.swing.JPanel P2piecescontainer;
    private javax.swing.JPanel P2piecespanel;
    private javax.swing.JLabel P3namelabel;
    private javax.swing.JPanel P3piecescontainer;
    private javax.swing.JPanel P3piecespanel;
    private javax.swing.JLabel P4namelabel;
    private javax.swing.JPanel P4piecescontainer;
    private javax.swing.JPanel P4piecespanel;
    private javax.swing.JButton PassButton;
    private javax.swing.JPanel PiecesPanel;
    private javax.swing.JTextField Player1Field;
    private javax.swing.JTextField Player2Field;
    private javax.swing.JTextField Player3Field;
    private javax.swing.JTextField Player4Field;
    private javax.swing.JTextArea RulesTextArea;
    private javax.swing.JFrame RulesWindow;
    private javax.swing.JFileChooser SaveGameWindow;
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JTextPane chatArea;
    private javax.swing.JTextField chatField;
    private javax.swing.JCheckBoxMenuItem chatWindowMenuItem;
    private javax.swing.JMenuItem contentsMenuItem;
    private javax.swing.JMenu editMenu;
    private javax.swing.JLabel errorLabel;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JTextField joinGameIP;
    private javax.swing.JTextField joinGameName;
    private javax.swing.JButton joinGameStartButton;
    private javax.swing.JMenuItem joinMenuItem;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JButton newGameBackButton;
    private javax.swing.JMenuItem newGameMenuItem;
    private javax.swing.JButton newGameNextButton;
    private javax.swing.JComboBox newGameNumAI;
    private javax.swing.JComboBox newGameNumPlayers;
    private javax.swing.JButton newGameStartButton;
    private javax.swing.JMenuItem openMenuItem;
    private javax.swing.JLabel player1TypeLabel;
    private javax.swing.JLabel player2TypeLabel;
    private javax.swing.JLabel player3TypeLabel;
    private javax.swing.JLabel player4TypeLabel;
    private javax.swing.JScrollPane previousMovesScrollPane;
    private javax.swing.JLabel rateMovesLabel;
    private javax.swing.JCheckBoxMenuItem rateMovesMenuItem;
    private javax.swing.JMenuItem saveMenuItem;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JMenuItem tutorialMenuItem;
    // End of variables declaration//GEN-END:variables
}
