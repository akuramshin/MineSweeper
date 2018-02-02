package mineSweeper;

import javax.swing.JButton;

public class Tile extends JButton{

	public enum TileType {MINE, EMPTY, NUM, FLAGGED, NEUTRAL}
	
	boolean pressed = false;
	TileType type = TileType.NEUTRAL;
	int row = 0;
	int col = 0;
	
	void flag(){
		if (type == TileType.FLAGGED){
			type = TileType.NEUTRAL;
			return;
		}
		type = TileType.FLAGGED;
	}
}
