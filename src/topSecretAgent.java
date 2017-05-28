import java.util.*;

import ctf.common.AgentEnvironment;
import ctf.agent.Agent;
import ctf.common.AgentAction;



/*Create Map class
 *
 */
class Map {

    //Allows formation of grid coordinates
    public class Location {
        int x;
        int y;
        Location(int x, int y) {
            x = this.x;
            y = this.y;
        }

        @Override
        public int hashCode() {
            return (int)Math.pow(10,7) + x + 1000*y;
        }

        @Override
        public boolean equals(Object o) {
            if (x == ((Location)o).x && y == ((Location)o).y)
                return true;
            else
                return false;
        }
    }
    //class creates each grid piece on the map
    public class MapPiece {
        Hashtable<String,Boolean> flags;

        MapPiece(Hashtable<String, Boolean> options) {
            flags = new Hashtable<String, Boolean>();
            update(options);
        }

        MapPiece() {
            flags = new Hashtable<String,Boolean>();
        }

        void update(Hashtable<String,Boolean> options) {
            if (options.size() > 0)
                for (Enumeration<String> e = options.keys(); e.hasMoreElements();) {
                    Object temp = e.nextElement();
                    flags.put((String)temp,options.get((String)temp));
                }
        }
        boolean deadEnd () {
            if (flags.containsKey("deadEnd"))
                if (flags.get("deadEnd"))
                    return true;
            return false;
        }

        boolean flagRow () {
            if (flags.containsKey("baseNorth") && flags.get("baseNorth"))
                return false;
            else if (flags.containsKey("baseNorth") && !flags.get("baseNorth"))
                return true;
            if (flags.containsKey("baseSouth") && flags.get("baseSouth"))
                return false;
            else if (flags.containsKey("baseSouth") && !flags.get("baseSouth"))
                return true;
            return false;
        }
    }

    Hashtable<Location,MapPiece> Map;
    int size = -1;
    Boolean complete = false;
    Location test;

    Map() {
        Map = new Hashtable<Location,MapPiece>();
        addMapPiece(1,10);
        addMapPiece(10,10);
        addMapPiece(1,1);
        addMapPiece(10,1);
    }

    void addMapPiece(int x, int y) {
        Map.put(new Location(x,y),new MapPiece());
    }
    void addMapPiece(int x, int y, Hashtable<String, Boolean> hash) {
        Map.put(new Location(x,y),new MapPiece(hash));
    }
    void addMapPiece(int x, int y, MapPiece tile) {
        Map.put(new Location(x,y),tile);
    }

    void setDeadEnd(int x, int y) {
        Hashtable<String,Boolean> tmp = new Hashtable<String,Boolean>();
        tmp.put("deadEnd",true);
        Map.get(new Location(x,y)).update(tmp);
    }

    boolean isDeadEnd(int x, int y) {
        if (Map.get(new Location(x,y)) == null)
            return false;
        return Map.get(new Location(x,y)).deadEnd();
    }

    int[] pathToFlag(int x, int y) {
        return new int[] {};
    }

    void updateMap(int x, int y, AgentEnvironment env) {
        MapPiece current = Map.get(new Location(x,y));
        Hashtable<String,Boolean> upd = new Hashtable<String,Boolean>();
        if (env.isBaseNorth(AgentEnvironment.OUR_TEAM,false))
            upd.put("baseNorth",true);
        else
            upd.put("baseNorth", false);
        if (env.isBaseSouth(AgentEnvironment.OUR_TEAM,false))
            upd.put("baseSouth",true);
        else
            upd.put("baseSouth",false);
        current.update(upd);
        upd.clear();
        //North
        if (Map.get(new Location(x,y+1)) == null)
            addMapPiece(x,y+1);
        if (env.isObstacleNorthImmediate())
            upd.put("blocked",true);
        Map.get(new Location(x,y+1)).update(upd);
        upd.clear();
        //South
        if (Map.get(new Location(x,y-1)) == null)
            addMapPiece(x,y-1);
        if (env.isObstacleSouthImmediate())
            upd.put("blocked",true);
        Map.get(new Location(x,y-1)).update(upd);
        upd.clear();
        //East
        if (Map.get(new Location(x+1,y)) == null)
            addMapPiece(x+1,y);
        if (env.isObstacleEastImmediate())
            upd.put("blocked",true);
        Map.get(new Location(x+1,y)).update(upd);
        upd.clear();
        //West
        if (Map.get(new Location(x-1,y)) == null)
            addMapPiece(x-1,y);
        if (env.isObstacleWestImmediate())
            upd.put("blocked",true);
        Map.get(new Location(x-1,y)).update(upd);
        upd.clear();
        if (size == -1)
            prune();
    }
    void prepareMine(int x, int y) {

    }
    void match() {

    }

