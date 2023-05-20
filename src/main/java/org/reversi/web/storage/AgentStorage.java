package org.reversi.web.storage;

import org.reversi.web.model.ReversiAgent;

import java.util.HashMap;
import java.util.Map;

public class AgentStorage {
    private final static Map<String, ReversiAgent> AGENTS = new HashMap<>();
    private final static AgentStorage INSTANCE = new AgentStorage();
    private AgentStorage() {}

    /**
     * getter for the singleton storage.
     *
     * @return the instance
     */
    public static synchronized AgentStorage getInstance() {
        return INSTANCE;
    }

    /**
     * getter for the stored agents
     *
     * @return the agents
     */
    public Map<String, ReversiAgent> getAgents() {
        return AGENTS;
    }

    /**
     * adds agent to the storage
     *
     * @param agent the agent
     */
    public void addAgent(ReversiAgent agent) {
        AGENTS.put(agent.getModel().getGameId(), agent);
    }
}
