package mineSweeper;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Iterator;

import javax.swing.*;
import javax.swing.border.*;

import mineSweeper.Tile.TileType;

public class MineSweeperGUI implements Runnable{

    private final JPanel gui = new JPanel(new BorderLayout(3, 3));
    private Tile[][] tileSquares;
    private JFrame f = new JFrame();
    JLabel mineCountLabel;
    private JPanel tileGrid;
    private int mineCount;
    private int sizeY;
    private int sizeX;
    private int mines;
    private int currMode;
    private int clickCount = 0;
    private boolean gameCreated = false;
    MineSweeper dataGrid;
    long startTime;
    
    private ImageIcon neutral = new ImageIcon("C:\\Users\\artur_000\\workspace\\Advanced Concept\\src\\images\\Neutral.jpg");
    private ImageIcon empty = new ImageIcon("C:\\Users\\artur_000\\workspace\\Advanced Concept\\src\\images\\Empty.jpg");
    private ImageIcon mine = new ImageIcon("C:\\Users\\artur_000\\workspace\\Advanced Concept\\src\\images\\Mine.jpg");
    private ImageIcon flag = new ImageIcon("C:\\Users\\artur_000\\workspace\\Advanced Concept\\src\\images\\Flag.jpg");


    
    MineSweeperGUI(int mode) {
        initializeGui(mode);
    }

