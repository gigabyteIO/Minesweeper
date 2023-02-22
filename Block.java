
public class Block {

	private BlockPosition position;
	private boolean hasMine;
	private boolean isHidden;
	private boolean isFlagged;
	private Direction[] checks;
	
	public Block(BlockPosition position, boolean hasMine, boolean isHidden, boolean isFlagged) {
		this.position = position;
		this.hasMine = hasMine;
		this.isHidden = isHidden;
		this.isFlagged = isFlagged;
		
		
		
	}
	
	
	private BlockPosition getPosition( ) {
		return position;
	}
	
	private boolean hasMine() {
		return hasMine();
	}
	
	private boolean isHidden() {
		return isHidden;
	}
	
	private boolean isFlagged() {
		return isFlagged;
	}
	
}
