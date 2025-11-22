package hr.ja.w3;

public interface BioTask<I, O> {

    void execute(I input, TaskContext context) throws Exception;
    O getOutput();
}
