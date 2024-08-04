package bugs.stackoverflow.belisarius.commands;

import bugs.stackoverflow.belisarius.services.MonitorService;

import org.sobotics.chatexchange.chat.Message;

public abstract class Command {
    protected final Message message;

    public Command(Message message) {
        this.message = message;
    }

    public boolean validate() {
        String content = this.message.getPlainContent();
        String commandName = this.getName();

        return content
            .split(" ")[1]
            .toLowerCase()
            .equals(commandName);
    }

    protected String getNameAndId() {
        String name = this.message.getUser().getName();
        long id = this.message.getUser().getId();

        return name + " (" + id + ")";
    }

    protected boolean isModOrRO() {
        boolean isMod = this.message.getUser().isModerator();
        boolean isRO = this.message.getUser().isRoomOwner();

        return isMod || isRO;
    }

    protected void sendFailMessage(MonitorService service) {
        service.replyToMessage(
            this.message.getId(),
            "You must be either a moderator or a room owner "
                + "to execute the " + this.getName() + " command."
        );
    }

    public abstract void execute(MonitorService service);

    abstract String getDescription();

    abstract String getName();
}
