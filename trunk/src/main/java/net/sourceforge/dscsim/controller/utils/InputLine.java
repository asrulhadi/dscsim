package net.sourceforge.dscsim.controller.utils;

public class InputLine {
	
	private String _partOne;
	private String _partTwo;
	public InputLine(String partOne, String partTwo) {
		_partOne = partOne;
		_partTwo = partTwo;
		
	}
	public String toString() {
		return _partOne + _partTwo;
	}
	
	public void setPartOne(String partOne) {
		_partOne = partOne;
	}
	public void setPartTwo(String partTwo) {
		_partTwo = partTwo;
	}
	
	public String getPartTwo() {
		return _partTwo;
	}
}