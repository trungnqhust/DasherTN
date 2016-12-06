package Helper;

/**
 * Created by admin on 12/2/2016.
 */


import GameObject.*;
import sun.rmi.runtime.Log;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import static CreateMap.MapCodeConst.*;
import static GameObject.ColorBox.*;

/**
 * Created by Hoangelato on 01/12/2016.
 */
public class GamePlayManager {
    public Conveyor[][] conveyorList = new Conveyor[36][36];
    public Vector<ConveyorSwitch> conveyorSwitchList = new Vector<ConveyorSwitch>();
    public Vector<ConveyorEnd> validConveyorEndList = new Vector<ConveyorEnd>();
    public BufferedImage truck, plane, ship, tree, tree1, water, road, source;

    public int map[][];
    private int numberofBoxes;
    private int numberofValidEnds;
    public int time;
    private int level;
    Random random = new Random();

    public Stack<Box> boxWaitingList = new Stack<Box>();
    ;
    public Vector<Box> boxOnMapList;
    //
    public int score;
    LogicPoint sourceLogicPoint;

    public Box box1;

    public GamePlayManager(File mapFile) {

        loadMap(mapFile);
//        getImagesForMap();


    }

    public static ColorBox convertIndexToColor(int index) {
        switch (index) {
            case 1:
                return BLUE;
            case 2:
                return GREEN;
            case 3:
                return RED;
            case 4:
                return YELLOW;
            case 5:
                return PINK;
            default:
                return WHITE;
        }

    }




    public void update() {
    }


