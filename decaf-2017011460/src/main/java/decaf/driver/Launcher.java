package decaf.driver;

/**
 * Entry of the compiler.
 */
public class Launcher {

    /**
     * Launch the compiler with command line args.
     *
     * @param args command line args and options
     */
    public static void withArgs(String[] args) {
        var parser = new OptParser();
        parser.parse(args).ifPresent(Launcher::withConfig);
    }

    /**
     * Launch the compiler with configuration.
     *
     * @param config compiler configuration
     */
    public static void withConfig(Config config) {
        var tasks = new TaskFactory(config);
        var task = switch (config.target) {
            case PA1 -> tasks.parse();
            case PA2 -> tasks.typeCheck();
            default -> throw new IllegalArgumentException("target not implemented");
        };
        task.apply(config.source);
    }
}
