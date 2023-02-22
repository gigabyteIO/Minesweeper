
public enum BlockPosition {

	UPPERLEFTCORNER(Direction.EAST, Direction.SOUTHEAST, Direction.SOUTH),
	UPPERSIDE(Direction.WEST, Direction.SOUTHWEST, Direction.SOUTH, Direction.SOUTHEAST, Direction.EAST),
	UPPERRIGHTCORNER(Direction.WEST, Direction.SOUTHWEST, Direction.SOUTH),
	RIGHTSIDE(Direction.NORTH, Direction.NORTHWEST, Direction.WEST, Direction.SOUTHWEST, Direction.SOUTH),
	LOWERRIGHTCORNER(Direction.WEST, Direction.NORTHWEST, Direction.NORTH),
	LOWERSIDE(Direction.EAST, Direction.NORTHEAST, Direction.NORTH, Direction.NORTHWEST, Direction.WEST),
	LOWERLEFTCORNER(Direction.EAST, Direction.NORTHEAST, Direction.NORTH),
	LEFTSIDE(Direction.SOUTH, Direction.SOUTHEAST, Direction.EAST, Direction.NORTHEAST, Direction.NORTH),
	CENTER(Direction.NORTHWEST, Direction.NORTH, Direction.NORTHEAST, Direction.EAST, Direction.SOUTHEAST, Direction.SOUTH, Direction.SOUTHWEST, Direction.WEST);

	
	Direction northwest, north, northeast, east, southeast, south, southwest, west;
	
	private BlockPosition(Direction east, Direction southeast, Direction south) {
		this.east = east;
		this.southeast = southeast;
		this.south = south;
	}

	private BlockPosition(Direction west, Direction southwest, Direction south, Direction southeast, Direction east) {
		this.west = west;
		this.southwest = southwest;
		this.south = south;
		this.southeast = southeast;
		this.east = east;
	}

	private BlockPosition(Direction northwest, Direction north, Direction northeast, Direction east, Direction southeast,
			Direction south, Direction southwest, Direction west) {
		this.northwest = northwest;
		this.north = north;
		this.northeast = northeast;
		this.east = east;
		this.southeast = southeast;
		this.south = south;
		this.southwest = southwest;
		this.west = west;
	}
	
	
//	public Direction[] getDirectionsToCheck(BlockType type) {
//		Direction[] directions;
//
//		if(type == UPPERLEFTCORNER) {
//			directions = new Direction[3];
//			directions[0] = 
//			directions[1]
//			directions[2]
//		}
//		if(type == UPPERRIGHTCORNER) {
//			directions = new Direction[3];
//		}
//		if(type == LOWERLEFTCORNER) {
//			directions = new Direction[3];
//		}
//		if(type == LOWERRIGHTCORNER) {
//			directions = new Direction[3];
//		}
//		if(type == UPPERSIDE) {
//			directions = new Direction[5];
//		}
//
//		if(type == LOWERSIDE) {
//			directions = new Direction[5];
//		}
//
//		if(type == LEFTSIDE) {
//			directions = new Direction[5];
//		}
//
//		if(type == RIGHTSIDE) {
//			directions = new Direction[5];
//		}
//
//		if(type == CENTER) {
//			directions = new Direction[8];
//		}
//		
//		return null;
//	}
	
	
	
}
