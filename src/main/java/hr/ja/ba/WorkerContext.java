package hr.ja.ba;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class WorkerContext {

    private final long id;

    public boolean isCancelled() {
        return Thread.currentThread().isInterrupted();
    }
}
