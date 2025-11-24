import hr.ja.ba.Worker2;
import hr.ja.ba.WorkerContext;
import hr.ja.ba.WorkerResult;

void main() {
    IO.println("Hello, World!");


}


class DemoWorker2 implements Worker2<DemoWorker2Input, DemoWorker2Output> {

    @Override
    public DemoWorker2Output run(WorkerContext context, DemoWorker2Input input) throws Exception {
        return null;
    }
}

class DemoWorker2Input implements Worker2.Input {

}

class DemoWorker2Output implements Worker2.Output {

}

class Manager2 {
    public  void executeWorker() throws Exception {
        DemoWorker2Input input = new DemoWorker2Input();
        run(DemoWorker2.class, input);
    }

    private void run(Class<? extends Worker2<?,?>> demoWorker2Class, DemoWorker2.Input input) {

    }
}