    void prune() {
        boolean Prune = false;
        Location pCoord = null;
        Location nCoord = null;
        boolean N = false;
        for (Enumeration<Location> e = Map.keys(); e.hasMoreElements();) {
            Location temp = e.nextElement();
            if (temp.y > 0 && Map.get(temp).flagRow()) {
                Prune = true;
                pCoord = temp;
            }
            if (temp.y < 0 && Map.get(temp).flagRow()) {
                N = true;
                nCoord = temp;
            }
        }
        if (Prune && N) {
            size = pCoord.y-1+(-(nCoord.y)+1)+1;
        }
    }

    void completed(){
        complete = true;
    }
    boolean isComplete(){
        return complete;
    }

}

enum Task {MAPPING, RANDOM_MOVES, FIND_MAP_SIZE, TOWARDSGOAL, MINE_DEFENSE}
enum Name {NORTH, SOUTH, EAST, WEST}




public class topSecretAgent extends Agent {

    static Map Map = new Map();

    //variables
    Name id;
    int move;
    int index, moveindex = 0;
    boolean initMove = true;
    int horizontal,vertical = 0;
    int[] path;
    boolean prevMoveMine = false;
    boolean northBase, southBase, eastBase, westBase = false; //our base
    final int neg1 = -1;
    ArrayList<Task> arrayTaskList = new ArrayList<Task>();
    LinkedList<Integer> history = new LinkedList<Integer>();
    LinkedList<Integer> lastX = new LinkedList<Integer>();
    LinkedList<Integer> lastY = new LinkedList<Integer>();

