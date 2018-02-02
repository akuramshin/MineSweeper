package mineSweeper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class MineSweeper {
	
	int[][] field;
	boolean[][] visited;
	int mines = 0;

	public static void main(String[] args){
		new MineSweeper(16, 16, 40, 0, 0);
	}
	
	MineSweeper(int sizeX, int sizeY, int mines, int row, int col){
		field = new int[sizeY][sizeX];
		visited = new boolean[sizeY][sizeX];
		this.mines = mines;
		generateMines(row, col);

		// Recursive call 
		tileGenerator(row, col);
		
		//printArray();
	}
	
	void tileGenerator(int row, int col){
		int mineCount = 0;
		visited[row][col] = true;

		Iterator<int[]> itr = getNeighbors(row, col).iterator(); 
		while (itr.hasNext()){
			int[] element = itr.next();
			if ((field[element[0]][element[1]] == 0 || field[element[0]][element[1]] == -2) && !visited[element[0]][element[1]]){
				tileGenerator(element[0], element[1]);
			}
			if (field[element[0]][element[1]] == -1){
				mineCount += 1;
			}
		}
		field[row][col] = mineCount;
	}
	
	ArrayList<int[]> getNeighbors(int row, int col){
		// return a 2D array of neighboring tiles like {{1, 1}, {2, 3}, {4, 5}}
		ArrayList<int[]> neighbors = new ArrayList<int[]>();
		
		int fromCol = Math.max(0, col - 1);
		int fromRow = Math.max(0, row - 1);
		
		int toCol = Math.min(field[0].length - 1, col + 1);
		int toRow = Math.min(field.length - 1, row + 1);

		for (int x = fromRow; x <= toRow; x++){
			for (int y = fromCol; y <= toCol; y++){
				if (x != row || y != col){
					neighbors.add(new int[] {x, y});
				}
			}
		}
		return neighbors;
	}
	
	void generateMines(int row, int col){
		int randRow;
		int randCol;
		Random random = new Random();
		
		for(int[] neighbor : getNeighbors(row, col)){
			field[neighbor[0]][neighbor[1]] = -2;
		}
		field[row][col] = -2;
		
		for (int size = 0; size < mines; size++){
			randRow = random.nextInt(field.length); 
			randCol = random.nextInt(field[0].length);
			if (field[randRow][randCol] != 0){
				size--;
				continue;
			}
			field[randRow][randCol] = -1;
		}
	}
	
	void printArray(){
		System.out.println();
		for (int x = 0; x < field.length; x++){
			System.out.println();
			for (int y = 0; y < field.length; y++){
				System.out.print(field[x][y] + ", ");
			}
		}
	}
}
