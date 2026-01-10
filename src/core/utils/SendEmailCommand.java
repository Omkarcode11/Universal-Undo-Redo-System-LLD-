package core.utils;

public class SendEmailCommand extends AbstractCommand {

    private final String email;
    private final String content;

    public SendEmailCommand(String email, String content){
        this.email = email;
        this.content = content;
    }

    @Override
    public void execute() {
        // TODO Auto-generated method stub
        System.out.println("mail send")
    }

    @Override
    public void undo() {
        throw new UnsupportedOperationException("Unimplemented method 'undo'");
    }

    public void redo(){
        throw new UnsupportedOperationException("Unimplemented method 'redo'");
    }
    
}
