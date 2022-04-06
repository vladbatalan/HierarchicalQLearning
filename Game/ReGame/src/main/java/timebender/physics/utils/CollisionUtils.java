package timebender.physics.utils;

import java.awt.*;

public class CollisionUtils {

    private static final int NUMBER_OF_TOP_INTERVALS = 4;
    private static final int NUMBER_OF_SIDE_INTERVALS = 8;
    private static final int NUMBER_OF_SIDE_POINTS = 10;

    // for side collision purposes, this function creates based on several parameters of an array of points
    // the returning vector of points is organised like this:
    // -> it has length/4 points for each side
    // -> the sides are in the following order:
    //      - top
    //      - right
    //      - bottom
    //      - left

    /**
     * Method responsible for extracting the points in which the collision is evaluated
     *
     * @return An array containing collision points
     */
    public static PointVector[] getSideCollisionPoints(Rectangle rect) {

        //consider the following points on the side of the square
        /*           __  -> deltaX
        __#____#____#__
        |             | -> Body
        #             # -> Point to check collision
        |             |
        #             #
        |             |
        #             # -> Point to check collision
        |             |     | -> deltaY
        |_#____#____#_|     |
        */

        float deltaX = (float) rect.width / NUMBER_OF_TOP_INTERVALS;
        float deltaY = (float) rect.height / NUMBER_OF_SIDE_INTERVALS;

        float topStep = (rect.width - 2 * deltaX) / NUMBER_OF_SIDE_POINTS;
        float sideStep = (rect.height - 2 * deltaY) / NUMBER_OF_SIDE_POINTS;

        int totalPoints = 4 * NUMBER_OF_SIDE_POINTS + 4;

        //points to be verified
        PointVector[] checkingPoints = new PointVector[totalPoints];

        for (int i = 0; i < NUMBER_OF_SIDE_POINTS + 1; i++) {

            int topIndex = i;
            int rightIndex = i + NUMBER_OF_SIDE_POINTS + 1;
            int bottomIndex = i + 2 * (NUMBER_OF_SIDE_POINTS + 1);
            int leftIndex = i + 3 * (NUMBER_OF_SIDE_POINTS + 1);

            checkingPoints[topIndex] = new PointVector(rect.x + deltaX + i * topStep, rect.y);
            checkingPoints[rightIndex] = new PointVector(rect.x + rect.width, rect.y + deltaY + i * sideStep);
            checkingPoints[bottomIndex] = new PointVector(rect.x + deltaX + i * topStep, rect.y + rect.height);
            checkingPoints[leftIndex] = new PointVector(rect.x, rect.y + deltaY + i * sideStep);
        }

        return checkingPoints;
    }


}