    public int getMove(AgentEnvironment inEnvironment) {
        moveindex++;
        System.out.println(moveindex);
        if (initMove) {
            arrayTaskList.add(Task.MAPPING);
            if (inEnvironment.isBaseSouth(AgentEnvironment.OUR_TEAM, false)) {
                id = Name.NORTH;
                vertical = 10;
                arrayTaskList.add(Task.MINE_DEFENSE);
                Map = new Map();
            }
            else{
                id = Name.SOUTH;
                vertical = 1;
                arrayTaskList.add(Task.TOWARDSGOAL);
            }
            if(inEnvironment.isBaseEast(AgentEnvironment.ENEMY_TEAM, false)){
                horizontal = 1;
            }
            else{
                horizontal = 10;
                eastBase = true;
            }
            initMove = false;
        }
        if(history.isEmpty()){
            history.addLast(-2);
        }

        boolean obstacleN = inEnvironment.isObstacleNorthImmediate() || Map.isDeadEnd(horizontal, vertical + 1);
        boolean obstacleS = inEnvironment.isObstacleSouthImmediate() || Map.isDeadEnd(horizontal, vertical - 1);
        boolean obstacleE = inEnvironment.isObstacleEastImmediate() || Map.isDeadEnd(horizontal + 1, vertical);
        boolean obstacleW = inEnvironment.isObstacleWestImmediate() || Map.isDeadEnd(horizontal - 1, vertical);
        if(id == Name.SOUTH){
            if(inEnvironment.isBaseNorth(AgentEnvironment.OUR_TEAM, false) && !inEnvironment.isBaseWest(AgentEnvironment.OUR_TEAM, false) && !inEnvironment.isBaseEast(AgentEnvironment.OUR_TEAM, false) && inEnvironment.isObstacleSouthImmediate()){
                vertical = 1;
                if(inEnvironment.isBaseEast(AgentEnvironment.ENEMY_TEAM, false)){
                    horizontal = 1;
                }
                else{
                    horizontal = 10;
                    eastBase = true;
                }
            }
        }
        else{
            if(inEnvironment.isBaseSouth(AgentEnvironment.OUR_TEAM, false) && !inEnvironment.isBaseWest(AgentEnvironment.OUR_TEAM, false) && !inEnvironment.isBaseEast(AgentEnvironment.OUR_TEAM, false) && inEnvironment.isObstacleNorthImmediate()){
                vertical = 10;
                if(inEnvironment.isBaseEast(AgentEnvironment.ENEMY_TEAM, false)){
                    horizontal = 1;
                }
                else{
                    horizontal = 10;
                    eastBase = true;
                }
            }
        }
        //Temporary Tasks
        if (arrayTaskList.contains(Task.MAPPING)) {
            Map.updateMap(horizontal, vertical, inEnvironment);
        }

        boolean[] obstacles = new boolean[] {obstacleN, obstacleS, obstacleE, obstacleW};
        int openings = 4;
        for (int i = 0; i < 4; i++){
            if (obstacles[i])
                openings--;
        }
        if (openings == 1) {
            System.out.println("DEADEND");
            Map.setDeadEnd(horizontal,vertical);
        }

        //Final Tasks
        if(arrayTaskList.contains(Task.MINE_DEFENSE)){
            move = MINE_DEFENSE(inEnvironment, obstacleN, obstacleS, obstacleE, obstacleW);
        }
        if(arrayTaskList.contains(Task.TOWARDSGOAL)) {
            move = towardsGoal(inEnvironment, obstacleN, obstacleS, obstacleE, obstacleW);
        }
        if(arrayTaskList.contains(Task.RANDOM_MOVES)){
            move = random(inEnvironment);
        }

        if(openings == 1){
            if(!obstacleN)
                move = AgentAction.MOVE_NORTH;
            else if(!obstacleS)
                move = AgentAction.MOVE_SOUTH;
            else if(!obstacleW)
                move = AgentAction.MOVE_WEST;
            else if(!obstacleE)
                move = AgentAction.MOVE_EAST;
        }


        lastX.addFirst(horizontal);
        lastY.addFirst(vertical);
        Object[] preXArray = lastX.toArray();
        Object[] preYArray = lastY.toArray();

        if(preXArray.length > 6 && preYArray.length > 6 && preXArray[0] == preXArray[2] && preYArray[0] == preYArray[2] && preXArray[1] == preXArray[3] && preYArray[1] == preYArray[3]){
            Map.setDeadEnd((Integer)preXArray[1], (Integer)preYArray[1]);
        }
        switch (move){
            case 0:
                vertical++;
                break;
            case 2:
                horizontal++;
                break;
            case 3:
                horizontal--;
                break;
            case 1:
                vertical--;
                break;
            case 379037:
                Map.prepareMine(horizontal, vertical);
                break;
            case -1:
                break;
            default:
                break;
        }


        if(move == AgentAction.PLANT_HYPERDEADLY_PROXIMITY_MINE)
            prevMoveMine = true;
        else
            prevMoveMine = false;
        if(move != AgentAction.PLANT_HYPERDEADLY_PROXIMITY_MINE){
            history.addLast(move);
        }
        if(move == oppositeMove(history.getLast()) && move != -1 && !inEnvironment.hasFlag()){
            Map.setDeadEnd(horizontal, vertical);
        }
        return move;

    }