    public final void initializeGui(int mode) {
    	currMode = mode;
        // set up the main GUI
        gui.setBorder(new EmptyBorder(5, 5, 5, 5));
        JToolBar tools = new JToolBar();
        tools.setFloatable(false);
        gui.add(tools, BorderLayout.PAGE_START);
        JButton beginnerButton = new JButton("Beginner");
        tools.add(beginnerButton); 
        beginnerButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				restart(1);
			}
        });
        // Lotta Guap!
        JButton intermediateButton = new JButton("Intermediate");
        tools.add(intermediateButton); 
        intermediateButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				restart(2);
			}
        });
        
        JButton expertButton = new JButton("Expert");
        tools.add(expertButton); 
        expertButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				restart(3);
			}
        });
        
        JButton restartButton = new JButton("Restart");
        tools.add(restartButton); 
        restartButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(currMode);
				restart(currMode);
			}
        });
        tools.addSeparator();
        
        if (currMode == 1){
        	sizeX = 8;
        	sizeY = 8;
        	mines = 10;
        }else if(currMode == 2){
        	sizeX = 16;
        	sizeY = 16;
        	mines = 50;
        }else{
        	sizeX = 30;
        	sizeY = 15;
        	mines = 100;
        }
        
        mineCountLabel = new JLabel();
        mineCount = mines;
        mineCountLabel.setText("Mines: " + mineCount);
        mineCountLabel.setFont(new Font("Serif", Font.BOLD, 14));
        mineCountLabel.setForeground(Color.RED);
        tools.add(mineCountLabel);
        
        createField(sizeX, sizeY);
        updateTileIcons();
        tileGrid.setBorder(new LineBorder(Color.BLACK));
        gui.add(tileGrid);
        
    }

    public final JComponent getGui() {
        return gui;
    }
    
    void createField(int sizeX, int sizeY){
    	tileSquares = new Tile[sizeY][sizeX];
    	tileGrid = new JPanel(new GridLayout(0, sizeX));
    	
        Insets bMargin = new Insets(0,0,0,0);
        for (int ii = 0; ii < tileSquares.length; ii++) {
            for (int jj = 0; jj < tileSquares[ii].length; jj++) {
                Tile b = new Tile();
                b.setMargin(bMargin);
                
                // Our sprites are 32x32 pixels, so we "fill" them in with a transparent icon
                ImageIcon icon = new ImageIcon(
                        new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB));
                
                b.setIcon(icon);
                b.setBackground(Color.gray);
                b.addMouseListener(new MouseAdapter(){
                    @Override
                    public void mouseReleased(MouseEvent e) { 
                    	if (!b.pressed){
                    		clickCount++;
                    		if (SwingUtilities.isRightMouseButton(e)) {
                                b.flag();
                                if(b.type == TileType.FLAGGED){
                                	mineCount --;
                                }else{
                                	mineCount++;
                                }
                                if(gameCreated){
	                                if(checkWin()){
	                                	// You win!!
	                                	win();
	                                }
                                  }
                            	}
                    		else {
                    			if (b.type != TileType.FLAGGED){
                    				clickTile(b);
                    				b.pressed = true;
                    				}
                            	}
                    		updateTileIcons();
                        }  
                    }
                });
                b.row = ii;
                b.col = jj;
                tileSquares[ii][jj] = b;
                
                // Add the button to the actual tileGrid Panel
                tileGrid.add(tileSquares[ii][jj]);
            }
        }
    }
    
    void updateTileIcons(){
    	for (int ii = 0; ii < tileSquares.length; ii++) {
            for (int jj = 0; jj < tileSquares[ii].length; jj++) {
            	if (tileSquares[ii][jj].type == TileType.MINE){
            		tileSquares[ii][jj].setIcon(mine);
            		continue;
            	}
            	if (tileSquares[ii][jj].type == TileType.EMPTY){
                	tileSquares[ii][jj].setIcon(empty);
                	continue;
                }
            	if (tileSquares[ii][jj].type == TileType.FLAGGED){
                	tileSquares[ii][jj].setIcon(flag);
                	continue;
                }
            	if (tileSquares[ii][jj].type == TileType.NUM){
                	tileSquares[ii][jj].setIcon(
                			new ImageIcon("C:\\Users\\artur_000\\workspace\\Advanced Concept\\src\\images\\number-" + dataGrid.field[ii][jj] + ".jpg"));
                	continue;
                }
            	tileSquares[ii][jj].setIcon(neutral);
            }
         }
        mineCountLabel.setText("Mines: " + mineCount);

    }
    
    void clickTile(Tile tile){
    	if(!gameCreated){
    		dataGrid = new MineSweeper(sizeX, sizeY, mines, tile.row, tile.col);
    		gameCreated = true;
    		startTime = System.currentTimeMillis();
    	}
    		switch(dataGrid.field[tile.row][tile.col]){
    		case 0: tile.type = TileType.EMPTY;
    				tile.pressed = true;
 
	    			Iterator<int[]> itr = dataGrid.getNeighbors(tile.row, tile.col).iterator(); 
		    		while (itr.hasNext()){
		    			int[] element = itr.next();
		    			if ((dataGrid.field[element[0]][element[1]] == 0 || dataGrid.field[element[0]][element[1]] > 0) && !tileSquares[element[0]][element[1]].pressed){
		    				clickTile(tileSquares[element[0]][element[1]]);
		    			}
		    		}
    				break;
    		case -1: tile.type = TileType.MINE; 
    				// YOU LOOSE!!!!

		    		for (int ii = 0; ii < tileSquares.length; ii++) {
		                for (int jj = 0; jj < tileSquares[ii].length; jj++) {
		                	tileSquares[ii][jj].pressed = true;
		                	if (dataGrid.field[ii][jj] == -1){
		                		tileSquares[ii][jj].type = TileType.MINE;
		                	}
		                }
		            }
					break;
    		default:
    				tile.type = TileType.NUM;
    				tile.pressed = true;
    				break;
    		}
    }
    

    public static void main(String[] args) {
    	(new Thread(new MineSweeperGUI(1))).start();
    }
    
    @Override
    public void run() {
        f.getContentPane().add(this.getGui());
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //f.setLocationByPlatform(true);

        // ensures the frame is the minimum size it needs to be
        // in order display the components within it
        f.pack();
        // ensures the minimum size is enforced.
        f.setMinimumSize(f.getSize());
        f.setSize(f.getSize());
        f.setVisible(true);
    }
    
    void restart(int mode){
    	f.dispose();
		gui.remove(tileGrid);
		gui.remove(gui.getComponent(0));
		f = new JFrame();
		tileSquares = null;
		initializeGui(mode);
		dataGrid = null;
		gameCreated = false;
		run();
    }
    
    void win(){
    	// Set all tiles to "pressed", so you can't use the board anymore
    	for (int ii = 0; ii < tileSquares.length; ii++) {
            for (int jj = 0; jj < tileSquares[ii].length; jj++) {
            	tileSquares[ii][jj].pressed = true;
            }
    	}
    	
    	// Creating the WIN frame
    	JFrame winFrame = new JFrame();
    	winFrame.setLayout(new BorderLayout());
    	winFrame.setBackground(Color.WHITE);
    	winFrame.setSize(300, 300);
        winFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JLabel youWin = new JLabel("You Win!");
        youWin.setFont(new Font("Impact", Font.BOLD, 24));
        youWin.setForeground(Color.GREEN);
        youWin.setHorizontalAlignment(JLabel.CENTER);
        youWin.setVerticalAlignment(JLabel.CENTER);
        
        JLabel clickCountLabel = new JLabel();
        clickCountLabel.setText("Number of Clicks: " + Integer.toString(clickCount));
        clickCountLabel.setFont(new Font("Arial", Font.BOLD, 16));
        clickCountLabel.setForeground(Color.BLUE);
        clickCountLabel.setHorizontalAlignment(JLabel.CENTER);
        
        JLabel Timer = new JLabel("Time: " + Long.toString((System.currentTimeMillis() - startTime) / 1000) + " seconds");
        Timer.setFont(new Font("Arial", Font.BOLD, 16));
        Timer.setForeground(Color.RED);
        Timer.setHorizontalAlignment(JLabel.CENTER);
        Timer.setVerticalAlignment(JLabel.CENTER);
        
    	winFrame.add(youWin, BorderLayout.NORTH);
    	winFrame.add(clickCountLabel, BorderLayout.CENTER);
    	winFrame.add(Timer, BorderLayout.SOUTH);
        winFrame.setVisible(true);
    }

    boolean checkWin(){
    	int mine = mines;
    	for (int ii = 0; ii < tileSquares.length; ii++) {
            for (int jj = 0; jj < tileSquares[ii].length; jj++) {
            	if (tileSquares[ii][jj].type == TileType.FLAGGED){
            		if (dataGrid.field[ii][jj] == -1){
            			mine--;
            		}
            	}
            	if(mine == 0 && mineCount == 0){
            		return true;
            	}

            }
         }	
    	return false;
    }
}