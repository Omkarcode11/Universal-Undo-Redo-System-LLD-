package core.utils;

public class SendEmailCommand extends AbstractCommand {

    private final String subject;
    private final String to;
    private final String body;

    public SendEmailCommand(String to, String subject, String body) {
        this.to = to;
        this.subject = subject;
        this.body = body;
    }

    @Override

    public void execute() {
        System.out.println("Sending email to: " + to);
        System.out.println("Subject: " + subject);
        System.out.println("Body: " + body);
    }

    @Override
    public void undo() {
        throw new UnsupportedOperationException("Unimplemented method 'undo'");
    }

    public void redo() {
        execute();
    }

}
