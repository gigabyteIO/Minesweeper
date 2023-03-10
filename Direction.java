
public enum Direction {
	NORTHWEST(-1, -1),
	NORTH(-1, 0),
	NORTHEAST(-1, 1),
	EAST(0, 1),
	SOUTHEAST(1, 1),
	SOUTH(1, 0),
	SOUTHWEST(1, -1),
	WEST(0, -1);
	
	private int row, col;
	
	private Direction(int row, int col) {
		this.row = row;
		this.col = col;
	}
	
	public int getRow() {
		return row;
	}
	
	public int getCol() {
		return col;
	}
}
