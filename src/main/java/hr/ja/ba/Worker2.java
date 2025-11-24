package hr.ja.ba;

public interface Worker2<Input, Output> {

    Output run(WorkerContext context, Input input) throws Exception;


    interface Input {

    }

    interface Output {

    }

}



