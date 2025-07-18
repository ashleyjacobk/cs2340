package commands;

@FunctionalInterface
public interface Command {
    /**
     * Execute the command with the provided tokenised arguments.
     * @param tokens tokens[0] is the command keyword; subsequent indices are the parameters supplied by the user
     * @throws Exception implementations may throw an exception if validation fails
     */
    void execute(String[] tokens) throws Exception;
} 