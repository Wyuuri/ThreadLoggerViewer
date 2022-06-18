package main.common;

import main.frontend.UserInterface;

public class StyleUtils {
	
	public final static int STARTING_X_COORDINATE = 30;
	public final static int STARTING_Y_COORDINATE = 50;
	public final static int GAP_X_COORDINATE = 120;
	public final static int GAP_Y_COORDINATE = 30;
	public final static int STARTING_Y_TEXT_COORDINATE = 40;
	public final static int STARTING_X_DASHED_LINE = 20;
	public final static int STARTING_Y_DASHED_LINE = 0;
	public final static int STARTING_X_DASHED_RECTANGLE = 10;
	public final static int STARTING_Y_DASHED_RECTANGLE = 30;
	public final static int DASHED_RECTANGLE_X_CORNER_ROUND = 5;
	public final static int DASHED_RECTANGLE_Y_CORNER_ROUND = 5;
	public final static int DASHED_RECTANGLE_HEIGHT = UserInterface.getMaxY() + GAP_Y_COORDINATE;
	public final static int SVG_HEIGHT = DASHED_RECTANGLE_HEIGHT + 100;
}