    public int towardsGoal(AgentEnvironment inEnvironment, boolean obstacleN, boolean obstacleS, boolean obstacleE, boolean obstacleW){


        if(inEnvironment.isAgentNorth(AgentEnvironment.ENEMY_TEAM, true))
            obstacleN = true;
        if(inEnvironment.isAgentSouth(AgentEnvironment.ENEMY_TEAM, true))
            obstacleS = true;
        if(inEnvironment.isAgentEast(AgentEnvironment.ENEMY_TEAM, true))
            obstacleE = true;
        if(inEnvironment.isAgentWest(AgentEnvironment.ENEMY_TEAM, true))
            obstacleW = true;

        boolean goalNorth;
        boolean goalSouth;
        boolean goalEast;
        boolean goalWest;


        if( !inEnvironment.hasFlag() ) {
            goalNorth = inEnvironment.isFlagNorth(
                    AgentEnvironment.ENEMY_TEAM, false );

            goalSouth = inEnvironment.isFlagSouth(
                    AgentEnvironment.ENEMY_TEAM, false );

            goalEast = inEnvironment.isFlagEast(
                    AgentEnvironment.ENEMY_TEAM, false );

            goalWest = inEnvironment.isFlagWest(
                    AgentEnvironment.ENEMY_TEAM, false );
        }
        else {
            goalNorth = inEnvironment.isBaseNorth(
                    AgentEnvironment.OUR_TEAM, false );

            goalSouth = inEnvironment.isBaseSouth(
                    AgentEnvironment.OUR_TEAM, false );

            goalEast = inEnvironment.isBaseEast(
                    AgentEnvironment.OUR_TEAM, false );

            goalWest = inEnvironment.isBaseWest(
                    AgentEnvironment.OUR_TEAM, false );
        }

        if( goalNorth && goalEast ) {

            if(inEnvironment.hasFlag() && !obstacleE){
                return AgentAction.MOVE_EAST;
            }
            else if(inEnvironment.hasFlag() && obstacleE && !obstacleS){
                return AgentAction.MOVE_SOUTH;
            }
            else if( !obstacleE ) {
                return AgentAction.MOVE_EAST;
            }
            else if( !obstacleN ) {
                return AgentAction.MOVE_NORTH;
            }
            else if(!obstacleW){
                return AgentAction.MOVE_WEST;
            }
            else if(!obstacleS){
                return AgentAction.MOVE_SOUTH;
            }
        }

        if( goalNorth && goalWest ) {
            if(inEnvironment.hasFlag() && !obstacleW){
                return AgentAction.MOVE_WEST;
            }
            else if(inEnvironment.hasFlag() && obstacleW && !obstacleS){
                return AgentAction.MOVE_SOUTH;
            }
            else if( !obstacleW ) {
                return AgentAction.MOVE_WEST;
            }
            else if( !obstacleN ) {
                return AgentAction.MOVE_NORTH;
            }
            else if(!obstacleE){
                return AgentAction.MOVE_EAST;
            }
            else if(!obstacleS){
                return AgentAction.MOVE_SOUTH;
            }
        }

        if( goalSouth && goalEast ) {
            if( !obstacleE ) {
                return AgentAction.MOVE_EAST;
            }
            else if( !obstacleS ) {
                return AgentAction.MOVE_SOUTH;
            }
            else if(!obstacleW){
                return AgentAction.MOVE_WEST;
            }
            else if(!obstacleN){
                return AgentAction.MOVE_NORTH;
            }
        }

        if( goalSouth && goalWest) {
            if( !obstacleW ) {
                return AgentAction.MOVE_WEST;
            }
            else if( !obstacleS ) {
                return AgentAction.MOVE_SOUTH;
            }
            else if(!obstacleE){
                return AgentAction.MOVE_EAST;
            }
            else if(!obstacleN){
                return AgentAction.MOVE_NORTH;
            }
        }

        if( goalNorth) {
            if(inEnvironment.hasFlag() && !obstacleN){
                return AgentAction.MOVE_NORTH;
            }
            else if(!obstacleN){
                return AgentAction.MOVE_NORTH;
            }
            else if(!obstacleE){
                return AgentAction.MOVE_EAST;
            }
            else if(!obstacleW){
                return AgentAction.MOVE_WEST;
            }
            else if(!obstacleS){
                return AgentAction.MOVE_SOUTH;
            }
        }

        if( goalEast) {
            if(inEnvironment.hasFlag() && !obstacleS){
                return AgentAction.MOVE_SOUTH;
            }
            else if( !obstacleE ) {
                return AgentAction.MOVE_EAST;
            }
            else if( !obstacleN ) {
                return AgentAction.MOVE_NORTH;
            }
            else if(!obstacleS){
                return AgentAction.MOVE_SOUTH;
            }
            else if(!obstacleW){
                return AgentAction.MOVE_WEST;
            }
        }

        if( goalWest) {
            if(inEnvironment.hasFlag() && !obstacleS){
                return AgentAction.MOVE_SOUTH;
            }
            else if( !obstacleW ) {
                return AgentAction.MOVE_WEST;
            }
            else if(!obstacleN ) {
                return AgentAction.MOVE_NORTH;
            }
            else if(!obstacleS){
                return AgentAction.MOVE_SOUTH;
            }
            else if(!obstacleE){
                return AgentAction.MOVE_EAST;
            }
        }

        if( goalSouth) {
            if(inEnvironment.hasFlag() && !obstacleS){
                return AgentAction.MOVE_SOUTH;
            }
            else if(!obstacleS){
                return AgentAction.MOVE_SOUTH;
            }
            else if(!obstacleE){
                return AgentAction.MOVE_EAST;
            }
            else if(!obstacleW){
                return AgentAction.MOVE_WEST;
            }
            else if(!obstacleN){
                return AgentAction.MOVE_NORTH;
            }
        }
        return AgentAction.DO_NOTHING;
    }

    public int random(AgentEnvironment inEnvironment){
        double rand = Math.random();

        if( rand < 0.25 ) {
            return AgentAction.MOVE_NORTH;
        }
        else if( rand < 0.5 ) {
            return AgentAction.MOVE_SOUTH;
        }
        else if( rand < 0.75 ) {
            return AgentAction.MOVE_EAST;
        }
        else {
            return AgentAction.MOVE_WEST;
        }
    }



