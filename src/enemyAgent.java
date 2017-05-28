/**
 * Created by caitlin.ye on 5/28/17.
 */

public class enemyAgent {

    import ctf.common.AgentEnvironment;
import ctf.agent.Agent;

import ctf.common.AgentAction;

    public class enemyAgent extends Agent {

        // implements Agent.getMove() interface
        public int getMove(AgentEnvironment inEnvironment) {

            // booleans describing direction of goal
            // goal is either enemy flag, or our base
            boolean goalNorth;
            boolean goalSouth;
            boolean goalEast;
            boolean goalWest;


            if (!inEnvironment.hasFlag() && !inEnvironment.hasFlag(inEnvironment.OUR_TEAM)) {
                // make goal the enemy flag
                goalNorth = inEnvironment.isFlagNorth(
                        inEnvironment.ENEMY_TEAM, false);

                goalSouth = inEnvironment.isFlagSouth(
                        inEnvironment.ENEMY_TEAM, false);

                goalEast = inEnvironment.isFlagEast(
                        inEnvironment.ENEMY_TEAM, false);

                goalWest = inEnvironment.isFlagWest(
                        inEnvironment.ENEMY_TEAM, false);
            } else if (inEnvironment.hasFlag(inEnvironment.ENEMY_TEAM)) {
                //The enemy has our flag
                goalNorth = inEnvironment.isAgentNorth(
                        inEnvironment.ENEMY_TEAM, false);
                goalEast = inEnvironment.isAgentEast(
                        inEnvironment.ENEMY_TEAM, false);
                goalSouth = inEnvironment.isAgentSouth(
                        inEnvironment.ENEMY_TEAM, false);
                goalWest = inEnvironment.isAgentWest(
                        inEnvironment.ENEMY_TEAM, false);
            } else if (!inEnvironment.hasFlag() &&
                    inEnvironment.hasFlag(inEnvironment.OUR_TEAM)) {
                //This agent does not have the flag but a teammate does
                //Run offense, targeting enemy agents
                goalNorth = inEnvironment.isAgentNorth(
                        inEnvironment.ENEMY_TEAM, false);
                goalEast = inEnvironment.isAgentEast(
                        inEnvironment.ENEMY_TEAM, false);
                goalSouth = inEnvironment.isAgentSouth(
                        inEnvironment.ENEMY_TEAM, false);
                goalWest = inEnvironment.isAgentWest(
                        inEnvironment.ENEMY_TEAM, false);
            } else {
                // we have enemy flag.
                // make goal our base
                goalNorth = inEnvironment.isBaseNorth(
                        inEnvironment.OUR_TEAM, false);

                goalSouth = inEnvironment.isBaseSouth(
                        inEnvironment.OUR_TEAM, false);

                goalEast = inEnvironment.isBaseEast(
                        inEnvironment.OUR_TEAM, false);

                goalWest = inEnvironment.isBaseWest(
                        inEnvironment.OUR_TEAM, false);
            }

            // now we have direction booleans for our goal

            // check for immediate obstacles blocking our path
            boolean obstNorth = inEnvironment.isObstacleNorthImmediate();
            boolean obstSouth = inEnvironment.isObstacleSouthImmediate();
            boolean obstEast = inEnvironment.isObstacleEastImmediate();
            boolean obstWest = inEnvironment.isObstacleWestImmediate();
            //Check if enemy agents are immediately nearby
            boolean enemyAgentNorth = inEnvironment.isAgentNorth(
                    inEnvironment.ENEMY_TEAM, true);
            boolean enemyAgentEast = inEnvironment.isAgentEast(
                    inEnvironment.ENEMY_TEAM, true);
            boolean enemyAgentSouth = inEnvironment.isAgentSouth(
                    inEnvironment.ENEMY_TEAM, true);
            boolean enemyAgentWest = inEnvironment.isAgentWest(
                    inEnvironment.ENEMY_TEAM, true);


        }

    }


}
