package bugs.stackoverflow.belisarius.commands;

import bugs.stackoverflow.belisarius.services.MonitorService;

public interface Command {

    boolean validate();

    void execute(MonitorService service);

    String getDescription();

    String getName();

}