    private void loadMap(File mapFile) {
        map = new int[36][36];
        try {

            FileReader f = new FileReader(mapFile);
            BufferedReader reader = new BufferedReader(f);

            numberofBoxes = Integer.parseInt(reader.readLine());
            time = Integer.parseInt(reader.readLine());
            level = Integer.parseInt(reader.readLine());
            int j = 0;
            String line = reader.readLine();
            while (line != null) {
                String[] lineSplit = line.split(",");
                for (int i = 0; i < lineSplit.length; i++) {
                    map[j][i] = Integer.parseInt(lineSplit[i]);
                }
                line = reader.readLine();
                j++;

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int sum = 15; sum <= 60; sum++) {
            for (int i = 0; i <= sum; i++) {
                int j = sum - i;
                if ((i <= 35) && (j <= 35)) {
                    LogicPoint lp = new LogicPoint(i, j);
                    Point point = lp.convertToPoint();
                    if (map[i][j] != 0 && map[i][j] != 100) {
                        if (getDirectionFromMapCode(map[i][j]) != Direction.NONE) {
                            Conveyor conveyorToAdd = getConveyorFromMapCode(map[i][j], point.x, point.y);
                            conveyorList[i][j] = conveyorToAdd;
                            if (isSwitch(conveyorToAdd)) conveyorSwitchList.add((ConveyorSwitch) conveyorToAdd);
//                            conveyorList.add(conveyorToAdd);
                            if (isEnd(conveyorToAdd) && checkValidEnd(i, j))
                                validConveyorEndList.add((ConveyorEnd) conveyorToAdd);
                        }
                    }
                    if (map[i][j] == SOURCE) sourceLogicPoint = new LogicPoint(lp.getLogicX() + 1, lp.getLogicY());
                }
            }
        }

        getProbableDirectionForAllSwitches();

        startBoxManager();

    }

    public void getProbableDirectionForAllSwitches() {
        for (ConveyorSwitch conveyorSwitch : conveyorSwitchList) {
            getProbableDirection(conveyorSwitch);
        }
    }

    private void startBoxManager() {
        for (int i = 0; i < numberofBoxes; i++) {
            int r = random.nextInt(6);
            ColorBox colorBox = GamePlayManager.convertIndexToColor(r);
            int index = i % validConveyorEndList.size();
            validConveyorEndList.get(index).addToStack(colorBox);
        }

        for (int i = 0; i < validConveyorEndList.size(); i++) {
            ConveyorEnd conveyorEnd = validConveyorEndList.get(i);
            if (conveyorEnd.getStackColor().empty()) {
                conveyorEnd.setColor(WHITE);
            } else {
                conveyorEnd.setColor(conveyorEnd.getStackColor().peek());
                Box box = new Box(sourceLogicPoint, conveyorEnd.getColor(), level);
                boxWaitingList.push(box);
            }
        }
        for (Box box : boxWaitingList) {
            updateDirectionForBox(box);
        }

    }

    private boolean isExistPTS(int mapCode) {
        if (mapCode == TRUCK || mapCode == PLANE || mapCode == SHIP || mapCode == FORBIDDEN) return true;
        return false;
    }

    private boolean checkValidEnd(int i, int j) {
        switch (map[i][j]) {
            case END_DOWN:
                return isExistPTS(map[i][j + 1]);
            case END_UP:
                return isExistPTS(map[i][j - 1]);
            case END_LEFT:
                return isExistPTS(map[i - 1][j]);
            case END_LEFT:
                return isExistPTS(map[+1][j]);
            default:
                return false;
        }
    }
//    public void getImagesForMap() {
//        try {
//            truck = ImageIO.read(new File("resource/Create map button/Map_truck.png"));
//            plane = ImageIO.read(new File("resource/Create map button/Map_plane.png"));
//            ship = ImageIO.read(new File("resource/Create map button/Map_ship.png"));
//            tree = ImageIO.read(new File("resource/Create map button/Map_tree.png"));
//            tree1 = ImageIO.read(new File("resource/Create map button/Map_tree_1.png"));
//            water = ImageIO.read(new File("resource/Create map button/Map_water.png"));
//            road = ImageIO.read(new File("resource/Create map button/Map_road.png"));
//            source = ImageIO.read(new File("resource/Create map button/Map_truck.png"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    int nextBoxTime = 2000;
    int countTime = 0;


    public void makeBox() {
        countTime += 17;

//        int makeBoxTime = rand.nextInt()*2000 + 3000;
//        while (countTime <= makeBoxTime){

//        }
        System.out.println(countTime + "     " + nextBoxTime);

        if (!boxWaitingList.isEmpty() && boxOnMapList.size() < numberofBoxes && countTime > nextBoxTime) {
            boxOnMapList.add(boxWaitingList.poll());

            countTime = 0;
            nextBoxTime = (int) (Math.round(Math.random() * 2000) + 3000);
        }
    }

    public void updateDirectionForBox(Box box) {
        if (box.isAllowedToCheck) {
            box.setDirection(getDirectionFromMapCode(map[box.getLogicPoint().getLogicX()][box.getLogicPoint().getLogicY()]));
        }
        box.checkToChangDirection();

    }

    public void checkBoxToEnd() {
        for (Box box : boxOnMapList) {
            LogicPoint position = box.getLogicPoint();
            switch (map[position.getLogicX()][position.getLogicY()]) {
                case END_DOWN:
                case END_LEFT:
                case END_RIGHT:
                case END_UP:
                    for (ConveyorEnd conveyorEnd : validConveyorEndList) {
                        LogicPoint conveyorPosition = conveyorEnd.getLogicPoint()
                        if ((conveyorPosition.getLogicX() == position.getLogicX()) && (conveyorPosition.getLogicY() == position.getLogicY())) {

                        } else {
                            boxOnMapList.
                                    boxWaitingList.add(box);
                        }
                    }
            }
            if (box.isAllowedToCheck) {
                box.setDirection(getDirectionFromMapCode(map[box.getLogicPoint().getLogicX()][box.getLogicPoint().getLogicY()]));
            }
            box.checkToChangDirection();
        }
    }



    private boolean isSwitch(Conveyor c) {
        if (c instanceof ConveyorSwitch) return true;
        return false;
    }

    private boolean isEnd(Conveyor c) {
        if ((c instanceof ConveyorXEnd) || (c instanceof ConveyorYEnd)) return true;
        return false;
    }



    void getProbableDirection(ConveyorSwitch conveyorSwitch) {
        ArrayList<Direction> returnList = new ArrayList<Direction>();

        int logicX = conveyorSwitch.getLogicPoint().getLogicX();
        int logicY = conveyorSwitch.getLogicPoint().getLogicY();

        boolean[] isValidDirection = new boolean[4];

        int[] neighborsMapCode = new int[4];
        neighborsMapCode[0] = map[logicX][logicY - 1];
        neighborsMapCode[1] = map[logicX + 1][logicY];
        neighborsMapCode[2] = map[logicX][logicY + 1];
        neighborsMapCode[3] = map[logicX - 1][logicY];

        for (int i = 0; i < 4; ++i) {
            if (getDirectionFromMapCode(neighborsMapCode[i]) == Direction.NONE) {
                isValidDirection[i] = false;
            } else if (getDirectionFromMapCode(neighborsMapCode[i]) == Conveyor.convertIndexToDirection((i + 2) % 4)) {
                isValidDirection[i] = false;
            } else isValidDirection[i] = true;
        }

        for (int i = 0; i < 4; i++) {
            if (isValidDirection[i]) {
                returnList.add(Conveyor.convertIndexToDirection(i));
            }
        }

        conveyorSwitch.probableDirections = returnList;
        //System.out.println(returnList);
    }

    private Conveyor getConveyorFromMapCode(int mapCode, int posX, int posY) {
        switch (mapCode) {
            case CONVEYOR_UP:
            case CONVEYOR_RIGHT:
            case CONVEYOR_LEFT:
            case CONVEYOR_DOWN:
                return new ConveyorMoving(posX, posY).getConveyorByType(getConveyorTypeFromMapcode(mapCode));
            case NONSWITCH_DOWN:
            case NONSWITCH_LEFT:
            case NONSWITCH_RIGHT:
            case NONSWITCH_UP:
                return new ConveyorNonSwitch(posX, posY).getConveyorNonSwitchByDirection(getDirectionFromMapCode(mapCode));
            case SWITCH_DOWN:
            case SWITCH_LEFT:
            case SWITCH_UP:
            case SWITCH_RIGHT:
                return new ConveyorSwitch(posX, posY, getDirectionFromMapCode(mapCode));
            case END_DOWN:
            case END_UP:
                return new ConveyorYEnd(posX, posY);
            case END_LEFT:
            case END_RIGHT:
                return new ConveyorXEnd(posX, posY);
        }
        return new Conveyor(posX, posY);
    }

    public Direction getDirectionFromMapCode(int mapCode) {
        switch (mapCode) {
            case NONSWITCH_DOWN:
            case SWITCH_DOWN:
            case CONVEYOR_DOWN:
            case END_DOWN:
                return Direction.DOWN;
            case NONSWITCH_LEFT:
            case SWITCH_LEFT:
            case CONVEYOR_LEFT:
            case END_LEFT:
                return Direction.LEFT;
            case NONSWITCH_RIGHT:
            case SWITCH_RIGHT:
            case CONVEYOR_RIGHT:
            case END_RIGHT:
                return Direction.RIGHT;
            case NONSWITCH_UP:
            case SWITCH_UP:
            case CONVEYOR_UP:
            case END_UP:
                return Direction.UP;
            default:
                return Direction.NONE;
        }

    }

    private int getConveyorTypeFromMapcode(int mapCode) {
        switch (mapCode) {
            case CONVEYOR_UP:
            case CONVEYOR_DOWN:
                return ConveyorMoving.TYPE_Y_MID;
            case CONVEYOR_RIGHT:
            case CONVEYOR_LEFT:
                return ConveyorMoving.TYPE_X_MID;
            case END_UP:
            case END_DOWN:
                return ConveyorEnd.TYPE_Y_END;
            case END_LEFT:
            case END_RIGHT:
                return ConveyorEnd.TYPE_X_END;
            default:
                return 0;
        }
    }


}