    public int oppositeMove(int move){
        if(move == 0)
            return 1;
        if(move == 1)
            return 0;
        if (move == 2)
            return 3;
        if (move == 3)
            return 2;
        else
            return -1;
    }


    public int MINE_DEFENSE(AgentEnvironment inEnvironment, boolean obstacleN, boolean obstacleS, boolean obstacleE, boolean obstacleW){

        if(prevMoveMine){
            if (eastBase && !obstacleW)
                return AgentAction.MOVE_WEST;
            else if(!obstacleE)
                return AgentAction.MOVE_EAST;
        }
        else if(inEnvironment.isBaseEast(AgentEnvironment.OUR_TEAM, true)){
            if(!obstacleS && id == Name.NORTH)
                return AgentAction.PLANT_HYPERDEADLY_PROXIMITY_MINE;

            else if(!obstacleN && id == Name.SOUTH)
                return AgentAction.PLANT_HYPERDEADLY_PROXIMITY_MINE;
        }
        else if(inEnvironment.isBaseWest(AgentEnvironment.OUR_TEAM, true)){
            if(!obstacleS && id == Name.NORTH)
                return AgentAction.PLANT_HYPERDEADLY_PROXIMITY_MINE;
            else if(!obstacleN && id == Name.SOUTH)
                return AgentAction.PLANT_HYPERDEADLY_PROXIMITY_MINE;
        }
        else if(!inEnvironment.isBaseNorth(AgentEnvironment.OUR_TEAM,false) && !inEnvironment.isBaseSouth(AgentEnvironment.OUR_TEAM, false) &&(inEnvironment.isBaseEast(AgentEnvironment.OUR_TEAM, false) || inEnvironment.isBaseWest(AgentEnvironment.OUR_TEAM, false))){
            return AgentAction.PLANT_HYPERDEADLY_PROXIMITY_MINE;
        }
        else if(id == Name.SOUTH && obstacleN){
            if(eastBase && obstacleE && !obstacleW){
                return AgentAction.MOVE_WEST;
            }
            else if(!eastBase && !obstacleE)
                return AgentAction.MOVE_EAST;

        }
        else if(id == Name.NORTH && obstacleS){
            if(eastBase && obstacleE && !obstacleW){
                return AgentAction.MOVE_WEST;
            }
            else
                return AgentAction.MOVE_EAST;
        }
        else if (inEnvironment.isBaseSouth(AgentEnvironment.OUR_TEAM,true)){
            if(inEnvironment.isBaseEast(AgentEnvironment.ENEMY_TEAM, false) && !obstacleE){
                return AgentAction.MOVE_EAST;
            }
            else if(!obstacleW)
                return AgentAction.MOVE_WEST;
        }
        else if(inEnvironment.isBaseNorth(AgentEnvironment.OUR_TEAM, true)){
            if(inEnvironment.isBaseEast(AgentEnvironment.ENEMY_TEAM, false)){
                return AgentAction.MOVE_EAST;
            }
            else if(!obstacleW)
                return AgentAction.MOVE_WEST;
        }
        else if(inEnvironment.isBaseSouth(AgentEnvironment.OUR_TEAM, false)){
            if(!obstacleS && id == Name.NORTH){
                return AgentAction.MOVE_SOUTH;
            }
            else if(!obstacleS && id == Name.SOUTH){
                return AgentAction.MOVE_NORTH;
            }
            else if(inEnvironment.isBaseEast(AgentEnvironment.OUR_TEAM, false) && !obstacleE && id == Name.SOUTH){
                return AgentAction.MOVE_EAST;
            }
            else if(inEnvironment.isBaseEast(AgentEnvironment.OUR_TEAM, false) && !obstacleE && id == Name.NORTH){
                return AgentAction.MOVE_SOUTH;
            }
            else if(inEnvironment.isBaseWest(AgentEnvironment.OUR_TEAM, false) && !obstacleW && id == Name.SOUTH){
                return AgentAction.MOVE_WEST;
            }
            else if(inEnvironment.isBaseWest(AgentEnvironment.OUR_TEAM, false) && !obstacleW && id == Name.NORTH){
                return AgentAction.MOVE_SOUTH;
            }
        }

        return AgentAction.DO_NOTHING;
    }

}
